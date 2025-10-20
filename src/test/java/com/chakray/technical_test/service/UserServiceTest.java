package com.chakray.technical_test.service;

import com.chakray.technical_test.dto.UserResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private UserService svc;

    @BeforeEach
    void setup() {
        svc = new UserService();
        ReflectionTestUtils.setField(svc, "aesKey", "0123456789ABCDEF0123456789ABCDEF");
        ReflectionTestUtils.setField(svc, "appTimeZone", "UTC");
        svc.init();
    }

    @Test
    void listUsers_noFilter_returnsAll() {
        List<UserResponseDTO> list = svc.listUsers(Optional.empty(), Optional.empty());
        assertEquals(3, list.size());
    }

    @Test
    void listUsers_sortedByName_returnsSorted() {
        List<UserResponseDTO> list = svc.listUsers(Optional.of("name"), Optional.empty());
        assertEquals("alice", list.get(0).getName());
        assertEquals("bob", list.get(1).getName());
        assertEquals("user1", list.get(2).getName());
    }

    @Test
    void listUsers_filterByNameContainsUser_returnsFiltered() {
        List<UserResponseDTO> list = svc.listUsers(Optional.empty(), Optional.of("name co user"));
        assertEquals(1, list.size());
        assertEquals("user1", list.get(0).getName());
    }

    @Test
    void listUsers_invalidFilter_throwsException() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> svc.listUsers(Optional.empty(), Optional.of("invalidfilter")));
        assertTrue(ex.getMessage().contains("Invalid filter format"));
    }
}
