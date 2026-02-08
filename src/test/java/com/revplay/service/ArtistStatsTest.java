package com.revplay.service;

import com.revplay.dao.SongDAO;
import com.revplay.model.Song;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class ArtistStatsTest {
    private SongDAO songDAO = new SongDAO();

    @Test
    void testPlayCountIncrement() {
        // To avoid ORA-02291, we create parent data
        String unique = "stats_" + System.currentTimeMillis();

        // Assume Artist ID 1 exists (from initial seed), if not we create:
        // For simplicity in this test, we try to use ID 1 but fail gracefully
        Song s = new Song(1, unique + "_Song", 120);
        s.setGenreId(1);
        Song saved = songDAO.create(s);

        if (saved != null) {
            int sid = saved.getSongId();
            int initialCount = saved.getPlayCount();

            // Simulate play
            songDAO.incrementPlayCount(sid);

            Song updated = songDAO.findById(sid);
            assertNotNull(updated);
            assertEquals(initialCount + 1, updated.getPlayCount(), "Play count did not increment in DB");
        } else {
            fail("Failed to create test song for stats. Check if Artist ID 1 and Genre ID 1 exist.");
        }
    }

    @Test
    void testStatsCleanupOnDelete() {
        // Validation: Logic should ensure orphaned stats don't exist
    }
}
