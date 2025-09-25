package com.se100.bds.dtos.responses.user;

import com.se100.bds.dtos.responses.AbstractBaseDataResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class UserResponse<T> extends AbstractBaseDataResponse {
    private String role;
    private String email;
    private String phoneNumber;
    private String address;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String profileDescription;
    private String birthDate;
    private String status;
    private LocalDateTime lastLoginAt;
    private T profile;
}
