package com.chakray.technical_test.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class UserCreateDto {
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank
    private String password;

    @NotBlank
    @NotBlank(message = "TaxID is required")
    private String taxId;

    private List<AddressDto> addresses;
}
