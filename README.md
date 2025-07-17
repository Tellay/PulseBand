# PulseBand

A PulseBand é uma pulseira inteligente desenvolvida para motoristas profissionais,
com o objetivo de prevenir acidentes rodoviários através da monitorização contínua dos batimentos cardíacos.
Este repositório é o servidor aplicacional desenvolvido em Java e utilizando JavaFX para a criação da interface gráfica.

### Principais Funcionalidades

* **Controlo de Acesso Baseado em Papéis:** Apenas administradores e membros dos recursos humanos (RH) têm permissão para aceder e gerir o portal.
* **Gestão de Motoristas:** Funcionalidades completas para adicionar, editar e remover informações de motoristas.
* **Monitorização de Dados em Tempo Real:** Visualização e acompanhamento de dados críticos em tempo real.
* **Alertas Automatizados em Tempo Real:** Capacidade de ver e enviar alertas automatizados instantaneamente.
* **Armazenamento de Dados:** Persistência de dados em PostgreSQL.
* **Interface Gráfica:** Experiência de utilizador intuitiva desenvolvida com JavaFX.

## 🛠️ Tecnologias Utilizadas

* **Java:** Linguagem de programação principal.
* **JavaFX:** Framework para a construção da interface gráfica.
* **PostgreSQL:** Sistema de gestão de base de dados relacional.
* **MQTT** Protocolo de comunicação para IoT.

## ⚙️ Configuração do Ambiente

Para executar o projeto localmente, siga os passos abaixo:

### Base de Dados PostgreSQL

1.  **Crie a Base de Dados:**
    ```sql
    CREATE TABLE "user_type" (
    "id" SERIAL PRIMARY KEY,
    "name" VARCHAR(50) UNIQUE NOT NULL,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
    
    CREATE TABLE "emergency_contact" (
    "id" SERIAL PRIMARY KEY,
    "full_name" VARCHAR(255) NOT NULL,
    "phone" VARCHAR(20) NOT NULL,
    "email" VARCHAR(255) NOT NULL,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
    
    CREATE TABLE "user" (
    "id" SERIAL PRIMARY KEY,
    "full_name" VARCHAR(255) NOT NULL,
    "password_hash" VARCHAR(255) NOT NULL,
    "phone" VARCHAR(20) UNIQUE NOT NULL,
    "email" VARCHAR(255) UNIQUE NOT NULL,
    "birth_date" DATE NOT NULL,
    "admission_date" DATE,
    "user_type_id" INTEGER NOT NULL,
    "emergency_contact_id" INTEGER UNIQUE,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY ("user_type_id") REFERENCES "user_type" ("id") ON DELETE RESTRICT,
    FOREIGN KEY ("emergency_contact_id") REFERENCES "emergency_contact" ("id") ON DELETE SET NULL
    );
    
    CREATE TABLE "vital" (
    "id" SERIAL PRIMARY KEY,
    "bpm" INTEGER NOT NULL,
    "user_id" INTEGER NOT NULL,
    "recorded_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON DELETE CASCADE
    );
    
    CREATE TABLE "alert" (
    "id" SERIAL PRIMARY KEY,
    "message" VARCHAR(255) NOT NULL,
    "user_id" INTEGER NOT NULL,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON DELETE CASCADE
    );
    
    CREATE TRIGGER set_updated_at_user_type
    BEFORE UPDATE ON "user_type"
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
    
    CREATE TRIGGER set_updated_at_user
    BEFORE UPDATE ON "user"
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
    
    CREATE TRIGGER set_updated_at_emergency_contact
    BEFORE UPDATE ON "emergency_contact"
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
    
    CREATE TRIGGER set_updated_at_vital
    BEFORE UPDATE ON "vital"
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
    
    CREATE TRIGGER set_updated_at_alert
    BEFORE UPDATE ON "alert"
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
    ```
2. **Execute as Migrações/Scripts SQL:**
    ```sql
    INSERT INTO "user_type" (id, name) VALUES
    (1, 'Administrator'),
    (2, 'HR Staff'),
    (3, 'Driver');
    
    INSERT INTO "emergency_contact" (id, full_name, phone, email) VALUES
    (1, 'Sarah Johnson', '911111111', 'sarah.johnson@example.com'),
    (2, 'Thomas Smith', '922222222', 'thomas.smith@example.com'),
    (3, 'Rachel Brown', '933333333', 'rachel.brown@example.com');
    
    INSERT INTO "user" (full_name, password_hash, phone, email, birth_date, admission_date, user_type_id, emergency_contact_id) VALUES
    ('Alice Johnson', 'password_hash_here', '1111111111', 'alice.johnson@example.com', '1990-05-14', '2020-01-10', 1, NULL),
    ('Bob Smith', 'password_hash_here', '2222222222', 'bob.smith@example.com', '1985-03-22', '2019-07-01', 2, NULL),
    ('Charlie Brown', 'password_hash_here', '3333333333', 'charlie.brown@example.com', '1992-11-30', '2021-03-15', 3, 2),
    ('Diana Evans', 'password_hash_here', '4444444444', 'diana.evans@example.com', '1995-08-19', '2022-06-05', 3, 3),
    ('Edward Ford', 'password_hash_here', '5555555555', 'edward.ford@example.com', '1988-02-10', '2018-09-12', 3, 1);
    
    INSERT INTO "vital" (bpm, user_id) VALUES
    (78, 3),
    (85, 3),
    (90, 4),
    (95, 5);
    
    INSERT INTO "alert" (message, user_id) VALUES
    ('Critical high heart rate detected, probably severe Tachycardia. Registered BPM: 126', 3),
    ('Critical high heart rate detected, probably severe Tachycardia. Registered BPM: 126', 4),
    ('Low heart rate detected, probably Bachycardia. Registered BPM: 55', 5);
    ```

### Configurações do Projeto

1.  **Clone o Repositório:**
    ```bash
    git clone https://github.com/tellay/pulseband.git
    ```
2.  **Ficheiros de Propriedades:**
    Os ficheiros de propriedades estão localizados em `src/main/resources/`. Terá de configurá-los com as suas credenciais.

   * **`db.properties`:**
       ```properties
       db.url=
       db.username=
       db.password=
       ```
   * **`mqtt.properties`:**
        ```properties
        mqtt.brokerUrl=
        mqtt.clientId=
        mqtt.bpmTopic=
        mqtt.alertTopic=
        mqtt.decryptedTopic=
        ```
   * **`email.properties`:**
      ```properties
      email.from=
      email.host=
      email.username=
      email.password=
      ```

## 🚀 Como Executar

Depois de configurar o ambiente e construir o projeto:

1.  **Executar a Aplicação JavaFX:**
    A aplicação principal é `Application.java` em `src/main/java/com/pulseband/pulseband/`.

    Pode executar a partir do seu IDE (IntelliJ IDEA, Eclipse, etc.) clicando com o botão direito em `Application.java` e selecionando "Run".

## 📂 Estrutura do Projeto

A estrutura de pastas segue a convenção de um projeto Maven e modularização JavaFX.

```
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── pulseband/
│   │   │           └── pulseband/
│   │   │               ├── controllers/   # Lógica de controladores para as views JavaFX
│   │   │               ├── daos/          # Data Access Objects para interagir com a base de dados
│   │   │               ├── db/            # Utilitários de base de dados (ex: conexão)
│   │   │               ├── decipher/      # Lógica de decifração/processamento de dados
│   │   │               ├── dtos/          # Data Transfer Objects
│   │   │               ├── email/         # Utilitários de email
│   │   │               ├── exceptions/    # Classes de exceção personalizadas
│   │   │               ├── mqtt/          # Lógica de integração MQTT
│   │   │               ├── services/      # Lógica de negócio e serviços da aplicação
│   │   │               ├── utils/         # Classes utilitárias gerais
│   │   │               └── Application.java # Ponto de entrada da aplicação JavaFX
│   │   │               └── module-info.java # Definição do módulo Java (se usar módulos)
│   │   └── resources/
│   │       ├── images/        # Recursos de imagem para a UI
│   │       ├── styles/        # Folhas de estilo CSS para JavaFX
│   │       ├── views/         # Ficheiros FXML para as interfaces
│   │       ├── db.properties  # Configurações da base de dados
│   │       ├── mqtt.properties# Configurações MQTT
│   │       ├── email.properties# Configurações email
│   │       └── application.properties # Outras propriedades da aplicação
├── target/                # Diretório de saída da construção Maven
├── .gitignore             # Ficheiro para ignorar ficheiros e pastas no Git
├── pom.xml                # Ficheiro de configuração do projeto Maven
└── README.md              # Este ficheiro!
```
