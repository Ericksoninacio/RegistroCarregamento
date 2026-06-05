<<<<<<< HEAD
# Registro de Carregamento

App Android para registro offline de carregamentos de carga, com sincronização automática via e-mail.

## Stack

| Camada | Tecnologia |
|--------|-----------|
| UI | Jetpack Compose + Material 3 |
| Injeção de dependência | Hilt |
| Banco local | Room (SQLite) |
| Câmera | CameraX |
| OCR de placa | ML Kit Text Recognition |
| Sincronização | WorkManager |
| E-mail | JavaMail (android-mail 1.6.7) |
| Imagens | Coil |
| Permissões | Accompanist Permissions |

## Requisitos

- Android Studio Ladybug (2024.2+)
- JDK 17
- Android 10+ (API 29)

## Como abrir

1. Clone ou extraia o projeto
2. Abra no Android Studio: `File → Open → pasta RegistroCarregamento`
3. Aguarde o Gradle sync
4. Execute em dispositivo físico (câmera necessária)

## Configuração de E-mail

O envio usa Gmail com **Senha de App** (não a senha normal da conta).

### Criar Senha de App no Gmail

1. Acesse [myaccount.google.com](https://myaccount.google.com)
2. Segurança → Verificação em 2 etapas (ative se necessário)
3. Segurança → **Senhas de app**
4. Crie um app "Registro de Carregamento"
5. Copie a senha gerada (16 caracteres, ex: `xxxx xxxx xxxx xxxx`)

### No App

1. Abra o app → ícone ⚙️ Configurações
2. Preencha:
   - **E-mail remetente**: sua conta Gmail
   - **Senha de App**: a senha de 16 caracteres
   - **E-mail destinatário**: quem vai receber os registros
3. Toque **Salvar configurações**

## Fluxo do App

```
Tela 1: Registro
  ↓ Fotografa placa (ML Kit reconhece automaticamente)
  ↓ Preenche Cliente e Cidade

Tela 2: Documentos
  ↓ Fotografa NFe (obrigatória), NFe 2 (opcional), CT-e (obrigatória)

Tela 3: Validação
  ↓ Confirma se cidade bate com CT-e

Tela 4: Resumo
  ↓ Revisa todos os dados

Tela 5: Processando
  ↓ Salva no Room + adiciona à fila

Tela 6: Concluído
  ↓ Status: Pendente de envio

WorkManager (background, 15min)
  ↓ Detecta internet
  ↓ Envia e-mail com dados + fotos anexadas
  ↓ Marca como sincronizado
```

## Estrutura de arquivos de foto

```
/Android/data/com.registrocarregamento/files/Carregamentos/
└── 20260531_ABC1D23/
    ├── placa.jpg
    ├── nfe1.jpg
    ├── nfe2.jpg   ← se fotografada
    └── cte.jpg
```

## Estrutura do Projeto

```
app/src/main/java/com/registrocarregamento/
├── App.kt                          # Application + Hilt + WorkManager config
├── MainActivity.kt
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── dao/Daos.kt             # CarregamentoDao + FilaSincronizacaoDao
│   │   └── entity/Entities.kt     # CarregamentoEntity + FilaSincronizacaoEntity
│   └── repository/
│       └── CarregamentoRepository.kt
├── di/
│   └── AppModule.kt                # Hilt: DB, DAOs
├── domain/
│   └── model/Carregamento.kt       # Models de domínio
├── ui/
│   ├── components/Components.kt    # Componentes reutilizáveis
│   ├── navigation/AppNavigation.kt
│   ├── screens/
│   │   ├── RegistroViewModel.kt    # ViewModel compartilhado entre telas
│   │   ├── RegistroScreen.kt       # Tela 1
│   │   ├── DocumentosScreen.kt     # Tela 2
│   │   ├── ValidacaoScreen.kt      # Tela 3
│   │   ├── ResumoScreen.kt         # Tela 4
│   │   ├── ProcessandoConcluidoScreens.kt  # Telas 5 e 6
│   │   ├── HistoricoScreen.kt      # Histórico de registros
│   │   ├── ConfiguracoesScreen.kt  # Config de e-mail
│   │   └── CameraScreen.kt         # Câmera com CameraX
│   └── theme/Theme.kt
├── util/Utils.kt                   # NetworkUtil, FileUtil, DateUtil
└── worker/SincronizacaoWorker.kt   # WorkManager + JavaMail
```
=======
# RegistroCarregamento
>>>>>>> 8c0c31b7fc199c0a8cf8a05043aef7a835de6a9e
