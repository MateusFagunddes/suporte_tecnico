<?php
// Script de teste para verificar sistema de notificações

header('Content-Type: text/html; charset=utf-8');

$pdo = new PDO("mysql:host=localhost;dbname=suporte_tecnico;charset=utf8","root","");

echo "<h1>Teste do Sistema de Notificações FCM</h1>";

// 1. Verificar se a tabela fcm_tokens existe
function tabelaFcmTokensExiste($pdo) {
    try {
        $stmt = $pdo->query("SHOW TABLES LIKE 'fcm_tokens'");
        return $stmt->rowCount() > 0;
    } catch (Exception $e) {
        return false;
    }
}

echo "<h2>1. Verificação da Tabela FCM</h2>";
if (tabelaFcmTokensExiste($pdo)) {
    echo "✅ Tabela fcm_tokens existe!<br>";

    // Verificar estrutura da tabela
    $stmt = $pdo->query("DESCRIBE fcm_tokens");
    $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
    echo "📋 Estrutura da tabela:<br>";
    foreach ($columns as $column) {
        echo "- {$column['Field']}: {$column['Type']}<br>";
    }
} else {
    echo "❌ Tabela fcm_tokens NÃO existe! Execute o arquivo create_fcm_table.sql primeiro.<br>";
}

// 2. Verificar usuários existentes
echo "<h2>2. Usuários Cadastrados</h2>";
$stmt = $pdo->query("SELECT id, nome, email, role FROM usuarios ORDER BY id");
$usuarios = $stmt->fetchAll(PDO::FETCH_ASSOC);

if (count($usuarios) > 0) {
    echo "📋 Usuários cadastrados:<br>";
    echo "<table border='1' style='border-collapse: collapse; margin: 10px 0;'>";
    echo "<tr><th>ID</th><th>Nome</th><th>Email</th><th>Role</th><th>FCM Token</th></tr>";

    foreach ($usuarios as $user) {
        // Verificar se tem token FCM
        $token = "❌ Não";
        if (tabelaFcmTokensExiste($pdo)) {
            $stmt_token = $pdo->prepare("SELECT fcm_token FROM fcm_tokens WHERE usuario_id = ?");
            $stmt_token->execute([$user['id']]);
            $fcm_data = $stmt_token->fetch(PDO::FETCH_ASSOC);
            if ($fcm_data && !empty($fcm_data['fcm_token'])) {
                $token = "✅ Sim (" . substr($fcm_data['fcm_token'], 0, 20) . "...)";
            }
        }

        echo "<tr>";
        echo "<td>{$user['id']}</td>";
        echo "<td>{$user['nome']}</td>";
        echo "<td>{$user['email']}</td>";
        echo "<td>{$user['role']}</td>";
        echo "<td>{$token}</td>";
        echo "</tr>";
    }
    echo "</table>";
} else {
    echo "❌ Nenhum usuário cadastrado!<br>";
}

// 3. Verificar chamados
echo "<h2>3. Chamados Existentes</h2>";
$stmt = $pdo->query("SELECT c.id, c.titulo, c.status, c.data_abertura, u.nome as nome_usuario FROM chamados c LEFT JOIN usuarios u ON c.usuario_id = u.id ORDER BY c.data_abertura DESC LIMIT 5");
$chamados = $stmt->fetchAll(PDO::FETCH_ASSOC);

if (count($chamados) > 0) {
    echo "📋 Últimos chamados:<br>";
    echo "<table border='1' style='border-collapse: collapse; margin: 10px 0;'>";
    echo "<tr><th>ID</th><th>Título</th><th>Status</th><th>Usuário</th><th>Data</th></tr>";

    foreach ($chamados as $chamado) {
        echo "<tr>";
        echo "<td>{$chamado['id']}</td>";
        echo "<td>{$chamado['titulo']}</td>";
        echo "<td>{$chamado['status']}</td>";
        echo "<td>{$chamado['nome_usuario']}</td>";
        echo "<td>{$chamado['data_abertura']}</td>";
        echo "</tr>";
    }
    echo "</table>";
} else {
    echo "❌ Nenhum chamado encontrado!<br>";
}

// 4. Verificação da configuração FCM no PHP
echo "<h2>4. Configuração FCM</h2>";
$api_content = file_get_contents('api.php');
if (strpos($api_content, 'YOUR_FCM_SERVER_KEY') !== false) {
    echo "❌ PROBLEMA: A chave FCM ainda está como 'YOUR_FCM_SERVER_KEY' no arquivo api.php!<br>";
    echo "📝 <strong>AÇÃO NECESSÁRIA:</strong> Você precisa:<br>";
    echo "1. Ir ao <a href='https://console.firebase.google.com' target='_blank'>Console do Firebase</a><br>";
    echo "2. Selecionar seu projeto<br>";
    echo "3. Ir em Configurações do Projeto > Cloud Messaging<br>";
    echo "4. Copiar a 'Chave do servidor'<br>";
    echo "5. Substituir 'YOUR_FCM_SERVER_KEY' no arquivo api.php pela chave real<br>";
} else if (strpos($api_content, 'AAAA') !== false) {
    echo "⚠️ ATENÇÃO: Parece que você tem uma chave FCM configurada, mas verifique se está correta.<br>";
} else {
    echo "❌ Não foi possível verificar a configuração da chave FCM.<br>";
}

echo "<h2>5. Lista de Verificação</h2>";
echo "<p>Para que as notificações funcionem, certifique-se de que:</p>";
echo "<ul>";
echo "<li>✅ A tabela fcm_tokens foi criada (execute create_fcm_table.sql)</li>";
echo "<li>⚠️ A chave FCM real foi configurada no api.php</li>";
echo "<li>⚠️ O arquivo google-services.json está na pasta android/app/</li>";
echo "<li>⚠️ O app Android está registrado e salvo tokens FCM</li>";
echo "<li>⚠️ A URL do servidor está correta e acessível</li>";
echo "</ul>";

?>
