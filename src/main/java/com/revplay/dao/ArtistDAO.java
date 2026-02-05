package com.revplay.dao;
import com.revplay.model.Artist;
import com.revplay.util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
public class ArtistDAO {
    private static final Logger logger = LogManager.getLogger(ArtistDAO.class);

    public Artist create(Artist artist) {
        String sql = "INSERT INTO artists (user_id, bio, genre, social_links) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"artist_id"})) {
            ps.setInt(1, artist.getUserId());
            ps.setString(2, artist.getBio());
            ps.setString(3, artist.getGenre());
            ps.setString(4, artist.getSocialLinks());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) artist.setArtistId(rs.getInt(1));
            return artist;
        } catch (SQLException e) {
            logger.error("Create artist failed", e);
            return null;
        }
    }

    public Artist getByUserId(int userId) {
        String sql = "SELECT * FROM artists WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Artist a = new Artist();
                a.setArtistId(rs.getInt("artist_id"));
                a.setUserId(rs.getInt("user_id"));
                a.setBio(rs.getString("bio"));
                a.setGenre(rs.getString("genre"));
                a.setSocialLinks(rs.getString("social_links"));
                return a;
            }
        } catch (SQLException e) {
            logger.error("Get artist failed", e);
        }
        return null;
    }

    public boolean update(Artist artist) {
        String sql = "UPDATE artists SET bio = ?, genre = ?, social_links = ? WHERE artist_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, artist.getBio());
            ps.setString(2, artist.getGenre());
            ps.setString(3, artist.getSocialLinks());
            ps.setInt(4, artist.getArtistId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Update artist failed", e);
            return false;
        }
    }
}