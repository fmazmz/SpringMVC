package org.example.springmvc.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/signup/**", "/images/**", "/css/**").permitAll()

                        .requestMatchers("/cars/new").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/cars").hasAnyRole("ADMIN", "DRIVER", "APP_USER")
                        .requestMatchers("/cars/**").hasRole("ADMIN")

                        .requestMatchers("/drivers/new").hasRole("APP_USER")
                        .requestMatchers("/drivers/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/bookings/new").hasRole("DRIVER")
                        .requestMatchers(HttpMethod.POST, "/bookings/new").hasRole("DRIVER")

                        .requestMatchers(HttpMethod.GET, "/bookings/my-bookings").hasRole("DRIVER")
                        .requestMatchers(HttpMethod.POST, "/bookings/my-bookings/*/delete").hasRole("DRIVER")

                        .requestMatchers(HttpMethod.GET, "/bookings/*/update").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/bookings/*/update").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/bookings/*/delete").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/bookings/*").hasAnyRole("ADMIN", "DRIVER")
                        .requestMatchers(HttpMethod.GET, "/bookings").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf(withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
