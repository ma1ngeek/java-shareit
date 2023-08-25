package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    UserService service;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto user) {
        log.info("POST / user={}", user);
        return service.createUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto user, @PathVariable long userId) {
        log.info("PATCH / userId={}", userId);
        return service.updateUser(user, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable long userId) {
        log.info("DELETE / userId={}", userId);
        service.deleteUser(userId);
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable long id) {
        log.info("GET /{}", id);
        return service.getUser(id);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("GET /");
        return service.getAllUsers();
    }

}
