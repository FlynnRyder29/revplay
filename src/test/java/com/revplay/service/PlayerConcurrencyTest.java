package com.revplay.service;

import com.revplay.model.Song;
import org.junit.jupiter.api.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerConcurrencyTest {
    private PlayerService playerService = new PlayerService();

    @Test
    void testSimultaneousQueueAdditions() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int songId = i;
            executor.submit(() -> {
                Song s = new Song();
                s.setSongId(songId);
                s.setTitle("Simulated Song " + songId);
                playerService.addToQueue(s);
                latch.countDown();
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        // Verify queue size matches total additions
        assertEquals(threadCount, playerService.getQueueSize(), "Concurrent queue additions failed");
    }
}
