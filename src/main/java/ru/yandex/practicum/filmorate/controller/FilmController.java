package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public ResponseEntity<FilmDTO> addFilm(@RequestBody @Valid Film film) {
        Film createdFilm = filmService.addFilm(film);
        return ResponseEntity.status(HttpStatus.CREATED).body(FilmMapper.toFilmDTO(createdFilm));
    }

    @PutMapping
    public ResponseEntity<FilmDTO> updateFilm(@RequestBody @Valid Film film) {
        Film updatedFilm = filmService.updateFilm(film);
        return ResponseEntity.ok(FilmMapper.toFilmDTO(updatedFilm));
    }

    @GetMapping
    public ResponseEntity<List<FilmDTO>> getFilmsList() {
        return ResponseEntity.ok(FilmMapper.toFilmDTOList(filmService.getFilmsList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmDTO> getFilmById(@PathVariable int id) {
        return ResponseEntity.ok(FilmMapper.toFilmDTO(filmService.getFilmById(id)));
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlikeFilm(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<FilmDTO>> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return ResponseEntity.ok(FilmMapper.toFilmDTOList(filmService.getMostLikedFilms(count)));
    }
}
