package com.github.durmm.collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assumptions.assumeThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        static StringHolder of(String value) {
            return new StringHolder(value);
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

        @Test
        void addSingleHugeAmountShouldBeAdded() {
            List<Integer> list = create();

            List<Integer> expected = IntStream.range(0, 65536)
                    .peek(list::add)
                    .boxed()
                    .collect(Collectors.toList());

            assertThat(list).hasSameElementsAs(expected);
        }
    }

    @Nested
    class AddAtIndex {
        @Test
        void addAtIndexShouldAddElementAtSpecifiedIndex() {
            List<Integer> list = create();

            list.add(1);
            list.add(3);

            list.add(1, 2);

            assertThat(list).containsExactly(1, 2, 3);
        }

        @Test
        void addAtIndexShouldAppendWhenUsingLastIndex() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);

            list.add(2, 3);

            assertThat(list).containsExactly(1, 2, 3);
        }

        @Test
        void addAtIndexShouldPrependWhenUsingFirstIndex() {
            List<Integer> list = create();

            list.add(2);
            list.add(3);

            list.add(0, 1);

            assertThat(list).containsExactly(1, 2, 3);
        }

        @Test
        void addAtIndexShouldAppendWhenListIsEmpty() {
            List<Integer> list = create();

            list.add(0, 1);

            assertThat(list).containsExactly(1);
        }

        @Test
        void addAtIndexShouldThrowWhenIndexIsNegative() {
            List<Integer> list = create();

            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> list.add(-1, 1));
        }

        @Test
        void addAtIndexShouldThrowWhenIndexGreaterThenSize() {
            List<Integer> list = create();
            list.add(1);

            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> list.add(2, 1));
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
        void removeElementShouldDecreaseSize() {
            List<String> list = create();

            list.add("a");
            list.add("b");

            list.remove("a");

            assertThat(list.size()).isEqualTo(1);
        }
    }

    @Nested
    class RemoveObject {
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

        @Test
        void clearShouldRemoveAllElementsFromList() {
            List<String> list = create();

            list.add("a");
            list.add("b");

            list.clear();

            assertThat(list).isEmpty();
        }
    }

    /**
     * The removed element should not keep it's reference on out list to let GC clear them.
     */
    @Nested
    class RemoveByIndex {
        @Test
        void removeByIndexShouldReturnRemovedElement() {
            List<Integer> list = create();

            list.add(41);

            assertThat(list.remove(0)).isEqualTo(41);
        }

        @Test
        void removeByIndexShouldRemoveElement() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);
            list.add(3);

            list.remove(1);

            assertThat(list).containsExactly(1, 3);
        }

        @Test
        void removeByIndexShouldRemoveLastElementEffectively() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);
            list.add(3);

            // This is a special case on sequential (array) based lists.
            // The last element removal should cost us very cheap and
            // no memory reallocation should be done here
            list.remove(2);

            assertThat(list).containsExactly(1, 2);
        }

        @Test
        void removeByIndexShouldThrowWhenIndexIsNegative() {
            List<Integer> list = create();

            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> list.remove(-1));
        }

        @Test
        void removeByIndexShouldThrowWhenIndexIsGreaterThenSize() {
            List<Integer> list = create();
            list.add(1);
            list.add(2);

            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> list.remove(2));
        }
    }

    @Nested
    class RemoveCollection {
        @Test
        void removeCollectionShouldRemove() {
            List<StringHolder> list = create();

            list.add(StringHolder.of("a"));
            list.add(StringHolder.of("b"));
            list.add(StringHolder.of("c"));
            list.add(StringHolder.of("d"));

            List<StringHolder> toRemove = Arrays.asList(StringHolder.of("a"), StringHolder.of("b"), StringHolder.of("z"));
            list.removeAll(toRemove);

            assertThat(list).containsExactly(
                    StringHolder.of("c"),
                    StringHolder.of("d")
            );
        }

        @Test
        void removeCollectionShouldThrowWhenCollectionIsNull() {
            List<Object> list = create();

            assertThatNullPointerException()
                    .isThrownBy(() -> list.removeAll(null));
        }

        @Test
        void removeCollectionShouldReturnFalseIfNothingWhereRemoved() {
            List<Integer> list = create();

            list.add(1);

            assertThat(list.removeAll(Collections.singletonList(2))).isFalse();
        }

        @Test
        void removeCollectionShouldReturnTrueIfSomeElementsAreRemoved() {
            List<Integer> list = create();

            list.add(1);

            assertThat(list.removeAll(Collections.singletonList(1))).isTrue();
        }
    }

    @Nested
    class Set {
        @Test
        void setShouldReplaceElement() {
            List<Integer> list = create();

            list.add(1);
            list.add(3);

            list.set(1, 2);

            assertThat(list).containsExactly(1, 2);
        }

        @Test
        void setShouldThrowWhenIndexOutOfRange() {
            List<Integer> list = create();
            list.add(1);

            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> list.set(1, 5));
        }

        @Test
        void setShouldThrowWhenIndexIsNegative() {
            List<Integer> list = create();

            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> list.set(-1, 5));
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
        void addAllShouldReturnTrueWhenListIsChanged() {
            List<Integer> list = create();

            assertThat(list.addAll(Arrays.asList(1, 2))).isTrue();
        }

        @Test
        void addAllShouldReturnFalseWhenListIsNotChanged() {
            List<Integer> list = create();

            // adding an empty collection should not modify list
            assertThat(list.addAll(Collections.emptyList())).isFalse();
        }

        @Test
        void addAllFromIndexShouldReturnTrueWhenListIsChanged() {
            List<Integer> list = create();

            assertThat(list.addAll(0, Arrays.asList(1, 2))).isTrue();
        }

        @Test
        void addAllFromIndexShouldReturnFalseWhenListIsNotChanged() {
            List<Integer> list = create();

            assertThat(list.addAll(0, Collections.emptyList())).isFalse();
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
        void addAllFromIndexShouldAddCollectionInTheMiddle() {
            List<Integer> list = create();

            list.add(1);
            list.add(4);
            list.add(5);

            list.addAll(1, Arrays.asList(2, 3));

            assertThat(list).containsExactly(1, 2, 3, 4, 5);
        }

        @Test
        void addAllFromIndexOnEmptyListFromTheFirstIndex() {
            List<Integer> list = create();

            list.addAll(0, Arrays.asList(1, 2));

            assertThat(list).containsExactly(1, 2);
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

    @Nested
    class IndexOf {

        @Test
        void indexOfShouldReturnNotFoundOnEmptyList() {
            List<Object> list = create();

            assertThat(list.indexOf("a")).isEqualTo(-1);
        }

        @Test
        void indexOfShouldReturnNotFoundWhenCannotFindElement() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);

            assertThat(list.indexOf(3)).isEqualTo(-1);
        }

        @Test
        void indexOfShouldReturnFirstOccurrence() {
            List<Integer> list = create();

            list.add(1);
            list.add(1);

            assertThat(list.indexOf(1)).isEqualTo(0);
        }

        @Test
        void indexOfShouldUseEqualsToTestObjectEquality() {
            List<StringHolder> list = create();

            list.add(new StringHolder("a"));
            list.add(new StringHolder("a"));
            list.add(new StringHolder("b"));
            list.add(new StringHolder("b"));

            assertThat(list.indexOf(new StringHolder("b"))).isEqualTo(2);
        }

        @Test
        void indexOfShouldReturnIndexOfFirstNullElement() {
            List<Object> list = create();

            list.add(new Object());
            list.add(null);
            list.add(null);

            assertThat(list.indexOf(null)).isEqualTo(1);
        }
    }

    @Nested
    class LastIndexOf {

        @Test
        void lastIndexOfShouldReturnNotFoundOnEmptyList() {
            List<Object> list = create();

            assertThat(list.lastIndexOf("a")).isEqualTo(-1);
        }

        @Test
        void lastIndexOfShouldReturnNotFoundWhenCannotFindElement() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);

            assertThat(list.lastIndexOf(3)).isEqualTo(-1);
        }

        @Test
        void lastIndexOfShouldReturnFirstOccurrence() {
            List<Integer> list = create();

            list.add(1);
            list.add(1);

            assertThat(list.lastIndexOf(1)).isEqualTo(1);
        }

        @Test
        void lastIndexOfShouldUseEqualsToTestObjectEquality() {
            List<StringHolder> list = create();

            list.add(new StringHolder("a"));
            list.add(new StringHolder("a"));
            list.add(new StringHolder("b"));
            list.add(new StringHolder("b"));

            assertThat(list.lastIndexOf(new StringHolder("b"))).isEqualTo(3);
        }

        @Test
        void lastIndexOfShouldReturnIndexOfLastNullElement() {
            List<Object> list = create();

            list.add(new Object());
            list.add(null);
            list.add(null);

            assertThat(list.lastIndexOf(null)).isEqualTo(2);
        }
    }

    @Nested
    class Contains {
        @Test
        void containsAllShouldReturnTrueWhenAllElementsArePresent() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);
            list.add(3);
            list.add(4);

            assertThat(list.containsAll(Arrays.asList(1, 2))).isTrue();
            assertThat(list.containsAll(Arrays.asList(2, 4))).isTrue();
            assertThat(list.containsAll(Arrays.asList(1, 4))).isTrue();
            assertThat(list.containsAll(Arrays.asList(1, 2, 3, 4))).isTrue();
        }

        @Test
        void containsAllShouldReturnFalseWhenOneOfElementsAreMissing() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);
            list.add(3);
            list.add(4);

            assertThat(list.containsAll(Arrays.asList(1, 5))).isFalse();
            assertThat(list.containsAll(Arrays.asList(-1, -2))).isFalse();
            assertThat(list.containsAll(Arrays.asList(1, 2, 3, 4, 5))).isFalse();
        }

        @Test
        void containsShouldReturnTrueWhenElementIsPresent() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);

            assertThat(list.contains(2)).isTrue();
        }

        @Test
        void containsShouldReturnFalseWhenElementIsNotPresent() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);

            assertThat(list.contains(3)).isFalse();
        }

        @Test
        void containsShouldReturnFalseWhenTypesAreIncompatible() {
            List<Integer> list = create();
            list.add(1);

            assertThat(list.contains("a")).isFalse();
        }

        @Test
        void containsShouldReturnTrueWhenNullIsPresent() {
            List<Integer> list = create();
            list.add(1);
            list.add(null);

            assertThat(list.contains(null)).isTrue();
        }

        @Test
        void containsShouldUseEqualsToTestEquality() {
            List<StringHolder> list = create();
            list.add(StringHolder.of("a"));
            list.add(StringHolder.of("b"));

            assertThat(list.contains(StringHolder.of("a"))).isTrue();
            assertThat(list.contains(StringHolder.of("b"))).isTrue();

            assertThat(list.contains(StringHolder.of("c"))).isFalse();
        }
    }

    @Nested
    class ToArray {
        @Test
        void toArrayOnEmptyListShouldBeEmpty() {
            List<Object> list = create();

            assertThat(list.toArray())
                    .isNotNull()
                    .isEmpty();
        }

        @Test
        void toArrayShouldReturnArrayRepresentationOfList() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);

            assertThat(list.toArray())
                    .hasSameSizeAs(list)
                    .containsExactly(1, 2);
        }

        @Test
        void toArrayShouldNotAffectOriginalListWhenModified() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);

            Object[] objects = list.toArray();

            objects[0] = 64;
            objects[1] = 128;

            assertThat(list).containsExactly(1, 2);
        }

        @Test
        void toArrayWithArgShouldReturnTypeSafeArray() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);

            Integer[] integers = list.toArray(new Integer[0]);

            assertThat(integers)
                    .hasSameSizeAs(list)
                    .containsExactly(1, 2);
        }

        @Test
        void toArrayWithArgThatGreaterThenListSizeShouldReturnArrayWithNullsAppended() {
            List<Integer> list = create();

            list.add(1);
            list.add(2);

            int expectedSize = 4;
            Integer[] integers = list.toArray(new Integer[expectedSize]);

            assertThat(integers)
                    .hasSize(expectedSize)
                    .containsExactly(1, 2, null, null);
        }

        @Test
        void toArrayWithIncompatibleTypeShouldThrow() {
            List<Integer> list = create();

            list.add(1);

            assertThatExceptionOfType(ArrayStoreException.class)
                    .isThrownBy(() -> list.toArray(new String[0]));
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