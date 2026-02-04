package com.revplay.dao;
import com.revplay.model.Song;
import com.revplay.util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class FavoriteDAO {
    private static final Logger logger = LogManager.getLogger(FavoriteDAO.class);

    public boolean addFavorite(int userId, int songId) {
        String sql = "INSERT INTO favorites (user_id, song_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, songId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Add favorite failed", e);
            return false;
        }
    }

    public boolean removeFavorite(int userId, int songId) {
        String sql = "DELETE FROM favorites WHERE user_id = ? AND song_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, songId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Remove favorite failed", e);
            return false;
        }
    }

    public List<Song> getFavorites(int userId) {
        String sql = "SELECT s.*, u.username as artist_name FROM favorites f " +
                "JOIN songs s ON f.song_id = s.song_id " +
                "JOIN artists ar ON s.artist_id = ar.artist_id " +
                "JOIN users u ON ar.user_id = u.user_id WHERE f.user_id = ?";
        List<Song> songs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Song song = new Song();
                song.setSongId(rs.getInt("song_id"));
                song.setTitle(rs.getString("title"));
                song.setArtistName(rs.getString("artist_name"));
                song.setDurationSeconds(rs.getInt("duration_seconds"));
                songs.add(song);
            }
        } catch (SQLException e) {
            logger.error("Get favorites failed", e);
        }
        return songs;
    }

    public List<String> getUsersWhoFavoritedSong(int songId) {
        String sql = "SELECT u.username FROM favorites f JOIN users u ON f.user_id = u.user_id WHERE f.song_id = ?";
        List<String> usernames = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, songId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) usernames.add(rs.getString("username"));
        } catch (SQLException e) {
            logger.error("Get users who favorited failed", e);
        }
        return usernames;
    }
}