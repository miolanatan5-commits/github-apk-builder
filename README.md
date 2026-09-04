# GitHub APK Builder

Aplicativo Android em Kotlin + Jetpack Compose que valida um token do GitHub e cria repositórios via API.

## Segurança
O token é armazenado localmente com `EncryptedSharedPreferences`, protegido pelo Android Keystore. Ele é enviado somente ao `api.github.com` via HTTPS e nunca é incluído no código-fonte.

## Token recomendado
Use um fine-grained Personal Access Token com apenas as permissões necessárias. Para criar repositórios, habilite criação de repositórios e Contents: Read and write. Revogue o token no GitHub quando quiser.
