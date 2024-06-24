package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@Value
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemDto {
    @EqualsAndHashCode.Include
    @Null(groups = ValidationGroup.OnCreate.class)
    Long id;

    @NotNull(groups = ValidationGroup.OnCreate.class)
    @Size(min = 2, max = 30, message = "Длина названия должна быть в диапазоне 2-30 символов.")
    String name;

    @NotNull(groups = ValidationGroup.OnCreate.class)
    @Size(min = 2, max = 200, message = "Длина описания должна быть в диапазоне 2-200 символов.")
    String description;

    @NotNull(groups = ValidationGroup.OnCreate.class)
    Boolean available;

    Long requestId;
}
