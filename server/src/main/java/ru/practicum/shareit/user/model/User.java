package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import static ru.practicum.shareit.utils.SqlHelper.SCHEMA;
import static ru.practicum.shareit.utils.SqlHelper.TABLE_USERS;
import static ru.practicum.shareit.utils.SqlHelper.USER_EMAIL;
import static ru.practicum.shareit.utils.SqlHelper.USER_ID;
import static ru.practicum.shareit.utils.SqlHelper.USER_NAME;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = TABLE_USERS, schema = SCHEMA)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = USER_ID)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = USER_NAME, nullable = false)
    private String name;

    @Column(name = USER_EMAIL, nullable = false, unique = true)
    private String email;
}
