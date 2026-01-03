package com.digiworld.vakeelpro.entities;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reset_tokens")
@Data
public class ResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime expiryDate;

    public ResetToken() {
        this.expiryDate = LocalDateTime.now().plusMinutes(15); // 15-minute expiration
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}