package com.revplay.model;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
public class Album implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int albumId;
    private int artistId;
    private String title;
    private LocalDate releaseDate;
    private String coverUrl;
    private LocalDateTime createdAt;

    public Album() {}

    public Album(int artistId, String title) {
        this.artistId = artistId;
        this.title = title;
    }

    // Getters and Setters
    public int getAlbumId() { return albumId; }
    public void setAlbumId(int albumId) { this.albumId = albumId; }
    public int getArtistId() { return artistId; }
    public void setArtistId(int artistId) { this.artistId = artistId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}