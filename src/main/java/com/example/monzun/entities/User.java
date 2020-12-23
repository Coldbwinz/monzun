package com.example.monzun.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", schema = "public")

public class User {
    @Id
    @SequenceGenerator(name = "users_seq",
            sequenceName = "users_user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "users_seq")
    @Column(name = "user_id", updatable = false, nullable = false)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Column(name = "phone")
    private String phone;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logo_id")
    private Attachment logo;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked;
    @Column(name = "block_reason")
    private String blockReason;
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<PasswordResetToken> passwordResetTokens;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
