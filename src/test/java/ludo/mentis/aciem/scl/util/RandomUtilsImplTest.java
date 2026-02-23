package ludo.mentis.aciem.scl.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RandomUtilsImplTest {

    private RandomUtilsImpl randomUtils;

    @BeforeEach
    void setUp() {
        randomUtils = new RandomUtilsImpl();
    }

    @Test
    void testCreateRandomSublist_Success() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        int n = 3;
        List<Integer> sublist = randomUtils.createRandomSublist(list, n);

        assertEquals(n, sublist.size());
        assertTrue(list.containsAll(sublist));
    }

    @Test
    void testCreateRandomSublist_NEqualsZero() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        List<Integer> sublist = randomUtils.createRandomSublist(list, 0);

        assertNotNull(sublist);
        assertTrue(sublist.isEmpty());
    }

    @Test
    void testCreateRandomSublist_NNegative_ShouldThrowException() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertThrows(IllegalArgumentException.class, () -> randomUtils.createRandomSublist(list, -1));
    }

    @Test
    void testCreateRandomSublist_NGreaterThanSize_ShouldThrowException() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertThrows(IllegalArgumentException.class, () -> randomUtils.createRandomSublist(list, 4));
    }

    @Test
    void testCreateRandomSublist_EmptyListAndNGreaterThanZero_ShouldThrowException() {
        List<Integer> list = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> randomUtils.createRandomSublist(list, 1));
    }

    enum TestEnum {
        VAL1, VAL2, VAL3
    }

    @Test
    void testPickRandomEnumValue_Success() {
        TestEnum value = randomUtils.pickRandomEnumValue(TestEnum.class);
        assertNotNull(value);
        assertTrue(Arrays.asList(TestEnum.values()).contains(value));
    }

    @Test
    void testPickRandomBoolean() {
        // Since it's random, we just check if it returns. 
        // We could run it many times to see if we get both, but it's not strictly necessary for a simple unit test.
        assertDoesNotThrow(() -> randomUtils.pickRandomBoolean());
    }

    @Test
    void testGetRandomDate_Success() {
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 12, 31);
        LocalDate randomDate = randomUtils.getRandomDate(start, end);

        assertFalse(randomDate.isBefore(start));
        assertFalse(randomDate.isAfter(end));
    }

    @Test
    void testGetRandomDate_SameDate() {
        LocalDate date = LocalDate.of(2023, 1, 1);
        LocalDate randomDate = randomUtils.getRandomDate(date, date);

        assertEquals(date, randomDate);
    }
}
