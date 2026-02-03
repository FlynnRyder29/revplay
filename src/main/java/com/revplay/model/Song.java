package com.revplay.model;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
public class Song implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int songId;
    private int artistId;
    private Integer albumId;
    private Integer genreId;
    private String title;
    private int durationSeconds;
    private LocalDate releaseDate;
    private int playCount;
    private LocalDateTime createdAt;

    // For display
    private String artistName;
    private String albumTitle;
    private String genreName;

    public Song() {}

    public Song(int artistId, String title, int durationSeconds) {
        this.artistId = artistId;
        this.title = title;
        this.durationSeconds = durationSeconds;
    }

    public String getFormattedDuration() {
        int minutes = durationSeconds / 60;
        int seconds = durationSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    // Getters and Setters
    public int getSongId() { return songId; }
    public void setSongId(int songId) { this.songId = songId; }
    public int getArtistId() { return artistId; }
    public void setArtistId(int artistId) { this.artistId = artistId; }
    public Integer getAlbumId() { return albumId; }
    public void setAlbumId(Integer albumId) { this.albumId = albumId; }
    public Integer getGenreId() { return genreId; }
    public void setGenreId(Integer genreId) { this.genreId = genreId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
    public int getPlayCount() { return playCount; }
    public void setPlayCount(int playCount) { this.playCount = playCount; }
    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    public String getAlbumTitle() { return albumTitle; }
    public void setAlbumTitle(String albumTitle) { this.albumTitle = albumTitle; }
    public String getGenreName() { return genreName; }
    public void setGenreName(String genreName) { this.genreName = genreName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}