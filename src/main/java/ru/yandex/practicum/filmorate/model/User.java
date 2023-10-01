package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class User {

    private int id; // целочисленный идентификатор
    @NotNull @NotBlank @Email private String email; // электронная почта
    @NotNull @NotBlank private String login; // логин пользователя — login;
    private String name; // имя для отображения — name;
    private LocalDate birthday;// дата рождения — birthday
}
