package org.seo.board.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.seo.board.domain.Chapter;
import org.seo.board.domain.ChapterComment;
import org.seo.board.domain.Comment;
import org.seo.board.domain.Novel;
import org.seo.board.domain.User;
import org.seo.board.domain.UserShelf;
import org.seo.board.dto.AddChapterCommentRequest;
import org.seo.board.dto.AddChapterRequest;
import org.seo.board.dto.AddCommentRequest;
import org.seo.board.dto.AddNovelRequest;
import org.seo.board.dto.ChapterViewResponse;
import org.seo.board.dto.NovelViewResponse;
import org.seo.board.dto.UpdateChapterCommentRequest;
import org.seo.board.dto.UpdateChapterRequest;
import org.seo.board.dto.UpdateNovelRequest;
import org.seo.board.dto.UpdateUserShelfRequest;
import org.seo.board.dto.UserShelfViewResponse;
import org.seo.board.repository.ChapterCommentRepository;
import org.seo.board.repository.ChapterRepository;
import org.seo.board.repository.CommentRepository;
import org.seo.board.repository.NovelRepository;
import org.seo.board.repository.UserShelfRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NovelService {

    private final NovelRepository novelRepository;
    private final ChapterRepository chapterRepository;
    private final UserShelfRepository userShelfRepository;
    private final ChapterCommentRepository chapterCommentRepository;

    // 소설 생성
    public Novel save(AddNovelRequest request, String username)
            throws IllegalStateException, IOException {

        Novel novel;

        if (request.getCoverImage() != null && !request.getCoverImage().isEmpty()) {
            // 표지 이미지 저장
            String novelDirPath = "D:/cover_image/" + username + "/" + request.getTitle();
            File novelDir = new File(novelDirPath);

            if (!novelDir.exists()) {
                novelDir.mkdirs();
            }

            String filePath = novelDirPath + "/" + request.getCoverImage().getOriginalFilename();
            // request의 file을 filePath의 경로에 저장
            request.getCoverImage().transferTo(new File(filePath));

            novel = novelRepository
                    .save(request.toEntity(username, novelDirPath, request.getCoverImage().getOriginalFilename()));
        } else {
            novel = novelRepository.save(request.toEntity(username));
        }

        return novel;
    }

    // 소설 수정
    public Novel update(Long id, UpdateNovelRequest request, String username)
            throws IllegalStateException, IOException {

        Novel novel = novelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        // 표지 경로
        String newDirPath = "D:/cover_image/" + username + "/" + request.getTitle();
        File newDir = new File(newDirPath);
        File oldDir = new File("D:/cover_image/" + username + "/" + novel.getTitle());

        // 소설 제목이 변경되면 폴더의 이름도 변경
        if (!(novel.getTitle().equals(request.getTitle()))) {
            if (oldDir.exists()) {
                oldDir.renameTo(newDir);
            }
        }

        if (request.getCoverImage() != null && !request.getCoverImage().isEmpty()) {
            // 기존 표지 삭제
            if (novel.getCoverImagePath() != null) {
                File deleteFile = new File(novel.getCoverImagePath());
                if (deleteFile.exists()) {
                    deleteFile.delete();
                }
            }

            if (!newDir.exists()) {
                newDir.mkdirs();
            }

            String filePath = newDirPath + "/" + request.getCoverImage().getOriginalFilename();
            // request의 file을 filePath의 경로에 저장
            request.getCoverImage().transferTo(new File(filePath));

            novel.updateCoverImage(newDirPath, request.getCoverImage().getOriginalFilename());
        }

        novel.update(request.getTitle(), request.getContent());

        return novelRepository.save(novel);
    }

    // 소설 삭제
    public void delete(Long id) {
        Novel novel = novelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        // 소설 삭제 될 때 저장된 coverimage도 함께
        String novelDirPath = "D:/cover_image/" + novel.getAuthor() + "/" + novel.getTitle();
        File novelDir = new File(novelDirPath);

        if (novelDir.exists()) {
            for (File file : novelDir.listFiles()) {
                file.delete();
            }
            novelDir.delete();
        }

        novelRepository.delete(novel);
    }

    public Novel findById(Long id) {
        return novelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    // 소설 목록
    public Page<NovelViewResponse> novelPaging(Pageable pageable) {
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 10; // 한페이지에 보여줄 글 갯수

        Page<Novel> novelPage = novelRepository
                .findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "lastUpdatedAt")));

        Page<NovelViewResponse> novelList = novelPage.map(NovelViewResponse::new);

        return novelList;
    }

    // 최신순(desc)
    public List<ChapterViewResponse> findByIdDesc(Long novelId) {
        System.out.println("desc");

        List<Chapter> chapters = chapterRepository.findByNovelIdOrderByEpisodeDesc(novelId);

        List<ChapterViewResponse> chapterList = chapters.stream().map(ChapterViewResponse::new)
                .collect(Collectors.toList());

        return chapterList;
    }

    // 1화부터(asc)
    public List<ChapterViewResponse> findByIdAsc(Long novelId) {
        System.out.println("asc");

        List<Chapter> chapters = chapterRepository.findByNovelIdOrderByEpisodeAsc(novelId);

        List<ChapterViewResponse> chapterList = chapters.stream().map(ChapterViewResponse::new)
                .collect(Collectors.toList());

        return chapterList;
    }

    // 회차 조회
    public Chapter getChapter(Long id) {
        return chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    // 자신의 작품 목록
    public List<Novel> myNovels(String username) {

        List<Novel> novelList = novelRepository.findByAuthor(username);

        return novelList;
    }

    // 새 회차 등록
    public Chapter saveChapter(AddChapterRequest request, String username) {
        Novel novel = novelRepository.findById(request.getNovelId())
                .orElseThrow(() -> new IllegalArgumentException("not found: " + request.getNovelId()));

        System.out.println("novel: " + novel.getId());
        System.out.println("requset novelId: " + request.getNovelId());

        Long topEpisode = 0L;

        // 제일 높은 회차를 가져온다
        Optional<Chapter> episodeChapter = chapterRepository
                .findTopEpisodeByNovelIdOrderByEpisodeDesc(request.getNovelId());

        if (episodeChapter.isPresent()) {
            topEpisode = episodeChapter.get().getEpisode();
        }

        System.out.println("topEpisode: " + topEpisode);
        Chapter chapter = chapterRepository.save(request.toEntity(novel, username, topEpisode));

        novel.updateTime(chapter.getCreatedAt());
        novelRepository.save(novel);

        return chapter;
    }

    // 회차 수정
    public Chapter updateChapter(UpdateChapterRequest request, Long id, String username) {

        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        chapter.update(request.getTitle(), request.getContent());
        chapterRepository.save(chapter);

        return chapter;
    }

    // 최신 회차 삭제
    public void deleteChapter(Long novelId) {
        Chapter chapter = chapterRepository.findTopEpisodeByNovelIdOrderByEpisodeDesc(novelId)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + novelId));

        chapterRepository.delete(chapter);
    }

    // main 화면 상위 10개
    public List<Novel> findTop() {
        return novelRepository.findTop10ByOrderByIdDesc();
    }

    // 로그인 된 상태로 회차 조회시 usershelf에 저장
    public UserShelf saveUserShelf(User user, Chapter chapter, Novel novel) {

        Optional<UserShelf> userShelfOp = userShelfRepository.findByUserAndNovel(user, novel);
        UserShelf userShelf;

        if (userShelfOp.isPresent()) {
            // usershelf 존재하면
            userShelf = userShelfOp.get();
            userShelf.update(chapter.getId(), LocalDateTime.now(), chapter.getEpisode());
        } else {
            // usershelf 존재하지 않는다면
            userShelf = new UserShelf(chapter.getId(), novel, user, LocalDateTime.now(), chapter.getEpisode());
        }

        // 다음 회차 ( 다음 회차가 존재하면 다음화 보기 기능을 활성화 )
        Optional<Chapter> nextChapterOp = chapterRepository.findFirstByNovelAndEpisodeGreaterThanOrderByEpisode(novel,
                chapter.getEpisode());
        Chapter nextChapter;

        if (nextChapterOp.isPresent()) {
            nextChapter = nextChapterOp.get();
            userShelf.nextChapterId(nextChapter.getId());
        } else {
            userShelf.nextChapterId(null);
        }

        return userShelfRepository.save(userShelf);
    }

    // 내 서재
    public Page<UserShelf> myShelf(User user, String viewpage, Pageable pageable) {
        Page<UserShelf> userShelfList = null;
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 10; // 한페이지에 보여줄 글 갯수

        if (viewpage.equals("favorite")) {
            userShelfList = userShelfRepository.findByUserAndFavoriteTrue(user, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "lastReadDate")));
        } else {
            userShelfList = userShelfRepository.findByUserAndLastReadChapterIdIsNotNull(user, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "lastReadDate")));
        }


        return userShelfList;
    }

    // 댓글 작성
    public ChapterComment saveComment(AddChapterCommentRequest request, User user) {
        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new IllegalArgumentException("not found" + request.getChapterId()));

        ChapterComment chaptercomment = chapterCommentRepository.save(request.toEntity(chapter, user.getUsername()));

        return chaptercomment;
    }

    // 댓글 수정
    public ChapterComment updateComment(Long id, UpdateChapterCommentRequest request) {
        ChapterComment chapterComment = chapterCommentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        chapterComment.update(request.getContent());

        chapterCommentRepository.save(chapterComment);

        return chapterComment;
    }

    // 댓글 삭제
    public void deleteComment(Long id) {
        chapterCommentRepository.deleteById(id);
    }

    // 댓글 추천
    public ChapterComment recommendComment(Long id) {
        ChapterComment chapterComment = chapterCommentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        chapterComment.updateRecommend();
        chapterCommentRepository.save(chapterComment);

        return chapterComment;
    }

    // novel.html에서 마지막으로 본 회차 리턴
    public UserShelf findUserShelf(Long userId, Long novelId) {
        UserShelf userShelf = userShelfRepository.findByUserIdAndNovelId(userId, novelId)
                .orElse(null);

        return userShelf;
    }

    // 다음화 정보
    public Chapter getNextChapter(Chapter chapter) {
        Chapter nextChapter = chapterRepository.findNextChapter(chapter.getNovel().getId(), chapter.getEpisode())
                .orElse(null);

        return nextChapter;
    }

    // 이전화 정보
    public Chapter getPreChapterId(Chapter chapter) {
        Chapter preChapter = chapterRepository.findPreChapter(chapter.getNovel().getId(), chapter.getEpisode())
                .orElse(null);

        return preChapter;
    }

    // 읽던 위치 저장
    @Transactional
    public UserShelf updateReadPosition(UpdateUserShelfRequest request) {
        UserShelf userShelf = userShelfRepository.findByLastReadChapterId(request.getChapterId())
                .orElseThrow(() -> new IllegalArgumentException("not found chapterId: " + request.getChapterId()));

        userShelf.updateReadPosition(request.getReadPosition(), request.getMaxScroll());

        return userShelfRepository.save(userShelf);
    }

    // 다 읽었는지 확인
    public boolean isChapterReadFinished(UserShelf userShelf) {
        if (userShelf.getReadPosition() == null || userShelf.getMaxScroll() == null) {
            return false;
        }
        // 2정도의 오차 허용
        return userShelf.getReadPosition() >= (userShelf.getMaxScroll() - 2);
    }

    // userShelf 조회
    public UserShelf findByUserShelf(Long userId, Long novelId) {
        return userShelfRepository.findByUserIdAndNovelId(userId, novelId).orElse(null);
    }

    // chapterComment list 반환
    public List<ChapterComment> getChapterComments(Long chapterId) {
        return chapterCommentRepository.findByChapterId(chapterId);
    }

    // 소설 추천
    @Transactional
    public UserShelf recommendNovel(Long novelId, User user) {
        Novel novel = novelRepository.findById(novelId)
                .orElseThrow(() -> new IllegalArgumentException("not found novelId: " + novelId));

        UserShelf userShelf = userShelfRepository.findByNovelIdAndUserId(novelId, user.getId())
                .orElseGet(() -> new UserShelf(user, novel));

        if (userShelf != null && userShelf.getRecommend()) {
            // 이미 추천했다면 추천 취소
            novel.recommendCancle();
            userShelf.recommendCancle();
            novelRepository.save(novel);
            userShelfRepository.save(userShelf);
            return userShelf;
        }

        novel.recommendNovel();
        novelRepository.save(novel);
        userShelf.recommendNovel();
        userShelfRepository.save(userShelf);

        return userShelf;
    }

    // 선호작 등록 / 해제
    public UserShelf favoriteNovel(User user, Long novelId) {
        UserShelf userShelf = userShelfRepository.findByNovelIdAndUserId(novelId, user.getId())
                .orElseGet(() -> {
                    Novel novel = novelRepository.findById(novelId)
                            .orElseThrow(() -> new IllegalArgumentException("not found novelID: " + novelId));
                    return new UserShelf(user, novel);
                });

        userShelf.favorite();
        userShelfRepository.save(userShelf);

        return userShelf;
    }

}
