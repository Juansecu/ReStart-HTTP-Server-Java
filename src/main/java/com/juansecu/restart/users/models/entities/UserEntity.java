package com.juansecu.restart.users.models.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.juansecu.restart.auth.enums.ERole;
import com.juansecu.restart.auth.models.entities.PermissionEntity;
import com.juansecu.restart.auth.models.entities.RoleEntity;
import com.juansecu.restart.shared.generators.NumericIdGenerator;

@Entity(name = "Users")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@Setter
public class UserEntity implements UserDetails {
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
    @Column(name = "Registered_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Date registeredAt;
    @Column(name = "Last_logged_in_at")
    private Date lastLoggedInAt;
    @Column(name = "Last_updated_email_at")
    private Date lastUpdatedEmailAt;
    @Column(name = "Deleted_at")
    private Date deletedAt;
    @Column(name = "Updated_at", nullable = false)
    @UpdateTimestamp
    private Date updatedAt;
    @JoinTable(
        inverseJoinColumns = @JoinColumn(
            name = "Permission_id",
            referencedColumnName = "Permission_id"
        ),
        joinColumns = @JoinColumn(
            name = "User_id",
            referencedColumnName = "User_id"
        ),
        name = "User_permissions"
    )
    @ManyToMany(
        cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH
        },
        fetch = FetchType.EAGER
    )
    private List<PermissionEntity> permissions;
    @JoinTable(
        inverseJoinColumns = @JoinColumn(
            name = "Role_id",
            referencedColumnName = "Role_id"
        ),
        joinColumns = @JoinColumn(
            name = "User_id",
            referencedColumnName = "User_id"
        ),
        name = "User_roles"
    )
    @ManyToMany(
        cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH
        },
        fetch = FetchType.EAGER
    )
    private List<RoleEntity> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<GrantedAuthority> authorities = new ArrayList<>();

        this.permissions.forEach(permission ->
            authorities.add(new SimpleGrantedAuthority(permission.getName()))
        );

        this.roles.forEach(role -> {
            // Add role permissions to authorities
            if (role.getPermissions() != null) {
                role.getPermissions().forEach(permission -> {
                    if (
                        !authorities.contains(
                            new SimpleGrantedAuthority(permission.getName())
                        )
                    )
                        authorities.add(new SimpleGrantedAuthority(permission.getName()));
                });
            }

            // Add role to authorities
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });

        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.deletedAt == null &&
            this.getAuthorities().contains(
                new SimpleGrantedAuthority(ERole.USER.getRole())
            );
    }
}
