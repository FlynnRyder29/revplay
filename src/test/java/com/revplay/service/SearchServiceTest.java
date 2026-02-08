package com.revplay.service;

import com.revplay.model.Song;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SearchServiceTest {
    private SongService songService = new SongService();

    @Test
    void testPartialKeywordSearch() {
        // Search for 'Rev' to find 'Revival'
        List<Song> results = songService.search("Att");
        assertTrue(results.size() > 0);
    }

    @Test
    void testCaseInsensitivity() {
        List<Song> lower = songService.search("rock");
        List<Song> upper = songService.search("ROCK");
        assertEquals(lower.size(), upper.size(), "Search results should be case insensitive");
    }

    @Test
    void testNoResultsSearch() {
        List<Song> results = songService.search("XYZ_UNLIKELY_KEYWORD_123");
        assertTrue(results.isEmpty(), "Zero matches should return an empty list, not null");
    }
}
