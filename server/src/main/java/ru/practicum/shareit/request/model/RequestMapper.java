package ru.practicum.shareit.request.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.args.CreateRequestArgs;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemInfoDto;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    CreateRequestArgs toCreateRequestArgs(RequestDto requestDto, Long userId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    Request toModel(CreateRequestArgs createRequestArgs);

    RequestDto toDto(Request request);

    RequestWithItemInfoDto toItemInfoDto(Request requests, List<ItemRequestDto> items);

    ItemRequestDto toItemRequestDto(Item item);

    default List<RequestWithItemInfoDto> toRequestWithItemInfoDto(List<Request> requests, Map<Long, List<Item>> items) {
        LinkedList<RequestWithItemInfoDto> dtos = new LinkedList<>();
        for (Request request : requests) {
            List<ItemRequestDto> itemRequestDtos;
            if (items.containsKey(request.getId())) {
                itemRequestDtos = items.get(request.getId()).stream().map(this::toItemRequestDto).collect(Collectors.toList());
            } else {
                itemRequestDtos = List.of();
            }
            dtos.add(toItemInfoDto(request, itemRequestDtos));
        }
        return dtos;
    }
}
