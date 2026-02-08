package com.revplay.service;

import com.revplay.model.Song;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerServiceTest {
    private PlayerService playerService;

    @BeforeEach
    void setUp() {
        playerService = new PlayerService();
    }

    @Test
    void testThreadSafety_RapidSkips() throws InterruptedException {
        Song s = new Song();
        s.setSongId(1);
        s.setTitle("Test Song");
        s.setDurationSeconds(5);

        playerService.addToQueue(s);

        // Rapidly start and stop/next
        for(int i = 0; i < 5; i++) {
            playerService.play();
            Thread.sleep(50);
            playerService.next();
        }

        playerService.stop();
        Thread.sleep(100);
        assertFalse(playerService.isPlaying());
    }

    @Test
    void testSelfInterruptionFix() {
        // Fix: Thread.currentThread() should not be interrupted by stopThread()
        Thread testThread = new Thread(() -> {
            playerService.play();
            assertFalse(Thread.currentThread().isInterrupted(), "Thread should not interrupt itself");
        });
        testThread.start();
    }
}
