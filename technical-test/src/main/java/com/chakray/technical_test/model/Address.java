package com.chakray.technical_test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private Integer id;
    private String name;
    private String street;
    private String countryCode;
}
