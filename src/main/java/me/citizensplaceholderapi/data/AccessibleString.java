/*
 * The contents of this file is free and unencumbered software released into the
 * public domain. For more information, please refer to <http://unlicense.org/>
 */
package me.citizensplaceholderapi.data;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class AccessibleString {

    @Nonnull
    private String string;

    public AccessibleString() {
        this.string = "";
    }

    public AccessibleString(@Nonnull final String string) {
        this.string = string;
    }

    public void setString(@Nonnull final String string) {
        this.string = string;
    }

    @Nonnull
    public String getString() {
        return this.string;
    }

    /**
     * @param endIndex the end index
     * @return characters that the string start with till endIndex
     */
    @Nonnull
    public String getStartsWith(final int endIndex) {
        int counter = -1;
        final StringBuilder sb = new StringBuilder();
        for (final char character : this.string.toCharArray()) {
            counter++;
            if (counter >= endIndex)
                break;
            sb.append(character);
        }
        return sb.toString();
    }

    public String substring(final int beginIndex) {
        return this.string.substring(beginIndex);
    }

    public String substring(final int beginIndex, final int endIndex) {
        return this.string.substring(beginIndex, endIndex);
    }

    public static AccessibleString createString(final String string) {
        return new AccessibleString(string);
    }

    public static AccessibleString parseString(final Object object) {
        final String newString;
        if (object instanceof Number) {
            newString = String.valueOf(object);
            return new AccessibleString(newString);
        } else if (object instanceof Collection) {
            newString = object.toString();
            return new AccessibleString(newString);
        } else if (object instanceof Map) {
            newString = ((Map) object).entrySet().toString();
            return new AccessibleString(newString);
        } else if (object instanceof UUID) {
            newString = object.toString();
            return new AccessibleString(newString);
        }
        return new AccessibleString(String.valueOf(object));
    }

    @Override
    public String toString() {
        return this.string;
    }

}
