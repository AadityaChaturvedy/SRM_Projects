package com.srmist.academia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String passwordHash;

    @Column(columnDefinition = "text")   // ✅ use TEXT in Postgres
    private String cookies;  // serialized cookies/session

    // Constructors
    public User() {}

    public User(String email, String passwordHash, String cookies) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.cookies = cookies;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getCookies() { return cookies; }
    public void setCookies(String cookies) { this.cookies = cookies; }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String email;
        private String passwordHash;
        private String cookies;

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder cookies(String cookies) {
            this.cookies = cookies;
            return this;
        }

        public User build() {
            return new User(email, passwordHash, cookies);
        }
    }
}
