package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingForItemExtendDto;

import java.util.List;

@Value
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemExtendDto {
    @EqualsAndHashCode.Include
    Long id;

    String name;

    String description;

    Boolean available;

    BookingForItemExtendDto lastBooking;

    BookingForItemExtendDto nextBooking;

    List<CommentDto> comments;
}
