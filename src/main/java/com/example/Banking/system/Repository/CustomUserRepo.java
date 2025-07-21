package com.example.Banking.system.Repository;

import com.example.Banking.system.Model.CustomUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomUserRepo extends JpaRepository<CustomUser,Long> {

    boolean existsByPhoneNumber(@NotBlank @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid India phone Number(must be start with 6-9 and be 10 Digits)"
    ) String phoneNumber);

    boolean existsByEmail(@NotBlank @Email String email);

    boolean existsByUserName(@NotBlank String userName);

    CustomUser findByUserName(String username);

    CustomUser findByEmail(@NotBlank @Email String email);

    CustomUser findByPhoneNumber(@NotBlank @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid India phone Number(must be start with 6-9 and be 10 Digits)"
    ) String phoneNumber);

}
