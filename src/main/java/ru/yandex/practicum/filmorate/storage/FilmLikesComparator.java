package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class FilmLikesComparator implements Comparator<Film> {
    @Override
    public int compare(Film film1, Film film2) {
        if (film1.getLikes().isEmpty() && film2.getLikes().isEmpty()) {
            return film1.getId().compareTo(film2.getId()); // Сортируем по ID, если лайков нет
        }
        if (film1.getLikes().isEmpty()) {
            return 1;
        }
        if (film2.getLikes().isEmpty()) {
            return -1;
        }
        // Основная сортировка по количеству лайков
        int likesComparison = Integer.compare(film2.getLikes().size(), film1.getLikes().size());
        if (likesComparison == 0) {
            // Вторичная сортировка по ID
            return film1.getId().compareTo(film2.getId());
        }
        return likesComparison;
    }
}