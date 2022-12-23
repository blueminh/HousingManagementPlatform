package sem.hoa.dtos;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PairTest {

    @Test
    void testGetFirst() {
        Pair<Integer, Integer> a = new Pair<Integer, Integer>(1, 2);
        assertThat(a.getFirst()).isEqualTo(1);
    }

    @Test
    void testGetSecond() {
        Pair<Integer, Integer> a = new Pair<Integer, Integer>(1, 2);
        assertThat(a.getSecond()).isEqualTo(2);
    }
}