package com.ejemplo.proveedor.service;

import com.ejemplo.proveedor.model.Usuario;
import com.ejemplo.proveedor.repository.UsuarioRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ejemplo.proveedor.security.SecurityRoles;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UsuarioRepository usuarioRepository;

  public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Usuario usuario = usuarioRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

    System.out.println("Usuario: " + usuario.getUsername());
    usuario.getRoles()
        .forEach(r -> System.out.println("ROL: " + r.getNombre()));

    List<GrantedAuthority> authorities = usuario.getRoles().stream()
        .<GrantedAuthority>map(rol ->
            new SimpleGrantedAuthority(SecurityRoles.authority(rol.getNombre())))
        .collect(Collectors.toList());

    return new User(
        usuario.getUsername(),
        usuario.getPassword(),
        usuario.isActivo(),
        true,
        true,
        true,
        authorities);
  }
}
