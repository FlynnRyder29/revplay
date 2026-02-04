package com.revplay.dao;
import com.revplay.model.Song;
import com.revplay.util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class HistoryDAO {
    private static final Logger logger = LogManager.getLogger(HistoryDAO.class);

    public void recordPlay(int userId, int songId) {
        // Add to history
        String sql1 = "INSERT INTO listening_history (user_id, song_id) VALUES (?, ?)";
        // Update recently played
        String sql2 = "MERGE INTO recently_played rp USING (SELECT ? as user_id, ? as song_id FROM DUAL) src " +
                "ON (rp.user_id = src.user_id AND rp.song_id = src.song_id) " +
                "WHEN MATCHED THEN UPDATE SET last_played = CURRENT_TIMESTAMP " +
                "WHEN NOT MATCHED THEN INSERT (user_id, song_id) VALUES (src.user_id, src.song_id)";
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps1 = conn.prepareStatement(sql1);
            ps1.setInt(1, userId);
            ps1.setInt(2, songId);
            ps1.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement(sql2);
            ps2.setInt(1, userId);
            ps2.setInt(2, songId);
            ps2.executeUpdate();
        } catch (SQLException e) {
            logger.error("Record play failed", e);
        }
    }

    public List<Song> getRecentlyPlayed(int userId, int limit) {
        String sql = "SELECT * FROM (SELECT s.*, u.username as artist_name FROM recently_played rp " +
                "JOIN songs s ON rp.song_id = s.song_id " +
                "JOIN artists ar ON s.artist_id = ar.artist_id " +
                "JOIN users u ON ar.user_id = u.user_id " +
                "WHERE rp.user_id = ? ORDER BY rp.last_played DESC) WHERE ROWNUM <= ?";
        List<Song> songs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Song song = new Song();
                song.setSongId(rs.getInt("song_id"));
                song.setTitle(rs.getString("title"));
                song.setArtistName(rs.getString("artist_name"));
                songs.add(song);
            }
        } catch (SQLException e) {
            logger.error("Get recently played failed", e);
        }
        return songs;
    }

    public List<Song> getListeningHistory(int userId) {
        String sql = "SELECT s.*, u.username as artist_name, lh.played_at FROM listening_history lh " +
                "JOIN songs s ON lh.song_id = s.song_id " +
                "JOIN artists ar ON s.artist_id = ar.artist_id " +
                "JOIN users u ON ar.user_id = u.user_id " +
                "WHERE lh.user_id = ? ORDER BY lh.played_at DESC";
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
                songs.add(song);
            }
        } catch (SQLException e) {
            logger.error("Get history failed", e);
        }
        return songs;
    }
}