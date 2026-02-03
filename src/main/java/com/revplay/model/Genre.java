package com.revplay.model;
import java.io.Serial;
import java.io.Serializable;
public class Genre implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int genreId;
    private String name;

    public Genre() {}

    public Genre(String name) {
        this.name = name;
    }

    public int getGenreId() { return genreId; }
    public void setGenreId(int genreId) { this.genreId = genreId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}