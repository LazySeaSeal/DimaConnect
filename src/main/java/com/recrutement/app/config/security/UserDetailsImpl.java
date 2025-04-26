package com.recrutement.app.config.security;

import com.recrutement.app.model.Candidat;
import com.recrutement.app.model.Entreprise;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String email, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserDetailsImpl buildFromCandidat(Candidat candidat) {
        return new UserDetailsImpl(
                candidat.getId(),
                candidat.getEmail(),
                candidat.getMotDePasse(),
                Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_CANDIDAT))
        );
    }

    public static UserDetailsImpl buildFromEntreprise(Entreprise entreprise) {
        return new UserDetailsImpl(
                entreprise.getId(),
                entreprise.getEmail(),
                entreprise.getMotDePasse(),
                Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_ENTREPRISE))
        );
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
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
        return true;
    }
}
