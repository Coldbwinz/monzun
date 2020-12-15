package com.example.monzun.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "password_reset_tokens", schema = "public")
public class PasswordResetToken {
    @Id
    @Column(name = "token_id", updatable = false, nullable = false)
    @SequenceGenerator(name = "pr_tokens_seq",
            sequenceName = "password_reset_tokens_token_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "pr_tokens_seq")
    private Long id;
    @Column(name = "token")
    private String token;
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "expired_at")
    private LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(2880); //2 days
}
