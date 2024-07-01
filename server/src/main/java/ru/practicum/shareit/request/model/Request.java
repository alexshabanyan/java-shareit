package ru.practicum.shareit.request.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.practicum.shareit.utils.SqlHelper.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = TABLE_REQUESTS, schema = SCHEMA)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = REQUEST_ID)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = REQUEST_DESCRIPTION)
    private String description;

    @CreationTimestamp
    @Column(name = REQUEST_CREATED)
    private LocalDateTime created;

    @Column(name = REQUEST_USER_ID)
    private Long userId;
}
