package pl.ticket.aiagent.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import pl.ticket.aiagent.conversation.Conversation;
import pl.ticket.aiagent.conversation.ConversationStore;
import pl.ticket.aiagent.dto.AiAgentResponse;
import pl.ticket.aiagent.exception.AiModelEmptyResponseException;
import pl.ticket.aiagent.exception.AiModelUnavailableException;
import pl.ticket.aiagent.security.CallerContext;
import pl.ticket.aiagent.security.CallerContextProvider;
import pl.ticket.aiagent.tools.ObservedToolCallback;
import pl.ticket.aiagent.tools.SelectedToolCallbackResolver;
import pl.ticket.aiagent.tools.ToolCandidate;
import pl.ticket.aiagent.tools.ToolCandidateSelector;
import pl.ticket.aiagent.tools.ToolInvocationRecorder;

import java.util.List;

@Service
public class AiAgentService
{
    private final ChatClient chatClient;
    private final ToolCandidateSelector toolCandidateSelector;
    private final SelectedToolCallbackResolver toolCallbackResolver;
    private final ToolInvocationRecorder toolInvocationRecorder;
    private final CallerContextProvider callerContextProvider;
    private final ConversationStore conversationStore;
    private final AiAgentFlowLogger flowLogger;

    public AiAgentService(
            // Spring AI tworzy builder automatycznie; na nim doklejamy system prompt, advisor i potem budujemy ChatClient.
            ChatClient.Builder chatClientBuilder,
            // Nasz @Component z trescia system promptu dla agenta.
            AiAgentInstructions instructions,
            // Nasz @Bean z SpringAiChatMemoryConfig. W srodku ma ChatMemory, a ono uzywa naszego ChatMemoryRepository.
            MessageChatMemoryAdvisor chatMemoryAdvisor,
            // Nasz @Component zalezy od profilu: local/test wybiera toole z registry, inne profile moga nic nie wybierac.
            ToolCandidateSelector toolCandidateSelector,
            // Nasz @Component. Zamienia wybrane nazwy tooli na realne ToolCallback z ToolCatalog.
            SelectedToolCallbackResolver toolCallbackResolver,
            // Nasz interface; Spring wstrzykuje InMemoryToolInvocationRecorder, ktory zapisuje historie wywolan w mapie.
            ToolInvocationRecorder toolInvocationRecorder,
            // Nasz @Component. Czyta aktualnego uzytkownika, role i scope'y z SecurityContext.
            CallerContextProvider callerContextProvider,
            // Nasz interface; Spring wstrzykuje InMemoryConversationStore z rozmowami trzymanymi w pamieci JVM.
            ConversationStore conversationStore,
            // Nasz @Component tylko od logow diagnostycznych flow: selected tools, resolved callbacks, completed request.
            AiAgentFlowLogger flowLogger
    ) {
        this.chatClient = chatClientBuilder
                .defaultSystem(instructions.systemPrompt())
                // Advisor dopina pamiec rozmowy do kazdego promptu.
                .defaultAdvisors(chatMemoryAdvisor)
                .build();
        this.toolCandidateSelector = toolCandidateSelector;
        this.toolCallbackResolver = toolCallbackResolver;
        this.toolInvocationRecorder = toolInvocationRecorder;
        this.callerContextProvider = callerContextProvider;
        this.conversationStore = conversationStore;
        this.flowLogger = flowLogger;
    }

    public AiAgentResponse ask(String userMessage) {
        return ask(userMessage, null);
    }

    public AiAgentResponse ask(String userMessage, String conversationId) {
        // Kontekst pochodzi z aktualnego SecurityContext: userId, role i scope'y do polityki tooli.
        CallerContext callerContext = callerContextProvider.current();

        // ConversationStore pilnuje naszej rozmowy i wlasciciela; advisor pozniej uzyje tego samego id.
        Conversation conversation = conversationStore.getOrCreate(conversationId, callerContext);

        // Selector wybiera kandydatow z registry/polityki, jeszcze bez dotykania callbackow MCP.
        List<ToolCandidate> selectedTools = toolCandidateSelector.selectFor(userMessage, callerContext);
        flowLogger.requestStarted(conversation.id(), selectedTools);

        // Resolver zamienia nazwy kandydatow na realne ToolCallback; brak callbacka oznacza blad konfiguracji/discovery.
        List<ToolCallback> callbacks = toolCallbackResolver.resolve(selectedTools);
        flowLogger.toolCallbacksResolved(conversation.id(), callbacks);

        // Spring AI zna tylko ToolCallback. Opakowujemy prawdziwe callbacki, zeby w call() zapisac ich uzycie.
        List<ToolCallback> observedCallbacks = callbacks.stream()
                .map(callback -> observeToolCallback(conversation.id(), callback))
                .toList();

        String answer;
        try {
            answer = chatClient.prompt()
                    .user(userMessage)
                    // Parametr wskazuje advisorowi, ktora rozmowe odczytac/zapisac przez ChatMemory.
                    .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversation.id()))
                    // Tu tylko przekazujemy dostepne toole modelowi. Zapis uzycia nastapi dopiero, gdy model wywola call().
                    .toolCallbacks(observedCallbacks)
                    // W call() Spring AI odpala petle model -> tool -> model; wtedy moze wywolac ObservedToolCallback.call().
                    .call()
                    .content();
        } catch (TransientAiException | NonTransientAiException | ResourceAccessException exception) {
            // Bledy klienta/modelu mapujemy na nasz wyjatek HTTP/API.
            throw new AiModelUnavailableException(exception);
        }

        if (!StringUtils.hasText(answer)) {
            // Pusta odpowiedz modelu traktujemy jako blad kontraktu odpowiedzi.
            throw new AiModelEmptyResponseException();
        }

        flowLogger.requestCompleted(conversation.id());

        // Zwracamy id rozmowy, zeby kolejne pytanie moglo trafic do tej samej pamieci/advisora.
        return new AiAgentResponse(answer, AiAgentResponse.Status.COMPLETED, conversation.id());
    }

    private ToolCallback observeToolCallback(String conversationId, ToolCallback callback) {
        // Recorder jest nasz; Spring go nie zna. Wywola tylko ToolCallback.call(), a wrapper zapisze success/failure.
        return new ObservedToolCallback(conversationId, callback, toolInvocationRecorder);
    }
}
