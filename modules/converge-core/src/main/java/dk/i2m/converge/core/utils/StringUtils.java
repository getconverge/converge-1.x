/*
 * Copyright 2010 Interactive Media Management
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.core.utils;

/**
 * Utilities for working with {@link String}s.
 *
 * @author Allan Lykke Christensen
 */
public final class StringUtils {

    /** String used for line break on the system where the code is executed. */
    public static final String LINE_BREAK = System.getProperty("line.separator");

    /** Length of a long word. */
    public static final long LONG_WORD = 7;

    /** Lix rating, very easy to read. */
    public static final long LIX_VERY_EASY = 24;

    /** Lix rating, easy to read. */
    public static final long LIX_EASY = 35;

    /** Lix rating, normal to read. */
    public static final long LIX_NORMAL = 44;

    /** Lix rating, hard to read. */
    public static final long LIX_HARD = 55;

    /**
     * Non-instantiable class.
     */
    private StringUtils() {
    }

    /**
     * Strips HTML tags from a {@link String}.
     *
     * @param htmlString
     *          {@link String} containing HTML tags
     * @return String with HTML tags stripped
     */
    public static String stripHtml(final String htmlString) {
        StringBuilder sb = new StringBuilder("");
        boolean intag = false;
        for (int i = 0; i < htmlString.length(); i++) {
            char c = htmlString.charAt(i);
            switch (c) {
                case '<':
                    intag = true;
                    break;
                case '>':
                    intag = false;
                    break;
                default:
                    if (!intag) {
                        sb.append(c);
                    }
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * Determine the number of words in a string.
     *
     * @param string
     *          String to be examined
     * @return number of words, 0 or higher
     */
    public static long countWords(final String string) {
        long numWords = 0;
        int index = 0;
        boolean prevWhitespace = true;

        if (string != null) {
            while (index < string.length()) {
                char c = string.charAt(index++);
                boolean currWhitespace = Character.isWhitespace(c);
                if (prevWhitespace && !currWhitespace) {
                    numWords++;
                }
                prevWhitespace = currWhitespace;
            }
        }
        return numWords;
    }

    /**
     * Counts the occurance of a character in a given {@link String}.
     *
     * @param string
     *          String to check for character occurances
     * @param character
     *          Character to check for
     * @return Count of occurances of the <code>character</code> in a
     *         <code>string</code>.
     */
    public static long countCharacter(final String string,
            final String character) {
        long occurances = 0;
        int index = 0;
        boolean prevChar = true;

        if (string != null) {
            while (index < string.length()) {
                char c = string.charAt(index++);
                boolean currChar = character.equals(c);
                if (prevChar && !currChar) {
                    occurances++;
                }
                prevChar = currChar;
            }
        }
        return occurances;
    }

    /**
     * Lix count of a given string.
     *
     * @param string
     *          {@link String} for which to calculate the Lix
     * @return Lix count for the given <code>string</code>
     */
    public static long lix(final String string) {
        long lix = 0;
        long numberOfWords = StringUtils.countWords(string);
        long numberOfPeriods = StringUtils.countCharacter(string, ".");

        lix = (numberOfWords / numberOfPeriods) + ((LONG_WORD / numberOfWords) *
                100);

        return lix;
    }
}
