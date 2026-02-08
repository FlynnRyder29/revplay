package com.revplay.ui;
import com.revplay.model.*;
import com.revplay.service.*;
import java.util.List;
import java.util.Scanner;
public class UserMenu {
    private Scanner scanner;
    private User user;
    private MenuHandler menuHandler;
    private SongService songService = new SongService();
    private UserService userService = new UserService();
    private PlaylistService playlistService = new PlaylistService();
    private AlbumService albumService = new AlbumService();
    private PlayerService playerService = new PlayerService();

    public UserMenu(Scanner scanner, User user, MenuHandler menuHandler) {
        this.scanner = scanner;
        this.user = user;
        this.menuHandler = menuHandler;
        playerService.setCurrentUser(user.getUserId());
    }

    public void show() {
        System.out.println("\n--- " + user.getUsername() + "'s Menu ---");
        System.out.println("1. Search Songs");
        System.out.println("2. Browse Library (Genre/Artist/Album)");
        System.out.println("3. My Favorites");
        System.out.println("4. My Playlists");
        System.out.println("5. Public Playlists");
        System.out.println("6. Recently Played");
        System.out.println("7. Listening History");
        System.out.println("8. Player Controls");
        System.out.println("9. Change Password");
        System.out.println("0. Logout");
        System.out.print("Choice: ");

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input!");
            return;
        }

        switch (choice) {
            case 1 -> searchSongs();
            case 2 -> browseLibrary();
            case 3 -> showFavorites();
            case 4 -> managePlaylists();
            case 5 -> showPublicPlaylists();
            case 6 -> showRecentlyPlayed();
            case 7 -> showHistory();
            case 8 -> playerControls();
            case 9 -> changePassword();
            case 0 -> {
                menuHandler.logout();
                System.out.println("Logged out successfully!");
            }
            default -> System.out.println("Invalid choice!");
        }
    }

    private void changePassword() {
        System.out.print("Current Password: ");
        String oldPass = scanner.nextLine();
        System.out.print("New Password: ");
        String newPass = scanner.nextLine();

        if (userService.changePassword(user.getUserId(), oldPass, newPass)) {
            System.out.println("Password changed successfully!");
        } else {
            System.out.println("Failed to change password. Old password might be incorrect.");
        }
    }

    private void searchSongs() {
        System.out.print("Search keyword: ");
        String keyword = scanner.nextLine();
        List<Song> songs = songService.search(keyword);
        displaySongs(songs);
        if (!songs.isEmpty()) songActions(songs);
    }

    private void displaySongs(List<Song> songs) {
        if (songs.isEmpty()) {
            System.out.println("No songs found.");
            return;
        }
        System.out.println("\n--- Songs ---");
        for (int i = 0; i < songs.size(); i++) {
            Song s = songs.get(i);
            System.out.printf("%d. %s - %s (%s)%n", i+1, s.getTitle(), s.getArtistName(), s.getFormattedDuration());
        }
    }

    private void songActions(List<Song> songs) {
        if (songs.isEmpty()) return; // Extra safety
        System.out.println("\nActions: [P]lay, [F]avorite, [A]dd to playlist, [B]ack");
        String action = scanner.nextLine().toUpperCase();
        if (!"B".equals(action)) {
            try {
                System.out.print("Song number: ");
                int idx = Integer.parseInt(scanner.nextLine()) - 1;
                if (idx >= 0 && idx < songs.size()) {
                    Song song = songs.get(idx);
                    switch (action) {
                        case "P" -> {
                            playerService.addToQueue(song);
                            playerService.play();
                            playerControls(); // Automatically enter control loop
                        }
                        case "F" -> { songService.addToFavorites(user.getUserId(), song.getSongId()); System.out.println("Added to favorites!"); }
                        case "A" -> addToPlaylist(song.getSongId());
                    }
                } else {
                    System.out.println("Invalid song selection.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    private void browseLibrary() {
        System.out.println("\n--- Browse Library ---");
        System.out.println("1. By Genre");
        System.out.println("2. By Artist");
        System.out.println("3. By Album");
        System.out.println("0. Back");
        System.out.print("Choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1 -> browseByGenre();
                case 2 -> browseByArtist();
                case 3 -> browseByAlbum();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private void browseByArtist() {
        List<Artist> artists = userService.getAllArtists();
        if (artists.isEmpty()) {
            System.out.println("No artists found.");
            return;
        }

        System.out.println("\n--- Artists ---");
        for (int i=0; i < artists.size(); i++) {
            System.out.println((i+1) + ". " + artists.get(i).getName());
        }
        System.out.print("Select Artist Number: ");

        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx >= 0 && idx < artists.size()) {
                List<Song> songs = songService.getByArtist(artists.get(idx).getArtistId());
                displaySongs(songs);
                if (!songs.isEmpty()) songActions(songs);
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private void browseByAlbum() {
        List<Album> albums = albumService.getAllAlbums();
        if (albums.isEmpty()) {
            System.out.println("No albums found.");
            return;
        }

        System.out.println("\n--- Albums ---");
        for (int i=0; i < albums.size(); i++) {
            System.out.println((i+1) + ". " + albums.get(i).getTitle());
        }
        System.out.print("Select Album Number: ");

        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx >= 0 && idx < albums.size()) {
                List<Song> songs = songService.getByAlbum(albums.get(idx).getAlbumId());
                displaySongs(songs);
                if (!songs.isEmpty()) songActions(songs);
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    private void browseByGenre() {
        System.out.println("1.Pop 2.Rock 3.Hip-Hop 4.Jazz 5.Classical 6.Electronic 7.R&B 8.Country");
        System.out.print("Genre: ");
        try {
            int genreId = Integer.parseInt(scanner.nextLine());
            List<Song> songs = songService.getByGenre(genreId);
            displaySongs(songs);
            if (!songs.isEmpty()) songActions(songs);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Genre ID.");
        }
    }

    private void showFavorites() {
        List<Song> songs = songService.getFavorites(user.getUserId());
        if (songs.isEmpty()) {
            System.out.println("No favorites added yet.");
        } else {
            displaySongs(songs);
            songActions(songs); // Allow actions on favorites too
        }
    }

    private void managePlaylists() {
        System.out.println("\n1.View My Playlists 2.Create Playlist 3.Update Playlist 4.Delete Playlist 5.Search Playlists");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return;
        }

        switch (choice) {
            case 1 -> viewMyPlaylists();
            case 2 -> createPlaylist();
            case 3 -> updatePlaylist();
            case 4 -> deletePlaylist();
            case 5 -> searchPlaylists();
        }
    }

    private void viewMyPlaylists() {
        List<Playlist> playlists = playlistService.getUserPlaylists(user.getUserId());
        if (playlists.isEmpty()) {
            System.out.println("No playlists found.");
            return;
        }
        playlists.forEach(p -> System.out.println(p.getPlaylistId() + ". " + p.getName()));
        playlistActions();
    }

    private void playlistActions() {
        System.out.println("Select Playlist ID to Manage/Play (0 to cancel): ");
        try {
            int pid = Integer.parseInt(scanner.nextLine());
            if (pid == 0) return;

            System.out.println("1. Play Playlist");
            System.out.println("2. View Songs");
            System.out.println("3. Remove Song");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            int action = Integer.parseInt(scanner.nextLine());

            switch(action) {
                case 1 -> playPlaylist(pid);
                case 2 -> viewPlaylistSongs(pid);
                case 3 -> removeSongFromPlaylist(pid);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input");
        }
    }

    private void playPlaylist(int playlistId) {
        List<Song> songs = playlistService.getPlaylistSongs(playlistId);
        if (songs.isEmpty()) {
            System.out.println("Playlist is empty!");
            return;
        }
        playerService.clearQueue();
        songs.forEach(playerService::addToQueue);
        System.out.println("Playlist loaded into queue!");
        playerService.play();
        playerControls();
    }

    private void viewPlaylistSongs(int playlistId) {
        List<Song> songs = playlistService.getPlaylistSongs(playlistId);
        if (songs.isEmpty()) {
            System.out.println("Playlist is empty.");
        } else {
            displaySongs(songs);
        }
    }

    private void removeSongFromPlaylist(int playlistId) {
        List<Song> songs = playlistService.getPlaylistSongs(playlistId);
        if (songs.isEmpty()) {
            System.out.println("Playlist is empty.");
            return;
        }
        displaySongs(songs);
        System.out.print("Song Number to Remove: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < songs.size()) {
                Song s = songs.get(idx);
                if (playlistService.removeSong(playlistId, s.getSongId())) {
                    System.out.println("Removed from playlist.");
                } else {
                    System.out.println("Failed to remove.");
                }
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input");
        }
    }

    private void searchPlaylists() {
        System.out.print("Search Keyword: ");
        String kw = scanner.nextLine();
        List<Playlist> playlists = playlistService.searchPlaylists(kw);
        if (playlists.isEmpty()) {
            System.out.println("No playlists found.");
        } else {
            playlists.forEach(p -> System.out.println(p.getPlaylistId() + ". " + p.getName() + " (" + p.getDescription() + ")"));
            System.out.println("Select Playlist ID to Play (0 to skip): ");
            try {
                int pid = Integer.parseInt(scanner.nextLine());
                if (pid > 0) playPlaylist(pid);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
    }
    private void createPlaylist() {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Description: ");
        String desc = scanner.nextLine();
        System.out.print("Public? (y/n): ");
        boolean isPublic = "y".equalsIgnoreCase(scanner.nextLine());
        playlistService.create(user.getUserId(), name, desc, isPublic);
        System.out.println("Playlist created!");
    }

    private void updatePlaylist() {
        System.out.print("Playlist ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("New Name: ");
        String name = scanner.nextLine();
        System.out.print("New Description: ");
        String desc = scanner.nextLine();
        System.out.print("Public? (y/n): ");
        boolean isPublic = "y".equalsIgnoreCase(scanner.nextLine());
        Playlist p = new Playlist(user.getUserId(), name, desc, isPublic);
        p.setPlaylistId(id);
        playlistService.update(p);
        System.out.println("Updated!");
    }

    private void deletePlaylist() {
        System.out.print("Playlist ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        playlistService.delete(id, user.getUserId());
        System.out.println("Deleted!");
    }

    private void addToPlaylist(int songId) {
        List<Playlist> playlists = playlistService.getUserPlaylists(user.getUserId());
        if (playlists.isEmpty()) {
            System.out.println("No playlists found. Create one first!");
            return;
        }
        playlists.forEach(p -> System.out.println(p.getPlaylistId() + ". " + p.getName()));
        System.out.print("Playlist ID: ");
        try {
            int pid = Integer.parseInt(scanner.nextLine());
            playlistService.addSong(pid, songId);
            System.out.println("Added!");
        } catch (NumberFormatException e) {
            System.out.println("Invalid Playlist ID.");
        }
    }
    private void showPublicPlaylists() {
        List<Playlist> playlists = playlistService.getPublicPlaylists();
        if (playlists.isEmpty()) {
            System.out.println("No public playlists available.");
        } else {
            playlists.forEach(p -> System.out.println(p.getPlaylistId() + ". " + p.getName() + " - " + p.getDescription()));
            System.out.println("Select Playlist ID to Play (0 to skip): ");
            try {
                int pid = Integer.parseInt(scanner.nextLine());
                if (pid > 0) playPlaylist(pid);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
    }

    private void showRecentlyPlayed() {
        List<Song> songs = songService.getRecentlyPlayed(user.getUserId());
        if (songs.isEmpty()) {
            System.out.println("No recently played songs.");
        } else {
            displaySongs(songs);
        }
    }

    private void showHistory() {
        List<Song> songs = songService.getListeningHistory(user.getUserId());
        if (songs.isEmpty()) {
            System.out.println("No listening history.");
        } else {
            displaySongs(songs);
        }
    }

    private void playerControls() {
        boolean inPlayerMode = true;
        playerService.setSilentMode(false); // Enable progress bar

        while (inPlayerMode) {
            // clear screen roughly
            System.out.println("\n------------------------------------------------");
            // Dynamic Menu: Show Resume if paused, Pause if playing
            String playPauseOption = playerService.isPlaying() ? "[U]pause" : "[R]esume/[P]lay";

            System.out.printf("CONTROLS: %s [N]ext [V]Previous [T]Repeat [Q]ueue [C]lear [S]top [B]ack%n", playPauseOption);
            System.out.print("Command: ");

            String cmd = scanner.nextLine().toUpperCase();
            switch (cmd) {
                case "P", "R" -> {
                    if (playerService.isPlaying()) System.out.println("Already playing.");
                    else if (playerService.hasCurrent()) playerService.resume();
                    else playerService.play();
                }
                case "U" -> playerService.pause();
                case "N" -> playerService.next();
                // Fixed typo in menu logic for previous (was 'R' before, now 'P' for standard, but menu says 'Previous')
                // Keeping 'R' for Repeat, using 'V' or 'L' or just keeping 'R' for Prev?
                // Let's stick to standard: P=Play/Pause toggle often, but here separate.
                // Let's use: N=Next, L=Last/Prev, U=Unpause/Pause, P=Play
                // The previous code used R for Previous. Let's fix map:
                // P -> Play/Resume
                // U -> Pause
                // N -> Next
                // V -> Previous (PreVious)
                // T -> Toggle Repeat
                case "V" -> playerService.previous();
                case "T" -> playerService.toggleRepeat();
                case "Q" -> playerService.showQueue();
                case "C" -> playerService.clearQueue();
                case "S" -> {
                    playerService.stop();
                    inPlayerMode=false;
                }

                case "B" -> {
                    inPlayerMode = false;
                    playerService.setSilentMode(true); // Suppress progress bar so it doesn't garble menu
                    System.out.println("Returning to menu (music continues in background)...");
                }
                default -> System.out.println("Invalid command.");
            }
        }
    }
}