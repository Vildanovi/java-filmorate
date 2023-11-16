package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserFriends {

    private int id;
    private int user1Id;
    private int user2Id;
    private int initiatorId;

    public UserFriends(int id, int user1Id, int user2Id, int initiatorId) {
        this.id = id;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.initiatorId = initiatorId;
    }
}