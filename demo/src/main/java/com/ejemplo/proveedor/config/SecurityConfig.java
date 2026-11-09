package com.ejemplo.proveedor.config;

import jakarta.servlet.DispatcherType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
import com.ejemplo.proveedor.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final CustomUserDetailsService userDetailsService;

  public SecurityConfig(CustomUserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  @Bean
  AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
    return new ProviderManager(authenticationProvider);
  }

  @Bean
  SecurityContextRepository securityContextRepository() {
    return new HttpSessionSecurityContextRepository();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of(
        "https://zapateria-erp-front.web.app",
        "http://localhost:4200"));
    configuration.setAllowCredentials(true);
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      AuthenticationProvider authenticationProvider,
      SecurityContextRepository securityContextRepository) throws Exception {
    http.authenticationProvider(authenticationProvider)
        .securityContext(securityContext ->
            securityContext.securityContextRepository(securityContextRepository))
        .cors(Customizer.withDefaults())
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(authorize -> authorize
            .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
            .requestMatchers("/api/ventas/**").hasAnyRole("ADMIN", "VENTAS")
            .requestMatchers("/api/clientes/**").hasAnyRole("ADMIN", "VENTAS")
            .requestMatchers("/api/inventarios/**", "/api/productos/**")
                .hasAnyRole("ADMIN", "INVENTARIOS")
            .requestMatchers("/api/proveedores/**").hasAnyRole("ADMIN", "INVENTARIOS")
            .requestMatchers(
                "/api/cuentas-por-cobrar/**",
                "/api/cuentas-por-cobrar-historia/**")
                .hasAnyRole("ADMIN", "VENTAS", "CONTABILIDAD")
            .requestMatchers("/api/cuentas-por-pagar/**")
                .hasAnyRole("ADMIN", "CONTABILIDAD")
            .requestMatchers("/api/reportes/**").hasAnyRole("ADMIN", "CONTABILIDAD")
            .anyRequest().authenticated())
        .formLogin(form -> form.disable());

    return http.build();
  }
}
