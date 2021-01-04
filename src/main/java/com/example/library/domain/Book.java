package com.example.library.domain;

import lombok.Data;

import java.util.List;

@Data
public class Book {
    private final long id;
    private final String title;
    private final String isbn;
    private final Genre genre;
    private final List<Author> authors;
}
