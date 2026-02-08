# 🎵 RevPlay - Music Streaming Console Application

**RevPlay** is a comprehensive, console-based music streaming platform built with Java and Oracle Database. It simulates core features of modern streaming services like Spotify, offering distinct experiences for Listeners and Artists.

## 🚀 Features

### 👤 User Roles & Authentication
- **Secure Login/Registration**: Email/Password authentication with SHA-256 hashing and salt.
- **Role-Based Access**:
  - **Listeners**: Browse, stream, playlist management, favorites.
  - **Artists**: Upload songs, manage albums, view analytics, update profile.
- **Password Management**: Change password and recovery via security questions.

### 🎧 Music Player Simulation
- **Real-time Playback**: Simulates song duration with a dynamic progress bar.
- **Queue System**: Add songs to queue, auto-play next.
- **Controls**: Play, Pause, Resume, Next, Previous, Toggle Repeat.
- **Background Threading**: Music plays on a separate thread, keeping the menu responsive.

### 📚 Content Management
- **Smart Browsing**: Browse by **Genre**, **Artist**, or **Album**.
- **Search**: Find songs by title, artist, or album name.
- **Library**:
  - **Playlists**: Create, edit, privacy settings (Public/Private), add/remove songs.
  - **Favorites**: Quickly like songs for easy access.
  - **History**: view recently played tracks and full listening history.

### 🎤 Artist Tools
- **Upload Manager**: smooth song uploading with genre selection.
- **Album Management**: Create albums and organize songs.
- **Analytics**: View total plays and see who has favorited your tracks.

## �️ Quality & Reliability

### 🧵 Thread Safety & Concurrency
- **Concurrent Queue**: Uses `CopyOnWriteArrayList` for glitch-free multi-threaded song additions.
- **Volatile Control**: Precise playback state management using `volatile` flags and synchronization.
- **Stress-Tested CPU usage**: Optimized simulation loops for minimal resource footprint.

### 🧪 Comprehensive Testing Suite
- **Hybrid Testing Stratgey**: Unit tests run on **H2 (In-Memory)**; Integration tests run on **Oracle DB**.
- **9 Core Test Categories**:
  - **Negative/Validation**: Hardened boundary checks for inputs.
  - **Transaction Safety**: Verified database consistency after errors.
  - **Edge Cases**: Empty playlists, 0-second durations, and rapid-fire controls.
  - **Concurrency Stress**: Simulated 10+ simultaneous users interacting with the player.
  - **Security Audit**: Exhaustive testing of password recovery and brute-force prevention.

## �🛠️ Tech Stack

- **Language**: Java 17+
- **Database**: Oracle Database (JDBC)
- **Build Tool**: Maven
- **Logging**: Log4j2
- **Architecture**: DAO Pattern (Service Layer, Model, Data Access, UI)

## ⚙️ Setup Instructions

### 1. Database Setup
1.  Ensure you have an Oracle Database instance running.
2.  Execute the SQL scripts found in `src/main/resources/sql/create_schema.sql` (or refer to Part 2 of the walkthrough) to create tables and sequences.
3.  Configure your database connection in `src/main/resources/db.properties`:
    ```properties
    db.url=jdbc:oracle:thin:@localhost:1521:xe
    db.user=YOUR_USERNAME
    db.password=YOUR_PASSWORD
    ```

### 2. Build & Run
**Using Maven:**
```bash
# Compile and package
mvn clean package

# Run the application
mvn exec:java -Dexec.mainClass="com.revplay.App"
```

**Using IDE (IntelliJ/Eclipse):**
1.  Import as a Maven Project.
2.  Run `src/main/java/com/revplay/App.java`.

## 🎮 Usage Guide

### Navigation
- **Menus**: Navigate using numeric inputs (e.g., `1`, `2`).
- **Player Controls**: While music is playing/queued, use single-letter commands:
  - `P`: Play / Resume
  - `U`: Pause (Un-play)
  - `N`: Next Track
  - `V`: Previous Track
  - `Q`: View Queue
  - `B`: Back to Menu (keeps music playing)

### Common Workflows
- **Listener**: Register -> Login -> Browse/Search -> `[P]lay` or `[A]dd to Playlist`.
- **Artist**: Register (select Artist) -> Login -> "Upload Song" or "Manage Albums".

## 📂 Project Structure
```
com.revplay
├── dao          # Data Access Objects (DB interactions)
├── model        # POJOs (User, Song, Artist, etc.)
├── service      # Business Logic (Player, Auth, Song Management)
├── ui           # Console Interface (Menus, Input Handling)
└── util         # Untilites (DBConnection, InputValidator)
```

## 📝 License
This project is created for educational purposes.