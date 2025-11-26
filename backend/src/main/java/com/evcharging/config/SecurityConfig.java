package com.evcharging.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) //enable @PreAuthorize
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy(
                "ROLE_ADMIN > ROLE_STAFF\n" +
                        "ROLE_STAFF > ROLE_EV_DRIVER"
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource, RoleHierarchy roleHierarchy) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //JWT is stateless
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/register-driver",
                                "/auth/login",
                                "/admin/auth/createAdmin",
                                "/admin/auth/login",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        //Admin user management
                        .requestMatchers("/admin/auth/getAllAdmins").hasRole("ADMIN")
                        .requestMatchers("/admin/auth/ById/**").hasRole("ADMIN")
                        .requestMatchers("/admin/auth/updateAdmin/**").hasRole("ADMIN")
                        .requestMatchers("/admin/auth/deleteAdmin/**").hasRole("ADMIN")

                        //Admin API endpoints
                                .requestMatchers("/api/admin/staff/**").hasRole("ADMIN")
                                .requestMatchers("/api/subscriptions/**").hasRole("ADMIN")

                        // Charging point admin controls
                        .requestMatchers(
                                HttpMethod.POST, "/api/charging-points/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.DELETE, "/api/charging-points/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.PUT, "/api/charging-points/*/pricing"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.PUT, "/api/charging-points/*/status"
                        ).hasRole("ADMIN")

                        // Station creation should be admin-only
                        .requestMatchers(
                                HttpMethod.POST, "/api/stations/createStation"
                        ).hasRole("ADMIN")

                        //Staff endpoints
                        .requestMatchers("/staff/**").hasRole("CS_STAFF")
                        .requestMatchers("/api/staff/**").hasRole("CS_STAFF")

                        //Driver endpoints
                        .requestMatchers("/drivers/**").hasRole("EV_DRIVER")
                                .requestMatchers("api/subscriptions/register").hasRole("EV_DRIVER")


                        //Driver can read stations, sessions, wallets, transactions, charging points
                        .requestMatchers(
                                HttpMethod.GET, "/api/stations/**"
                        ).hasRole("EV_DRIVER")
                        .requestMatchers(
                                HttpMethod.GET, "/api/charging-points/**"
                        ).hasRole("EV_DRIVER")
                        .requestMatchers("/api/sessions/**").hasRole("EV_DRIVER")
                        .requestMatchers("/api/wallets/**").hasRole("EV_DRIVER")
                        .requestMatchers("/api/transactions/**").hasRole("EV_DRIVER")

                        //All other requests must be authenticated
//                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    // Create hierarchical authority voter
                    auth.anyRequest().authenticated();
                });
        return http.build();
    }

    @Bean
    public DefaultMethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }
}
