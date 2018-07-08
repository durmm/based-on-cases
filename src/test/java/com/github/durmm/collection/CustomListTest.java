package com.github.durmm.collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assumptions.assumeThat;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CustomListTest {
    private <T> List<T> create() {
        return new CustomList<>();
    }

    private final static class StringHolder {
        private final String value;

        private StringHolder(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof StringHolder)) {
                return false;
            }

            StringHolder that = (StringHolder) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    @Nested
    class AddSingle {
        @Test
        void addShouldReturnTrueWhenAdded() {
            List<String> list = create();

            assertThat(list.add("a")).isTrue();
        }

        @Test
        void addSingleElementShouldAddElementIntoList() {
            List<String> list = create();

            String expected = "a";
            list.add(expected);

            String actual = list.get(0);
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    class AddCollection {

    }

    @Nested
    class GetByIndex {
        @Test
        void getFirstElementShouldReturnItself() {
            List<String> list = create();

            list.add("a");

            assertThat(list.get(0)).isEqualTo("a");
        }

        @Test
        void getElementByIndexShouldReturnItself() {
            List<String> list = create();

            list.add("a");
            assertThat(list.get(0)).isEqualTo("a");

            list.add("b");
            assertThat(list.get(1)).isEqualTo("b");

            list.add("c");
            assertThat(list.get(2)).isEqualTo("c");
        }
    }

    @Nested
    class GetByIndexExceptional {
        @Test
        @SuppressWarnings("ConstantConditions")
        void getShouldThrowWhenIndexIsNegative() {
            List<String> list = create();

            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> list.get(-1));
        }

        @Test
        void getShouldThrowWhenListIsEmpty() {
            List<String> list = create();

            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> list.get(0));
        }

        @Test
        void getShouldThrowWhenQueryNonExistingIndex() {
            List<String> list = create();

            list.add("a");
            list.add("b");

            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> list.get(2));
        }
    }

    @Nested
    class Size {
        @Test
        void sizeOnEmptyListShouldBeZero() {
            List<String> list = create();

            assertThat(list).hasSize(0);
        }

        @Test
        void isEmptyOnEmptyListShouldReturnTrue() {
            List<String> list = create();

            assertThat(list.isEmpty()).isTrue();
        }

        @Test
        void sizeShouldIncreaseWhenAddingSingleElement() {
            List<String> list = create();

            list.add("a");
            assertThat(list.size()).isEqualTo(1);

            list.add("b");
            assertThat(list.size()).isEqualTo(2);
        }

        @Test
        void isEmptyShouldReturnFalseWhenAddingSingleElement() {
            List<String> list = create();

            list.add("a");

            assertThat(list.isEmpty()).isFalse();
        }

        @Test
        void removeElementShouldIncreaseSize() {
            List<String> list = create();

            list.add("a");
            list.add("b");

            list.remove("a");

            assertThat(list.size()).isEqualTo(1);
        }
    }

    @Nested
    class Remove {
        @Test
        void removeShouldReturnTrueWhenFound() {
            List<String> list = create();
            String element = "a";
            list.add(element);

            assertThat(list.remove(element)).isTrue();
        }

        @Test
        void removeShouldReturnFalseWhenNotFound() {
            List<String> list = create();
            String element = "a";
            list.add(element);

            assertThat(list.remove("b")).isFalse();
        }

        @Test
        void removeShouldReturnTrueWhenRemoveNull() {
            List<String> list = create();
            list.add("a");
            list.add(null);

            // we have null in the list so we should remove it.
            assertThat(list.remove(null)).isTrue();
        }

        @Test
        void removeShouldReturnFalseWhenRemoveNull() {
            List<String> list = create();
            list.add("a");

            assertThat(list.remove(null)).isFalse();
        }

        @Test
        void removeShouldReturnTrueAndUseEqualsMethodToTestEquality() {
            List<StringHolder> list = create();
            list.add(new StringHolder("a"));
            list.add(new StringHolder("b"));

            assertThat(list.remove(new StringHolder("a"))).isTrue();
        }

        @Test
        void removeShouldReturnFalseAndUseEqualsMethodToTestEquality() {
            List<StringHolder> list = create();
            list.add(new StringHolder("a"));
            list.add(new StringHolder("b"));

            assertThat(list.remove(new StringHolder("c"))).isFalse();
        }

        @Test
        void removeShouldReturnFirstOccurrence() {
            List<String> list = create();

            list.add("a");
            list.add("b");
            list.add("a");

            assertThat(list.remove("a")).isTrue();
            assertThat(list).containsExactly("b", "a");
        }
    }

    @Nested
    class IteratorTest {
        @Test
        void iteratorOnEmptyListShouldReturnFalseOnHasNext() {
            Iterator<Object> objectIterator = create().iterator();
            assumeThat(objectIterator).isNotNull();

            assertThat(objectIterator.hasNext()).isFalse();
        }

        @Test
        void iteratorShouldThrowWhenCallingNext() {
            Iterator<Object> objectIterator = create().iterator();
            assumeThat(objectIterator).isNotNull();

            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(objectIterator::next);
        }
    }
}