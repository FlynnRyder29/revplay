package com.revplay.service;

import com.revplay.model.Song;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerBoundaryTest {
    private PlayerService playerService = new PlayerService();

    @Test
    void testDurationExtremes() {
        Song s1 = new Song();
        s1.setDurationSeconds(0); // Zero duration
        playerService.addToQueue(s1);
        playerService.play();
        assertFalse(playerService.isPlaying(), "Player should stop immediately if duration is 0");

        Song s2 = new Song();
        s2.setDurationSeconds(Integer.MAX_VALUE); // Extremely large
        playerService.addToQueue(s2);
        // Verify no overflow in time formatting or loop logic
        assertDoesNotThrow(() -> playerService.play());
    }

    @Test
    void testNextOnEmptyQueue() {
        playerService.clearQueue();
        assertDoesNotThrow(() -> playerService.next());
        assertFalse(playerService.hasCurrent());
    }
}
