package com.example.library.dao;

import com.example.library.domain.Genre;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class GenreDaoJdbc extends AbstractDaoJdbc<Genre> {
    public GenreDaoJdbc(NamedParameterJdbcTemplate jdbc) {
        super(jdbc, new GenreMapper(), "genres", List.of("id", "name"));
    }

    public static class GenreMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet resultSet, int i) throws SQLException {
            return new Genre(
                    resultSet.getInt("id"),
                    resultSet.getString("name")
            );
        }
    }
}
