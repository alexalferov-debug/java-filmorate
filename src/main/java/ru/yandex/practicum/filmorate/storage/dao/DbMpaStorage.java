package ru.yandex.practicum.filmorate.storage.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.dao.mapper.MpaMapper;

import java.util.List;

@Repository
public class DbMpaStorage implements MpaStorage {
    private final JdbcTemplate jdbc;
    private static final Logger logger = LoggerFactory.getLogger(DbMpaStorage.class);

    @Autowired
    public DbMpaStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Mpa getMpa(int id) {
        String getMpaQuery = "select * from RATING" +
                " where id = ?";
        List<Mpa> mpa = jdbc.query(getMpaQuery, new MpaMapper(), id);
        if (mpa.isEmpty()) {
            logger.warn("MPA с id {} не найдено в базе данных.", id);
            throw new NotFoundException("MPA с id " + id + " не найдено в базе данных.");
        }
        return mpa.getFirst();
    }

    @Override
    public List<Mpa> getMpaList() {
        String getMpaQuery = "select * from RATING";
        List<Mpa> mpa = jdbc.query(getMpaQuery, new MpaMapper());
        if (mpa.isEmpty()) {
            logger.warn("Список MPA пуст");
            throw new NotFoundException("Список MPA пуст");
        }
        return mpa;
    }
}
