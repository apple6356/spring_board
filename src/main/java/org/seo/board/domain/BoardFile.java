package org.seo.board.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class BoardFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "stored_file_name")
    private String storedFileName;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    @Builder
    public BoardFile(Board board, String originalFileName, String storedFileName) {
        this.board = board;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
    }
}
