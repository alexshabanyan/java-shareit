package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.user.model.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

import static ru.practicum.shareit.utils.SqlHelper.COMMENT_AUTHOR_ID;
import static ru.practicum.shareit.utils.SqlHelper.COMMENT_CREATED;
import static ru.practicum.shareit.utils.SqlHelper.COMMENT_ID;
import static ru.practicum.shareit.utils.SqlHelper.COMMENT_ITEM_ID;
import static ru.practicum.shareit.utils.SqlHelper.COMMENT_TEXT;
import static ru.practicum.shareit.utils.SqlHelper.SCHEMA;
import static ru.practicum.shareit.utils.SqlHelper.TABLE_COMMENTS;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = TABLE_COMMENTS, schema = SCHEMA)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COMMENT_ID)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = COMMENT_ITEM_ID, nullable = false)
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = COMMENT_AUTHOR_ID)
    private User author;

    @Column(name = COMMENT_TEXT, nullable = false)
    private String text;

    @CreationTimestamp
    @Column(name = COMMENT_CREATED, nullable = false)
    private LocalDateTime created;
}
