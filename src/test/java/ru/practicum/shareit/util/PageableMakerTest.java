package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PageableMakerTest {

    @Test
    public void makePageTest() {
        assertThrows(IllegalArgumentException.class, () -> PageableMaker.makePage(-1, 1));
        assertThrows(IllegalArgumentException.class, () -> PageableMaker.makePage(0, 0));
        assertThrows(IllegalArgumentException.class, () -> PageableMaker.makePage(-1, -1));
    }
}
