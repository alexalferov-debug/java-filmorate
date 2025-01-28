package ru.yandex.practicum.filmorate.storage.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dao.mapper.GenreMapper;

import java.util.List;

@Repository
public class DbGenreStorage implements GenreStorage {

    private final JdbcTemplate jdbc;
    private static final Logger logger = LoggerFactory.getLogger(DbGenreStorage.class);

    @Autowired
    public DbGenreStorage(final JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Genre getGenre(int genreId) {
        String getGenreQuery = "select * from genre where id = ?";
        List<Genre> genreList = jdbc.query(getGenreQuery, new GenreMapper(), genreId);
        if (genreList.isEmpty()) {
            logger.warn("No genre found for genre id {}", genreId);
            throw new NotFoundException("Genre not found with id " + genreId);
        }
        return genreList.getFirst();
    }

    @Override
    public List<Genre> getAllGenres() {
        String getGenreQuery = "select * from genre ORDER BY id";
        List<Genre> genreList = jdbc.query(getGenreQuery, new GenreMapper());
        if (genreList.isEmpty()) {
            logger.warn("Genre list is empty");
            throw new NotFoundException("Genre list is empty");
        }
        return genreList;
    }
}
