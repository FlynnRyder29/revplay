package com.revplay.service;

import com.revplay.dao.SongDAO;
import com.revplay.dao.FavoriteDAO;
import com.revplay.dao.HistoryDAO;
import com.revplay.model.Song;
import java.util.List;

public class SongService {
    private SongDAO songDAO = new SongDAO();
    private FavoriteDAO favoriteDAO = new FavoriteDAO();
    private HistoryDAO historyDAO = new HistoryDAO();

    public List<Song> search(String keyword) {
        return songDAO.search(keyword);
    }

    public List<Song> getByGenre(int genreId) {
        return songDAO.getByGenre(genreId);
    }

    public List<Song> getByArtist(int artistId) {
        return songDAO.getByArtist(artistId);
    }

    public List<Song> getByAlbum(int albumId) {
        return songDAO.getByAlbum(albumId);
    }

    public boolean addToFavorites(int userId, int songId) {
        return favoriteDAO.addFavorite(userId, songId);
    }

    public boolean removeFromFavorites(int userId, int songId) {
        return favoriteDAO.removeFavorite(userId, songId);
    }

    public List<Song> getFavorites(int userId) {
        return favoriteDAO.getFavorites(userId);
    }

    public List<Song> getRecentlyPlayed(int userId) {
        return historyDAO.getRecentlyPlayed(userId, 10);
    }

    public List<Song> getListeningHistory(int userId) {
        return historyDAO.getListeningHistory(userId);
    }

    public boolean updateSong(Song song) {
        return songDAO.update(song);
    }
}
