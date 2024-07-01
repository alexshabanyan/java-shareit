package ru.practicum.shareit.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.time.LocalDateTime;
import java.util.stream.Stream;

class ValidBookingIntervalValidatorTest {

    private ValidBookingIntervalValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ValidBookingIntervalValidator();
    }

    @ParameterizedTest
    @MethodSource("argumentsForValidator")
    void shouldPassCorrectDtos(CreateBookingDto dto, boolean isValid) {
        assert isValid == validator.isValid(dto, null);
    }

    private static Stream<Arguments> argumentsForValidator() {
        return Stream.of(
                Arguments.of(new CreateBookingDto(1L, LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusDays(1)),
                        true),
                Arguments.of(new CreateBookingDto(1L, null, LocalDateTime.now().plusDays(1)),
                        false),
                Arguments.of(new CreateBookingDto(1L, LocalDateTime.now().plusMinutes(5), null),
                        false),
                Arguments.of(new CreateBookingDto(1L, LocalDateTime.now().plusMinutes(5), LocalDateTime.now().minusDays(1)),
                        false),
                Arguments.of(new CreateBookingDto(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1)),
                        false)
        );
    }
}