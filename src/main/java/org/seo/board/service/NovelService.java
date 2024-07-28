package org.seo.board.service;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.seo.board.domain.Board;
import org.seo.board.domain.BoardFile;
import org.seo.board.domain.Chapter;
import org.seo.board.domain.Novel;
import org.seo.board.dto.AddChapterRequest;
import org.seo.board.dto.AddNovelRequest;
import org.seo.board.dto.BoardListViewResponse;
import org.seo.board.dto.ChapterViewResponse;
import org.seo.board.dto.NovelListViewRequest;
import org.seo.board.dto.NovelViewResponse;
import org.seo.board.dto.UpdateChapterRequest;
import org.seo.board.dto.UpdateNovelRequest;
import org.seo.board.repository.ChapterRepository;
import org.seo.board.repository.NovelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NovelService {

    private final NovelRepository novelRepository;
    private final ChapterRepository chapterRepository;

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

    // 내 서재
	// public Page<NovelViewResponse> myLibrary(String username, Pageable pageable) {

    //     int page = pageable.getPageNumber() - 1;
    //     int pageLimit = 20; // 한페이지에 보여줄 글 갯수

    //     Page<Novel> novelPage = novelRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "lastUpdatedAt")));

    //     Page<NovelViewResponse> novelList = novelPage.map(NovelViewResponse::new);

    //     return novelList;
	// }

}
