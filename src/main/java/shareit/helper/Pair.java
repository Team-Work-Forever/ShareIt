package shareit.helper;

import java.io.Serializable;

public class Pair<R, L> implements Serializable, CSVSerializable {
    
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

    @Override
    public String[] serialize() {
        
        if (key instanceof CSVSerializable && value instanceof CSVSerializable) {
            return new String[] {
                toStringSerialize(),
                ((CSVSerializable)value).toString()
            };
        } else if (key instanceof CSVSerializable) {
            return new String[] {
                toStringSerialize(),
                value.toString()
            };
        }

        return null;

    }

    public String toStringSerialize() {

        StringBuilder builder = new StringBuilder();

        String[] array = ((CSVSerializable)key).serialize();

        for (String property : array) {
            if (array.length > 1) {
                builder.append(property + ":");
            } else
                builder.append(property);
        }

        return builder.toString();

    }

}