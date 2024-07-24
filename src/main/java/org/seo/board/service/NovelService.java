package org.seo.board.service;

import java.io.File;
import java.util.List;

import org.seo.board.domain.Board;
import org.seo.board.domain.BoardFile;
import org.seo.board.domain.Novel;
import org.seo.board.dto.AddNovelRequest;
import org.seo.board.dto.NovelListViewRequest;
import org.seo.board.dto.UpdateNovelRequest;
import org.seo.board.repository.NovelRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NovelService {

    private final NovelRepository novelRepository;

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

}
