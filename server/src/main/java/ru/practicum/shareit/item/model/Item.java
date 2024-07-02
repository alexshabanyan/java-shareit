package ru.practicum.shareit.item.model;

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

import static ru.practicum.shareit.utils.SqlHelper.ITEM_AVAILABLE;
import static ru.practicum.shareit.utils.SqlHelper.ITEM_DESCRIPTION;
import static ru.practicum.shareit.utils.SqlHelper.ITEM_ID;
import static ru.practicum.shareit.utils.SqlHelper.ITEM_NAME;
import static ru.practicum.shareit.utils.SqlHelper.ITEM_OWNER_ID;
import static ru.practicum.shareit.utils.SqlHelper.ITEM_REQUEST_ID;
import static ru.practicum.shareit.utils.SqlHelper.SCHEMA;
import static ru.practicum.shareit.utils.SqlHelper.TABLE_ITEMS;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = TABLE_ITEMS, schema = SCHEMA)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ITEM_ID)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = ITEM_NAME, nullable = false)
    private String name;

    @Column(name = ITEM_DESCRIPTION, nullable = false)
    private String description;

    @Column(name = ITEM_AVAILABLE, nullable = false)
    private Boolean available;

    @Column(name = ITEM_OWNER_ID, nullable = false)
    private Long ownerId;

    @Column(name = ITEM_REQUEST_ID)
    private Long requestId;
}
