# Sistema de Notificações - Configuração

## Funcionalidades Implementadas

✅ **Sistema de Roles**: usuarios/tecnicos com permissões diferentes
✅ **Ícone de Ferramenta**: Técnicos têm ícone de chave inglesa no nome
✅ **Botões de Refresh**: Telas inicial e de chamados têm botão atualizar
✅ **Atualização de Status**: Técnicos podem atualizar status dos chamados
✅ **Visibilidade Filtrada**: Usuários veem apenas seus chamados, técnicos veem todos
✅ **Data Brasileira**: Formato 'd-m-Y H:i:s' para todas as datas
✅ **Editar/Excluir**: Regras de permissão baseadas em status e role
✅ **Sistema de Notificações**: FCM para notificar mudanças de status e novos chamados

## Sistema de Notificações

### Backend (PHP/MySQL)
1. **Tabela FCM Tokens**: Armazena tokens dos dispositivos por usuário
2. **Funções de Notificação**:
   - `enviarNotificacao()`: Envia notificação via FCM
   - `notificarTecnicos()`: Notifica todos os técnicos sobre novo chamado
   - `notificarDonoStatus()`: Notifica dono do chamado sobre mudança de status

### Android (Kotlin/FCM)
1. **AppFirebaseService**: Gerencia recebimento e registro de tokens
2. **Login Integration**: Registra token FCM automaticamente no login
3. **Token Management**: Atualiza token quando renovado

## Configuração Necessária

### 1. Firebase Setup
1. Criar projeto no Firebase Console
2. Adicionar app Android com package `com.example.suporte`
3. Baixar `google-services.json` para `android/app/`
4. Obter Server Key do Firebase para usar no PHP

### 2. PHP Configuration
No arquivo `api.php`, substitua a linha:
```php
$serverKey = 'SUA_CHAVE_DO_SERVIDOR_FIREBASE';
```

### 3. Database
Execute o arquivo `schema.sql` para criar/atualizar as tabelas:
```sql
-- Tabela usuarios já deve existir com campo 'role'
-- Nova tabela fcm_tokens será criada
```

## Fluxo de Notificações

### Novo Chamado Criado
1. Usuário cria chamado
2. Endpoint `criar` é chamado
3. `notificarTecnicos()` busca todos os técnicos
4. Envia notificação FCM para dispositivos dos técnicos

### Status Atualizado
1. Técnico atualiza status do chamado
2. Endpoint `atualizar_status` é chamado
3. `notificarDonoStatus()` busca o dono do chamado
4. Envia notificação FCM para dispositivo do usuário

## Permissões Implementadas

### Usuário (role: 'usuario')
- ✅ Ver apenas seus próprios chamados
- ✅ Criar novos chamados
- ✅ Editar/excluir apenas chamados com status 'aberto'
- ✅ Receber notificações de mudança de status

### Técnico (role: 'tecnico')
- ✅ Ver todos os chamados
- ✅ Atualizar status de qualquer chamado
- ✅ Editar/excluir qualquer chamado
- ✅ Receber notificações de novos chamados
- ✅ Ícone de ferramenta ao lado do nome

## Testando o Sistema

### 1. Criar Usuários
```sql
-- Usuário comum
INSERT INTO usuarios (nome, email, senha, role) VALUES
('João Silva', 'joao@email.com', MD5('123456'), 'usuario');

-- Técnico
INSERT INTO usuarios (nome, email, senha, role) VALUES
('Maria Tech', 'maria@email.com', MD5('123456'), 'tecnico');
```

### 2. Testar Notificações
1. Fazer login com usuário comum
2. Criar um chamado
3. Fazer login com técnico em outro dispositivo
4. Verificar se notificação chegou
5. Atualizar status do chamado
6. Verificar se usuário recebeu notificação

### 3. Logs de Debug
- Android: `adb logcat | grep FCM`
- PHP: Verificar logs do servidor web

## Estrutura dos Arquivos Modificados

### Backend
- `schema.sql`: Estrutura do banco com roles e tokens FCM
- `api.php`: Endpoints com notificações integradas

### Android
- `AppFirebaseService.kt`: Serviço FCM
- `ApiService.kt`: Interface com endpoint de token
- `Repository.kt`: Método salvarFcmToken
- `MainViewModel.kt`: Integração com Repository
- `LoginScreen.kt`: Registro automático de token
- `MainScreen.kt`: UI com ícones e permissões
- `AppScreens.kt`: Componentes com regras de negócio

## Próximos Passos

1. Configurar Firebase project
2. Adicionar Server Key no PHP
3. Testar notificações end-to-end
4. Ajustar mensagens de notificação conforme necessário
5. Implementar notificações locais (opcional)
