package com.revplay.service;
import com.revplay.dao.PlaylistDAO;
import com.revplay.model.Playlist;
import java.util.List;
public class PlaylistService {
    private PlaylistDAO playlistDAO = new PlaylistDAO();

    public Playlist create(int userId, String name, String desc, boolean isPublic) {
        Playlist p = new Playlist(userId, name, desc, isPublic);
        return playlistDAO.create(p);
    }

    public List<Playlist> getUserPlaylists(int userId) {
        return playlistDAO.getByUser(userId);
    }

    public List<Playlist> getPublicPlaylists() {
        return playlistDAO.getPublicPlaylists();
    }

    public boolean addSong(int playlistId, int songId) {
        return playlistDAO.addSong(playlistId, songId);
    }

    public boolean removeSong(int playlistId, int songId) {
        return playlistDAO.removeSong(playlistId, songId);
    }

    public boolean update(Playlist playlist) {
        return playlistDAO.update(playlist);
    }

    public boolean delete(int playlistId, int userId) {
        return playlistDAO.delete(playlistId, userId);
    }
}