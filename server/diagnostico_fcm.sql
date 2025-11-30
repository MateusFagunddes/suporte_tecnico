-- Script para verificar e corrigir problemas de notificações FCM
-- Execute este script no MySQL/phpMyAdmin para diagnosticar problemas

USE suporte_tecnico;

-- 1. Verificar se a tabela fcm_tokens existe
SELECT
    CASE
        WHEN COUNT(*) > 0 THEN 'Tabela fcm_tokens existe ✅'
        ELSE 'Tabela fcm_tokens NÃO existe ❌'
    END AS status_tabela
FROM information_schema.tables
WHERE table_schema = 'suporte_tecnico' AND table_name = 'fcm_tokens';

-- 2. Criar a tabela se não existir
CREATE TABLE IF NOT EXISTS fcm_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT UNIQUE,
    fcm_token TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- 3. Verificar usuários que têm tokens FCM cadastrados
SELECT
    'Verificação de Tokens FCM' AS titulo;

SELECT
    u.id,
    u.nome,
    u.email,
    u.role,
    CASE
        WHEN ft.fcm_token IS NOT NULL THEN 'Token Cadastrado ✅'
        ELSE 'Sem Token ❌'
    END AS status_fcm,
    CASE
        WHEN ft.fcm_token IS NOT NULL THEN CONCAT(LEFT(ft.fcm_token, 30), '...')
        ELSE 'N/A'
    END AS token_preview
FROM usuarios u
LEFT JOIN fcm_tokens ft ON u.id = ft.usuario_id
ORDER BY u.role, u.id;

-- 4. Verificar chamados recentes para teste
SELECT
    'Chamados Recentes (para teste de notificação)' AS titulo;

SELECT
    c.id,
    c.titulo,
    c.status,
    c.data_abertura,
    u.nome as usuario,
    u.role,
    CASE
        WHEN ft.fcm_token IS NOT NULL THEN 'Pode receber notificação ✅'
        ELSE 'Não pode receber notificação ❌'
    END AS notificacao_status
FROM chamados c
JOIN usuarios u ON c.usuario_id = u.id
LEFT JOIN fcm_tokens ft ON u.id = ft.usuario_id
ORDER BY c.data_abertura DESC
LIMIT 10;

-- 5. Contar técnicos que podem receber notificações
SELECT
    'Técnicos que podem receber notificações de novos chamados' AS titulo;

SELECT
    COUNT(*) as total_tecnicos,
    COUNT(ft.fcm_token) as tecnicos_com_token,
    CASE
        WHEN COUNT(ft.fcm_token) > 0 THEN 'Notificações para técnicos funcionarão ✅'
        ELSE 'Nenhum técnico cadastrado com token FCM ❌'
    END AS status
FROM usuarios u
LEFT JOIN fcm_tokens ft ON u.id = ft.usuario_id
WHERE u.role = 'tecnico';

-- 6. Script de teste - inserir token de exemplo (DESCOMENTE E AJUSTE SE NECESSÁRIO)
-- IMPORTANTE: Só descomente e execute se você souber o ID do usuário e tiver um token FCM real

-- INSERT INTO fcm_tokens (usuario_id, fcm_token)
-- VALUES (1, 'exemplo_token_fcm_aqui')
-- ON DUPLICATE KEY UPDATE fcm_token = VALUES(fcm_token);

SELECT 'Verificação completa!' AS resultado;
