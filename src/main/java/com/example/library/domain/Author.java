package com.example.library.domain;

import lombok.Data;

@Data
public class Author {
    private final long id;
    private final String name;
    private final String surname;
}
