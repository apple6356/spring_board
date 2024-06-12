package org.seo.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seo.board.domain.Board;
import org.seo.board.domain.User;
import org.seo.board.dto.AddBoardRequest;
import org.seo.board.dto.UpdateBoardRequest;
import org.seo.board.repository.BoardRepository;
import org.seo.board.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BoardControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper; // 직렬화, 역직렬화를 위한 클래스

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    UserRepository userRepository;

    User user;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        boardRepository.deleteAll();
    }

    @BeforeEach
    void setSecurityContext() {
        userRepository.deleteAll();
        user = userRepository.save(User.builder()
                .email("user@email.com")
                .password("test")
                .build());

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
    }

    @DisplayName("/write : 글 작성")
    @Test
    public void write() throws Exception {
        final String url = "/api/boards";
        final String title = "title";
        final String content = "content";
        final AddBoardRequest boardDTO = new AddBoardRequest(title, content);
        // 객체 json으로 직렬화
        final String requestBody = objectMapper.writeValueAsString(boardDTO);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("username");

        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .principal(principal)
                .content(requestBody));

        result.andExpect(status().isCreated());
        List<Board> boards = boardRepository.findAll();

        assertThat(boards.size()).isEqualTo(1);
        assertThat(boards.get(0).getTitle()).isEqualTo(title);
        assertThat(boards.get(0).getContent()).isEqualTo(content);

        for (Board board : boards) {
            System.out.println("board.getTitle() = " + board.getTitle());
            System.out.println("board.getContent() = " + board.getContent());
        }
    }


    @DisplayName("/board : 글 전체 조회")
    @Test
    public void findAll() throws Exception {
        final String url = "/api/boards";
        Board saveBoard = createDefaultBoard();

        final ResultActions result = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(saveBoard.getContent()))
                .andExpect(jsonPath("$[0].title").value(saveBoard.getTitle()));
    }

    @DisplayName("/board/{id} : 글 조회")
    @Test
    public void findById() throws Exception {
        final String url = "/api/boards/{id}";
        Board saveBoard = createDefaultBoard();

        final ResultActions result = mockMvc.perform(get(url, saveBoard.getId()));

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(saveBoard.getContent()))
                .andExpect(jsonPath("$.title").value(saveBoard.getTitle()));
    }

    @DisplayName("/board/{id} : 글 삭제")
    @Test
    public void deleteBoard() throws Exception {
        final String url = "/api/boards/{id}";
        Board saveBoard = createDefaultBoard();

        mockMvc.perform(delete(url, saveBoard.getId()))
                .andExpect(status().isOk());

        List<Board> boardList = boardRepository.findAll();
        assertThat(boardList).isEmpty();
    }

    @DisplayName("/api/boards/{id} : 글 수정")
    @Test
    public void updateBoard() throws Exception {
        final String url = "/api/boards/{id}";
        Board saveBoard = createDefaultBoard();

        final String newTitle = "update";
        final String newContent = "update content";

        UpdateBoardRequest request = new UpdateBoardRequest(newTitle, newContent);

        ResultActions result = mockMvc.perform(put(url, saveBoard.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk());

        Board board = boardRepository.findById(saveBoard.getId()).get();

        assertThat(board.getTitle()).isEqualTo(newTitle);
        assertThat(board.getContent()).isEqualTo(newContent);

        System.out.println("board.getTitle() = " + board.getTitle());
        System.out.println("board.getContent() = " + board.getContent());
    }

    private Board createDefaultBoard() {
        return boardRepository.save(Board.builder()
                .title("title")
                .author(user.getUsername())
                .content("content")
                .build());
    }
}