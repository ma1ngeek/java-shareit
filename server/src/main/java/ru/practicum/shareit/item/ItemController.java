package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Constants;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoPost;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPost;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    @Autowired
    private ItemService service;
    @Autowired
    private UserService userService;

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDtoPost dto,
                           @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("POST /items {} userId={}", dto, userId);

        UserDto user = userService.getUser(userId);

        return service.createItem(dto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDtoPost dto,
                              @PathVariable long itemId,
                              @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("PATCH /items/{}/", itemId);
        UserDto user = userService.getUser(userId);
        return service.updateItem(dto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId,
                           @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("DELETE /items/{}/", itemId);
        UserDto user = userService.getUser(userId);
        service.deleteItem(itemId, userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable long id,
                               @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("GET /items/{}/", id);
        return service.getItem(id, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        log.info("GET /items/ userId={}", userId);
        UserDto user = userService.getUser(userId);
        return service.getItemsByOwner(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestParam(defaultValue = "0") int from,
                                @RequestParam(defaultValue = "10") int size) {
        log.info("GET /items/search");
        return service.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDtoPost text,
                                    @PathVariable Long itemId,
                                    @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("POST /items/{}/comment {}", itemId, text);
        return service.createComment(text, itemId, userId);
    }
}
