package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Value
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RequestDto {
    @EqualsAndHashCode.Include
    @Null
    Long id;

    @NotNull
    @Size(min = 2, max = 200, message = "Длина описания должна быть в диапазоне 2-200 символов.")
    String description;

    @Null
    LocalDateTime created;
}
