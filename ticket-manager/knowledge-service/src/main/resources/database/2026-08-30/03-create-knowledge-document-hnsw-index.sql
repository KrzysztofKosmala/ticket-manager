--liquibase formatted sql

--changeset ticket:03-create-knowledge-document-hnsw-index
CREATE INDEX knowledge_document_embedding_hnsw_idx
    ON knowledge_document
    USING HNSW (embedding vector_cosine_ops);
