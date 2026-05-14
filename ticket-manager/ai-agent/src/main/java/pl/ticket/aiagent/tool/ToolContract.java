package pl.ticket.aiagent.tool;

public record ToolContract(
        String name,
        String legacyName,
        String description,
        String argumentDescription,
        ToolAccessMode accessMode,
        String requiredScope,
        Class<?> inputType,
        Class<?> outputType
) {
}
