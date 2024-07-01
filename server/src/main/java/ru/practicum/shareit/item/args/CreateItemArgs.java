package ru.practicum.shareit.item.args;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class CreateItemArgs {
    Long ownerId;

    String name;

    String description;

    Boolean available;

    Long requestId;
}
