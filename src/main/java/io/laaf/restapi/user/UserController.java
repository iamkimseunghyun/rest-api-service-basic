package io.laaf.restapi.user;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserController {
    private UserDaoService service;

    public UserController(UserDaoService service) {
        this.service = service;
    }

    @GetMapping("/users")
    public List<User> retrieveAllUsers() {
        List<User> users = service.findAll();
        return users;
    }

    // GET /users/1 or /users/10 -> String
    @GetMapping("/users/{id}")
    public ResponseEntity<EntityModel<User>> retrieveUser(@PathVariable int id) {
        User user = service.findOne(id);
        if (user == null) {
            throw new UserNotFoundException(String.format("ID[%s] not found", id));
        }

        // HATEOAS
        EntityModel<User> model = EntityModel.of(user);
        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        model.add(linkTo.withRel("all-users"));

        return ResponseEntity.ok(model);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User savedUser = service.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand((savedUser.getId()))
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable int id) {
        User user = service.deleteById(id);

        if (user == null) {
            throw new UserNotFoundException(String.format("ID[%s] not found", id));
        }
    }

    @PutMapping("/users/{id}")
    public void updateUser(@PathVariable int id, @RequestBody User setUser) {
        User findUser = service.findOne(id);

        if (findUser == null) {
            throw new UserNotFoundException(String.format("ID[%s] not found", id));
        }

        // 날짜 형식이 맞지 않으면 400 배드 리퀘스트 에러 발생 -> 포스트맨에서 json 리퀘스트로 보냈기 때문에 당연한 것
        // 리퀘스트 형식이 이상하면 400 배드 리퀘스트가 먼저 발생 -> 위와 같은 이유
        // 컨트롤러와 서비스 코드 리팩토링 필요
        // 생각해 보니 이 수준에서는 현재 코드가 가장 나은 듯 함

        service.updateById(findUser, setUser);
    }

}
