package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;

import static ru.practicum.shareit.utils.SqlHelper.*;

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
