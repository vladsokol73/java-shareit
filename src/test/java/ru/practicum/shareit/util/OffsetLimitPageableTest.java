package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class OffsetLimitPageableTest {
    static final Integer from = 1;
    static final Integer size = 2;

    static final Sort sort = Sort.unsorted();

    static Pageable testPageable = new OffsetLimitPageable(from, size, sort);

    @Test
    void getPageNumber_shouldReturnZero() {
        assertEquals(0, testPageable.getPageNumber());
    }

    @Test
    void getPageSize_shouldReturnSizeValue() {
        assertEquals(size, testPageable.getPageSize());
    }

    @Test
    void getOffset_shouldReturnFromValue() {
        assertEquals((long) from, testPageable.getOffset());
    }

    @Test
    void getSort_shouldReturnSort() {
        assertEquals(sort, testPageable.getSort());
    }

    @Test
    void next_shouldReturnPageableWithIncreaseOffset() {
        assertEquals(from + size, testPageable.next().getOffset());
    }

    @Test
    void previousOrFirst_shouldReturnTheSame() {
        Pageable pageable = testPageable.previousOrFirst();

        assertEquals(testPageable.getOffset(), pageable.getOffset());
        assertEquals(testPageable.getPageSize(), pageable.getPageSize());
    }

    @Test
    void first_shouldReturnTheSame() {
        Pageable pageable = testPageable.first();

        assertEquals(testPageable.getOffset(), pageable.getOffset());
        assertEquals(testPageable.getPageSize(), pageable.getPageSize());
    }

    @Test
    void withPage_shouldReturnPageableWithIncreaseOffset() {
        int withPage = 2;
        assertEquals(from + size * withPage, testPageable.withPage(withPage).getOffset());
    }

    @Test
    void hasPrevious_shouldReturnFalse() {
        assertFalse(testPageable.hasPrevious());
    }
}
