Instruções rápidas:
- Ajuste BASE_URL em Network.kt (ex: http://10.0.2.2/)
- Para FCM: crie projeto no Firebase, adicione app Android, baixe google-services.json e coloque em android/app/
- Importe android/ no Android Studio. Pode ser necessário habilitar KAPT (apply plugin: 'kotlin-kapt') se usar Room annotation processing.
