<?php
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET,POST,OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

$pdo = new PDO("mysql:host=localhost;dbname=suporte_tecnico;charset=utf8","root","");

$acao = isset($_GET['acao']) ? $_GET['acao'] : '';

// Função para enviar notificação FCM
function enviarNotificacao($fcm_token, $title, $body) {
    // IMPORTANTE: Substitua pela sua chave real do servidor FCM
    // Você pode obter esta chave no Console do Firebase -> Configurações do Projeto -> Cloud Messaging
    $serverKey = 'chave';

    $notification = [
        'title' => $title,
        'body' => $body,
        'icon' => 'ic_notification',
        'sound' => 'default'
    ];

    $data = [
        'to' => $fcm_token,
        'notification' => $notification,
        'data' => [
            'message' => $body
        ]
    ];

    $headers = [
        'Authorization: key=' . $serverKey,
        'Content-Type: application/json'
    ];

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send');
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));

    $result = curl_exec($ch);
    curl_close($ch);

    return $result;
}

// Função para verificar se a tabela fcm_tokens existe
function tabelaFcmTokensExiste($pdo) {
    try {
        $stmt = $pdo->query("SHOW TABLES LIKE 'fcm_tokens'");
        return $stmt->rowCount() > 0;
    } catch (Exception $e) {
        return false;
    }
}

// Função para notificar técnicos sobre novo chamado
function notificarTecnicos($pdo, $titulo_chamado) {
    if (!tabelaFcmTokensExiste($pdo)) {
        error_log("Tabela fcm_tokens não existe. Pulando notificações.");
        return;
    }

    $stmt = $pdo->prepare("
        SELECT ft.fcm_token
        FROM fcm_tokens ft
        JOIN usuarios u ON ft.usuario_id = u.id
        WHERE u.role = 'tecnico' AND ft.fcm_token IS NOT NULL
    ");
    $stmt->execute();
    $tecnicos = $stmt->fetchAll(PDO::FETCH_ASSOC);

    foreach ($tecnicos as $tecnico) {
        enviarNotificacao(
            $tecnico['fcm_token'],
            'Novo Chamado',
            "Um novo chamado foi criado: $titulo_chamado"
        );
    }
}

// Função para notificar dono do chamado sobre mudança de status
function notificarDonoStatus($pdo, $chamado_id, $novo_status) {
    if (!tabelaFcmTokensExiste($pdo)) {
        error_log("Tabela fcm_tokens não existe. Pulando notificações.");
        return;
    }

    $stmt = $pdo->prepare("
        SELECT c.titulo, ft.fcm_token, u.nome
        FROM chamados c
        JOIN usuarios u ON c.usuario_id = u.id
        JOIN fcm_tokens ft ON u.id = ft.usuario_id
        WHERE c.id = ? AND ft.fcm_token IS NOT NULL
    ");
    $stmt->execute([$chamado_id]);
    $chamado = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($chamado) {
        enviarNotificacao(
            $chamado['fcm_token'],
            'Status do Chamado Atualizado',
            "Seu chamado '{$chamado['titulo']}' foi alterado para: $novo_status"
        );
    }
}

$acao = isset($_GET['acao']) ? $_GET['acao'] : '';

if ($acao == 'listar') {
    $usuario_id = $_GET['usuario_id'] ?? null;
    $user_role = $_GET['user_role'] ?? 'usuario';

    if ($user_role == 'tecnico') {
        // Técnicos veem todos os chamados
        $stmt = $pdo->query("SELECT c.*, u.nome as nome_usuario, u.email as email_usuario FROM chamados c LEFT JOIN usuarios u ON c.usuario_id = u.id ORDER BY c.data_abertura DESC");
    } else {
        // Usuários veem apenas seus próprios chamados
        if ($usuario_id) {
            $stmt = $pdo->prepare("SELECT c.*, u.nome as nome_usuario, u.email as email_usuario FROM chamados c LEFT JOIN usuarios u ON c.usuario_id = u.id WHERE c.usuario_id = ? ORDER BY c.data_abertura DESC");
            $stmt->execute([$usuario_id]);
        } else {
            // Se não tiver usuario_id, retorna array vazio
            echo json_encode([]);
            exit;
        }
    }

    echo json_encode($stmt->fetchAll(PDO::FETCH_ASSOC));
    exit;
}

if ($acao == 'criar') {
    $titulo = $_POST['titulo'] ?? '';
    $descricao = $_POST['descricao'] ?? '';
    $usuario_id = $_POST['usuario_id'] ?? null;
    $stmt = $pdo->prepare("INSERT INTO chamados (titulo, descricao, usuario_id) VALUES (?, ?, ?)");
    $ok = $stmt->execute([$titulo, $descricao, $usuario_id]);

    if ($ok) {
        // Notificar técnicos sobre novo chamado
        notificarTecnicos($pdo, $titulo);
    }

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

    if ($ok) {
        // Notificar dono do chamado sobre mudança de status
        notificarDonoStatus($pdo, $chamado_id, $status);
    }

    echo json_encode(['status' => $ok ? 'ok' : 'error']);
    exit;
}

if ($acao == 'editar_chamado') {
    $chamado_id = $_POST['chamado_id'] ?? '';
    $titulo = $_POST['titulo'] ?? '';
    $descricao = $_POST['descricao'] ?? '';
    $usuario_id = $_POST['usuario_id'] ?? null;
    $user_role = $_POST['user_role'] ?? 'usuario';

    // Verificar permissões
    $stmt = $pdo->prepare("SELECT usuario_id, status FROM chamados WHERE id = ?");
    $stmt->execute([$chamado_id]);
    $chamado = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$chamado) {
        echo json_encode(['status' => 'error', 'message' => 'Chamado não encontrado']);
        exit;
    }

    // Regras de permissão
    $pode_editar = false;
    if ($user_role == 'tecnico') {
        $pode_editar = true; // Técnico pode editar qualquer chamado
    } else if ($chamado['usuario_id'] == $usuario_id && $chamado['status'] == 'aberto') {
        $pode_editar = true; // Usuário pode editar seus chamados abertos
    }

    if (!$pode_editar) {
        echo json_encode(['status' => 'error', 'message' => 'Sem permissão para editar este chamado']);
        exit;
    }

    $stmt = $pdo->prepare("UPDATE chamados SET titulo = ?, descricao = ? WHERE id = ?");
    $ok = $stmt->execute([$titulo, $descricao, $chamado_id]);
    echo json_encode(['status' => $ok ? 'ok' : 'error']);
    exit;
}

if ($acao == 'excluir_chamado') {
    $chamado_id = $_POST['chamado_id'] ?? '';
    $usuario_id = $_POST['usuario_id'] ?? null;
    $user_role = $_POST['user_role'] ?? 'usuario';

    // Verificar permissões
    $stmt = $pdo->prepare("SELECT usuario_id, status FROM chamados WHERE id = ?");
    $stmt->execute([$chamado_id]);
    $chamado = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$chamado) {
        echo json_encode(['status' => 'error', 'message' => 'Chamado não encontrado']);
        exit;
    }

    // Regras de permissão
    $pode_excluir = false;
    if ($user_role == 'tecnico') {
        $pode_excluir = true; // Técnico pode excluir qualquer chamado
    } else if ($chamado['usuario_id'] == $usuario_id && $chamado['status'] == 'aberto') {
        $pode_excluir = true; // Usuário pode excluir seus chamados abertos
    }

    if (!$pode_excluir) {
        echo json_encode(['status' => 'error', 'message' => 'Sem permissão para excluir este chamado']);
        exit;
    }

    $stmt = $pdo->prepare("DELETE FROM chamados WHERE id = ?");
    $ok = $stmt->execute([$chamado_id]);
    echo json_encode(['status' => $ok ? 'ok' : 'error']);
    exit;
}

if ($acao == 'salvar_fcm_token') {
    $usuario_id = $_POST['usuario_id'] ?? null;
    $fcm_token = $_POST['fcm_token'] ?? '';

    if (!$usuario_id || !$fcm_token) {
        echo json_encode(['status' => 'error', 'message' => 'Parâmetros obrigatórios ausentes']);
        exit;
    }

    // Verificar se a tabela existe antes de tentar usar
    if (!tabelaFcmTokensExiste($pdo)) {
        echo json_encode(['status' => 'error', 'message' => 'Tabela fcm_tokens não existe. Execute o schema.sql primeiro.']);
        exit;
    }

    $stmt = $pdo->prepare("INSERT INTO fcm_tokens (usuario_id, fcm_token) VALUES (?, ?) ON DUPLICATE KEY UPDATE fcm_token = VALUES(fcm_token)");
    $ok = $stmt->execute([$usuario_id, $fcm_token]);    echo json_encode(['status' => $ok ? 'ok' : 'error']);
    exit;
}

// === CRUD de Usuários ===

if ($acao == 'listar_usuarios') {
    // Apenas usuários técnicos podem listar usuários (implementar verificação se necessário)
    $stmt = $pdo->query("SELECT id, nome, email, role FROM usuarios ORDER BY nome");
    echo json_encode($stmt->fetchAll(PDO::FETCH_ASSOC));
    exit;
}

if ($acao == 'criar_usuario') {
    $nome = $_POST['nome'] ?? '';
    $email = $_POST['email'] ?? '';
    $senha = $_POST['senha'] ?? '';
    $role = $_POST['role'] ?? 'usuario';

    if (!$nome || !$email || !$senha) {
        echo json_encode(['status' => 'error', 'message' => 'Dados obrigatórios ausentes']);
        exit;
    }

    // Verificar se email já existe
    $stmt = $pdo->prepare("SELECT id FROM usuarios WHERE email = ?");
    $stmt->execute([$email]);
    if ($stmt->fetch()) {
        echo json_encode(['status' => 'error', 'message' => 'Email já cadastrado']);
        exit;
    }

    $stmt = $pdo->prepare("INSERT INTO usuarios (nome, email, senha, role) VALUES (?, ?, ?, ?)");
    try {
        $ok = $stmt->execute([$nome, $email, $senha, $role]);
        $id = $pdo->lastInsertId();
        echo json_encode(['status' => $ok ? 'ok' : 'error', 'id' => $id]);
    } catch (PDOException $e) {
        echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
    }
    exit;
}

if ($acao == 'editar_usuario') {
    $usuario_id = $_POST['usuario_id'] ?? '';
    $nome = $_POST['nome'] ?? '';
    $email = $_POST['email'] ?? '';
    $senha = $_POST['senha'] ?? '';
    $role = $_POST['role'] ?? 'usuario';

    if (!$usuario_id || !$nome || !$email) {
        echo json_encode(['status' => 'error', 'message' => 'Dados obrigatórios ausentes']);
        exit;
    }

    // Verificar se email já existe em outro usuário
    $stmt = $pdo->prepare("SELECT id FROM usuarios WHERE email = ? AND id != ?");
    $stmt->execute([$email, $usuario_id]);
    if ($stmt->fetch()) {
        echo json_encode(['status' => 'error', 'message' => 'Email já cadastrado para outro usuário']);
        exit;
    }

    // Atualizar com ou sem senha
    if (!empty($senha)) {
        $stmt = $pdo->prepare("UPDATE usuarios SET nome = ?, email = ?, senha = ?, role = ? WHERE id = ?");
        $ok = $stmt->execute([$nome, $email, $senha, $role, $usuario_id]);
    } else {
        $stmt = $pdo->prepare("UPDATE usuarios SET nome = ?, email = ?, role = ? WHERE id = ?");
        $ok = $stmt->execute([$nome, $email, $role, $usuario_id]);
    }

    echo json_encode(['status' => $ok ? 'ok' : 'error']);
    exit;
}

if ($acao == 'excluir_usuario') {
    $usuario_id = $_POST['usuario_id'] ?? '';

    if (!$usuario_id) {
        echo json_encode(['status' => 'error', 'message' => 'ID do usuário obrigatório']);
        exit;
    }

    // Verificar se usuário tem chamados associados
    $stmt = $pdo->prepare("SELECT COUNT(*) as total FROM chamados WHERE usuario_id = ?");
    $stmt->execute([$usuario_id]);
    $result = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($result['total'] > 0) {
        echo json_encode(['status' => 'error', 'message' => 'Usuário possui chamados associados. Não é possível excluir.']);
        exit;
    }

    $stmt = $pdo->prepare("DELETE FROM usuarios WHERE id = ?");
    $ok = $stmt->execute([$usuario_id]);
    echo json_encode(['status' => $ok ? 'ok' : 'error']);
    exit;
}

echo json_encode(['error' => 'acao inválida']);
