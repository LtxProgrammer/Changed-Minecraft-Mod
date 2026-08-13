package net.ltxprogrammer.changed.computers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/// Basic lexical path parser, handles Windows-style paths.
public class LexicalPath implements Iterable<LexicalPath> {
    // TODO alternative lexical parser for Unix-style

    private static final String[] EMPTY_ELEMENTS = new String[0];
    private static final LexicalPath EMPTY = new LexicalPath(false, EMPTY_ELEMENTS);

    private final boolean driveRelative;
    protected final String[] elements;

    LexicalPath(boolean driveRelative, String[] elements) {
        this.driveRelative = driveRelative;
        this.elements = elements;
    }

    @Override
    public String toString() {
        return (driveRelative ? "/" : "") + String.join("/", elements);
    }

    private static void removeAndLeftShift(String[] elements, int index, int count) {
        for (int i = index; i < elements.length; ++i)
            elements[i] = (i + count == elements.length ? null : elements[i + count]);
    }

    private static char validateDriveLetter(char driveLetter) {
        if (driveLetter < 'A' || driveLetter > 'Z')
            throw new IllegalArgumentException(driveLetter + " is not a valid drive letter");
        return driveLetter;
    }

    private static String[] validateElements(String[] elements) {
        for (String elem : elements) {
            if (elem.isBlank())
                throw new IllegalArgumentException("empty string in path elements");
            for (int i = 0; i < elem.length(); ++i) {
                char c = elem.charAt(i);
                if (c >= 'a' && c <= 'z')
                    continue;
                if (c >= 'A' && c <= 'Z')
                    continue;
                if (c >= '0' && c <= '9')
                    continue;
                switch (c) {
                    case '_':
                    case '.':
                    case '-':
                    case ' ':
                        continue;
                    default:
                        throw new IllegalArgumentException("invalid character \"" + c + "\" in path element \"" + elem + "\"");
                }
            }
        }

        return elements;
    }

    protected static LexicalPath of(String[] elements) {
        int newLength = elements.length;

        for (int idx = 0; idx < newLength; ++idx) {
            var elem = elements[idx];
            if ("..".equals(elem)) {
                if (idx == 0)
                    continue;

                removeAndLeftShift(elements, idx, 2);
                idx -= 2;
                newLength -= 2;
                continue;
            } else if (".".equals(elem)) {
                removeAndLeftShift(elements, idx, 1);
                idx--;
                newLength--;
                continue;
            } else if (elem.isEmpty() && idx == newLength - 1) {
                newLength--;
                continue;
            }
        }

        if (newLength == 0)
            return EMPTY;

        String first = elements[0];

        if (first.isEmpty() || (first.length() >= 2 && first.charAt(1) == ':')) {
            removeAndLeftShift(elements, 0, 1);
            newLength--;
        }

        if (newLength != elements.length) {
            String[] oldArray = elements;
            elements = newLength == 0 ? EMPTY_ELEMENTS : new String[newLength];
            System.arraycopy(oldArray, 0, elements, 0, newLength);
        }

        if (first.isEmpty())
            return new LexicalPath(true, validateElements(elements));
        if (first.length() >= 2 && first.charAt(1) == ':') {
            if (first.length() > 2)
                throw new IllegalArgumentException(first + " is an invalid drive indicator");
            return new Absolute(validateDriveLetter(first.charAt(0)), validateElements(elements));
        }

        return new LexicalPath(false, validateElements(elements));
    }

    public static LexicalPath of(String path, String... rest) {
        LexicalPath lexicalPath = of(path.split("/"));
        for (String varg : rest)
            lexicalPath = lexicalPath.resolve(varg);
        return lexicalPath;
    }

    public static Absolute fromDriveLetter(char driveLetter) {
        return new Absolute(driveLetter, EMPTY_ELEMENTS);
    }

    public boolean isRelative() {
        return !this.isAbsolute();
    }

    public boolean isAbsolute() {
        return false;
    }

    public Absolute assertAbsolute() {
        throw new IllegalStateException("assertAbsolute() called on a relative path");
    }

    public boolean isDriveRelative() {
        return driveRelative;
    }

    public boolean isEmpty() {
        return !this.isAbsolute() && !this.isDriveRelative() && elements.length == 0;
    }

    public LexicalPath getRoot() {
        if (this.isDriveRelative())
            return elements.length == 0 ? this : new LexicalPath(true, EMPTY_ELEMENTS);

        if (elements.length == 1)
            return this;
        if (elements.length == 0)
            return EMPTY;
        String[] singleton = new String[1];
        singleton[0] = elements[0];
        return new LexicalPath(false, singleton);
    }

    public LexicalPath getFileName() {
        if (elements.length == 0)
            return null;

        String[] singleton = new String[1];
        singleton[0] = elements[elements.length - 1];
        return new LexicalPath(false, singleton);
    }

    public @Nullable LexicalPath getParent() {
        if (elements.length == 0)
            return null;

        String[] minusOne = new String[elements.length - 1];
        System.arraycopy(elements, 0, minusOne, 0, elements.length - 1);
        return new LexicalPath(driveRelative, minusOne);
    }

    public LexicalPath resolve(String other) {
        return this.resolve(LexicalPath.of(other));
    }

    public LexicalPath resolve(LexicalPath other) {
        if (other.isAbsolute())
            return other;

        String[] totalElements;
        if (this.isDriveRelative()) {
            totalElements = new String[this.elements.length + other.elements.length + 1];
            totalElements[0] = "";
            System.arraycopy(this.elements, 0, totalElements, 1, this.elements.length);
            System.arraycopy(other.elements, 0, totalElements, 1 + this.elements.length, other.elements.length);
        } else {
            totalElements = new String[this.elements.length + other.elements.length];
            System.arraycopy(this.elements, 0, totalElements, 0, this.elements.length);
            System.arraycopy(other.elements, 0, totalElements, this.elements.length, other.elements.length);
        }

        return of(totalElements);
    }

    public LexicalPath relativize(String other) {
        return this.resolve(LexicalPath.of(other));
    }

    public LexicalPath relativize(LexicalPath other) {
        if (this.isDriveRelative()) {
            if (other.isDriveRelative()) {
                int matchesUntil = 0;
                for (; matchesUntil < this.elements.length && matchesUntil < other.elements.length; ++matchesUntil) {
                    if (this.elements[matchesUntil].equals(other.elements[matchesUntil]))
                        continue;
                    break;
                }

                String[] nextElements = new String[(other.elements.length - matchesUntil) + (this.elements.length - matchesUntil)];
                for (int i = 0; i < this.elements.length - matchesUntil; ++i)
                    nextElements[i] = "..";
                System.arraycopy(other.elements, matchesUntil,
                        nextElements, this.elements.length - matchesUntil,
                        other.elements.length - matchesUntil);
                return of(nextElements);
            }
        }

        int lastThisMatch = -1;
        int lastOtherMatch = -1;

        for (int i = 0; i < this.elements.length; ++i) {
            for (int o = other.elements.length - 1; o >= 0; --o) {
                if (this.elements[i].equals(other.elements[o])) {
                    lastThisMatch = i;
                    lastOtherMatch = o;
                    break;
                }
            }
        }

        if (lastThisMatch < 0)
            throw new IllegalArgumentException("cannot relativize \"" + other + "\" to \"" + this + "\"");

        String[] remainingThisElements = new String[this.elements.length - lastThisMatch];
        String[] remainingOtherElements = new String[other.elements.length - lastOtherMatch];
        System.arraycopy(this.elements, lastThisMatch, remainingThisElements, 0, remainingThisElements.length);
        System.arraycopy(other.elements, lastThisMatch, remainingOtherElements, 0, remainingOtherElements.length);

        int matchesUntil = 0;
        for (; matchesUntil < remainingThisElements.length && matchesUntil < remainingOtherElements.length; ++matchesUntil) {
            if (remainingThisElements[matchesUntil].equals(remainingOtherElements[matchesUntil]))
                continue;
            break;
        }

        String[] nextElements = new String[(remainingOtherElements.length - matchesUntil) + (remainingThisElements.length - matchesUntil)];
        for (int i = 0; i < remainingThisElements.length - matchesUntil; ++i)
            nextElements[i] = "..";
        System.arraycopy(remainingOtherElements, matchesUntil,
                nextElements, remainingThisElements.length - matchesUntil,
                remainingOtherElements.length - matchesUntil);
        return new LexicalPath(matchesUntil == 0, nextElements);
    }

    @Override
    public @NotNull Iterator<LexicalPath> iterator() {
        return new Iterator<>() {
            int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < elements.length;
            }

            @Override
            public LexicalPath next() {
                String[] singleton = new String[1];
                singleton[0] = elements[currentIndex];
                ++currentIndex;

                return new LexicalPath(false, singleton);
            }
        };
    }

    public static class Absolute extends LexicalPath {
        private final char driveLetter;

        Absolute(char driveLetter, String[] elements) {
            super(false, elements);
            this.driveLetter = driveLetter;
        }

        public char getDriveLetter() {
            return driveLetter;
        }

        @Override
        public boolean isAbsolute() {
            return true;
        }

        @Override
        public Absolute assertAbsolute() {
            return this;
        }

        @Override
        public String toString() {
            return driveLetter + ":/" + String.join("/", elements);
        }

        @Override
        public Absolute getRoot() {
            return elements.length == 0 ? this : new Absolute(driveLetter, EMPTY_ELEMENTS);
        }

        @Override
        public @Nullable Absolute getParent() {
            if (elements.length == 0)
                return null;

            String[] minusOne = new String[elements.length - 1];
            System.arraycopy(elements, 0, minusOne, 0, elements.length - 1);
            return new Absolute(driveLetter, minusOne);
        }

        @Override
        public Absolute resolve(String other) {
            return this.resolve(LexicalPath.of(other));
        }

        @Override
        public Absolute resolve(LexicalPath other) {
            if (other.isAbsolute())
                return other.assertAbsolute();
            if (other.isDriveRelative())
                return new Absolute(driveLetter, other.elements);

            String[] totalElements = new String[this.elements.length + other.elements.length + 1];
            totalElements[0] = driveLetter + ":";
            System.arraycopy(this.elements, 0, totalElements, 1, this.elements.length);
            System.arraycopy(other.elements, 0, totalElements, 1 + this.elements.length, other.elements.length);
            return of(totalElements).assertAbsolute();
        }

        @Override
        public LexicalPath relativize(LexicalPath other) {
            char otherDriveLetter = '!'; // Invalid drive letter
            if (other instanceof Absolute otherAbsolute)
                otherDriveLetter = otherAbsolute.driveLetter;

            if (other.isAbsolute() && this.driveLetter != otherDriveLetter)
                return other;
            if (this.elements.length == 0 && this.driveLetter == otherDriveLetter)
                return new LexicalPath(false, other.elements);

            if (this.driveLetter == otherDriveLetter) {
                int matchesUntil = 0;
                for (; matchesUntil < this.elements.length && matchesUntil < other.elements.length; ++matchesUntil) {
                    if (this.elements[matchesUntil].equals(other.elements[matchesUntil]))
                        continue;
                    break;
                }

                String[] nextElements = new String[(other.elements.length - matchesUntil) + (this.elements.length - matchesUntil)];
                for (int i = 0; i < this.elements.length - matchesUntil; ++i)
                    nextElements[i] = "..";
                System.arraycopy(other.elements, matchesUntil,
                        nextElements, this.elements.length - matchesUntil,
                        other.elements.length - matchesUntil);
                return of(nextElements);
            }

            return super.relativize(other);
        }
    }
}
