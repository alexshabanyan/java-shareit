package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDto {
    @EqualsAndHashCode.Include
    Long id;

    String name;

    String email;
}
