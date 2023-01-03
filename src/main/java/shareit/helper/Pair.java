package shareit.helper;

import java.io.Serializable;

public class Pair<R, L> implements Serializable {
    
    private final R key;
    private final L value;

    public Pair(R key, L value) {
        assert key != null;
        assert value != null;

        this.key = key;
        this.value = value;
    }

    public R getKey() {
        return key;
    }

    public L getValue() {
        return value;
    }

}