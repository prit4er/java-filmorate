package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserFilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String BASE_URL_USERS = "/users";
    private static final String BASE_URL_FILMS = "/films";

    // Пример данных для пользователей и фильмов
    private static final String USER1_JSON = "{\"id\": 1, \"email\": \"user1@example.com\", \"login\": \"user1\", \"name\": \"User One\", \"birthday\": \"1990-01-01\"}";
    private static final String USER2_JSON = "{\"id\": 2, \"email\": \"user2@example.com\", \"login\": \"user2\", \"name\": \"User Two\", \"birthday\": \"1992-01-01\"}";
    private static final String FILM1_JSON = "{\"id\": 1, \"name\": \"Film One\", \"description\": \"A great film\"}";

    @BeforeEach
    public void setup() throws Exception {
        // Создание пользователей
        mockMvc.perform(post(BASE_URL_USERS)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(USER1_JSON))
               .andExpect(status().isCreated());

        mockMvc.perform(post(BASE_URL_USERS)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(USER2_JSON))
               .andExpect(status().isCreated());

        // Создание фильма
        mockMvc.perform(post(BASE_URL_FILMS)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(FILM1_JSON))
               .andExpect(status().isCreated());
    }

    @Test
    public void testAddFriend() throws Exception {
        mockMvc.perform(put(BASE_URL_USERS + "/1/friends/2"))
               .andExpect(status().isOk());
    }

    @Test
    public void testAddFriendUserNotFound() throws Exception {
        mockMvc.perform(put(BASE_URL_USERS + "/999/friends/2"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.errorMessage").value("Пользователь с id = 999 не найден."));
    }

    @Test
    public void testRemoveFriend() throws Exception {
        // Добавляем в друзья
        mockMvc.perform(put(BASE_URL_USERS + "/1/friends/2"))
               .andExpect(status().isOk());

        // Удаляем из друзей
        mockMvc.perform(delete(BASE_URL_USERS + "/1/friends/2"))
               .andExpect(status().isOk());
    }

    @Test
    public void testRemoveFriendUserNotFound() throws Exception {
        mockMvc.perform(delete(BASE_URL_USERS + "/999/friends/2"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.errorMessage").value("Пользователь с id = 999 не найден."));
    }

    @Test
    public void testGetFriends() throws Exception {
        mockMvc.perform(put(BASE_URL_USERS + "/1/friends/2"))
               .andExpect(status().isOk());

        mockMvc.perform(get(BASE_URL_USERS + "/1/friends"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(2));
    }

    @Test
    public void testGetCommonFriends() throws Exception {
        mockMvc.perform(put(BASE_URL_USERS + "/1/friends/2"))
               .andExpect(status().isOk());

        mockMvc.perform(get(BASE_URL_USERS + "/1/friends/common/2"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(2));
    }

    @Test
    public void testLikeFilm() throws Exception {
        mockMvc.perform(put(BASE_URL_FILMS + "/1/like/1"))
               .andExpect(status().isOk());
    }

    @Test
    public void testLikeFilmUserNotFound() throws Exception {
        mockMvc.perform(put(BASE_URL_FILMS + "/1/like/999"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.errorMessage").value("Пользователь с id = 999 не найден."));
    }

    @Test
    public void testUnlikeFilm() throws Exception {
        mockMvc.perform(put(BASE_URL_FILMS + "/1/like/1"))
               .andExpect(status().isOk());

        mockMvc.perform(delete(BASE_URL_FILMS + "/1/like/1"))
               .andExpect(status().isOk());
    }

    @Test
    public void testGetPopularFilms() throws Exception {
        mockMvc.perform(get(BASE_URL_FILMS + "/popular?count=5"))
               .andExpect(status().isOk());
    }

    @Test
    public void testGetPopularFilmsDefaultCount() throws Exception {
        mockMvc.perform(get(BASE_URL_FILMS + "/popular"))
               .andExpect(status().isOk());
    }

    @Test
    public void testAddFriendValidationError() throws Exception {
        mockMvc.perform(put(BASE_URL_USERS + "/1/friends/invalid"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.errorMessage").value("Ошибка валидации"));
    }

    @Test
    public void testAddFriendInternalError() throws Exception {
        mockMvc.perform(put(BASE_URL_USERS + "/1/friends/2"))
               .andExpect(status().isOk());

        // Эмулируем ошибку сервера
        mockMvc.perform(put(BASE_URL_USERS + "/1/friends/2"))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.errorMessage").value("Произошла ошибка"));
    }
}