package com.ejemplo.proveedor.config;

import jakarta.servlet.DispatcherType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
import com.ejemplo.proveedor.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final UserDetailsServiceImpl userDetailsService;

  public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
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
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
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
        .csrf(csrf -> csrf
            .csrfTokenRepository(new HttpSessionCsrfTokenRepository())
            .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            .ignoringRequestMatchers(request ->
                HttpMethod.POST.matches(request.getMethod())
                    && ("/api/auth/login".equals(request.getServletPath())
                        || "/api/auth/register".equals(request.getServletPath()))))
        .cors(Customizer.withDefaults())
        .authorizeHttpRequests(authorize -> authorize
            .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
            .requestMatchers("/error").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register")
                .permitAll()
            .requestMatchers(HttpMethod.GET, "/api/auth/csrf").permitAll()
            .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
            .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
            .requestMatchers("/api/bitacora/**").hasRole("ADMIN")
            .requestMatchers("/api/roles/**").hasRole("ADMIN")
            .requestMatchers("/api/ventas/**").hasAnyRole("ADMIN", "VENTAS")
            .requestMatchers("/api/clientes/**").hasAnyRole("ADMIN", "VENTAS")
            .requestMatchers("/api/inventarios/**", "/api/productos/**")
                .hasAnyRole("ADMIN", "INVENTARIOS")
            .requestMatchers("/api/proveedores/**").hasAnyRole("ADMIN", "INVENTARIOS")
            .requestMatchers("/api/cuentas-contables/**")
                .hasAnyRole("ADMIN", "COMPRAS", "CONTABILIDAD", "INVENTARIOS")
            .requestMatchers("/api/ordenes-compra/**")
                .hasAnyRole("ADMIN", "COMPRAS", "INVENTARIOS")
            .requestMatchers("/api/recepciones-mercancia/**")
                .hasAnyRole("ADMIN", "RECEPCION", "INVENTARIOS")
            .requestMatchers("/api/facturas-proveedor/**")
                .hasAnyRole("ADMIN", "CONTABILIDAD")
            .requestMatchers("/api/pagos/**")
                .hasAnyRole("ADMIN", "TESORERIA", "CONTABILIDAD")
            .requestMatchers("/api/politicas-aprobacion/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/api/bitacora-auditoria/**")
                .hasAnyRole("ADMIN", "AUDITOR")
            .requestMatchers("/api/bitacora-auditoria/**").hasRole("ADMIN")
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
