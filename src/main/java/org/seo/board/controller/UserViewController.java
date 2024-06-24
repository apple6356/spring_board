package org.seo.board.controller;

import lombok.RequiredArgsConstructor;
import org.seo.board.service.UserDetailService;
import org.seo.board.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class UserViewController {

    private final UserService userService;
    private final UserDetailService userDetailService;

    @GetMapping("/oauthLogin")
    public String loginOauth() {
        return "oauthLogin";
    }

    @GetMapping("/login")
    public String login() {
        return "Login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

}
