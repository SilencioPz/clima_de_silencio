🌦️ ClimaDeSilencioNoAr - Aplicativo de Previsão do Tempo em Kotlin 🌦️

Aplicativo minimalista para consultar condições climáticas com design clean e dados precisos.
-------------------------------------------------------------------------------------------------
✨ Sobre o Projeto

Aplicativo Android desenvolvido em Kotlin que oferece:

✅ Previsão do tempo em tempo real via API OpenWeather.

📍 Geolocalização automática ou cidade padrão Rondonópolis - Mato Grosso.

🎨 Interface moderna com Jetpack Compose.

🔌 Arquitetura com injeção de dependência (Hilt).

-------------------------------------------------------------------------------------------------
🚀 Roadmap

Versão	        Status	            Observação
-------------------------------------------------------------------------------------------------
Kotlin 1.9	    ✅ Estável	        Compatível com Android 12+ (API 31)

Jetpack Compose	✅ Implementado	    UI declarativa

Versão Wear OS	🔄 Em breve	        Integração com relógios smart

-------------------------------------------------------------------------------------------------
🛠️ Tecnologias & Ferramentas

Componente	Detalhes

Linguagem	Kotlin 1.9

IDE	Android Studio Narwhal (2025.1.1)

Clean Architecture

Bibliotecas	Retrofit (API), Hilt (DI), Coroutines, Jetpack Compose

API Externa	OpenWeatherMap (https://openweathermap.org/api)

-------------------------------------------------------------------------------------------------
📂 Estrutura do Projeto
text

ClimaDeSilencioNoAr/  
├── app/  
│   ├── src/main/  
│   │   ├── java/com/example/climadesilencionoar  
│   │   │   ├── core/           # App.kt (Hilt)  
│   │   │   ├── data/           # Banco de dados
│   │   │   ├── models/         # Modelos  
|   |   |   |__ remote/         # API
│   │   │   ├── ui/             # Composable, ViewModels, Themes
│   │   │   └── utils/          # Constants
|   |   |   |__ raíz/           # MainActivity
│   │   └── res/                # Recursos (layouts, strings, drawables)  
├── build.gradle                # Configurações do módulo principal  
└── .gitignore                  # Ignora local.properties, build/, etc.  

⚡ Como Executar

Pré-requisitos:

    Android Studio 2025.1+

    API Key do OpenWeather (insira em local.properties[necessário criar um]):
    properties

    OPEN_WEATHER_API_KEY=sua_chave_aqui  

Passos:

    Clone o repositório:
    bash

    git clone https://github.com/seu-user/ClimaDeSilencioNoAr.git  

    Abra o projeto no Android Studio e sincronize o Gradle.

    Execute em um emulador ou dispositivo físico (Android 12+).

🔒 Boas Práticas Implementadas

✔️ Segurança: Chaves de API protegidas via local.properties (ignorado pelo Git).
✔️ Performance: Coroutines para chamadas assíncronas e cache de dados.
✔️ Testes: Unitários com JUnit e MockK (em src/test).
✔️ Documentação: README detalhado.
🌟 Próximos Passos

    Adicionar suporte a widgets do Android.

    Implementar light mode dinâmico.

    Integrar Firebase Crashlytics para monitoramento.

📌 Compatibilidade

    Mínima: Android 8.0 (API 26)

    Recomendada: Android 12+ (API 31)

    Dependências:
    gradle

    implementation("androidx.core:core-ktx:1.12.0")  
    implementation("com.google.dagger:hilt-android:2.48")  

👨‍💻 Desenvolvido com ☕ e ♫ por você!
Feito no 🇧🇷 com ajuda dos parceiros DeepSeek e Claude! 😎
