package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank(message = "имя не может быть пустым")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "емайл не может быть пустым")
    @Email(message = "неверный формат емайл")
    private String email;
}
