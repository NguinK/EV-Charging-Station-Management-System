package com.evcharging.config;


//import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/auth/**", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // mở toàn bộ auth
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

//    @Bean
//    public org.springdoc.core.customizers.OpenApiCustomiser securityResponses() {
//        return openApi -> openApi.getPaths().forEach((path, item) ->
//                item.readOperations().forEach(op -> {
//                    // Nếu operation yêu cầu bearerAuth thì thêm response mẫu
//                    var requiresAuth = op.getSecurity() != null && !op.getSecurity().isEmpty();
//                    if (requiresAuth) {
//                        op.getResponses()
//                                .addApiResponse("401", new ApiResponse().description("Unauthorized"))
//                                .addApiResponse("403", new ApiResponse().description("Forbidden"));
//                    }
//                })
//        );
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}