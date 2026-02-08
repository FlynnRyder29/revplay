# RevPlay - Detailed System Design Specification

This document provides a deep-dive into the technical design, architectural patterns, and security mechanisms of the RevPlay Music Application.

---

## 🏗️ 1. Modular & Layered Architecture

RevPlay is designed using a **4-Tier Modular Architecture**. This separation of concerns ensures that the UI logic, business rules, and data persistence layers remain independent.

```mermaid
graph TB
    subgraph "CLIENT TIER"
        UI[Console Interface]
    end

    subgraph "APPLICATION TIER (Modular)"
        subgraph "Identity & Access"
            Auth[Auth Service]
        end
        subgraph "Media Logic"
            Player[Player Engine]
            Streaming[Streaming Logic]
        end
        subgraph "Library Management"
            PlaylistMgt[Playlist Mgmt]
            Search[Search Engine]
        end
    end

    subgraph "DATA ACCESS TIER"
        DAO[Data Access Objects]
        JDBC[JDBC Connector]
    end

    subgraph "STORAGE TIER"
        DB[(Oracle Database)]
    end

    UI --> Auth
    UI --> Player
    UI --> PlaylistMgt
    UI --> Search

    Auth & Player & Streaming & PlaylistMgt & Search --> DAO
    DAO --> JDBC
    JDBC --> DB
```

---

## 📂 2. Modular Design Explanation

The system is organized into distinct packages, each representing a logical module:

| Module | Package | Responsibility |
|:---|:---|:---|
| **Identity** | `com.revplay.service.UserService` | User lifecycle, authentication, and role management. |
| **Media Player** | `com.revplay.service.PlayerService` | Real-time simulation, queue handling, and background threading. |
| **Data Factory** | `com.revplay.dao` | Abstraction of SQL logic using the DAO pattern to prevent DB leakage into services. |
| **Domain Models** | `com.revplay.model` | Stateless POJOs representing the core business entities. |
| **Utilities** | `com.revplay.util` | Cross-cutting concerns like DB connection pooling and password security. |

---

## 📊 3. Class Diagram

The following diagram illustrates the relationships between the core model classes.

```mermaid
classDiagram
    class User {
        +int userId
        +string username
        +string email
        +string userType
        +login()
        +changePassword()
    }
    class Artist {
        +int artistId
        +string bio
        +string socialLinks
        +uploadSong()
    }
    class Song {
        +int songId
        +string title
        +int duration
        +int playCount
        +getFormattedDuration()
    }
    class Album {
        +int albumId
        +string title
        +date releaseDate
    }
    class Playlist {
        +int playlistId
        +string name
        +boolean isPublic
        +addSong()
    }

    User <|-- Artist : "Specialization"
    Artist "1" -- "o*" Song : "Uploads"
    Artist "1" -- "o*" Album : "Releases"
    Album "1" -- "o*" Song : "Contains"
    Playlist "o*" -- "o*" Song : "Includes"
    User "1" -- "o*" Playlist : "Creates"
```

---

## 🧩 4. Component Diagram

Visualizes the physical organization and dependencies of the system components.

```mermaid
graph TD
    subgraph "Presentation"
        UI["Console UI (Interface)"]
    end
    subgraph "Business Logic"
        Service["Service Layer"]
    end
    subgraph "Data Access"
        DAO["DAO Classes"]
    end
    subgraph "Infrastructure"
        Util["Utility Classes"]
        DB[(Oracle Database)]
    end

    UI -->|Command| Service
    Service -->|Manage Data| DAO
    DAO -->|SQL/JDBC| DB
    Service -.->|Security/Logging| Util
    DAO -.->|Shared Connection| Util
```

---

## 🔄 5. Sequence Diagram: Playing a Song

This diagram shows the interaction flow when a user selects a song to play.

```mermaid
sequenceDiagram
    participant User
    participant UserMenu
    participant SongService
    participant PlayerService
    participant HistoryDAO
    participant DB

    User->>UserMenu: Select Song [P]
    UserMenu->>PlayerService: addToQueue(song)
    UserMenu->>PlayerService: play()
    
    activate PlayerService
    PlayerService->>HistoryDAO: recordPlay(userId, songId)
    HistoryDAO->>DB: INSERT INTO history
    PlayerService->>PlayerService: startSimulationThread()
    
    loop Every Second
        PlayerService->>User: printProgressBar()
    end
    
    PlayerService-->>User: Song Finished
    deactivate PlayerService
```

---

## 🛡️ 6. Security Architecture

RevPlay implements a multi-layered security strategy:

1.  **Authentication Security**:
    - Passwords are never stored in plain text.
    - Uses **SHA-256 Hashing with dynamic Salt** (unique per user) to prevent rainbow table attacks.
2.  **Data Persistence Security**:
    - **Parameterized Queries**: Every database call uses `PreparedStatement` to eliminate **SQL Injection** risks.
    - **Principle of Least Privilege**: DAO methods are scoped to specific tables.
3.  **Input Validation**:
    - Centralized `InputValidator` checks email formats, username patterns, and password complexity.

---

## 🚀 7. Deployment Architecture

The application is deployed as a standalone JAR on a client machine, connecting to a centralized Oracle Database.

```mermaid
graph TB
    subgraph "Physical Tier: Client Machine"
        direction TB
        JRE["Java Runtime Environment"]
        JAR["RevPlay.jar (Standalone App)"]
        JAR --- JRE
    end

    subgraph "Physical Tier: Server"
        Oracle[(Oracle Database 21c/XE)]
    end

    JAR ==>|JDBC over TCP/1521| Oracle
```

- **Client**: Any OS with JRE 17+ installed.
- **Server**: Centralized Oracle DB hosting user data and song metadata.
- **Network**: Standard TCP/IP connection via JDBC thin driver.