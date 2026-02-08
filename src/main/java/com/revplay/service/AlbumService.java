package com.revplay.service;

import com.revplay.dao.AlbumDAO;
import com.revplay.model.Album;
import com.revplay.model.Song;
import com.revplay.dao.SongDAO;
import java.util.List;
import java.time.LocalDate;

public class AlbumService {
    private AlbumDAO albumDAO = new AlbumDAO();
    private SongDAO songDAO = new SongDAO();

    public Album create(int artistId, String title) {
        Album album = new Album(artistId, title);
        album.setReleaseDate(LocalDate.now()); // Default to today
        return albumDAO.create(album);
    }

    public List<Album> getByArtist(int artistId) {
        return albumDAO.getByArtist(artistId);
    }

    public List<Album> getAllAlbums() {
        return albumDAO.getAll();
    }

    public boolean delete(int albumId, int artistId) {
        // Option: Either delete songs or unlink them. Here we unlink for safety or delete if cascade.
        // Let's just delete the album record. Database FK constraints might need attention if ON DELETE CASCADE isn't set.
        // For this simple app, we'll assume it's fine or we unlink songs first.
        List<Song> songs = songDAO.getByAlbum(albumId);
        for(Song s : songs) {
            s.setAlbumId(null);
            songDAO.update(s);
        }
        return albumDAO.delete(albumId, artistId);
    }

    public boolean update(int albumId, int artistId, String newTitle) {
        Album a = albumDAO.getById(albumId);
        if (a != null && a.getArtistId() == artistId) {
            a.setTitle(newTitle);
            return albumDAO.update(a);
        }
        return false;
    }
}
