package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

import static ru.practicum.shareit.utils.SqlHelper.REQUEST_CREATED;
import static ru.practicum.shareit.utils.SqlHelper.REQUEST_DESCRIPTION;
import static ru.practicum.shareit.utils.SqlHelper.REQUEST_ID;
import static ru.practicum.shareit.utils.SqlHelper.REQUEST_USER_ID;
import static ru.practicum.shareit.utils.SqlHelper.SCHEMA;
import static ru.practicum.shareit.utils.SqlHelper.TABLE_REQUESTS;

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
