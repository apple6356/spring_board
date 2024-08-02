package org.seo.board.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.seo.board.domain.Chapter;
import org.seo.board.domain.Novel;
import org.seo.board.domain.User;
import org.seo.board.domain.UserShelf;
import org.seo.board.dto.AddChapterRequest;
import org.seo.board.dto.AddNovelRequest;
import org.seo.board.dto.ChapterViewResponse;
import org.seo.board.dto.NovelViewResponse;
import org.seo.board.dto.UpdateChapterRequest;
import org.seo.board.dto.UpdateNovelRequest;
import org.seo.board.dto.UserShelfViewResponse;
import org.seo.board.repository.ChapterRepository;
import org.seo.board.repository.NovelRepository;
import org.seo.board.repository.UserShelfRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NovelService {

    private final NovelRepository novelRepository;
    private final ChapterRepository chapterRepository;
    private final UserShelfRepository userShelfRepository;

    // 소설 생성
    public Novel save(AddNovelRequest request, String username) {

        Novel novel = novelRepository.save(request.toEntity(username));

        return novel;
    }

    // 소설 수정
    public Novel update(Long id, UpdateNovelRequest request) {

        Novel novel = novelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        novel.update(request.getTitle(), request.getContent());

        return novelRepository.save(novel);
    }

    public Novel findById(Long id) {
        return novelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    // 소설 목록
    public Page<NovelViewResponse> novelPaging(Pageable pageable) {
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 10; // 한페이지에 보여줄 글 갯수

        Page<Novel> novelPage = novelRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "lastUpdatedAt")));

        Page<NovelViewResponse> novelList = novelPage.map(NovelViewResponse::new);

        return novelList;
    }

    // 최신순(desc)
    public List<ChapterViewResponse> findByIdDesc(Long novelId) {
        System.out.println("desc");
        
        List<Chapter> chapters = chapterRepository.findByNovelIdOrderByEpisodeDesc(novelId);

        List<ChapterViewResponse> chapterList = chapters.stream().map(ChapterViewResponse::new).collect(Collectors.toList());

        return chapterList;
    }

    // 1화부터(asc)
    public List<ChapterViewResponse> findByIdAsc(Long novelId) {
        System.out.println("asc");

        List<Chapter> chapters = chapterRepository.findByNovelIdOrderByEpisodeAsc(novelId);

        List<ChapterViewResponse> chapterList = chapters.stream().map(ChapterViewResponse::new).collect(Collectors.toList());

        return chapterList;
    }

    // 회차 조회
    public Chapter findByIdChapter(Long id) {
        return chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    // 자신의 작품 목록
    public List<Novel> myNovels(String username) {

        List<Novel> novelList = novelRepository.findByAuthor(username);

        return novelList;
    }

    // 소설 삭제
    public void delete(Long id) {
        Novel novel = novelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        novelRepository.delete(novel);
    }

    // 새 회차 등록
    public Chapter saveChapter(AddChapterRequest request, String username) {
        Novel novel = novelRepository.findById(request.getNovelId())
                .orElseThrow(() -> new IllegalArgumentException("not found: " + request.getNovelId()));

        System.out.println("novel: " + novel.getId());
        System.out.println("requset novelId: " + request.getNovelId());

        
        Long topEpisode = 0L;
        
        // 제일 높은 회차를 가져온다
        Optional<Chapter> episodeChapter = chapterRepository.findTopEpisodeByNovelIdOrderByEpisodeDesc(request.getNovelId());
        
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
    public void saveUserShelf(User user, Chapter chapter, Novel novel) {

        Optional<UserShelf> userShelfOp = userShelfRepository.findByUserAndNovel(user, novel);
        UserShelf userShelf;

        if(userShelfOp.isPresent()) {
            // usershelf 존재하면
            userShelf = userShelfOp.get();
            userShelf.update(chapter.getId(), LocalDateTime.now(), chapter.getEpisode());
        } else {
            // usershelf 존재하지 않는다면
            userShelf = new UserShelf(chapter.getId(), novel, user, LocalDateTime.now(), chapter.getEpisode());
        }

        // 다음 회차 ( 다음 회차가 존재하면 다음화 보기 기능을 활성화 )
        Optional<Chapter> nextChapterOp = chapterRepository.findFirstByNovelAndEpisodeGreaterThanOrderByEpisode(novel, chapter.getEpisode());
        Chapter nextChapter;

        if (nextChapterOp.isPresent()) {
            nextChapter = nextChapterOp.get();
            userShelf.nextChapterId(nextChapter.getId());
        } else {
            userShelf.nextChapterId(null);
        }

        userShelfRepository.save(userShelf);
    }
    
    // 내 서재
    public Page<UserShelfViewResponse> myShelf(User user, Pageable pageable) {

        int page = pageable.getPageNumber() - 1;
        int pageLimit = 20; // 한페이지에 보여줄 글 갯수

        Page<UserShelf> userShelfPage = userShelfRepository.findByUser(user, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "lastReadDate")));
        Page<UserShelfViewResponse> userShelfs = userShelfPage.map(UserShelfViewResponse::new);

        return userShelfs;
    }

    // 표지 이미지 업로드
    // @Transactional
    // public void coverImage(MultipartFile file, Long id) throws IOException {

    //     String novelDir = "/cover/" + id;
    //     Path novelPath = Paths.get(novelDir);
    //     if (!Files.exists(novelPath)) {
    //         Files.createDirectories(novelPath);
    //     }

    //     String filename = file.getOriginalFilename();

    //     String fileDir = novelDir + "/" + filename;
    //     Path filePath = novelPath.resolve(filename);
    //     Files.write(filePath, file.getBytes());

    //     novelRepository.updateCoverImage(fileDir, id);
    // }


}
