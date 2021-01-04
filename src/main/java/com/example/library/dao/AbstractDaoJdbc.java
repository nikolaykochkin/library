package com.example.library.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractDaoJdbc<T> {

    protected final NamedParameterJdbcTemplate jdbc;
    private final RowMapper<T> rowMapper;
    private final ObjectMapper mapper = new ObjectMapper();
    private String findByIdQuery;
    private String findAllQuery;
    private String insertQuery;
    private String deleteQuery;

    public AbstractDaoJdbc(NamedParameterJdbcTemplate jdbc, RowMapper<T> rowMapper, String table, List<String> fields) {
        this.jdbc = jdbc;
        this.rowMapper = rowMapper;
        createQueries(table, fields);
    }

    protected void setFindByIdQuery(String findByIdQuery) {
        this.findByIdQuery = findByIdQuery;
    }

    protected void setFindAllQuery(String findAllQuery) {
        this.findAllQuery = findAllQuery;
    }

    protected void setInsertQuery(String insertQuery) {
        this.insertQuery = insertQuery;
    }

    protected void setDeleteQuery(String deleteQuery) {
        this.deleteQuery = deleteQuery;
    }

    private void createQueries(String table, List<String> fields) {
        findByIdQuery = String.format("SELECT * FROM %s WHERE ID = :id", table);
        findAllQuery = String.format("SELECT * FROM %s", table);
        deleteQuery = String.format("DELETE FROM %s WHERE ID = :id", table);
        insertQuery = createInsertQuery(table, fields);
    }

    private String createInsertQuery(String table, List<String> fields) {
        return "INSERT INTO " + table + " (" + String.join(", ", fields)
                + ") values (" + fields.stream()
                .map(field -> ":" + field)
                .collect(Collectors.joining(", "));
    }

    public T findById(long id) {
        return jdbc.queryForObject(
                findByIdQuery,
                Collections.singletonMap("id", id),
                rowMapper
        );
    }

    public List<T> findAll() {
        return jdbc.query(findAllQuery, rowMapper);
    }


    public void insert(T element) {
        jdbc.update(
                insertQuery,
                mapper.convertValue(element, new TypeReference<Map<String, Object>>() {
                })
        );
    }

    public void delete(T element) {
        jdbc.update(
                deleteQuery,
                mapper.convertValue(element, new TypeReference<Map<String, Object>>() {
                })
        );
    }

}
