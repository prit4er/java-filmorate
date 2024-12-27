package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingRepository;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRatingRowMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaRatingRepository.class, MpaRatingRowMapper.class})
class MpaRatingRepositoryTest {

    private final MpaRatingRepository mpaRatingRepository;

    @Test
    void shouldReturnListOfRatings() {
        List<MpaRating> ratings = mpaRatingRepository.findAll();

        assertThat(ratings).hasSize(5)
                           .extracting(MpaRating::getName)
                           .containsExactlyInAnyOrder("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    void shouldReturnRatingByIdWhenExists() {
        Optional<MpaRating> rating = mpaRatingRepository.findById(1L);

        assertThat(rating).isPresent()
                          .hasValueSatisfying(r -> {
                              assertThat(r.getId()).isEqualTo(1L);
                              assertThat(r.getName()).isEqualTo("G");
                          });
    }

    @Test
    void shouldReturnEmptyOptionalWhenIdDoesNotExist() {
        Optional<MpaRating> rating = mpaRatingRepository.findById(999L);

        assertThat(rating).isEmpty();
    }
}