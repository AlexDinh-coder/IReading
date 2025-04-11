package com.example.iread.Model;

public class Genre {
    private String name;
    private boolean isNew;

    public Genre(String name, boolean isNew) {
        this.name = name;
        this.isNew = isNew;
    }

    public String getName() {
        return name;
    }

    public boolean isNew() {
        return isNew;
    }
}
