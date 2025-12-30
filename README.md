# Pronus - Plataforma de Fonoaudiologia com IA 🗣️🧠

O **Pronus** é um sistema inovador de gerenciamento para clínicas de fonoaudiologia que integra Inteligência Artificial para auxiliar no diagnóstico e acompanhamento de pacientes. O sistema centraliza o agendamento de consultas, gerenciamento de pacientes e fonoaudiólogos, e oferece uma ferramenta poderosa de pré-avaliação e análise de fala impulsionada por IA (Gemini e Deepgram).

## 🚀 Funcionalidades Principais

*   **Gestão de Usuários:** Perfis distintos para Pacientes (Clientes), Fonoaudiólogos (Profissionais/Especialistas) e Secretárias.
*   **Agendamento de Consultas:** Sistema completo para marcar, visualizar e gerenciar consultas e disponibilidade de horários.
*   **Análise de Fala com IA:**
    *   **Geração de Conteúdo:** Criação automática de palavras e frases para teste de pronúncia.
    *   **Avaliação de Pronúncia:** Análise de áudio para identificar precisão fonética e fluência.
    *   **Relatórios Automáticos:** Geração de feedbacks detalhados sobre o desempenho do paciente.
*   **Sessões de Treino:** Interface para realização de exercícios de fala prescritos.
*   **Chat:** Comunicação direta entre pacientes e profissionais.
*   **Emissão de Certificados e Relatórios:** Geração de documentos em PDF para acompanhamento da evolução.
*   **Dashboard:** Visão geral para profissionais com métricas e histórico de pacientes.

## 🛠️ Tecnologias Utilizadas

### Backend (API REST)
*   **Java 17** & **Spring Boot 3.5.6**
*   **Spring Security + JWT:** Autenticação e autorização robustas.
*   **Spring Data JPA (Hibernate):** Persistência de dados.
*   **Flyway:** Versionamento e migração de banco de dados.
*   **Integrações de IA:**
    *   **Google Gemini API:** Análise semântica e geração de conteúdo.
    *   **Deepgram (inferido):** Transcrição e análise de áudio.
*   **Swagger / OpenAPI:** Documentação interativa da API.

### Frontend (Web)
*   **React 19**
*   **Vite:** Build tool rápida e leve.
*   **React Router:** Navegação SPA.
*   **HTML2PDF:** Geração de relatórios no navegador.

### Banco de Dados & Infraestrutura
*   **PostgreSQL:** Banco de dados relacional principal.
*   **H2 Database:** Banco em memória para testes.
*   **Docker & Docker Compose:** Containerização do ambiente (banco de dados).

## 📋 Pré-requisitos

Certifique-se de ter instalado em sua máquina:
*   [Java JDK 17+](https://adoptium.net/)
*   [Node.js 18+](https://nodejs.org/)
*   [Docker](https://www.docker.com/) (Opcional, mas recomendado para o banco de dados)
*   [Maven](https://maven.apache.org/) (Opcional, o projeto inclui o `mvnw`)

## ⚙️ Configuração e Execução

### 1. Configuração do Banco de Dados
O projeto utiliza PostgreSQL. A maneira mais fácil de subir o banco é via Docker:

```bash
docker-compose up -d
```
*Isso iniciará o PostgreSQL na porta padrão (5432).*

### 2. Configuração do Backend

1.  Navegue até a raiz do projeto.
2.  Verifique o arquivo `src/main/resources/application.properties`. Certifique-se de que as chaves de API (Gemini/Deepgram) estão configuradas corretamente ou definidas como variáveis de ambiente.
    *   *Nota: Para segurança, evite commitar chaves reais no repositório.*
3.  Execute a aplicação Spring Boot:

**No Windows:**
```bash
.\mvnw spring-boot:run
```

**No Linux/Mac:**
```bash
./mvnw spring-boot:run
```

O servidor backend iniciará em `http://localhost:8080`.
*A documentação da API estará disponível em: `http://localhost:8080/swagger-ui.html`*

### 3. Configuração do Frontend

1.  Navegue até a pasta `Frontend`:
    ```bash
    cd Frontend
    ```
2.  Instale as dependências:
    ```bash
    npm install
    ```
3.  Inicie o servidor de desenvolvimento:
    ```bash
    npm run dev
    ```

O frontend estará disponível geralmente em `http://localhost:5173`.

## 🧪 Executando Testes

Para rodar os testes unitários e de integração do backend:

```bash
.\mvnw test
```

## 📂 Estrutura do Projeto

```
Pronus/
├── src/main/java/.../prototipo_ia  # Código Fonte Backend (Controllers, Services, Entities)
├── Frontend/                       # Código Fonte Frontend (React)
├── docker-compose.yml              # Definição dos containers
├── pom.xml                         # Dependências do Backend
└── README.md                       # Documentação do Projeto
```

## 🤝 Contribuição

Este projeto foi desenvolvido como parte de um desafio/protótipo acadêmico (INATEL).

---
*Desenvolvido pela equipe Pronus.*
