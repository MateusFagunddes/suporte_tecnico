<?php
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET,POST,OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

$pdo = new PDO("mysql:host=localhost;dbname=suporte_tecnico;charset=utf8","root","");

$acao = isset($_GET['acao']) ? $_GET['acao'] : '';

if ($acao == 'listar') {
    $stmt = $pdo->query("SELECT c.*, u.nome as nome_usuario FROM chamados c LEFT JOIN usuarios u ON c.usuario_id = u.id ORDER BY c.data_abertura DESC");
    echo json_encode($stmt->fetchAll(PDO::FETCH_ASSOC));
    exit;
}

if ($acao == 'criar') {
    $titulo = $_POST['titulo'] ?? '';
    $descricao = $_POST['descricao'] ?? '';
    $usuario_id = $_POST['usuario_id'] ?? null;
    $stmt = $pdo->prepare("INSERT INTO chamados (titulo, descricao, usuario_id) VALUES (?, ?, ?)");
    $ok = $stmt->execute([$titulo, $descricao, $usuario_id]);
    echo json_encode(['status' => $ok ? 'ok' : 'error']);
    exit;
}

if ($acao == 'login') {
    $email = $_POST['email'] ?? '';
    $senha = $_POST['senha'] ?? '';
    $stmt = $pdo->prepare("SELECT id,nome,email FROM usuarios WHERE email = ? AND senha = ?");
    $stmt->execute([$email, $senha]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);
    echo json_encode($user ?: []);
    exit;
}

if ($acao == 'registrar') {
    $nome = $_POST['nome'] ?? '';
    $email = $_POST['email'] ?? '';
    $senha = $_POST['senha'] ?? '';
    // Very basic: no hashing (change in production)
    $stmt = $pdo->prepare("INSERT INTO usuarios (nome, email, senha) VALUES (?, ?, ?)");
    try {
        $ok = $stmt->execute([$nome, $email, $senha]);
        $id = $pdo->lastInsertId();
        echo json_encode(['status' => $ok ? 'ok' : 'error', 'id' => $id]);
    } catch (PDOException $e) {
        echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
    }
    exit;
}

echo json_encode(['error' => 'acao inválida']);
?>
