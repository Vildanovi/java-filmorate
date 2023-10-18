package ru.yandex.practicum.filmorate.service;

// Создайте UserService, который будет отвечать за такие операции
// с пользователями, как добавление в друзья, удаление из друзей,
// вывод списка общих друзей. Пока пользователям не надо одобрять
// заявки в друзья — добавляем сразу. То есть если Лена стала
// другом Саши, то это значит, что Саша теперь друг Лены.

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Service
public class UserService {


    public void addToFriend(User user) {

    }

    public void deleteFromFriend(User user) {

    }

    public List<User> getCommonFriends() {
        return null;
    }


}
