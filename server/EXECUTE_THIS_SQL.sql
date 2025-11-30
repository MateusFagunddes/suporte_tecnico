-- Execute este comando no seu banco de dados suporte_tecnico
-- via phpMyAdmin ou qualquer interface MySQL

USE suporte_tecnico;

CREATE TABLE IF NOT EXISTS fcm_tokens (
  id INT AUTO_INCREMENT PRIMARY KEY,
  usuario_id INT UNIQUE,
  fcm_token TEXT,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Verificar se a tabela foi criada
DESCRIBE fcm_tokens;
