package org.seo.board.config.auth;

import org.seo.board.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomSecurityUserDetails implements UserDetails, OAuth2User {

    private final User user;
    private Map<String, Object> attributes;

    // spring 로그인
    public CustomSecurityUserDetails(User user) {
        this.user = user;
    }

    // oauth 로그인
    public CustomSecurityUserDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override // 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    // 고유한 값 반환
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override // 사용자의 패스워드 반환
    public String getPassword() {
        return user.getPassword();
    }

    @Override // 계정 만료 여부 확인
    public boolean isAccountNonExpired() {
        return true; // true > 만료되지 않음
    }

    @Override // 계정 잠근 여부 확인
    public boolean isAccountNonLocked() {
        return true; // true > 잠금 안됨
    }

    @Override // 패스워드 잠금 여부 확인
    public boolean isCredentialsNonExpired() {
        return true; // true > 잠금 안됨
    }

    @Override // 계정 사용 가능 여부 확인
    public boolean isEnabled() {
        return true; // true > 사용 가능
    }

    // 리소스 서버로부터 받는 정보 ( google 등 )
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // User의 pk
    @Override
    public String getName() {
        return user.getId() + "";
    }
}
