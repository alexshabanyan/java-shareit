package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = userMapper.toModel(userDto);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto get(Long userId) {
        return userRepository.findById(userId, UserDto.class).orElseThrow(() -> new NotFoundException(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<UserDto> getAll() {
        return userRepository.findBy(UserDto.class);
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return userMapper.toDto(userRepository.save(userMapper.updateModel(user.get(), userDto)));
        } else {
            throw new NotFoundException(userDto.getId());
        }
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
