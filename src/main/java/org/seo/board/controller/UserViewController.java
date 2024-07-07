package org.seo.board.controller;

import lombok.RequiredArgsConstructor;
import org.seo.board.domain.User;
import org.seo.board.service.UserDetailService;
import org.seo.board.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class UserViewController {

    private final UserService userService;
    private final UserDetailService userDetailService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/info")
    public String info(Model model, @AuthenticationPrincipal Object principal) {

        if (principal == null) {
            System.out.println("principal is null");
            return "redirect:/login";
        }

        String email = null;

        // principal의 객체타입이 UserDetails인지 확인
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        }

        User user = userService.findByEmail(email);

        model.addAttribute("user", user);

        return "info";
    }

}