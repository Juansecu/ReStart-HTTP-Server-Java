package com.juansecu.restart.users.models.entities;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.juansecu.restart.shared.generators.NumericIdGenerator;

@Entity(name = "Users")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@Setter
public class UserEntity {
    @Column(name = "User_id")
    @Id
    @NumericIdGenerator
    private Integer userId;
    @Column(length = 50, name = "Email", nullable = false, unique = true)
    @NonNull
    private String email;
    @Column(length = 12, name = "Username", nullable = false, unique = true)
    @NonNull
    private String username;
    @Column(length = 60, name = "Password", nullable = false)
    @NonNull
    private String password;
    @Column(name = "Birthdate", nullable = false)
    @NonNull
    private Date birthdate;
    @Column(name = "Registered_at", nullable = false)
    @CreationTimestamp
    private Date registeredAt;
    @Column(name = "Last_logged_in_at")
    private Date lastLoggedInAt;
    @Column(name = "Updated_at", nullable = false)
    @UpdateTimestamp
    private Date updatedAt;
}
