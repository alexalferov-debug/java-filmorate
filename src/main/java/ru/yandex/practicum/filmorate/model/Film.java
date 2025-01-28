package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
@EqualsAndHashCode
public class Film {
    private int id;

    @NotNull(message = "Название не может быть пустым")
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    @NotBlank
    private String description;

    @ValidReleaseDate()
    private LocalDate releaseDate;

    private Set<Genre> genres;

    private Mpa mpa;

    @Positive(message = "продолжительность фильма должна быть положительным числом")
    private int duration;

    Set<Integer> likedUsers = new HashSet<>();

    public boolean addLikedUser(int userId) {
        return likedUsers.add(userId);
    }

    public boolean removeLikedUser(int userId) {
        return likedUsers.remove(userId);
    }

    public int getLikesCount() {
        return likedUsers.size();
    }
}
