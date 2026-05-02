package com.carsharing.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {

    @NotBlank(message = "Ім'я є обов'язковим")
    @Size(max = 150, message = "Ім'я не повинно перевищувати 150 символів")
    private String fullName;

    @NotBlank(message = "Email є обов'язковим")
    @Email(message = "Некоректний формат email")
    @Size(max = 120, message = "Email не повинен перевищувати 120 символів")
    private String email;

    @NotBlank(message = "Телефон є обов'язковим")
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Телефон має містити 10-15 цифр і може починатися з +"
    )
    private String phone;
}