--liquibase formatted sql
--changeset kkosmala:1
CREATE TABLE ai_conversation (
    id VARCHAR(64) PRIMARY KEY,
    owner_subject VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    status VARCHAR(32) NOT NULL
);

CREATE TABLE ai_conversation_message (
    id BIGSERIAL PRIMARY KEY,
    conversation_id VARCHAR(64) NOT NULL,
    role VARCHAR(32) NOT NULL,
    content TEXT NOT NULL,
    message_order INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_ai_conversation_message_conversation_id
        FOREIGN KEY (conversation_id) REFERENCES ai_conversation(id) ON DELETE CASCADE
);

CREATE INDEX idx_ai_conversation_owner_subject ON ai_conversation(owner_subject);
CREATE INDEX idx_ai_conversation_message_conversation_id ON ai_conversation_message(conversation_id);

CREATE TABLE ai_agent_run (
    id VARCHAR(64) PRIMARY KEY,
    conversation_id VARCHAR(64) NOT NULL,
    user_message TEXT NOT NULL,
    selected_tools_json TEXT NOT NULL,
    answer TEXT,
    status VARCHAR(32) NOT NULL,
    error_message TEXT,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    CONSTRAINT fk_ai_agent_run_conversation_id
        FOREIGN KEY (conversation_id) REFERENCES ai_conversation(id) ON DELETE CASCADE
);

CREATE INDEX idx_ai_agent_run_conversation_id ON ai_agent_run(conversation_id);

CREATE TABLE ai_tool_invocation (
    id BIGSERIAL PRIMARY KEY,
    conversation_id VARCHAR(64) NOT NULL,
    run_id VARCHAR(64),
    tool_name VARCHAR(255) NOT NULL,
    arguments_json TEXT,
    result_json TEXT,
    status VARCHAR(32) NOT NULL,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_ai_tool_invocation_run_id
        FOREIGN KEY (run_id) REFERENCES ai_agent_run(id) ON DELETE SET NULL
);

CREATE INDEX idx_ai_tool_invocation_conversation_id ON ai_tool_invocation(conversation_id);
CREATE INDEX idx_ai_tool_invocation_run_id ON ai_tool_invocation(run_id);
