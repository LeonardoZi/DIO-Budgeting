# Budgeting API - Controle Financeiro por Voz

> **Trilha Spring Boot & Spring AI - DIO**
> 
> **Desenvolvido por: Leonardo Ziliotto**

---

 **Sobre o projeto**  
Budgeting API é uma API que recebe áudios com descrição de gastos, transcreve a fala, interpreta o caso do usuário, salva no banco de dados o local do gasto, valor e a categoria e gera uma resposta via áudio para a anotação. Pode também fazer consultas por categoria e data.
<br>

**Tecnologias Utilizadas**
* **Java 25**
* **Spring Boot 4.1**
* **Spring AI** (Transcrição de voz, Tool Calling e síntese de áudio)
* **Spring Data JPA & MySQL** (Persistência dos dados)
* **Spring Boot Docker Compose Support & Docker**
* **Gradle**

**Evoluções Autorais**  
* **Adição de novas categorias: food, education, utilities e leisure.**
* **Refinamento do prompt para consulta de valor.**
* **Adicionado data e hora da adição do registro.**
* **Adicionado consulta por voz filtrado por categoria e por data/horário.**

## Aprendizados com o desafio
**Com esse desafio aprendi conceitos de Domain-Driven Design (DDD), aprendi a usar o Spring Boot e o Spring AI, conheci o Spring Boot Docker Compose Support, que facilitou muito rodar a aplicação com MySQL sem precisar instalar nada manualmente. Também aprendi a usar tags como os Getters e Setters do Lombok e algumas configurações do Gradle.**


---

### Como executar a aplicação e testar o fluxo principal

#### 1. Pré-requisitos
- Java 25 instalado
- Docker e Docker Compose em execução
- Uma chave de API do Google Gemini (gere em https://aistudio.google.com), configurada em:
    1. `application.properties`: `spring.ai.google.genai.api-key=SUA_CHAVE`
    2. Variável de ambiente do sistema: `GEMINI_API_KEY=SUA_CHAVE`

#### 2. Passo a Passo
```bash
# Clone este repositório
git clone https://github.com/LeonardoZi/DIO-Budgeting

# Entre na pasta do projeto
cd DIO-Budgeting

# Execute a aplicação (o Docker Compose iniciará automaticamente)
./gradlew bootRun
```

#### 3. Teste do fluxo principal
Você pode enviar um arquivo de áudio de teste mp3 (ex: "audio.mp3") para a API via cURL:  
**Via Git Bash:**
```bash
curl -X POST http://localhost:8080/transactions/ai -F "file=@audio.mp3" -o resposta.wav
```
**Via PowerShell**:
 ```powershell
 curl.exe -X POST http://localhost:8080/transactions/ai -F "file=@audio.mp3" -o resposta.wav
 ```

### Outros endpoints disponíveis

Também é possível registrar e consultar transações diretamente via JSON, sem passar pelo fluxo de áudio.

**Criar uma transação manualmente (via Postman):**
1. Nova requisição **POST** para `http://localhost:8080/transactions`
2. Na aba **Body**, selecione **raw** → **JSON**
3. Cole:
```json
{
    "description": "Compra na farmácia",
    "category": "PHARMA",
    "amount": 8000
}
```
> `amount` é informado em centavos (ex: `8000` = R$ 80,00).

4. Clique em **Send**



**Consultar transações por categoria:**
```bash
curl http://localhost:8080/transactions/PHARMA
```

Categorias disponíveis: `GROCERIES`, `PHARMA`, `AUTO`, `FOOD`, `EDUCATION`, `UTILITIES`, `LEISURE`.
