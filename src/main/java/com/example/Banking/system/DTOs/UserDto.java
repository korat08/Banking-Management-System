package com.example.Banking.system.DTOs;

import com.example.Banking.system.Model.Role;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;

    private String userName;

    private String email;

    private String phoneNumber;

    private String city;

    private Set<Role> roles = new HashSet<>();
}
