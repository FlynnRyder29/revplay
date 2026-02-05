package com.revplay.service;
import com.revplay.dao.SongDAO;
import com.revplay.dao.HistoryDAO;
import com.revplay.model.Song;
import java.util.List;
import java.util.ArrayList;
public class PlayerService {
    private SongDAO songDAO = new SongDAO();
    private HistoryDAO historyDAO = new HistoryDAO();

    private List<Song> queue = new ArrayList<>();
    private int currentIndex = 0;
    private boolean isPlaying = false;
    private boolean repeat = false;
    private int currentUserId;

    public void setCurrentUser(int userId) {
        this.currentUserId = userId;
    }

    public void addToQueue(Song song) {
        queue.add(song);
        System.out.println("Added to queue: " + song.getTitle());
    }

    public void play() {
        if (queue.isEmpty()) {
            System.out.println("Queue is empty!");
            return;
        }
        isPlaying = true;
        Song current = queue.get(currentIndex);
        System.out.println("\n▶ Now Playing: " + current.getTitle());
        System.out.println("  Artist: " + current.getArtistName());
        System.out.println("  Duration: " + current.getFormattedDuration());
        System.out.println("  [=====>----------] 2:15 / " + current.getFormattedDuration());

        songDAO.incrementPlayCount(current.getSongId());
        historyDAO.recordPlay(currentUserId, current.getSongId());
    }

    public void pause() {
        if (isPlaying) {
            isPlaying = false;
            System.out.println("⏸ Paused");
        } else {
            System.out.println("Already paused");
        }
    }

    public void resume() {
        if (!isPlaying && !queue.isEmpty()) {
            isPlaying = true;
            System.out.println("▶ Resumed: " + queue.get(currentIndex).getTitle());
        }
    }

    public void next() {
        if (queue.isEmpty()) return;
        if (currentIndex < queue.size() - 1) {
            currentIndex++;
            play();
        } else if (repeat) {
            currentIndex = 0;
            play();
        } else {
            System.out.println("End of queue");
        }
    }

    public void previous() {
        if (queue.isEmpty()) return;
        if (currentIndex > 0) {
            currentIndex--;
            play();
        } else {
            System.out.println("Start of queue");
        }
    }

    public void toggleRepeat() {
        repeat = !repeat;
        System.out.println("Repeat: " + (repeat ? "ON" : "OFF"));
    }

    public void showQueue() {
        System.out.println("\n--- Current Queue ---");
        for (int i = 0; i < queue.size(); i++) {
            String marker = (i == currentIndex) ? " ▶ " : "   ";
            System.out.println(marker + (i+1) + ". " + queue.get(i).getTitle());
        }
    }

    public void clearQueue() {
        queue.clear();
        currentIndex = 0;
        isPlaying = false;
        System.out.println("Queue cleared");
    }
}