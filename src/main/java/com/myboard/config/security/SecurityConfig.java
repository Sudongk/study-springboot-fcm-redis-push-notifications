package com.myboard.config.security;

import com.myboard.util.filter.JwtFilter;
import com.myboard.util.filter.LoginFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final LoginFilter loginFilter;
    private final JwtFilter jwtFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(@Lazy LoginFilter loginFilter,
                          JwtFilter jwtFilter,
                          @Qualifier("customAuthenticationEntryPoint") AuthenticationEntryPoint authenticationEntryPoint,
                          @Qualifier("customUserDetailsService") UserDetailsService userDetailsService) {
        this.loginFilter = loginFilter;
        this.jwtFilter = jwtFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager inMemoryUser = new InMemoryUserDetailsManager();

        inMemoryUser.createUser(
                User.builder()
                        .username("user1")
                        .password(PasswordEncoder().encode("password"))
                        .roles("USER")
                        .build()
        );

        inMemoryUser.createUser(
                User.builder()
                        .username("user2")
                        .password(PasswordEncoder().encode("password"))
                        .roles("USER")
                        .build()
        );

        inMemoryUser.createUser(
                User.builder()
                        .username("user3")
                        .password(PasswordEncoder().encode("password"))
                        .roles("USER")
                        .build()
        );

        inMemoryUser.createUser(
                User.builder()
                        .username("admin")
                        .password(PasswordEncoder().encode("password"))
                        .roles("ADMIN")
                        .build()
        );
        
        return inMemoryUser;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(PasswordEncoder());

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf -> csrf.disable());
        httpSecurity.authorizeRequests()
                .antMatchers("/api/v1/user/create")
                .permitAll()
                .anyRequest()
                .authenticated();
        httpSecurity.addFilterAt(loginFilter, BasicAuthenticationFilter.class);
        httpSecurity.addFilterAt(jwtFilter, BasicAuthenticationFilter.class);
        httpSecurity
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint);

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder PasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
