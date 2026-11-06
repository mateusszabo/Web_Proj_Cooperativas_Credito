package br.edu.eseg.cooperativas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/h2-console/**").permitAll()
                        // Rotas exclusivas de alçada superior (ROLE_GESTOR) protegidas no servidor (Requisito 2.7 / RF08)
                        .requestMatchers(HttpMethod.POST, "/cooperativas/**").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.POST, "/operacoes-credito/*/renegociar").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.POST, "/transacoes/*/estornar").hasRole("GESTOR")
                        // Demais rotas autenticadas acessíveis por ROLE_ANALISTA e ROLE_GESTOR
                        .anyRequest().hasAnyRole("ANALISTA", "GESTOR")
                )
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
                // Necessário para permitir a visualização de frames do Console H2 em /h2-console
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}