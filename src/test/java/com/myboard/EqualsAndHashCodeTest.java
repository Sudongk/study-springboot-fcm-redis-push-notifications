package com.myboard;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class EqualsAndHashCodeTest {

    private static class TagTestClass {
        private String name;

        public TagTestClass(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TagTestClass that = (TagTestClass) o;
            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    private static class TagTestClass2 {
        private String name;

        public TagTestClass2(String name) {
            this.name = name;
        }
    }


    @Test
    void equalsAndHashCodeTestCase() {
        List<TagTestClass> tag1 = new ArrayList<>();
        List<TagTestClass> tag2 = new ArrayList<>();

        TagTestClass tag_IT_1 = new TagTestClass("IT");
        TagTestClass tag_MOVIE_1 = new TagTestClass("MOVIE");
        TagTestClass tag_TRIP_1 = new TagTestClass("TRIP");

        tag1.add(tag_IT_1);
        tag1.add(tag_MOVIE_1);
        tag1.add(tag_TRIP_1);

        TagTestClass tag_IT_2 = new TagTestClass("IT");
        TagTestClass tag_MOVIE_2 = new TagTestClass("MOVIE");
        TagTestClass tag_TRIP_2 = new TagTestClass("TRIP");

        tag2.add(tag_IT_2);
        tag2.add(tag_MOVIE_2);
        tag2.add(tag_TRIP_2);

        assertThat(tag1.containsAll(tag2)).isTrue();
    }

    @Test
    void equalsAndHashCodeTestCase2() {
        List<TagTestClass2> tag1 = new ArrayList<>();
        List<TagTestClass2> tag2 = new ArrayList<>();

        TagTestClass2 tag_IT_1 = new TagTestClass2("IT");
        TagTestClass2 tag_MOVIE_1 = new TagTestClass2("MOVIE");
        TagTestClass2 tag_TRIP_1 = new TagTestClass2("TRIP");

        tag1.add(tag_IT_1);
        tag1.add(tag_MOVIE_1);
        tag1.add(tag_TRIP_1);

        TagTestClass2 tag_IT_2 = new TagTestClass2("IT");
        TagTestClass2 tag_MOVIE_2 = new TagTestClass2("MOVIE");
        TagTestClass2 tag_TRIP_2 = new TagTestClass2("TRIP");

        tag2.add(tag_IT_2);
        tag2.add(tag_MOVIE_2);
        tag2.add(tag_TRIP_2);

        assertThat(tag1.containsAll(tag2)).isFalse();
    }

}
