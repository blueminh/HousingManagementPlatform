package sem.hoa.dtos;

import lombok.Getter;

public class Pair<A, B> {
    @Getter
    private A first;

    @Getter
    private B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }
}
