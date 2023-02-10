package com.myboard.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "article",
        indexes = {
                    @Index(name = "created_idx", columnList = "created_at"),
                    @Index(name = "title_idx", columnList = "title")
        }
)
public class Article extends BaseColumn {

    @Column(name = "title", nullable = false, length = 30)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT", length = 2000)
    private String content;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleComment> articleCommentList = new ArrayList<>();

    @Builder
    public Article(String title, String content, Long viewCount, Board board, User user) {
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.board = board;
        this.user = user;
        addArticleToBoard(board);
    }

    public void addArticleToBoard(Board board) {
        if (board != null) {
            board.getArticleList().add(this);
        }
    }

    public void updateArticleTitle(String newTitle) {
        this.title = newTitle;
    }

    public void updateArticleContent(String newContent) {
        this.content = newContent;
    }


}
