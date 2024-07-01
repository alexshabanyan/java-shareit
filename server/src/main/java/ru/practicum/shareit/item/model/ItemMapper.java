package ru.practicum.shareit.item.model;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.BookingForItemExtendDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.args.CreateCommentArgs;
import ru.practicum.shareit.item.args.CreateItemArgs;
import ru.practicum.shareit.item.args.UpdateItemArgs;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithExtendInfoDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    CreateItemArgs toCreateItemArgs(ItemDto itemDto, Long ownerId);

    UpdateItemArgs toUpdateItemArgs(ItemDto itemDto);

    @Mapping(target = "id", ignore = true)
    Item toModel(CreateItemArgs args);

    ItemDto toDto(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "requestId", ignore = true)
    void updateModel(@MappingTarget Item item, UpdateItemArgs updater);

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
    CreateCommentArgs toCreateCommentArgs(CommentDto commentDto, Long userId, Long itemId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "author", source = "author")
    Comment toModel(CreateCommentArgs args, User author);

    @Mapping(target = "itemId", source = "itemId")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "id", source = "commentDto.id")
    Comment toModel(CommentDto commentDto, User author, Long itemId);

    @Mapping(target = "authorName", source = "comment.author.name")
    CommentDto toDto(Comment comment);
}
