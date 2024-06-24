package ru.practicum.shareit.request.args;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class CreateRequestArgs {
    Long userId;

    String description;
}
