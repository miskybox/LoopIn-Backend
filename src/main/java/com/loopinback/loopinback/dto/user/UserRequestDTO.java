package com.loopinback.loopinback.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    @Null
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    private String profileImageUrl;
}
