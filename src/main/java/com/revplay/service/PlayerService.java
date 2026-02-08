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
    private volatile boolean isPlaying = false; // Thread-safe flag
    private volatile boolean silentMode = false; // To suppress progress bar
    private boolean repeat = false;
    private int currentUserId;
    private Thread playerThread;

    public void setCurrentUser(int userId) {
        this.currentUserId = userId;
    }

    public void setSilentMode(boolean silent) {
        this.silentMode = silent;
    }

    public boolean isPlaying() { return isPlaying; }

    public boolean hasCurrent() { return !queue.isEmpty() && currentIndex < queue.size(); }

    public void addToQueue(Song song) {
        queue.add(song);
        System.out.println("Added to queue: " + song.getTitle());
    }

    public void play() {
        if (queue.isEmpty()) {
            System.out.println("Queue is empty!");
            return;
        }

        // Stop existing thread if running
        stopThread();

        isPlaying = true;
        Song current = queue.get(currentIndex);
        System.out.println("\n▶ Now Playing: " + current.getTitle() + " - " + current.getArtistName());

        // Record play stats
        songDAO.incrementPlayCount(current.getSongId());
        historyDAO.recordPlay(currentUserId, current.getSongId());

        // Start simulation thread
        playerThread = new Thread(() -> startSimulation(current));
        playerThread.start();
    }

    private void startSimulation(Song song) {
        int duration = song.getDurationSeconds();
        try {
            for (int i = 0; i <= duration; i++) {
                if (!isPlaying) break;

                if (!silentMode) {
                    printProgressBar(i, duration);
                }
                Thread.sleep(1000); // Wait 1 real-time second
            }

            // If song finished naturally (not paused/stopped)
            if (isPlaying && (playerThread != null && !playerThread.isInterrupted())) {
                if (!silentMode) System.out.println("\nSong finished.");
                next(); // Auto-switch
            }
        } catch (InterruptedException e) {
            // Thread interrupted
        }
    }

    private void printProgressBar(int current, int total) {
        // ... (same as before)
        int barLength = 30;
        int progress = (int) ((double) current / total * barLength);

        StringBuilder bar = new StringBuilder("\r[");
        for (int i = 0; i < barLength; i++) {
            if (i < progress) bar.append("=");
            else if (i == progress) bar.append(">");
            else bar.append(" ");
        }
        bar.append("] " + formatTime(current) + " / " + formatTime(total));
        System.out.print(bar.toString());
    }

    private String formatTime(int seconds) {
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    private void stopThread() {
        if (playerThread != null && playerThread.isAlive()) {
            if (playerThread != Thread.currentThread()) {
                playerThread.interrupt();
            }
        }
    }

    public void pause() {
        if (isPlaying) {
            isPlaying = false;
            stopThread();
            System.out.println("\n⏸ Paused");
        } else {
            System.out.println("Already paused");
        }
    }

    public void resume() {
        if (!isPlaying && !queue.isEmpty()) {
            isPlaying = true; // Set flag back to true
            // Note: In a real app we'd resume from last position.
            // Here we restart the current song for simplicity or we could store 'lastPosition'.
            // For this bug fix, we just restart the thread.
            Song current = queue.get(currentIndex);
            System.out.println("\n▶ Resuming: " + current.getTitle());
            playerThread = new Thread(() -> startSimulation(current));
            playerThread.start();
        }
    }

    // ... next/prev ...

    public void next() {
        stopThread(); // Ensure current song stops

        if (queue.isEmpty() || currentIndex >= queue.size() - 1) {
            if (!repeat) {
                System.out.println("\nAutoplay: Fetching random song...");
                Song randomSong = songDAO.getRandomSong();
                if (randomSong != null) {
                    addToQueue(randomSong);
                    currentIndex = queue.size() - 1;
                    play();
                } else {
                    System.out.println("No more songs available!");
                    isPlaying = false;
                }
            } else {
                currentIndex = 0;
                play();
            }
        } else {
            currentIndex++;
            play();
        }
    }

    public void previous() {
        stopThread();
        if (queue.isEmpty()) return;
        if (currentIndex > 0) {
            currentIndex--;
            play();
        } else {
            System.out.println("\nStart of queue");
            play(); // Restart current
        }
    }

    public void toggleRepeat() {
        repeat = !repeat;
        System.out.println("\nRepeat: " + (repeat ? "ON" : "OFF"));
    }

    public void showQueue() {
        System.out.println("\n--- Current Queue ---");
        for (int i = 0; i < queue.size(); i++) {
            String marker = (i == currentIndex) ? " ▶ " : "   ";
            System.out.println(marker + (i+1) + ". " + queue.get(i).getTitle());
        }
    }

    public void clearQueue() {
        stopThread();
        queue.clear();
        currentIndex = 0;
        isPlaying = false;
        System.out.println("\nQueue cleared");
    }
}
