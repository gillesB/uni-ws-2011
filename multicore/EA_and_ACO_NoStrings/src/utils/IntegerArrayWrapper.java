package utils;

import java.util.Arrays;

//we need this wrapper because Java can not handle int[] as key for the hashmap as you would expect it
public final class IntegerArrayWrapper {

    public final int[] data;

    public IntegerArrayWrapper(int[] data) {
        if (data == null) {
            throw new NullPointerException();
        }
        this.data = data;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof IntegerArrayWrapper)) {
            return false;
        }
        return Arrays.equals(data, ((IntegerArrayWrapper) other).data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new IntegerArrayWrapper(data.clone());
    }


}
