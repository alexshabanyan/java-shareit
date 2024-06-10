package ru.practicum.shareit.item.dto;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.dto.BookingForItemExtendDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemDto toDto(Item item);

    @Mapping(target = "requestId", ignore = true)
    Item toModel(ItemDto itemDto, Long ownerId);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "requestId", ignore = true)
    void updateModel(@MappingTarget Item item, ItemDto updaterItemDto);

    @Mapping(target = "id", source = "item.id")
    ItemExtendDto toItemBookingInfoDto(Item item, List<CommentDto> comments,
                                       BookingForItemExtendDto lastBooking, BookingForItemExtendDto nextBooking);
}
