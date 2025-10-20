package com.chakray.technical_test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private UUID id;
    private String email;
    private String name;
    private String phone;
    private String passwordEncrypted;
    private String taxId;
    private String createdAt;
    private List<Address> addressLiss;
}
