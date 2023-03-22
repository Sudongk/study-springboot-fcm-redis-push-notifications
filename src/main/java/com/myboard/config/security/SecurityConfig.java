package com.myboard.config.security;

import com.myboard.exception.user.UserNotFoundException;
import com.myboard.repository.user.UserRepository;
import com.myboard.util.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final LogoutHandlerImpl logoutHandler;
    private final UserRepository userRepository;
    private final AuthenticationEntryPointImpl authenticationEntryPoint;

    public SecurityConfig(JwtFilter jwtFilter,
                          LogoutHandlerImpl logoutHandler,
                          UserRepository userRepository,
                          AuthenticationEntryPointImpl authenticationEntryPoint) {

        this.jwtFilter = jwtFilter;
        this.logoutHandler = logoutHandler;
        this.userRepository = userRepository;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests()
                .antMatchers("/api/v1/user/create", "/api/v1/user/login")
                .permitAll()
                .anyRequest()
                .authenticated();

        httpSecurity
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        httpSecurity
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint);

        httpSecurity
                .logout()
                .logoutUrl("/api/v1/user/logout")
                .addLogoutHandler(logoutHandler)
                // 로그아웃시 로그인 정보 초기화
                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());

        return httpSecurity.build();
    }

    @Bean
    public UserDetailsService inMemoryUserDetailsService() {
        InMemoryUserDetailsManager inMemoryUser = new InMemoryUserDetailsManager();

        inMemoryUser.createUser(
                User.builder()
                        .username("user1")
                        .password(passwordEncoder().encode("password"))
                        .roles("USER")
                        .build()
        );

        inMemoryUser.createUser(
                User.builder()
                        .username("user2")
                        .password(passwordEncoder().encode("password"))
                        .roles("USER")
                        .build()
        );

        inMemoryUser.createUser(
                User.builder()
                        .username("admin")
                        .password(passwordEncoder().encode("password"))
                        .roles("ADMIN")
                        .build()
        );

        return inMemoryUser;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findUserByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}