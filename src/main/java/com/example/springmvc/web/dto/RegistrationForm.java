package com.example.springmvc.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrationForm {

    @NotBlank(message = "Vorname ist erforderlich")
    @Size(max = 100, message = "Vorname darf maximal 100 Zeichen haben")
    private String firstName;

    @NotBlank(message = "Nachname ist erforderlich")
    @Size(max = 100, message = "Nachname darf maximal 100 Zeichen haben")
    private String lastName;

    @NotBlank(message = "E-Mail ist erforderlich")
    @Email(message = "E-Mail ist ungültig")
    private String email;

    @NotBlank(message = "Passwort ist erforderlich")
    @Size(min = 8, max = 100, message = "Passwort muss 8–100 Zeichen haben")
    private String password;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RegisterRequest toRegisterRequest() {
        return new RegisterRequest(email, password, firstName, lastName);
    }
}
