package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CreateBookingDtoJsonTest {
    private final JacksonTester<CreateBookingDto> json;

    @Test
    void testUserDto() throws Exception {
        final int itemId = 1;
        final LocalDateTime start = LocalDateTime.of(2024, 6, 17, 13, 57, 36);
        final LocalDateTime end = start.plusDays(1);

        CreateBookingDto dto = new CreateBookingDto(
                (long) itemId,
                start,
                end);

        JsonContent<CreateBookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-06-17T13:57:36");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-06-18T13:57:36");
    }
}