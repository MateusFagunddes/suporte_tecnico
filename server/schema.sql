-- MySQL schema for suporte_tecnico
CREATE DATABASE IF NOT EXISTS suporte_tecnico;
USE suporte_tecnico;

CREATE TABLE IF NOT EXISTS usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(100),
  email VARCHAR(100) UNIQUE,
  senha VARCHAR(100),
  role ENUM('usuario','tecnico') DEFAULT 'usuario'
);

CREATE TABLE IF NOT EXISTS chamados (
  id INT AUTO_INCREMENT PRIMARY KEY,
  titulo VARCHAR(100),
  descricao TEXT,
  status ENUM('aberto','em andamento','resolvido') DEFAULT 'aberto',
  data_abertura DATETIME DEFAULT CURRENT_TIMESTAMP,
  usuario_id INT,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS fcm_tokens (
  id INT AUTO_INCREMENT PRIMARY KEY,
  usuario_id INT UNIQUE,
  fcm_token TEXT,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);
