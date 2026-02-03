package com.revplay.model;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
public class Playlist implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int playlistId;
    private int userId;
    private String name;
    private String description;
    private boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Playlist() {}

    public Playlist(int userId, String name, String description, boolean isPublic) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
    }

    // Getters and Setters
    public int getPlaylistId() { return playlistId; }
    public void setPlaylistId(int playlistId) { this.playlistId = playlistId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}