package pl.tscript3r.notify.monitor.controllers.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.tscript3r.notify.monitor.api.v1.model.UserDTO;
import pl.tscript3r.notify.monitor.consts.v1.Paths;
import pl.tscript3r.notify.monitor.services.UserService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(Paths.USER_PATH)
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO add(@Valid @RequestBody UserDTO userDto) {
        log.debug("Adding new user with email=" + userDto.getEmail());
        return userService.add(userDto);
    }

}
