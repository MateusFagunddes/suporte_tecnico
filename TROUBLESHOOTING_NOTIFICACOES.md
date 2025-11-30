# Guia de Resolução de Problemas - Notificações Push FCM

## Problemas Identificados e Correções

### ❌ Problema Principal
As notificações push não estão sendo enviadas quando o status do chamado é alterado.

### ✅ Soluções Implementadas

#### 1. **URL Inconsistente Corrigida**
- **Problema**: O `AppFirebaseService.kt` usava URL diferente do `Network.kt`
- **Solução**: Ambos agora usam `https://braylen-kaolinic-tabularly.ngrok-free.dev//suporte_tecnico//server/`

#### 2. **Configuração de Segurança de Rede**
- **Adicionado**: Domínio ngrok ao `network_security_config.xml`

### ⚠️ Configurações Pendentes (VOCÊ PRECISA FAZER)

#### 1. **Chave FCM do Servidor**
```php
// No arquivo server/api.php, linha ~12
$serverKey = 'AAAA...:APA91b...'; // SUBSTITUA pela sua chave real!
```

**Como obter a chave:**
1. Acesse o [Console do Firebase](https://console.firebase.google.com)
2. Selecione seu projeto
3. Vá em ⚙️ Configurações → Configurações do Projeto
4. Aba "Cloud Messaging"
5. Copie a "Chave do servidor"

#### 2. **Verificar Banco de Dados**
Execute no MySQL/phpMyAdmin:
```sql
-- Usar o script de diagnóstico
SOURCE diagnostico_fcm.sql;
```

Ou crie a tabela manualmente:
```sql
SOURCE create_fcm_table.sql;
```

#### 3. **Verificar google-services.json**
- Arquivo deve estar em: `android/app/google-services.json`
- Deve ser baixado do Firebase Console para seu projeto

## 🔍 Como Testar

### 1. **Teste via Web**
Acesse: `http://localhost/suporte_tecnico/server/test_notifications.php`

### 2. **Teste Manual**
1. Abra o app Android
2. Faça login com um usuário
3. Crie um chamado
4. Com outro usuário (técnico), altere o status
5. Verifique se a notificação aparece

### 3. **Verificar Logs**
```bash
# Android Logcat
adb logcat | grep FCM

# Ou no Android Studio
# Logcat → Filter: FCM
```

## 📋 Checklist de Verificação

- [ ] ✅ URLs consistentes (corrigido)
- [ ] ✅ Tabela `fcm_tokens` criada
- [ ] ⚠️ Chave FCM configurada no PHP
- [ ] ⚠️ `google-services.json` no lugar correto
- [ ] ⚠️ Usuários têm tokens FCM salvos
- [ ] ⚠️ Servidor acessível pelo Android

## 🐛 Possíveis Problemas

### Notificação não chegando:

1. **Token não salvo**
   - Verifique logs: `FCM: Novo token: ...`
   - Token deve ser enviado para servidor

2. **Chave FCM inválida**
   - Erro 401 nas requisições FCM
   - Verifique se copiou chave correta

3. **URL não acessível**
   - Teste URL do ngrok no navegador
   - Verifique se ngrok está rodando

4. **Permissões Android**
   - App pode estar bloqueando notificações
   - Verificar configurações do sistema

## 💡 Dicas de Debug

### Forçar novo token FCM:
```kotlin
FirebaseMessaging.getInstance().deleteToken()
// App irá gerar novo token no próximo restart
```

### Teste de notificação manual:
```bash
curl -X POST https://fcm.googleapis.com/fcm/send \
  -H "Authorization: key=SUA_CHAVE_FCM" \
  -H "Content-Type: application/json" \
  -d '{
    "to": "TOKEN_DO_DISPOSITIVO",
    "notification": {
      "title": "Teste",
      "body": "Notificação de teste"
    }
  }'
```

## 📞 Próximos Passos

1. Configure a chave FCM no `api.php`
2. Execute `diagnostico_fcm.sql`
3. Teste com `test_notifications.php`
4. Verifique logs do Android
5. Teste alteração de status de chamado

**Após essas configurações, o sistema de notificações deve funcionar corretamente!**
