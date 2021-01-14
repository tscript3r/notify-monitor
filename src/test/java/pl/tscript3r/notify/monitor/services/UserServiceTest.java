package pl.tscript3r.notify.monitor.services;

import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;
import pl.tscript3r.notify.monitor.api.v1.model.UserDTO;

import static org.junit.Assert.assertEquals;

public class UserServiceTest {

    public static final String EMAIL = "test@test.app";
    UserService userService;

    @Before
    public void setUp() {
        userService = new UserService(new ModelMapper());
    }

    @Test
    public void getEmailByUserId_Should_ReturnUsersEmail_When_UserByGivenIdExists() {
        // given
        UserDTO userDto = new UserDTO();
        userDto.setEmail(EMAIL);

        // when
        Long usersId = userService.add(userDto).getId();

        // then
        assertEquals(EMAIL, userService.getEmailFromUserId(usersId));
    }

}