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
    private PlaylistService playlistService = new PlaylistService();
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
        System.out.println("2. Browse by Genre");
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
            case 2 -> browseByGenre();
            case 3 -> showFavorites();
            case 4 -> managePlaylists();
            case 5 -> showPublicPlaylists();
            case 6 -> showRecentlyPlayed();
            case 7 -> showHistory();
            case 8 -> playerControls();
            case 0 -> {
                menuHandler.logout();
                System.out.println("Logged out successfully!");
            }
            default -> System.out.println("Invalid choice!");
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
        System.out.println("\n1.View My Playlists 2.Create Playlist 3.Update Playlist 4.Delete Playlist");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return;
        }

        switch (choice) {
            case 1 -> {
                List<Playlist> playlists = playlistService.getUserPlaylists(user.getUserId());
                if (playlists.isEmpty()) {
                    System.out.println("No playlists found.");
                } else {
                    playlists.forEach(p -> System.out.println(p.getPlaylistId() + ". " + p.getName()));
                }
            }
            case 2 -> createPlaylist();
            case 3 -> updatePlaylist();
            case 4 -> deletePlaylist();
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

            System.out.printf("CONTROLS: %s [N]ext [P]revious [T]Repeat [Q]ueue [C]lear [B]ack%n", playPauseOption);
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