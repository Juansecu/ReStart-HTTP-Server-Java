package com.juansecu.restart.auth.models.entities;

import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.juansecu.restart.users.models.entities.UserEntity;

@Entity(name = "Permissions")
@Getter
@Setter
public class PermissionEntity {
    @Column(name = "Permission_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer permissionId;
    @Column(length = 30, name = "Name", nullable = false, unique = true)
    private String name;
    @Column(length = 100, name = "Description", nullable = false, unique = true)
    private String description;
    @Column(name = "Added_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Date addedAt;
    @Column(name = "Added_by")
    private Integer addedByUserId;
    @Column(name = "Updated_by")
    private Integer updatedByUserId;
    @Column(name = "Updated_at", nullable = false)
    @UpdateTimestamp
    private Date updatedAt;
    @JoinColumn(
        insertable = false,
        name = "Added_by",
        referencedColumnName = "User_id",
        updatable = false
    )
    @ManyToOne(
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY
    )
    private UserEntity addedBy;
    @JoinColumn(
        insertable = false,
        name = "Updated_by",
        referencedColumnName = "User_id",
        updatable = false
    )
    @ManyToOne(
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY
    )
    private UserEntity updatedBy;
    @JoinTable(
        inverseJoinColumns = @JoinColumn(
            name = "Role_id",
            referencedColumnName = "Role_id"
        ),
        joinColumns = @JoinColumn(
            name = "Permission_id",
            referencedColumnName = "Permission_id"
        ),
        name = "Role_permissions"
    )
    @ManyToMany(
        cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH
        },
        fetch = FetchType.LAZY
    )
    private List<RoleEntity> roles;
    @JoinTable(
        inverseJoinColumns = @JoinColumn(
            name = "User_id",
            referencedColumnName = "User_id"
        ),
        joinColumns = @JoinColumn(
            name = "Permission_id",
            referencedColumnName = "Permission_id"
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
        fetch = FetchType.LAZY
    )
    private List<UserEntity> users;
}
