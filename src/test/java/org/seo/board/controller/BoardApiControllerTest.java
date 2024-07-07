package org.seo.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seo.board.config.auth.CustomSecurityUserDetails;
import org.seo.board.config.error.ErrorCode;
import org.seo.board.domain.Board;
import org.seo.board.domain.Comment;
import org.seo.board.domain.User;
import org.seo.board.dto.*;
import org.seo.board.repository.BoardRepository;
import org.seo.board.repository.CommentRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BoardApiControllerTest {

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

    @Autowired
    CommentRepository commentRepository;

    User user;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        boardRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @BeforeEach
    void setSecurityContext() {
        userRepository.deleteAll();
        user = userRepository.save(User.builder()
                .email("user@email.com")
                .password("test")
                .build());

        CustomSecurityUserDetails userDetails = new CustomSecurityUserDetails(user);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), userDetails.getAuthorities()));
    }

    @DisplayName("/api/boards : 글 작성")
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

    @DisplayName("/api/recommend/{id} : 글 추천")
    @Test
    public void recommendBoard() throws Exception {
        final String url = "/api/recommend/{id}";
        Board saveBoard = createDefaultBoard();

        ResultActions resultActions = mockMvc.perform(put(url, saveBoard.getId()))
                .andExpect(status().isOk());

        Board board = boardRepository.findById(saveBoard.getId()).get();

        assertThat(board.getRecommend()).isEqualTo(1L);

        System.out.println("board.getRecommend() = " + board.getRecommend());
    }

    @DisplayName("addBoard: 글 생성 시 title이 null이면 실패")
    @Test
    public void addBoard() throws Exception {
        final String url = "/api/boards";
        final String title = null;
        final String content = "content";
        final AddBoardRequest userRequest = new AddBoardRequest(title, content);

        final String requestBody = objectMapper.writeValueAsString(userRequest);
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("username");

        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .principal(principal)
                .content(requestBody));

        result.andExpect(status().isBadRequest());
    }

    @DisplayName("addBoards: 글 생성 시 title이 20자를 넘으면 실패")
    @Test
    public void addBoardSizeValidation() throws Exception {
        Faker faker = new Faker();

        final String url = "/api/boards";
        final String title = faker.lorem().characters(51);
        final String content = "content";
        final AddBoardRequest userRequest = new AddBoardRequest(title, content);

        final String requestBody = objectMapper.writeValueAsString(userRequest);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("username");

        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .principal(principal)
                .content(requestBody));

        result.andExpect(status().isBadRequest());
    }

    @DisplayName("findBoard: 잘못된 Http 메서드로 게시글 조회하면 조회 실패")
    @Test
    public void invalidHttpMethod() throws Exception {
        final String url = "/api/boards/{id}";

        final ResultActions result = mockMvc.perform(post(url, 1));

        result
                .andDo(print()) // 응답이 어떻게 나오는지 콘솔 로그에서 확인 가능
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.message").value(ErrorCode.METHOD_NOT_ALLOWED.getMessage()));
    }

    @DisplayName("findBoard: 존재하지 않는 게시글 조회하면 실패")
    @Test
    public void findBoardInvalidBoard() throws Exception {

        final String url = "/api/boards/{id}";
        final long invalidId = 100;

        final ResultActions result = mockMvc.perform(get(url, invalidId));

        result
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ErrorCode.BOARD_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.code").value(ErrorCode.BOARD_NOT_FOUND.getCode()));
    }

    @DisplayName("addComment : 댓글 추가에 성공")
    @Test
    public void addComment() throws Exception {
        final String url = "/api/comments";

        Board savedBoard = createDefaultBoard();
        final Long boardId = savedBoard.getId();
        final String content = "content";
        final AddCommentRequest userRequest = new AddCommentRequest(boardId, content);
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("username");

        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .principal(principal)
                .content(requestBody));

        result.andExpect(status().isCreated());

        List<Comment> comments = commentRepository.findAll();

        assertThat(comments.size()).isEqualTo(1);
        assertThat(comments.get(0).getBoard().getId()).isEqualTo(boardId);
        assertThat(comments.get(0).getContent()).isEqualTo(content);
    }

    @DisplayName("/api/comments/{id} : 댓글 수정")
    @Test
    public void updateComment() throws Exception {
        final String url = "/api/comments/{id}";

        Board board = createDefaultBoard();
        Comment saveComment = createDefaultComment(board);
        String newContent = "newContent";

        final UpdateCommentRequest commentRequest = new UpdateCommentRequest(newContent);

        ResultActions result = mockMvc.perform(put(url, saveComment.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(commentRequest)));

        result.andExpect(status().isOk());

        Comment comment = commentRepository.findById(saveComment.getId()).get();

        assertThat(comment.getContent()).isEqualTo(newContent);

        System.out.println("comment.getContent() = " + comment.getContent());
    }

    @DisplayName("/api/comments/{id} : 댓글 삭제")
    @Test
    public void deleteComment() throws Exception {
        final String url = "/api/comments/{id}";

        Board board = createDefaultBoard();
        Comment saveComment = createDefaultComment(board);

        mockMvc.perform(delete(url, saveComment.getId()))
                .andExpect(status().isOk());

        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).isEmpty();
    }

    @DisplayName("/api/comment-recommend/{id} : 댓글 추천")
    @Test
    public void recommendComment() throws Exception {
        final String url = "/api/comment-recommend/{id}";

        Board board = createDefaultBoard();
        Comment saveComment = createDefaultComment(board);
        Long recommend = saveComment.getRecommend();

        final RecommendCommentRequest commentRequest = new RecommendCommentRequest(recommend);

        ResultActions result = mockMvc.perform(put(url, saveComment.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(commentRequest)));

        result.andExpect(status().isOk());

        Comment comment = commentRepository.findById(saveComment.getId()).get();

        assertThat(comment.getRecommend()).isEqualTo(1);

        System.out.println("comment.getRecommend() = " + comment.getRecommend());
    }

    private Board createDefaultBoard() {
        return boardRepository.save(Board.builder()
                .title("title")
                .author(user.getEmail())
                .content("content")
                .build());
    }

    private Comment createDefaultComment(Board board) {

        return commentRepository.save(Comment.builder()
                .author("author")
                .content("content")
                .board(board)
                .build());
    }
}