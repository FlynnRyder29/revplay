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
        System.out.println("\n--- Songs ---");
        for (int i = 0; i < songs.size(); i++) {
            Song s = songs.get(i);
            System.out.printf("%d. %s - %s (%s)%n", i+1, s.getTitle(), s.getArtistName(), s.getFormattedDuration());
        }
    }

    private void songActions(List<Song> songs) {
        System.out.println("\nActions: [P]lay, [F]avorite, [A]dd to playlist, [B]ack");
        String action = scanner.nextLine().toUpperCase();
        if (!"B".equals(action)) {
            System.out.print("Song number: ");
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            Song song = songs.get(idx);
            switch (action) {
                case "P" -> { playerService.addToQueue(song); playerService.play(); }
                case "F" -> { songService.addToFavorites(user.getUserId(), song.getSongId()); System.out.println("Added to favorites!"); }
                case "A" -> addToPlaylist(song.getSongId());
            }
        }
    }

    private void browseByGenre() {
        System.out.println("1.Pop 2.Rock 3.Hip-Hop 4.Jazz 5.Classical 6.Electronic 7.R&B 8.Country");
        System.out.print("Genre: ");
        int genreId = Integer.parseInt(scanner.nextLine());
        List<Song> songs = songService.getByGenre(genreId);
        displaySongs(songs);
        if (!songs.isEmpty()) songActions(songs);
    }

    private void showFavorites() {
        List<Song> songs = songService.getFavorites(user.getUserId());
        displaySongs(songs);
    }

    private void managePlaylists() {
        System.out.println("\n1.View My Playlists 2.Create Playlist 3.Update Playlist 4.Delete Playlist");
        int choice = Integer.parseInt(scanner.nextLine());
        switch (choice) {
            case 1 -> {
                List<Playlist> playlists = playlistService.getUserPlaylists(user.getUserId());
                playlists.forEach(p -> System.out.println(p.getPlaylistId() + ". " + p.getName()));
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
        playlists.forEach(p -> System.out.println(p.getPlaylistId() + ". " + p.getName()));
        System.out.print("Playlist ID: ");
        int pid = Integer.parseInt(scanner.nextLine());
        playlistService.addSong(pid, songId);
        System.out.println("Added!");
    }

    private void showPublicPlaylists() {
        List<Playlist> playlists = playlistService.getPublicPlaylists();
        playlists.forEach(p -> System.out.println(p.getPlaylistId() + ". " + p.getName() + " - " + p.getDescription()));
    }

    private void showRecentlyPlayed() {
        List<Song> songs = songService.getRecentlyPlayed(user.getUserId());
        displaySongs(songs);
    }

    private void showHistory() {
        List<Song> songs = songService.getListeningHistory(user.getUserId());
        displaySongs(songs);
    }

    private void playerControls() {
        System.out.println("\n[P]lay [U]pause [N]ext [R]Previous [T]Repeat [Q]ueue [C]lear [B]ack");
        String cmd = scanner.nextLine().toUpperCase();
        switch (cmd) {
            case "P" -> playerService.play();
            case "U" -> playerService.pause();
            case "N" -> playerService.next();
            case "R" -> playerService.previous();
            case "T" -> playerService.toggleRepeat();
            case "Q" -> playerService.showQueue();
            case "C" -> playerService.clearQueue();
        }
    }
}