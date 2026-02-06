package com.revplay.ui;
import com.revplay.model.*;
import com.revplay.dao.*;
import com.revplay.service.UserService;
import java.util.List;
import java.util.Scanner;
public class ArtistMenu {
    private Scanner scanner;
    private User user;
    private Artist artist;
    private MenuHandler menuHandler;
    private SongDAO songDAO = new SongDAO();
    private FavoriteDAO favoriteDAO = new FavoriteDAO();
    private ArtistDAO artistDAO = new ArtistDAO();

    public ArtistMenu(Scanner scanner, User user, MenuHandler menuHandler) {
        this.scanner = scanner;
        this.user = user;
        this.menuHandler = menuHandler;
        this.artist = artistDAO.getByUserId(user.getUserId());
    }

    public void show() {
        System.out.println("\n--- Artist Dashboard: " + user.getUsername() + " ---");
        System.out.println("1. Upload Song");
        System.out.println("2. My Songs");
        System.out.println("3. Update Profile");
        System.out.println("4. View Song Stats");
        System.out.println("5. Who Favorited My Songs");
        System.out.println("6. Delete Song");
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
            case 1 -> uploadSong();
            case 2 -> viewMySongs();
            case 3 -> updateProfile();
            case 4 -> viewStats();
            case 5 -> viewFavoritedBy();
            case 6 -> deleteSong();
            case 0 -> {
                menuHandler.logout();
                System.out.println("Logged out successfully!");
            }
            default -> System.out.println("Invalid choice!");
        }
    }

    private void uploadSong() {
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Duration (seconds): ");
        System.out.println("Available Genres:");
        System.out.println("1.Pop 2.Rock 3.Hip-Hop 4.Jazz 5.Classical 6.Electronic 7.R&B 8.Country");
        int duration = Integer.parseInt(scanner.nextLine());
        System.out.print("Genre ID (1-8): ");
        int genreId = Integer.parseInt(scanner.nextLine());

        Song song = new Song(artist.getArtistId(), title, duration);
        song.setGenreId(genreId);
        songDAO.create(song);
        System.out.println("Song uploaded!");
    }

    private void viewMySongs() {
        List<Song> songs = songDAO.getByArtist(artist.getArtistId());
        if (songs.isEmpty()) {
            System.out.println("You haven't uploaded any songs yet.");
        } else {
            songs.forEach(s -> System.out.printf("%d. %s (%s) - Plays: %d%n",
                    s.getSongId(), s.getTitle(), s.getFormattedDuration(), s.getPlayCount()));
        }
    }

    private void updateProfile() {
        System.out.print("New Bio: ");
        artist.setBio(scanner.nextLine());
        System.out.print("Genre: ");
        artist.setGenre(scanner.nextLine());
        System.out.print("Social Links: ");
        artist.setSocialLinks(scanner.nextLine());
        artistDAO.update(artist);
        System.out.println("Profile updated!");
    }

    private void viewStats() {
        List<Song> songs = songDAO.getByArtist(artist.getArtistId());
        if (songs.isEmpty()) {
            System.out.println("No stats available (no songs uploaded).");
        } else {
            int totalPlays = songs.stream().mapToInt(Song::getPlayCount).sum();
            System.out.println("Total Songs: " + songs.size());
            System.out.println("Total Plays: " + totalPlays);
        }
    }

    private void viewFavoritedBy() {
        System.out.print("Song ID: ");
        try {
            int songId = Integer.parseInt(scanner.nextLine());
            List<String> users = favoriteDAO.getUsersWhoFavoritedSong(songId);
            if (users.isEmpty()) {
                System.out.println("No users have favorited this song yet.");
            } else {
                System.out.println("Favorited by: " + String.join(", ", users));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Song ID.");
        }
    }

    private void deleteSong() {
        System.out.print("Song ID: ");
        try {
            int songId = Integer.parseInt(scanner.nextLine());
            songDAO.delete(songId, artist.getArtistId());
            System.out.println("Song deleted (if it existed and belonged to you)!");
        } catch (NumberFormatException e) {
            System.out.println("Invalid Song ID.");
        }
    }
}