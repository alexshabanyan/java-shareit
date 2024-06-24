package ru.practicum.shareit.booking.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.args.CreateBookingArgs;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    CreateBookingArgs toCreateBookingArgs(CreateBookingDto createBookingDto, Long bookerId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Booking toModel(CreateBookingArgs args, Item item, User booker);

    BookingDto toDto(Booking booking);
}
