package com.revplay.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Artist implements Serializable {
    private static final long serialVersionUID = 1L;

    private int artistId;
    private int userId;
    private String bio;
    private String genre;
    private String socialLinks;
    private LocalDateTime createdAt;

    // For display
    private String name;

    public Artist() {}

    public Artist(int userId, String bio, String genre) {
        this.userId = userId;
        this.bio = bio;
        this.genre = genre;
    }

    // Getters and Setters
    public int getArtistId() { return artistId; }
    public void setArtistId(int artistId) { this.artistId = artistId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getSocialLinks() { return socialLinks; }
    public void setSocialLinks(String socialLinks) { this.socialLinks = socialLinks; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
