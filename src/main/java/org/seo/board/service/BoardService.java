package org.seo.board.service;

import lombok.RequiredArgsConstructor;
import org.seo.board.config.error.exception.BoardNotFoundException;
import org.seo.board.domain.Board;
import org.seo.board.domain.BoardFile;
import org.seo.board.domain.Comment;
import org.seo.board.dto.*;
import org.seo.board.repository.BoardRepository;
import org.seo.board.repository.CommentRepository;
import org.seo.board.repository.FileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor // final이나 @NotNull인 필드의 생성자 추가
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final FileRepository fileRepository;

    // 글 작성(저장)
    public Board save(AddBoardRequest request, String userName) {
        Board board = boardRepository.save(request.toEntity(userName));

        return board;
    }

    // 이미지를 임시 저장 폴더에서 최종 저장 폴더로 이동
    @Transactional
    public void imageUpload(Long id, String content) throws IOException {
        System.out.println("imageUpload");
        System.out.println("content = " + content);

        AddBoardFileRequest fileRequest = new AddBoardFileRequest();

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        String targetDir = "/files/" + id;

        // 정규식 패턴
        /*
            "<img[^>]+src=\"([^\"]+)\"": 이 정규 표현식은 img 태그의 src 속성 값을 추출하기 위해 설계되었습니다.
            "<img": 문자열이 <img로 시작하는 것을 찾는다
            [^>]+: >가 아닌 모든 문자를 1개 이상 포함하는 패턴을 찾는다
            src=\": src=" 문자열을 찾는다.
            ([^\"]+): 큰따옴표(")가 아닌 모든 문자를 1개 이상 포함하는 패턴을 찾는다 src 속성 값 자체를 추출하는 역할을 한다
            \": 닫는 큰따옴표(")를 찾는다
        */
        String imgTagPattern = "<img[^>]+src=\"([^\"]+)\"";
        Pattern pattern = Pattern.compile(imgTagPattern);
        Matcher matcher = pattern.matcher(content);

        // 패턴이 일치하는 다음 문자열이 있으면 루프
        while (matcher.find()) {
            String src = matcher.group(1);

            // 파일 이름 추출
            String filename = src.substring(src.lastIndexOf("/") + 1);

            if (src.contains("/files/temp/")) {
                moveFileDirectory(id, filename);

                String originalFilename = filename.substring(filename.indexOf("_") + 1);

                fileRepository.save(fileRequest.toEntity(board, originalFilename, filename, targetDir));
            }
        }

        String updateContent = content.replaceAll("/files/temp/", "/files/" + id + "/");

        boardRepository.updateContent(id, updateContent);
    }

    // 실제 파일 이동
    public void moveFileDirectory(Long id, String filename) throws IOException {
        Path tempFilePath = Paths.get("/files/temp/", filename); // 임시 폴더
        Path boardDirPath = Paths.get("/files/" + id); // 이동할 폴더

        // 해당 디렉토리가 존재하지 않으면 생성
        if (!Files.exists(boardDirPath)) {
            Files.createDirectories(boardDirPath);
        }

        Path targetFilePath = boardDirPath.resolve(filename);

        // StandardCopyOption.REPLACE_EXISTING : 이미 파일이 존재한다면 대체
        Files.move(tempFilePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING); // 파일 저장
    }

    // 글 전체 조회
    public List<Board> findAll() {
        return boardRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    // 글 10개만 조회
    public List<Board> findTop() {
        return boardRepository.findTop10ByOrderByIdDesc();
    }

    // 추천 30 이상 글 10개만 조회
    public List<Board> findTopPopular() {
        return boardRepository.findTop10ByRecommendGreaterThanEqualOrderByIdDesc(30);
    }

    // 글 조회
    public Board findById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);
//                .orElseThrow(IllegalArgumentException("not found : " + id));
    }

    // 게시글을 작성한 유저인지 확인
//    private static void authorizeBoardAuthor(Board board) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        if (!board.getAuthor().equals(userName)) {
//            throw new IllegalArgumentException("not authorized");
//        }
//    }

    // 글 삭제
    public void delete(Long id) {
//        boardRepository.deleteById(id);

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        if (board.getFiles() != null) { // 게시글의 파일과 폴더 삭제
            List<BoardFile> boardFiles = board.getFiles();

            for (BoardFile boardFile : boardFiles) {
                File folder = new File(boardFile.getFilePath());

                while (folder.exists()) { // 폴더가 있는지 확인
                    File[] files = folder.listFiles();

                    for (File file : files) { // 폴더 내 파일 삭제
                        file.delete();
                    }

                    folder.delete(); // 폴더 삭제
                }
            }
        }

//        authorizeBoardAuthor(board);
        boardRepository.delete(board);
    }

    // 글 수정
    @Transactional // 메서드를 하나의 트랜잭션으로 묶음
    public Board update(Long id, UpdateBoardRequest request) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found" + id));

//        authorizeBoardAuthor(board); // 추가
        board.update(request.getTitle(), request.getContent());

        return board;
    }

    // 페이징
    public Page<BoardListViewResponse> paging(Pageable pageable) {
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 10; // 한페이지에 보여줄 글 갯수

        // JpaRepository 의 findAll() 사용시 pageable 인터페이스로 파라미터를 넘기면 페이징 사용가능
        Page<Board> boardPage = boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        Page<BoardListViewResponse> boardList = boardPage.map(BoardListViewResponse::new);

        return boardList;
    }

    // 인기 게시판 페이징
    public Page<BoardListViewResponse> popularPaging(Pageable pageable) {
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 10; // 한페이지에 보여줄 글 갯수

        Page<Board> boardPage = boardRepository.findByRecommendGreaterThanEqualOrderByIdDesc(30, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        Page<BoardListViewResponse> boardList = boardPage.map(BoardListViewResponse::new);

        return boardList;
    }

    // 글 검색
    public Page<BoardListViewResponse> searchBoards(Pageable pageable, String keyword) {
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 10; // 한페이지에 보여줄 글 갯수

        Page<Board> boardPage = boardRepository.findByTitleContains(keyword, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        Page<BoardListViewResponse>  boardList = boardPage.map(BoardListViewResponse::new);

        return boardList;
    }


    // 자신이 작성한 글 목록
    public Page<BoardListViewResponse> myBoards(Pageable pageable, String username) {
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 10; // 한페이지에 보여줄 글 갯수

        Page<Board> boardPage = boardRepository.findByAuthorLike(username, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        Page<BoardListViewResponse>  boardList = boardPage.map(BoardListViewResponse::new);

        return boardList;
    }

    // 자신이 작성한 댓글 목록
    public Page<CommentListViewResponse> myComments(Pageable pageable, String username) {
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 10; // 한페이지에 보여줄 글 갯수

        Page<Comment> commentPage = commentRepository.findByAuthorLike(username, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        Page<CommentListViewResponse>  commentList = commentPage.map(CommentListViewResponse::new);

        return commentList;
    }


    // 댓글 추가
    public Comment addComment(AddCommentRequest request, String username) {
        Board board = boardRepository.findById(request.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("not found : " + request.getBoardId()));

        return commentRepository.save(request.toEntity(username, board));
    }


    // 댓글 수정
    @Transactional
    public Comment updateComment(Long id, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        comment.update(request.getContent());
        commentRepository.save(comment);

        return comment;
    }

    // 댓글 삭제
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        commentRepository.delete(comment);
    }

    // 조회수 +1
    @Transactional
    public void updateHits(Long id) {
        boardRepository.updateHits(id);
    }

    // 추천수 +1
    @Transactional // 메서드를 하나의 트랜잭션으로 묶음
    public Board updateRecommend(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found" + id));

        board.updateRecommend();

        return board;
    }

    // 댓글 추천수 +1
    @Transactional
    public Comment updateCommentRecommend(Long id, RecommendCommentRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found" + id));

        comment.updateRecommend();

        return comment;
    }

}
