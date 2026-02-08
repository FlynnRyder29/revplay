package com.revplay.ui;
import com.revplay.model.*;
import com.revplay.dao.*;
import com.revplay.service.UserService;
import com.revplay.service.AlbumService;
import com.revplay.service.SongService;
import java.util.List;
import java.util.Scanner;
public class ArtistMenu {
    private Scanner scanner;
    private User user;
    private Artist artist;
    private MenuHandler menuHandler;
    private UserService userService = new UserService();
    private AlbumService albumService = new AlbumService();
    private SongService songService = new SongService();
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
        System.out.println("7. Manage Albums");
        System.out.println("8. Edit Song Details");
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
            case 1 -> uploadSong();
            case 2 -> viewMySongs();
            case 3 -> updateProfile();
            case 4 -> viewStats();
            case 5 -> viewFavoritedBy();
            case 6 -> deleteSong();
            case 7 -> manageAlbums();
            case 8 -> editSong();
            case 9 -> changePassword();
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
        int duration = Integer.parseInt(scanner.nextLine());

        System.out.println("Available Genres:");
        System.out.println("1.Pop 2.Rock 3.Hip-Hop 4.Jazz 5.Classical 6.Electronic 7.R&B 8.Country");
        System.out.print("Genre ID (1-8): ");
        int genreId = Integer.parseInt(scanner.nextLine());

        Integer albumId = null;
        List<Album> albums = albumService.getByArtist(artist.getArtistId());
        if (!albums.isEmpty()) {
            System.out.println("Add to Album? (0 for Single/None)");
            albums.forEach(a -> System.out.println(a.getAlbumId() + ". " + a.getTitle()));
            System.out.print("Album ID: ");
            int aid = Integer.parseInt(scanner.nextLine());
            if (aid > 0) albumId = aid;
        }

        Song song = new Song(artist.getArtistId(), title, duration);
        song.setGenreId(genreId);
        song.setAlbumId(albumId);
        song.setReleaseDate(java.time.LocalDate.now()); // Set release date
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
    private void manageAlbums() {
        while (true) {
            System.out.println("\n--- Manage Albums ---");
            System.out.println("1. Create Album");
            System.out.println("2. View My Albums");
            System.out.println("3. Update Album");
            System.out.println("4. Delete Album");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1 -> {
                        System.out.print("Album Title: ");
                        String title = scanner.nextLine();
                        albumService.create(artist.getArtistId(), title);
                        System.out.println("Album created of title:"+title);
                    }
                    case 2 -> {
                        List<Album> albums = albumService.getByArtist(artist.getArtistId());
                        if (albums.isEmpty()) System.out.println("No albums found.");
                        else albums.forEach(a -> System.out.println(a.getAlbumId() + ". " + a.getTitle() + " (" + a.getReleaseDate() + ")"));
                    }
                    case 3 -> {
                        System.out.print("Album ID: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        System.out.print("New Title: ");
                        String title = scanner.nextLine();
                        if(albumService.update(id, artist.getArtistId(), title)) System.out.println("Updated!");
                        else System.out.println("Update failed.");
                    }
                    case 4 -> {
                        System.out.print("Album ID: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        if(albumService.delete(id, artist.getArtistId())) System.out.println("Deleted!");
                        else System.out.println("Delete failed.");
                    }
                    case 0 -> { return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input");
            }
        }
    }

    private void editSong() {
        System.out.print("Song ID: ");
        try {
            int songId = Integer.parseInt(scanner.nextLine());
            // Verify ownership
            List<Song> songs = songDAO.getByArtist(artist.getArtistId());
            Song song = songs.stream().filter(s -> s.getSongId() == songId).findFirst().orElse(null);

            if (song == null) {
                System.out.println("Song not found or doesn't belong to you.");
                return;
            }

            System.out.println("Editing: " + song.getTitle());
            System.out.println("1. Title\n2. Genre\n3. Album");
            System.out.print("Update Field: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch(choice) {
                case 1 -> {
                    System.out.print("New Title: ");
                    song.setTitle(scanner.nextLine());
                }
                case 2 -> {
                    System.out.print("New Genre ID: ");
                    song.setGenreId(Integer.parseInt(scanner.nextLine()));
                }
                case 3 -> {
                    System.out.print("New Album ID (0 for none): ");
                    int aid = Integer.parseInt(scanner.nextLine());
                    song.setAlbumId(aid == 0 ? null : aid);
                }
                default -> { System.out.println("Invalid option"); return; }
            }

            if(songService.updateSong(song)) System.out.println("Song updated!");
            else System.out.println("Update failed.");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input");
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
}