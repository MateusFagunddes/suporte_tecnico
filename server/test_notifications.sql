-- Teste para verificar estrutura de notificações
-- Execute após aplicar o schema.sql

-- Verificar se a tabela fcm_tokens foi criada
DESCRIBE fcm_tokens;

-- Verificar usuários existentes
SELECT id, nome, email, role FROM usuarios;

-- Verificar chamados existentes
SELECT id, titulo, status, usuario_id, data_abertura FROM chamados;

-- Inserir um token de teste (substitua os valores pelos reais)
-- INSERT INTO fcm_tokens (usuario_id, fcm_token) VALUES (1, 'token_de_teste_fcm');

-- Verificar tokens cadastrados
SELECT ft.id, ft.fcm_token, u.nome, u.role
FROM fcm_tokens ft
JOIN usuarios u ON ft.usuario_id = u.id;
