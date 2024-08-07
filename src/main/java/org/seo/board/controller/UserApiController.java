package org.seo.board.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.seo.board.domain.User;
import org.seo.board.dto.AddUserRequest;
import org.seo.board.dto.UpdateUserRequest;
import org.seo.board.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
public class UserApiController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/user")
    public String signup(AddUserRequest request) {
        userService.save(request);
        return "redirect:/main";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        return "redirect:main";
    }

    // 유저 정보 변경 (usrename)
    @PutMapping("/api/user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @RequestBody UpdateUserRequest request) {
        User user = userService.update(id, request);

        return ResponseEntity.ok()
                .body(user);
    }

    // username 중복 확인
    @GetMapping("/check")
    public ResponseEntity<Boolean> nameCheck(@RequestParam("username") String username) {
        System.out.println("username = " + username);
        boolean check = userService.existsByUsername(username);
        System.out.println("check = " + check);

        return ResponseEntity.ok(check);
    }

    // 회원 탈퇴
    @DeleteMapping("/api/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);

        return ResponseEntity.ok().build();
    }

}