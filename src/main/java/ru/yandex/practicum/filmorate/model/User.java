package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private int id; // целочисленный идентификатор
    @NotBlank(message = "email не может быть пустым")
    @Email(message = "Некорректный email")
    private String email; // электронная почта
    @NotBlank(message = "login не может быть пустым")
    @Pattern(regexp = "[\\S]{0,}", message = "login не должен содержать пробелы")
    private String login; // логин пользователя — login;
    private String name; // имя для отображения — name;
    @NotNull (message = "Дата рождения не может быть пустым")
    @PastOrPresent (message = "Дата рождения не может быть позже текущей")
    private LocalDate birthday;// дата рождения — birthday
    @JsonIgnore
    private Set<Integer> friends = new HashSet<>(); //id друзей
}
