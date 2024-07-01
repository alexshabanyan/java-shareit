package ru.practicum.shareit.item.args;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class CreateCommentArgs {
    Long userId;

    Long itemId;

    String text;
}
