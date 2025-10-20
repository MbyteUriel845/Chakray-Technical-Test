package com.chakray.technical_test.dto;

import com.chakray.technical_test.model.Address;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserResponseDTO {
    private UUID id;
    private String email;
    private String name;
    private String phone;
    private String taxId;
    private String createdAt;
    private List<Address> addresses;
}
