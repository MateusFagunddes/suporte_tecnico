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
    $stmt = $pdo->prepare("SELECT id,nome,email,role FROM usuarios WHERE email = ? AND senha = ?");
    $stmt->execute([$email, $senha]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);
    echo json_encode($user ?: []);
    exit;
}

if ($acao == 'registrar') {
    $nome = $_POST['nome'] ?? '';
    $email = $_POST['email'] ?? '';
    $senha = $_POST['senha'] ?? '';
    $role = $_POST['role'] ?? 'usuario';
    // Very basic: no hashing (change in production)
    $stmt = $pdo->prepare("INSERT INTO usuarios (nome, email, senha, role) VALUES (?, ?, ?, ?)");
    try {
        $ok = $stmt->execute([$nome, $email, $senha, $role]);
        $id = $pdo->lastInsertId();
        echo json_encode(['status' => $ok ? 'ok' : 'error', 'id' => $id, 'role' => $role]);
    } catch (PDOException $e) {
        echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
    }
    exit;
}

if ($acao == 'atualizar_status') {
    $chamado_id = $_POST['chamado_id'] ?? '';
    $status = $_POST['status'] ?? '';

    // Validar se o status é válido
    $valid_statuses = ['aberto', 'em andamento', 'resolvido'];
    if (!in_array($status, $valid_statuses)) {
        echo json_encode(['status' => 'error', 'message' => 'Status inválido']);
        exit;
    }

    $stmt = $pdo->prepare("UPDATE chamados SET status = ? WHERE id = ?");
    $ok = $stmt->execute([$status, $chamado_id]);
    echo json_encode(['status' => $ok ? 'ok' : 'error']);
    exit;
}

echo json_encode(['error' => 'acao inválida']);
?>
