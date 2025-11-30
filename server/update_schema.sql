-- Script to update existing database with role support
-- Run this if you already have a database with users

-- Add the role column to existing usuarios table
ALTER TABLE usuarios ADD COLUMN role ENUM('usuario','tecnico') DEFAULT 'usuario';

-- Optionally, you can update specific users to be technicians
-- UPDATE usuarios SET role = 'tecnico' WHERE email = 'admin@example.com';
