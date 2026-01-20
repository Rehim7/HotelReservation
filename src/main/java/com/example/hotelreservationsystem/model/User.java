package com.example.hotelreservationsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "Users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "user_names",nullable = false)
    private String username;
    @Column(name = "Emails",nullable = false)
    private String email;
    @Column(name = "Passwords",nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "User_Roles",nullable = false)
    private Roles userRole;

    private Long ticketId;

    @OneToMany(targetEntity = UserOpinions.class, cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<UserOpinions> userOpinions;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Hotel ownedHotel;


    @OneToMany(targetEntity = Room.class,cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Room> rooms;


//    @OneToOne(targetEntity = Card.class,cascade = CascadeType.ALL,fetch = FetchType.LAZY)
//    private Card card;


    @Builder.Default
    @jakarta.persistence.Transient
    private boolean enabled = true; // TODO: Remove @Transient and add @Column after running migration.sql

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userRole.name()));
    }

    @Override
    public String getUsername() {
        return email;
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
        return this.enabled;
    }
}

