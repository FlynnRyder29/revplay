package com.revplay.dao;
import com.revplay.model.Song;
import com.revplay.util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class SongDAO {
    private static final Logger logger = LogManager.getLogger(SongDAO.class);

    public Song create(Song song) {
        String sql = "INSERT INTO songs (artist_id, album_id, genre_id, title, duration_seconds, release_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"song_id"})) {
            ps.setInt(1, song.getArtistId());
            ps.setObject(2, song.getAlbumId());
            ps.setObject(3, song.getGenreId());
            ps.setString(4, song.getTitle());
            ps.setInt(5, song.getDurationSeconds());
            ps.setObject(6, song.getReleaseDate());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) song.setSongId(rs.getInt(1));
            return song;
        } catch (SQLException e) {
            logger.error("Create song failed", e);
            return null;
        }
    }

    public List<Song> search(String keyword) {
        String sql = "SELECT s.*, u.username as artist_name, a.title as album_title, g.name as genre_name " +
                "FROM songs s " +
                "JOIN artists ar ON s.artist_id = ar.artist_id " +
                "JOIN users u ON ar.user_id = u.user_id " +
                "LEFT JOIN albums a ON s.album_id = a.album_id " +
                "LEFT JOIN genres g ON s.genre_id = g.genre_id " +
                "WHERE LOWER(s.title) LIKE ? OR LOWER(u.username) LIKE ?";
        List<Song> songs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) songs.add(mapResultSetToSong(rs));
        } catch (SQLException e) {
            logger.error("Search failed", e);
        }
        return songs;
    }

    public List<Song> getByGenre(int genreId) {
        String sql = "SELECT s.*, u.username as artist_name FROM songs s " +
                "JOIN artists ar ON s.artist_id = ar.artist_id " +
                "JOIN users u ON ar.user_id = u.user_id WHERE s.genre_id = ?";
        List<Song> songs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, genreId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) songs.add(mapResultSetToSong(rs));
        } catch (SQLException e) {
            logger.error("Get by genre failed", e);
        }
        return songs;
    }

    public List<Song> getByArtist(int artistId) {
        String sql = "SELECT s.*, u.username as artist_name FROM songs s " +
                "JOIN artists ar ON s.artist_id = ar.artist_id " +
                "JOIN users u ON ar.user_id = u.user_id WHERE s.artist_id = ?";
        List<Song> songs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, artistId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) songs.add(mapResultSetToSong(rs));
        } catch (SQLException e) {
            logger.error("Get by artist failed", e);
        }
        return songs;
    }

    public void incrementPlayCount(int songId) {
        String sql = "UPDATE songs SET play_count = play_count + 1 WHERE song_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, songId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Increment play count failed", e);
        }
    }

    public boolean delete(int songId, int artistId) {
        String sql = "DELETE FROM songs WHERE song_id = ? AND artist_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, songId);
            ps.setInt(2, artistId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Delete song failed", e);
            return false;
        }
    }

    private Song mapResultSetToSong(ResultSet rs) throws SQLException {
        Song song = new Song();
        song.setSongId(rs.getInt("song_id"));
        song.setArtistId(rs.getInt("artist_id"));
        song.setTitle(rs.getString("title"));
        song.setDurationSeconds(rs.getInt("duration_seconds"));
        song.setPlayCount(rs.getInt("play_count"));
        try { song.setArtistName(rs.getString("artist_name")); } catch (SQLException ignored) {}
        return song;
    }
}