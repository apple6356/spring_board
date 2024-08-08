package org.seo.board.config;

import lombok.RequiredArgsConstructor;
import org.seo.board.config.oauth.CustomOAuth2UserService;
import org.seo.board.service.UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

        private final UserDetailService userService;
        private final CustomOAuth2UserService customUserService;

        // 스프링 시큐리티 기능 비활성화
        @Bean
        public WebSecurityCustomizer configure() {
                return (web) -> web.ignoring()
                                .requestMatchers(
                                                new AntPathRequestMatcher("/img/**"),
                                                new AntPathRequestMatcher("/css/**"),
                                                new AntPathRequestMatcher("/js/**"));
        }

        // 특정 http 요청에 대한 웹 기반 보안 구성
        @SuppressWarnings("deprecation")
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                return http
                                .authorizeRequests(auth -> auth // 인증, 인가 설정
                                                .requestMatchers(new AntPathRequestMatcher("/api/token")).permitAll()
                                                .requestMatchers(new AntPathRequestMatcher("/api/**")).authenticated()
                                                .anyRequest().permitAll())
                                .formLogin(formLogin -> formLogin // form 기반 로그인 설정
                                                .loginPage("/login")
                                                .failureUrl("/login")
                                                .defaultSuccessUrl("/main"))
                                .oauth2Login(oauth2 -> oauth2
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/main")
                                                .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                                                .userService(customUserService))
                                                .permitAll())
                                .logout(logout -> logout // 로그아웃 설정
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/main")
                                                .invalidateHttpSession(true))
                                .csrf(AbstractHttpConfigurer::disable) // csrf 비활성화
                                .build();
        }

        // 인증 관리자 관련 설정
        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http,
                        BCryptPasswordEncoder bCryptPasswordEncoder,
                        UserDetailService userDetailService) throws Exception {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userService);
                authProvider.setPasswordEncoder(bCryptPasswordEncoder);
                return new ProviderManager(authProvider);
        }

        // 패스워드 인코더로 사용할 빈 등록
        @Bean
        public BCryptPasswordEncoder bCryptPasswordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
