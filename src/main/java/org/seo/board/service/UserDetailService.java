package org.seo.board.service;

import lombok.RequiredArgsConstructor;
import org.seo.board.config.auth.CustomSecurityUserDetails;
import org.seo.board.domain.User;
import org.seo.board.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
// 스프링 시큐리티에서 사용자 정보를 가져오는 인터페이스
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    // 사용자 이메일로 정보를 가져오는 메서드
    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException((email)));
        return new CustomSecurityUserDetails(user);
    }
}
