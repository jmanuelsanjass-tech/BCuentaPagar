package com.ejemplo.proveedor.config;

import jakarta.servlet.DispatcherType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.http.HttpMethod;
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
    configuration.setAllowedOrigins(List.of("*"));
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
        .csrf(csrf -> csrf.ignoringRequestMatchers(request ->
            ("POST".equals(request.getMethod())
                && ("/api/proveedores".equals(request.getServletPath())
                    || "/api/productos".equals(request.getServletPath())
                    || "/api/clientes".equals(request.getServletPath())
                    || "/api/auth/register".equals(request.getServletPath())
                    || "/api/auth/login".equals(request.getServletPath())
                    || "/api/auth/logout".equals(request.getServletPath())))
                || ("PUT".equals(request.getMethod())
                    && request.getServletPath().startsWith("/api/clientes/"))))
        .authorizeHttpRequests(authorize -> authorize.dispatcherTypeMatchers(DispatcherType.ERROR)
            .permitAll()
            .requestMatchers(
                HttpMethod.POST, "/api/auth/register", "/api/auth/login", "/api/auth/logout")
            .permitAll()
            .requestMatchers(HttpMethod.GET, "/api/proveedores").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/proveedores").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/clientes", "/api/clientes/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/clientes").permitAll()
            .requestMatchers(HttpMethod.PUT, "/api/clientes/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/productos", "/api/productos/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/productos").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/ventas", "/api/ventas/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/ventas").permitAll()
            .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll().anyRequest()
            .authenticated())
        .formLogin(form -> form.disable());

    return http.build();
  }
}
