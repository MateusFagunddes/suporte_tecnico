-- Criar tabela de status
CREATE TABLE IF NOT EXISTS status (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL UNIQUE,
    ativo BOOLEAN DEFAULT TRUE
);

-- Inserir status padrão
INSERT INTO status (nome, ativo) VALUES
('aberto', TRUE),
('em andamento', TRUE),
('resolvido', TRUE)
ON DUPLICATE KEY UPDATE nome=VALUES(nome);
