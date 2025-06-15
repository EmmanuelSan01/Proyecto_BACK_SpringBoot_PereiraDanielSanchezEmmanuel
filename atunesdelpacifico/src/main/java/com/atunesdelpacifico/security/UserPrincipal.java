package com.atunesdelpacifico.security;

import com.atunesdelpacifico.entity.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {
    
    private Long id;
    private String nombreUsuario;
    private String correo;
    private String contrasena;
    private Collection<? extends GrantedAuthority> authorities;
    
    public UserPrincipal(Long id, String nombreUsuario, String correo, String contrasena, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.contrasena = contrasena;
        this.authorities = authorities;
    }
    
    public static UserPrincipal create(Usuario usuario) {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre().toUpperCase());
        
        return new UserPrincipal(
                usuario.getIdUsuario(),
                usuario.getNombreUsuario(),
                usuario.getCorreo(),
                usuario.getContrasena(),
                Collections.singletonList(authority)
        );
    }
    
    public Long getId() {
        return id;
    }
    
    public String getCorreo() {
        return correo;
    }
    
    @Override
    public String getUsername() {
        return nombreUsuario;
    }
    
    @Override
    public String getPassword() {
        return contrasena;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
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
        return true;
    }
}
