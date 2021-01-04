package com.example.library.dao;

import com.example.library.domain.Author;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AuthorDaoJdbc extends AbstractDaoJdbc<Author> {

    public AuthorDaoJdbc(NamedParameterJdbcTemplate jdbc) {
        super(jdbc, new AuthorMapper(), "authors", List.of("id", "name", "surname"));
    }

    public static class AuthorMapper implements RowMapper<Author> {
        @Override
        public Author mapRow(ResultSet resultSet, int i) throws SQLException {
            return new Author(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("surname")
            );
        }
    }
}
