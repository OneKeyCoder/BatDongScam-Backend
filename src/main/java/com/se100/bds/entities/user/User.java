package com.se100.bds.entities.user;

import com.se100.bds.entities.AbstractBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import com.se100.bds.utils.Constants;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "user_id", nullable = false)),
})
public class User extends AbstractBaseEntity {
    @Column(name = "role", nullable = false)
    private Constants.RoleEnum role;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    // TODO: Change to use city and district id
    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "status", nullable = false)
    private Constants.StatusProfileEnum status;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Customer customer;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private SaleAgent saleAgent;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private PropertyOwner propertyOwner;
}
