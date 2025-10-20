package com.chakray.technical_test.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AddressDto {
    private Integer id;
    private String name;
    private String street;
    private String countryCode;
}
