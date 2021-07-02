package org.ethelred.mc;

import java.util.Map;
import java.util.function.Function;

/**
 * simplified type system for certain values.
 */
public class ScalarUnion {

    private static final Map<Class, Function> CONVERSIONS = Map.of(
        Byte.class,
        ScalarUnion::_int,
        Short.class,
        ScalarUnion::_int,
        Integer.class,
        ScalarUnion::_int,
        Boolean.class,
        x -> ((Boolean) x) ? 1 : 0,
        Long.class,
        x -> x,
        String.class,
        x -> x,
        Double.class,
        x -> x
    );
    private Object value;

    private static int _int(Object x) {
        return ((Number) x).intValue();
    }

    public ScalarUnion(Object in) {
        Function conversion = CONVERSIONS.getOrDefault(
            in.getClass(),
            x -> {
                throw new IllegalArgumentException();
            }
        );
        value = conversion.apply(in);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ScalarUnion other = (ScalarUnion) obj;
        if (value == null) {
            if (other.value != null) return false;
        } else if (!value.equals(other.value)) return false;
        return true;
    }
}
