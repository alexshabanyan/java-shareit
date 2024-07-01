package ru.practicum.shareit.item.model;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.dto.BookingForItemExtendDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithExtendInfoDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(target = "id", ignore = true)
    Item toModel(ItemDto itemDto, Long ownerId);

    ItemDto toDto(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "requestId", ignore = true)
    void updateModel(@MappingTarget Item item, ItemDto itemDto);

    @Mapping(target = "itemId", source = "booking.item.id")
    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingForItemExtendDto toItemExtendDto(Booking booking);

    default List<ItemWithExtendInfoDto> toExtendInfoDto(List<Item> items, Map<Long, List<Comment>> commentMapping,
                                                        Map<Long, List<Booking>> lastBookingMapping, Map<Long, List<Booking>> nextBookingMapping) {
        LinkedList<ItemWithExtendInfoDto> dtos = new LinkedList<>();
        for (Item item : items) {
            ItemWithExtendInfoDto.ItemWithExtendInfoDtoBuilder itemInfo = ItemWithExtendInfoDto.builder();
            itemInfo.id(item.getId());
            itemInfo.name(item.getName());
            itemInfo.description(item.getDescription());
            itemInfo.available(item.getAvailable());
            BookingForItemExtendDto lastBooking = null;
            if (lastBookingMapping != null && lastBookingMapping.containsKey(item.getId())) {
                Optional<Booking> booking = lastBookingMapping.get(item.getId()).stream().findFirst();
                if (booking.isPresent()) {
                    lastBooking = toItemExtendDto(booking.get());
                }
            }
            itemInfo.lastBooking(lastBooking);

            BookingForItemExtendDto nextBooking = null;
            if (nextBookingMapping != null && nextBookingMapping.containsKey(item.getId())) {
                Optional<Booking> booking = nextBookingMapping.get(item.getId()).stream().findFirst();
                if (booking.isPresent()) {
                    nextBooking = toItemExtendDto(booking.get());
                }
            }
            itemInfo.nextBooking(nextBooking);

            List<CommentDto> commentList = new ArrayList<>();
            if (commentMapping != null && commentMapping.containsKey(item.getId())) {
                commentList = commentMapping.get(item.getId()).stream().map(this::toDto).collect(Collectors.toList());
            }
            itemInfo.comments(commentList);
            dtos.add(itemInfo.build());
        }
        return dtos;
    }

    @Mapping(target = "itemId", source = "itemId")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "id", source = "commentDto.id")
    Comment toModel(CommentDto commentDto, User author, Long itemId);

    @Mapping(target = "authorName", source = "comment.author.name")
    CommentDto toDto(Comment comment);
}
