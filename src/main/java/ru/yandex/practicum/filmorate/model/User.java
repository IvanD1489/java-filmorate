package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private long id;
    @NotNull
    @Email(message = "Почта не может быть пустой и должна содержать символ @")
    private String email;
    private String login;
    private String name;
    private String birthday;
    private Set<Long> friends = new HashSet<>();

}
