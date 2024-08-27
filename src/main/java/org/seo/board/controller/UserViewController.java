package org.seo.board.controller;

import lombok.RequiredArgsConstructor;
import org.seo.board.domain.User;
import org.seo.board.dto.BoardListViewResponse;
import org.seo.board.dto.CommentListViewResponse;
import org.seo.board.service.BoardService;
import org.seo.board.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class UserViewController {

    private final UserService userService;
    private final BoardService boardService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    // 내 정보
    @GetMapping("/info")
    public String info(Model model, @AuthenticationPrincipal Object principal) {

        // 비로그인 시 principal은 null이 아닌 anonymousUser로 출력, class는 string
        if (principal.equals("anonymousUser")) {
            System.out.println("principal is anonymousUser");
            return "redirect:/login";
        }

        String email = "";

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

    // 본인이 작성한 글 목록
    @GetMapping("/myWriting")
    public String myWriting(@PageableDefault(page = 1) Pageable pageable, @RequestParam("username") String username
            , Model model, @AuthenticationPrincipal Object principal) {

        Page<BoardListViewResponse> boardList = boardService.myBoards(pageable, username);

        int blockLimit = 10;
        // 1 11 21 31
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;
        // 10 20 30 40
        int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit - 1 : boardList.getTotalPages();

        int prev = startPage - 1;
        int next = endPage + 1;

        String email = "";

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        }

        if (!email.equals("")) {
            User user = userService.findByEmail(email);
            model.addAttribute("user", user);
        }

        model.addAttribute("boardList", boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("prev", prev);
        model.addAttribute("next", next);
        model.addAttribute("username", username);

        return "myWriting";
    }

    // 본인이 작성한 댓글 목록
    @GetMapping("/myComments")
    public String myComments(@PageableDefault(page = 1) Pageable pageable, @RequestParam("username") String username
            , Model model, @AuthenticationPrincipal Object principal) {

        Page<CommentListViewResponse> commentList = boardService.myComments(pageable, username);

        int blockLimit = 10;
        // 1 11 21 31
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;
        // 10 20 30 40
        int endPage = ((startPage + blockLimit - 1) < commentList.getTotalPages()) ? startPage + blockLimit - 1 : commentList.getTotalPages();

        int prev = startPage - 1;
        int next = endPage + 1;

        String email = "";

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        }

        if (!email.equals("")) {
            User user = userService.findByEmail(email);
            model.addAttribute("user", user);
        }

        model.addAttribute("commentList", commentList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("prev", prev);
        model.addAttribute("next", next);
        model.addAttribute("username", username);

        return "myComments";
    }

}