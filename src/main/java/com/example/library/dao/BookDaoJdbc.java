package com.example.library.dao;

import com.example.library.domain.Author;
import com.example.library.domain.Book;
import com.example.library.domain.Genre;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BookDaoJdbc extends AbstractDaoJdbc<Book> {

    public BookDaoJdbc(NamedParameterJdbcTemplate jdbc) {
        super(jdbc, new BookMapper(jdbc), "books", Collections.emptyList());
        setQueries();
    }

    private void setQueries() {
        setFindAllQuery(createFindAllQuery());
        setFindByIdQuery(createFindAllQuery() + " where b.id = :id");
    }

    private String createFindAllQuery() {
        return "select " +
                "b.id id, " +
                "b.title title, " +
                "b.isbn isbn, " +
                "b.genre_id genre_id, " +
                "g.name genre_name " +
                "from books as b " +
                "left join genres as g " +
                "on b.genre_id = g.id";
    }

    @Override
    public void insert(Book element) {
        String bookSQL = "insert into books (id, title, isbn, genre_id) values (:id, :title, :isbn, :genre_id)";
        Map<String, Object> params = new HashMap<>();
        params.put("id", element.getId());
        params.put("title", element.getTitle());
        params.put("isbn", element.getIsbn());
        params.put("genre_id", element.getGenre().getId());
        jdbc.update(bookSQL, params);

        String bookAuthorsSQL = "insert into book_authors (book_id, author_id) values (:book_id, :author_id)";
        element.getAuthors().forEach(author ->
                jdbc.update(bookAuthorsSQL,
                        Map.of("book_id", element.getId(), "author_id", author.getId())));
    }

    @Override
    public void delete(Book element) {
        Map<String, Object> params = Collections.singletonMap("id", element.getId());
        jdbc.update("delete from book_authors where book_id = :id", params);
        jdbc.update("delete from books where id = :id", params);
    }

    public static class BookMapper implements RowMapper<Book> {

        private final NamedParameterJdbcTemplate jdbc;

        public BookMapper(NamedParameterJdbcTemplate jdbc) {
            this.jdbc = jdbc;
        }

        @Override
        public Book mapRow(ResultSet resultSet, int i) throws SQLException {
            long id = resultSet.getInt("id");
            String title = resultSet.getString("title");
            String isbn = resultSet.getString("isbn");

            Genre genre = new Genre(
                    resultSet.getInt("genre_id"),
                    resultSet.getString("genre_name")
            );

            String SQL =
                    "select " +
                            "a.*" +
                            "from book_authors as ba" +
                            "left join authors as a" +
                            "on ba.author_id = a.id" +
                            "where ba.book_id = :id";

            List<Author> authors = jdbc.query(SQL,
                    Collections.singletonMap("id", id), new AuthorDaoJdbc.AuthorMapper());

            return new Book(id, title, isbn, genre, authors);
        }
    }
}
