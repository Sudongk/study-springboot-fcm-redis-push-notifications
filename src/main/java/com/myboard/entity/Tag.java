package com.myboard.entity;

import com.myboard.exception.board.TagNameLengthException;
import com.myboard.exception.board.TagNameNullException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@EqualsAndHashCode(of = "name")
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {

    private static final int MAX_LENGTH = 20;

    @Column(name = "tag_name", nullable = false, length = MAX_LENGTH)
    private String name;

    private Tag(String tagName) {
        validateNull(tagName);
        validateBlank(tagName);
        validateLength(tagName);
        this.name = tagName;
    }

    public static Tag of(String tagName) {
        return new Tag(tagName);
    }

    public static List<Tag> convertListToTags(List<String> tagNames) {
        return tagNames.stream()
                .map(Tag::of)
                .collect(Collectors.toList());
    }

    private void validateBlank(String value) {
        if (value.isEmpty()) {
            throw new TagNameNullException();
        }
    }

    private void validateNull(String value) {
        if (Objects.isNull(value)) {
            throw new TagNameNullException();
        }
    }

    private void validateLength(String value) {
        if (value.length() > MAX_LENGTH) {
            throw new TagNameLengthException();
        }
    }
}

