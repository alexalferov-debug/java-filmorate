package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FriendDTO;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.dto.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable int id) {
        User user = userService.getUser(id);
        List<User> friends = userService.getFriendsList(id);
        return new ResponseEntity<>(UserMapper.toUserDTO(user, friends), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid User user) {
        User createdUser = userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toUserDTO(createdUser));
    }

    @PutMapping
    public ResponseEntity<UserDTO> updateUser(@RequestBody @Valid User user) {
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(UserMapper.toUserDTO(updatedUser));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsersList() {
        List<User> users = userService.getUsersList();
        List<UserDTO> userDTOs = users.stream()
                .map(user -> {
                    List<User> friendUsers = userService.getFriendsList(user.getId());
                    return new UserDTO(user, friendUsers);
                })
                .toList();
        return ResponseEntity.ok(userDTOs);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }


    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<FriendDTO>> getFriends(@PathVariable int id) {
        return ResponseEntity.ok(UserMapper.toFriendDTOs(userService.getFriendsList(id)));
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public ResponseEntity<List<FriendDTO>> getFriendsCommon(@PathVariable int id, @PathVariable int otherId) {
        return ResponseEntity.ok(UserMapper.toFriendDTOs(userService.getFriendsIntersect(id, otherId)));
    }
}
