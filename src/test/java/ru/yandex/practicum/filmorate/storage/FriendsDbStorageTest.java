package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;
import java.time.LocalDate;
import java.util.Set;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendsDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void addFriends() {
        User user1 = User.builder()
                .login("user1")
                .name("Nick1")
                .email("mail1@mail.ru")
                .birthday(LocalDate.of(1980,8,20))
                .build();
        User user2 = User.builder()
                .login("user2")
                .name("Nick2")
                .email("mail2@mail.ru")
                .birthday(LocalDate.of(1982,8,20))
                .build();
        FriendsDbStorage friendsDbStorage = new FriendsDbStorage(jdbcTemplate);
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);

        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);

        friendsDbStorage.addToFriend(user1.getId(), user2.getId());
        Set<Integer> userFriend = friendsDbStorage.getUserFriends(user2.getId());
        assertThat(userFriend.size())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(1);
    }
}
