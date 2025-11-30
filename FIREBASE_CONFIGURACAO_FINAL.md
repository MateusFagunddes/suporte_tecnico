# 🔥 Configuração Final do Firebase para o Projeto

## ✅ Arquivo google-services.json Atualizado!

O arquivo `google-services.json` foi atualizado com as configurações do seu projeto Firebase:
- **Projeto**: app-faculdade-2eb28
- **Package Name**: com.example.suporte (mantido o mesmo do projeto)

## ⚠️ AÇÃO NECESSÁRIA: Obter Chave FCM do Servidor

Para completar a configuração das notificações push, você precisa obter a chave do servidor FCM:

### Passos para obter a chave:

1. **Acesse o Console do Firebase:**
   - Vá para: https://console.firebase.google.com
   - Selecione o projeto: **app-faculdade-2eb28**

2. **Navegue até Cloud Messaging:**
   - Clique no ⚙️ (ícone de configurações) no canto superior esquerdo
   - Selecione "Configurações do projeto"
   - Clique na aba "Cloud Messaging"

3. **Copie a Chave do Servidor:**
   - Procure por "Chave do servidor" ou "Server key"
   - Copie a chave (formato: `AAAA...` ou `AAAAxxxx:APA91b...`)

4. **Atualize o arquivo PHP:**
   - Abra: `server/api.php`
   - Na linha ~15, substitua:
   ```php
   $serverKey = 'AAAA...:APA91b...'; // ← COLE SUA CHAVE AQUI
   ```

### Exemplo da chave FCM:
```php
// ANTES (linha ~15 do api.php):
$serverKey = 'AAAA...:APA91b...';

// DEPOIS (com sua chave real):
$serverKey = 'AAAAxxx_xxx:APA91bGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG';
```

## 📱 Adicionar App Android no Firebase (Se Necessário)

Se o seu projeto Firebase ainda não tem o app Android configurado:

1. No Console Firebase, clique em "Adicionar app" → Android
2. Use estas informações:
   - **Package name**: `com.example.suporte`
   - **App nickname**: `Suporte Técnico`
   - **SHA-1**: (opcional, para debug)

## 🧪 Como Testar Após Configurar a Chave

1. **Teste de Diagnóstico:**
   ```
   http://localhost/suporte_tecnico/server/test_notifications.php
   ```

2. **No App Android:**
   - Faça login
   - Crie um chamado
   - Com outro usuário (técnico), altere o status
   - Verifique se a notificação chega

3. **Verificar Logs:**
   - Android Studio → Logcat → Filter: "FCM"
   - Procure por: "Novo token: ..." e "Token enviado com sucesso"

## 📋 Checklist Final

- [x] ✅ Arquivo `google-services.json` atualizado
- [x] ✅ Package name compatível mantido
- [ ] ⚠️ **Chave FCM configurada no `api.php`** ← VOCÊ PRECISA FAZER
- [ ] ⚠️ Tabela `fcm_tokens` criada no banco
- [ ] ⚠️ Teste realizado

## 🚀 Após Configurar a Chave FCM

Execute no MySQL:
```sql
SOURCE server/diagnostico_fcm.sql;
```

E teste pelo navegador:
```
http://localhost/suporte_tecnico/server/test_notifications.php
```

**Após configurar a chave FCM, suas notificações push estarão funcionando! 🎉**
