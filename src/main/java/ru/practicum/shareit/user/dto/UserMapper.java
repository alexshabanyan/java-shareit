package ru.practicum.shareit.user.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserMapper {
    public User toModel(UserDto userDto) {
        if (userDto == null) {
            throw new RuntimeException();
        }
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public UserDto toDto(User user) {
        if (user == null) {
            throw new RuntimeException();
        }
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User updateModel(User oldUser, User newUser) {
        if (oldUser == null || newUser == null) {
            throw new RuntimeException();
        }
        User.UserBuilder builder = oldUser.toBuilder();
        if (newUser.getName() != null) {
            builder.name(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            builder.email(newUser.getEmail());
        }
        return builder.build();
    }
}
