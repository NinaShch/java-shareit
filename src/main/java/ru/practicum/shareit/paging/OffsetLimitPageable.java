package ru.practicum.shareit.paging;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.BadRequestException;

public class OffsetLimitPageable implements Pageable {

    private static final int MAX_PAGE_SIZE = Integer.MAX_VALUE;

    private final int from;
    private final int size;
    private final Sort sort;

    private OffsetLimitPageable(int from, int size, Sort sort) {
        this.from = from;
        this.size = size;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return 0;
    }

    @Override
    public int getPageSize() {
        return size;
    }

    @Override
    public long getOffset() {
        return from;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetLimitPageable(from + size, size, sort);
    }

    @Override
    public Pageable previousOrFirst() {
        return new OffsetLimitPageable(from, size, sort);
    }

    @Override
    public Pageable first() {
        return new OffsetLimitPageable(from, size, sort);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new OffsetLimitPageable(from + size * pageNumber, size, sort);
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }

    public static Pageable from(Integer from, Integer size) {
        return from(from, size, Sort.unsorted());
    }

    public static Pageable from(Integer from, Integer size, Sort sort) {
        if (from == null && size == null) {
            return unpaged(sort);
        }
        validatePaging(from, size);
        return new OffsetLimitPageable(from, size, sort);
    }

    public static Pageable unpaged(Sort sort) {
        return new OffsetLimitPageable(0, MAX_PAGE_SIZE, sort);
    }

    public static Pageable unpaged() {
        return unpaged(Sort.unsorted());
    }

    private static void validatePaging(Integer from, Integer size) {
        if (from == null && size == null) return;
        if (from == null || size == null) throw new BadRequestException("must provide both from and size or no one");
        if (size <= 0) throw new BadRequestException("size must be positive");
        if (from < 0) throw new BadRequestException("from must be positive or 0");
    }
}