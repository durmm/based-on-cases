package com.github.durmm.collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assumptions.assumeThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
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

            list.add("a");

            assertThat(list).containsOnly("a");
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
        void hasNextShouldReturnFalseOnEmptyList() {
            Iterator<Object> objectIterator = create().iterator();
            assumeThat(objectIterator).isNotNull();

            assertThat(objectIterator.hasNext()).isFalse();
        }

        @Test
        void nextShouldThrowWhenListIsEmpty() {
            Iterator<Object> objectIterator = create().iterator();
            assumeThat(objectIterator).isNotNull();

            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(objectIterator::next);
        }

        @Test
        void nextShouldReturnElementsInSameOrder() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);
            list.add(3);

            Iterator<Integer> iterator = list.iterator();

            assertThat(iterator.next()).isEqualTo(1);
            assertThat(iterator.next()).isEqualTo(2);
            assertThat(iterator.next()).isEqualTo(3);
        }

        @Test
        void nextShouldThrowAfterLastElement() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);
            list.add(3);

            Iterator<Integer> iterator = list.iterator();

            iterator.next(); // 1
            iterator.next(); // 2
            iterator.next(); // 3

            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(iterator::next);
        }

        @Test
        void forEachRemainingFromStartShouldIterateOverAll() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);
            list.add(3);

            List<Integer> actual = new ArrayList<>();
            Iterator<Integer> iterator = list.iterator();

            iterator.forEachRemaining(actual::add);

            assertThat(actual).containsExactly(1, 2, 3);
        }

        @Test
        void forEachRemainingFromMiddleShouldIterateFromPosition() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);
            list.add(3);

            List<Integer> actual = new ArrayList<>();
            Iterator<Integer> iterator = list.iterator();

            iterator.next(); // pull out first element

            iterator.forEachRemaining(actual::add);

            assertThat(actual).containsExactly(2, 3);
        }

        @Test
        void forEachRemainingShouldNotIterateWhenIteratorReachedEnd() {
            List<Integer> list = create();

            list.add(1);

            List<Integer> actual = new ArrayList<>();
            Iterator<Integer> iterator = list.iterator();

            iterator.next(); // reached end

            iterator.forEachRemaining(actual::add);

            assertThat(actual).isEmpty();
        }

        @Test
        void forEachRemainingOnEmptyList() {
            List<Integer> list = create();

            List<Integer> actual = new ArrayList<>();
            Iterator<Integer> iterator = list.iterator();

            iterator.forEachRemaining(actual::add);

            assertThat(actual).isEmpty();
        }

        @Test
        void removeShouldThrowWhenNextHasNotBeenCalledYet() {
            List<Object> list = create();

            Iterator<Object> iterator = list.iterator();

            assertThatIllegalStateException()
                    .isThrownBy(iterator::remove);
        }

        @Test
        void removeShouldRemoveLastElement() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);

            Iterator<Integer> iterator = list.iterator();

            while (iterator.hasNext()) {
                iterator.next();
            }

            iterator.remove();

            assertThat(list).containsExactly(1);
        }

        @Test
        void removeShouldRemoveFromMiddle() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);
            list.add(3);

            Iterator<Integer> iterator = list.iterator();

            iterator.next(); // 1
            iterator.next(); // 2

            iterator.remove(); // remove 2

            assertThat(list).containsExactly(1, 3);
        }

        @Test
        void removeShouldRemoveFirstElement() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);
            list.add(3);

            Iterator<Integer> iterator = list.iterator();

            iterator.next(); // 1

            iterator.remove(); // remove 1

            assertThat(list).containsExactly(2, 3);
        }
    }

    @Nested
    class BulkAdd {
        @Test
        void addAllShouldThrowWhenCollectionIsNull() {
            List<Integer> list = create();

            assertThatNullPointerException()
                    .isThrownBy(() -> list.addAll(null));

        }

        @Test
        void addAllFromIndexShouldCheckIndexFirstAndThenThrow() {
            List<Integer> list = create();

            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> list.addAll(1, null));

        }

        @Test
        void addAllFromIndexShouldThrowWhenCollectionIsNull() {
            List<Integer> list = create();
            list.add(1);

            assertThatNullPointerException()
                    .isThrownBy(() -> list.addAll(0, null));
        }

        @Test
        void addAllMultipleTimeShouldAppendAllCollections() {
            List<Integer> list = create();

            list.addAll(Arrays.asList(1, 2));
            list.addAll(Arrays.asList(3, 4));
            list.addAll(Arrays.asList(5, 6));

            assertThat(list).containsExactly(1, 2, 3, 4, 5, 6);
        }

        @Test
        void addAllFromCollectionShouldAppendItToExistingOne() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);
            list.add(3);

            list.addAll(Arrays.asList(4, 5, 6));

            assertThat(list)
                    .containsExactly(1, 2, 3, 4, 5, 6);
        }
    }

    /**
     * TODO: Please, explain details how and why this is happening.
     */
    @Nested
    class ConcurrentModifications {
        @Test
        void shouldThrowWhenTwoIteratorsModifyingSameList() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);

            Iterator<Integer> iterator1 = list.iterator();
            Iterator<Integer> iterator2 = list.iterator();

            // advance and remove
            iterator1.next();
            iterator1.remove();

            // second iterator should throw exception because
            // the first one already modified source list.
            assertThatExceptionOfType(ConcurrentModificationException.class)
                    .isThrownBy(iterator2::next);
        }

        @Test
        void forEachShouldThrowExceptionWhenRemovingElementsFromIt() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);

            assertThatExceptionOfType(ConcurrentModificationException.class)
                    .isThrownBy(() -> list.forEach(list::remove));
        }
    }
}