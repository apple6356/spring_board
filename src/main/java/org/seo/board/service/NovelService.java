package org.seo.board.service;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.seo.board.domain.Board;
import org.seo.board.domain.BoardFile;
import org.seo.board.domain.Chapter;
import org.seo.board.domain.Novel;
import org.seo.board.dto.AddChapterRequest;
import org.seo.board.dto.AddNovelRequest;
import org.seo.board.dto.ChapterViewResponse;
import org.seo.board.dto.NovelListViewRequest;
import org.seo.board.dto.UpdateChapterRequest;
import org.seo.board.dto.UpdateNovelRequest;
import org.seo.board.repository.ChapterRepository;
import org.seo.board.repository.NovelRepository;
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

    // 1화부터(asc)
    public Novel findById(Long id) {
        return novelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    // 최신순(desc)
    public List<ChapterViewResponse> findByIdAsc(Long novelId) {
        List<Chapter> chapters = chapterRepository.findByNovelIdOrderByEpisodeDesc(novelId);

        List<ChapterViewResponse> chapterList = chapters.stream().map(ChapterViewResponse::new).collect(Collectors.toList());

        return chapterList;
    }

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

}
