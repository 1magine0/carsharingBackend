package com.carsharing.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Ім'я є обов'язковим")
    @Size(max = 150, message = "Ім'я не повинно перевищувати 150 символів")
    private String fullName;

    @NotBlank(message = "Email є обов'язковим")
    @Email(message = "Некоректний формат email")
    @Size(max = 120, message = "Email не повинен перевищувати 120 символів")
    private String email;

    @NotBlank(message = "Телефон є обов'язковим")
    @Size(max = 30, message = "Телефон не повинен перевищувати 30 символів")
    private String phone;

    @NotBlank(message = "Пароль є обов'язковим")
    @Size(min = 6, max = 100, message = "Пароль повинен містити щонайменше 6 символів")
    private String password;

    @NotBlank(message = "Підтвердження пароля є обов'язковим")
    private String confirmPassword;
}