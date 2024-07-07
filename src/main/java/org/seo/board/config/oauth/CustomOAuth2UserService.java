package org.seo.board.config.oauth;

import lombok.RequiredArgsConstructor;
import org.seo.board.config.auth.CustomSecurityUserDetails;
import org.seo.board.config.oauth.provider.GoogleUserInfo;
import org.seo.board.config.oauth.provider.NaverUserInfo;
import org.seo.board.config.oauth.provider.OAuth2UserInfo;
import org.seo.board.domain.User;
import org.seo.board.domain.UserRole;
import org.seo.board.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 로그인하려는 서비스 구분 ( google인지 naver인지 구분 )
        String provider = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("provider = " + provider);

        OAuth2UserInfo oAuth2UserInfo = null;

        // 서비스에 따라
        if (provider.equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (provider.equals("naver")) {
            oAuth2UserInfo = new NaverUserInfo((Map<String, Object>) oAuth2User.getAttributes().get("response"));
        }

        System.out.println("oAuth2UserInfo.getProvider() = " + oAuth2UserInfo.getProvider());
        System.out.println("oAuth2UserInfo.getProviderId() = " + oAuth2UserInfo.getProviderId());

        String providerId = oAuth2UserInfo.getProviderId();
        String email = oAuth2UserInfo.getEmail();
        String name = provider + "_" + providerId;

        Optional<User> userOptional = userRepository.findByEmail(email);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            // 이미 존재한다면 업데이트
            userRepository.save(user);
        } else {
            user = User.builder()
                    .email(email)
                    .username(name)
                    .role(UserRole.USER)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(user);
        }

        return new CustomSecurityUserDetails(user, oAuth2User.getAttributes());

//        saveOrUpdate(oAuth2User);

//        return oAuth2User;
    }

//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//
//        User user = saveOrUpdate(oAuth2User);
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//
//        return new DefaultOAuth2User(
//                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
//                attributes,
//                "name"
//        );
//    }

//    private User saveOrUpdate(OAuth2User oAuth2User) throws OAuth2AuthenticationException {
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//        String email = (String) attributes.get("email");
//        String name = (String) attributes.get("name");
//        User user = userRepository.findByEmail(email)
//                .map(entity -> entity.update(name))
//                .orElse(User.builder()
//                        .email(email)
//                        .nickname(name)
//                        .build());
//
//        return userRepository.save(user);
//    }


}
