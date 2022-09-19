package ru.practicum.shareit.paging;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.BadRequestException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class OffsetLimitPageableTest {

    @Test
    public void successful_validation_without_paging() {
        OffsetLimitPageable.from(null, null);
    }

    @Test
    public void successful_validation_with_positive_arguments() {
        OffsetLimitPageable.from(1, 2);
    }

    @Test
    public void successful_validation_with_zero_from() {
        OffsetLimitPageable.from(0, 2);
    }

    @Test
    public void failed_validation_with_zero_size() {
        assertThrows(
                BadRequestException.class,
                () -> OffsetLimitPageable.from(1, 0)
        );
    }

    @Test
    public void failed_validation_with_negative_size() {
        assertThrows(
                BadRequestException.class,
                () -> OffsetLimitPageable.from(1, -1)
        );
    }

    @Test
    public void failed_validation_with_negative_from() {
        assertThrows(
                BadRequestException.class,
                () -> OffsetLimitPageable.from(-1, 2)
        );
    }

    @Test
    public void failed_validation_with_only_from() {
        assertThrows(
                BadRequestException.class,
                () -> OffsetLimitPageable.from(1, null)
        );
    }

    @Test
    public void failed_validation_with_only_size() {
        assertThrows(
                BadRequestException.class,
                () -> OffsetLimitPageable.from(null, 1)
        );
    }

    @Test
    public void correct_pagable_created_forgiven_params() {
        int from = 1;
        int size = 10;
        Sort sort = mock(Sort.class);

        Pageable pageable = OffsetLimitPageable.from(from, size, sort);
        assertEquals(0, pageable.getPageNumber());
        assertEquals(size, pageable.getPageSize());
        assertEquals(from, pageable.getOffset());
        assertEquals(sort, pageable.getSort());
        assertEquals(from + size, pageable.next().getOffset());
        assertEquals(from, pageable.previousOrFirst().getOffset());
        assertEquals(from, pageable.first().getOffset());
        assertEquals(101, pageable.withPage(10).getOffset());
        assertFalse(pageable.hasPrevious());
    }
}
