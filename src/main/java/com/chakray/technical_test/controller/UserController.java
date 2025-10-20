package com.chakray.technical_test.controller;

import com.chakray.technical_test.dto.UserCreateDto;
import com.chakray.technical_test.dto.UserResponseDTO;
import com.chakray.technical_test.dto.UserUpdateDTO;
import com.chakray.technical_test.response.ApiResponse;
import com.chakray.technical_test.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService svc;
    public UserController(UserService svc) { this.svc = svc; }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getUsers(@RequestParam(required = false) String sortedBy, @RequestParam(required = false) String filter
    ) {
        try {
            List<UserResponseDTO> users = svc.listUsers(
                    Optional.ofNullable(sortedBy),
                    Optional.ofNullable(filter)
            );
            return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.failure("Error retrieving users: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(@Valid @RequestBody UserCreateDto user){
        try{
            UserResponseDTO created = svc.createUser(user);
            return ResponseEntity.ok(ApiResponse.success(created,"User created successfully"));
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(ApiResponse.failure(e.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(@PathVariable UUID id, @Valid @RequestBody UserUpdateDTO dto){
        try {
            Optional<UserResponseDTO> updatedUserOpt = svc.updateUser(id, dto);

            return updatedUserOpt.map(userResponseDTO -> ResponseEntity.ok(ApiResponse.success(userResponseDTO, "User updated successfully"))).orElseGet(() -> ResponseEntity.badRequest().body(ApiResponse.failure("User not found")));

        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(ApiResponse.failure(e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.internalServerError().body(ApiResponse.failure("Error updating user: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id){
        boolean deleted = svc.deleteUser(id);
        if(deleted){
            return ResponseEntity.ok(ApiResponse.success(null,"User deleted successfully"));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.failure("User not found"));
        }
    }
}
