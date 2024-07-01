package ru.practicum.shareit.item.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.practicum.shareit.utils.SqlHelper.*;

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
