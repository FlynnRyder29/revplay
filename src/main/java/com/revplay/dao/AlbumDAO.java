package com.revplay.dao;
import com.revplay.model.Album;
import com.revplay.util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class AlbumDAO {
    private static final Logger logger = LogManager.getLogger(AlbumDAO.class);

    public Album create(Album album) {
        String sql = "INSERT INTO albums (artist_id, title, release_date) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"album_id"})) {
            ps.setInt(1, album.getArtistId());
            ps.setString(2, album.getTitle());
            ps.setObject(3, album.getReleaseDate());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) album.setAlbumId(rs.getInt(1));
            return album;
        } catch (SQLException e) {
            logger.error("Create album failed", e);
            return null;
        }
    }

    public List<Album> getByArtist(int artistId) {
        String sql = "SELECT * FROM albums WHERE artist_id = ?";
        List<Album> albums = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, artistId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) albums.add(mapResultSetToAlbum(rs));
        } catch (SQLException e) {
            logger.error("Get albums by artist failed", e);
        }
        return albums;
    }

    public Album getById(int albumId) {
        String sql = "SELECT * FROM albums WHERE album_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, albumId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSetToAlbum(rs);
        } catch (SQLException e) {
            logger.error("Get album by id failed", e);
        }
        return null;
    }

    public boolean update(Album album) {
        String sql = "UPDATE albums SET title = ?, cover_url = ? WHERE album_id = ? AND artist_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, album.getTitle());
            ps.setString(2, album.getCoverUrl());
            ps.setInt(3, album.getAlbumId());
            ps.setInt(4, album.getArtistId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Update album failed", e);
            return false;
        }
    }

    public boolean delete(int albumId, int artistId) {
        String sql = "DELETE FROM albums WHERE album_id = ? AND artist_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, albumId);
            ps.setInt(2, artistId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Delete album failed", e);
            return false;
        }
    }

    private Album mapResultSetToAlbum(ResultSet rs) throws SQLException {
        Album a = new Album();
        a.setAlbumId(rs.getInt("album_id"));
        a.setArtistId(rs.getInt("artist_id"));
        a.setTitle(rs.getString("title"));
        a.setReleaseDate(rs.getObject("release_date", java.time.LocalDate.class));
        a.setCoverUrl(rs.getString("cover_url"));
        return a;
    }
}