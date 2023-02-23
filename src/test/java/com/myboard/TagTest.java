package com.myboard;

import com.myboard.entity.Tag;
import com.myboard.exception.board.TagNameLengthException;
import com.myboard.exception.board.TagNameNullException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

public class TagTest {

    @Test
    @DisplayName("Tag 객체 생성 성공 - 태그 이름이 같으면 같은 태그")
    void createTag() {
        // given
        Tag tag = Tag.of("it");

        // when, then
        assertThat(tag).isEqualTo(Tag.of("it"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("태그 이름이 공백이거나 null일 경우 예외")
    void whenTagNameIsNullOrEmpty(String tagName) {
        // given, when, then
        assertThatThrownBy(() -> Tag.of(tagName))
                .isInstanceOf(TagNameNullException.class);
    }

    @Test
    @DisplayName("태그 이름이 너무 길면 예외")
    void whenTagNameIsToLong() {
        // given
        String tagName = RandomStringUtils.randomAlphabetic(30);

        // when, then
        assertThatThrownBy(() -> Tag.of(tagName))
                .isInstanceOf(TagNameLengthException.class);
    }


    @ParameterizedTest
    @ValueSource(strings = {"  IT  ", "it", "iT" ,"It"})
    @DisplayName("공백이 제거 후 소문자로 변경되어 생성")
    void convertListToTags(String tagName) {
        // given
        Tag tag = Tag.of("it");

        // when, then
        assertThat(Tag.of(tagName)).isEqualTo(tag);
    }

    @Test
    @DisplayName("태그 이름이 너무 길면 예외")
    void convertListToTags() {
        // given
        List<String> stringList = Arrays.asList("it", "movie", "trip");

        // when
        List<Tag> tags = Tag.convertListToTags(stringList);

        List<String> tagNameList = tags.stream()
                .map(Tag::getName)
                .collect(Collectors.toList());

        // then
        assertThat(tagNameList).isEqualTo(stringList);
    }
}
