package net.ltxprogrammer.changed.util;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

import java.util.NoSuchElementException;

public abstract class OptionalBoolean {
    private static final OptionalBoolean EMPTY = new Empty();
    private static final OptionalBoolean TRUE = new True();
    private static final OptionalBoolean FALSE = new False();

    private OptionalBoolean() {}

    public static OptionalBoolean empty() {
        return EMPTY;
    }

    public static OptionalBoolean of(boolean value) {
        return value ? TRUE : FALSE;
    }

    public static OptionalBoolean ofNullable(Boolean value) {
        if (value == null)
            return empty();
        else
            return of(value);
    }

    public abstract boolean get();
    public abstract boolean isPresent();
    public abstract boolean isEmpty();

    public abstract void ifPresent(BooleanConsumer action);

    private static class Empty extends OptionalBoolean {
        @Override
        public boolean get() {
            throw new NoSuchElementException("No value present");
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public void ifPresent(BooleanConsumer action) {}

        @Override
        public String toString() {
            return "OptionalBoolean.empty";
        }
    }

    private static class True extends OptionalBoolean {
        @Override
        public boolean get() {
            return true;
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void ifPresent(BooleanConsumer action) {
            action.accept(true);
        }

        @Override
        public String toString() {
            return "OptionalBoolean[true]";
        }
    }

    private static class False extends OptionalBoolean {
        @Override
        public boolean get() {
            return false;
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void ifPresent(BooleanConsumer action) {
            action.accept(false);
        }

        @Override
        public String toString() {
            return "OptionalBoolean[false]";
        }
    }
}
