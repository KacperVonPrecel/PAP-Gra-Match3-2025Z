package pap.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import pap.project.users.UserDetailService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig
{
    private final @NonNull UserDetailService userDetailService;

    public WebSecurityConfig(@NonNull UserDetailService userDetailService)
    {
        this.userDetailService = userDetailService;
    }

    @Bean
    public @NonNull SecurityFilterChain securityFilterChain(@NonNull HttpSecurity http) throws Exception
    {
        http
                .securityContext(context -> context
                        .securityContextRepository(securityContextRepository())
                )
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/api/auth/**").anonymous()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/xxx").authenticated()
                        .requestMatchers("/h2-console", "/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .logout(LogoutConfigurer::permitAll);

        return http.build();
    }

    @Bean
    public @NonNull AuthenticationManager authenticationManager(@NonNull AuthenticationConfiguration config) throws Exception
    {
        return config.getAuthenticationManager();
    }

    @Bean
    public @NonNull AuthenticationProvider authenticationProvider()
    {
        final DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public @NonNull BCryptPasswordEncoder bCryptPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public @NonNull SecurityContextRepository securityContextRepository()
    {
        return new HttpSessionSecurityContextRepository();
    }

}
