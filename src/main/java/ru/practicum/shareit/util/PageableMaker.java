package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@UtilityClass
public final class PageableMaker {

    public static Pageable makePage(Integer from, Integer size) {
        if (from < 0 || size < 1) {
            throw new IllegalArgumentException("Неверные параметры пагинации.");
        }
        int page = from / size;
        return PageRequest.of(page, size);
    }
}
