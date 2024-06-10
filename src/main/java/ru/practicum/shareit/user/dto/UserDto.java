package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDto {
    @EqualsAndHashCode.Include
    Long id;

    @NotNull(groups = ValidationGroup.OnCreate.class)
    @Size(min = 2, max = 30, message = "Длина имени должно быть в диапазоне 2-30 символов.")
    String name;

    @NotNull(groups = ValidationGroup.OnCreate.class)
    @Size(min = 1, message = "Неверный формат почты")
    @Email(message = "Неверный формат почты")
    String email;
}
