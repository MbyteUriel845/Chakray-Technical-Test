package com.chakray.technical_test.controller;

import com.chakray.technical_test.dto.UserResponseDTO;
import com.chakray.technical_test.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(UserControllerTest.TestUserServiceConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class TestUserServiceConfig {
        @Bean
        public UserService userService() {
            UserService svc = new UserService();
            ReflectionTestUtils.setField(svc, "aesKey", "0123456789ABCDEF0123456789ABCDEF");
            ReflectionTestUtils.setField(svc, "appTimeZone", "UTC");
            svc.init();
            return new UserService() {
                @Override
                public List<UserResponseDTO> listUsers(Optional<String> sortedByOpt, Optional<String> filterOpt) {
                    UserResponseDTO user1 = new UserResponseDTO();
                    user1.setId(UUID.randomUUID());
                    user1.setName("user1");
                    user1.setEmail("user1@mail.com");

                    UserResponseDTO alice = new UserResponseDTO();
                    alice.setId(UUID.randomUUID());
                    alice.setName("alice");
                    alice.setEmail("alice@mail.com");

                    UserResponseDTO bob = new UserResponseDTO();
                    bob.setId(UUID.randomUUID());
                    bob.setName("bob");
                    bob.setEmail("bob@mail.com");

                    List<UserResponseDTO> allUsers = List.of(alice, bob, user1);

                    if (filterOpt.isPresent()) {
                        String filter = filterOpt.get().toLowerCase();
                        allUsers = allUsers.stream()
                                .filter(u -> u.getName().toLowerCase().contains(filter))
                                .toList();
                    }

                    if (sortedByOpt.isPresent() && sortedByOpt.get().equals("name")) {
                        allUsers = allUsers.stream()
                                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                                .toList();
                    }

                    return allUsers;
                }
            };
        }
    }

    @Test
    void getUsers_withFilter_success() throws Exception {
        mockMvc.perform(get("/users")
                        .param("filter", "user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("user1"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getUsers_noFilter_success() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getUsers_sortedByName_success() throws Exception {
        mockMvc.perform(get("/users")
                        .param("sortedBy", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("alice"))
                .andExpect(jsonPath("$.data[1].name").value("bob"))
                .andExpect(jsonPath("$.data[2].name").value("user1"))
                .andExpect(jsonPath("$.success").value(true));
    }
}

