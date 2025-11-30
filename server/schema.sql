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
