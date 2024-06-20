package org.seo.board.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "users")
@NoArgsConstructor
@Getter
@Entity
public class User implements UserDetails { // UserDetails를 상속받아 인증 객체로 사용

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "username", unique = true)
    private String username;

    @Builder
    public User(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    @Override // 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override // 고유한 값 반환
    public String getUsername() {
        return email;
    }

    @Override // 사용자의 패스워드 반환
    public String getPassword() {
        return password;
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

    // 사용자 이름 변경
    public User update(String username) {
        this.username = username;

        return this;
    }
}
