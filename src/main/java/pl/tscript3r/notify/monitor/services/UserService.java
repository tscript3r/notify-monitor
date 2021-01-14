package pl.tscript3r.notify.monitor.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.api.v1.model.UserDTO;
import pl.tscript3r.notify.monitor.domain.User;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserService extends AbstractMapService<User, Long> {

    private final ModelMapper mapper;

    public UserDTO add(UserDTO userDto) {
        User user = mapper.map(userDto, User.class);
        return mapper.map(save(user), UserDTO.class);
    }

    public String getEmailFromUserId(Long id) {
        return findById(id).getEmail();
    }

}
