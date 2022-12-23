package com.myboard.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "board",
        indexes = @Index(name = "name_idx", columnList = "board_name")
)
public class Board extends BaseColumn {

    @Column(name = "board_name", nullable = false, length = 30)
    private String boardName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ElementCollection
    @CollectionTable(name = "board_tags", joinColumns = @JoinColumn(name = "board_id", nullable = false))
    List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Article> articleList = new ArrayList<>();

    @Builder
    public Board(String boardName, User user) {
        this.boardName = boardName;
        this.user = user;
        addBoardToUser(user);
    }

    private void addBoardToUser(User user) {
        user.getBoardList().add(this);
    }

    public void updateBoardName(String newBoardName) {
        this.boardName = newBoardName;
    }

    public void addTags(List<Tag> tags) {
        this.getTags().addAll(tags);
    }

    public void clearAndAddNewTags(List<Tag> newTags) {
        this.getTags().clear();
        this.getTags().addAll(newTags);
    }
}
