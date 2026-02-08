package com.revplay.dao;

import com.revplay.model.Playlist;
import com.revplay.model.Song;
import com.revplay.util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDAO {
    private static final Logger logger = LogManager.getLogger(PlaylistDAO.class);

    public Playlist create(Playlist playlist) {
        String sql = "INSERT INTO playlists (user_id, name, description, is_public) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"playlist_id"})) {
            ps.setInt(1, playlist.getUserId());
            ps.setString(2, playlist.getName());
            ps.setString(3, playlist.getDescription());
            ps.setInt(4, playlist.isPublic() ? 1 : 0);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) playlist.setPlaylistId(rs.getInt(1));
            return playlist;
        } catch (SQLException e) {
            logger.error("Create playlist failed", e);
            return null;
        }
    }

    public List<Playlist> getByUser(int userId) {
        String sql = "SELECT * FROM playlists WHERE user_id = ?";
        List<Playlist> playlists = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) playlists.add(mapResultSetToPlaylist(rs));
        } catch (SQLException e) {
            logger.error("Get playlists failed", e);
        }
        return playlists;
    }

    public List<Playlist> getPublicPlaylists() {
        String sql = "SELECT * FROM playlists WHERE is_public = 1";
        List<Playlist> playlists = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) playlists.add(mapResultSetToPlaylist(rs));
        } catch (SQLException e) {
            logger.error("Get public playlists failed", e);
        }
        return playlists;
    }

    public boolean addSong(int playlistId, int songId) {
        String sql = "INSERT INTO playlist_songs (playlist_id, song_id, position) " +
                "VALUES (?, ?, (SELECT COALESCE(MAX(position), 0) + 1 FROM playlist_songs WHERE playlist_id = ?))";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playlistId);
            ps.setInt(2, songId);
            ps.setInt(3, playlistId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Add song to playlist failed", e);
            return false;
        }
    }

    public boolean removeSong(int playlistId, int songId) {
        String sql = "DELETE FROM playlist_songs WHERE playlist_id = ? AND song_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playlistId);
            ps.setInt(2, songId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Remove song failed", e);
            return false;
        }
    }

    public boolean update(Playlist playlist) {
        String sql = "UPDATE playlists SET name = ?, description = ?, is_public = ?, updated_at = CURRENT_TIMESTAMP WHERE playlist_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playlist.getName());
            ps.setString(2, playlist.getDescription());
            ps.setInt(3, playlist.isPublic() ? 1 : 0);
            ps.setInt(4, playlist.getPlaylistId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Update playlist failed", e);
            return false;
        }
    }

    public boolean delete(int playlistId, int userId) {
        String sql = "DELETE FROM playlists WHERE playlist_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playlistId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Delete playlist failed", e);
            return false;
        }
    }

    public List<Song> getSongsInPlaylist(int playlistId) {
        String sql = "SELECT s.*, u.username as artist_name FROM playlist_songs ps " +
                "JOIN songs s ON ps.song_id = s.song_id " +
                "JOIN artists ar ON s.artist_id = ar.artist_id " +
                "JOIN users u ON ar.user_id = u.user_id " +
                "WHERE ps.playlist_id = ? ORDER BY ps.position";
        List<Song> songs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playlistId);
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
            logger.error("Get playlist songs failed", e);
        }
        return songs;
    }

    public List<Playlist> search(String keyword) {
        String sql = "SELECT * FROM playlists WHERE LOWER(name) LIKE ? OR LOWER(description) LIKE ?";
        List<Playlist> playlists = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) playlists.add(mapResultSetToPlaylist(rs));
        } catch (SQLException e) {
            logger.error("Search playlists failed", e);
        }
        return playlists;
    }

    private Playlist mapResultSetToPlaylist(ResultSet rs) throws SQLException {
        Playlist p = new Playlist();
        p.setPlaylistId(rs.getInt("playlist_id"));
        p.setUserId(rs.getInt("user_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPublic(rs.getInt("is_public") == 1);
        return p;
    }
}
