Projeto: Suporte Técnico - Skeleton
Conteúdo:
- server/ : API PHP + schema.sql
- android/ : skeleton Android app (Kotlin + Jetpack Compose) com Retrofit
Como usar:
1) No servidor PHP (com MySQL):
   - Importe server/schema.sql no seu MySQL.
   - Coloque server/api.php em um servidor PHP (ex: /var/www/html/api.php).
2) No Android Studio:
   - Abra a pasta android/ como um projeto.
   - Ajuste `BASE_URL` em android/app/src/main/java/com/example/suporte/di/Network.kt para o endereço do seu servidor.
   - Execute o app em um emulador ou dispositivo.
