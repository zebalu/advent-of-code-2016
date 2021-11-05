package com.chemaxon.bzaicsek.challenge

import groovy.transform.Memoized

class ScrambledLettersAndHash {

    static void main(String[] args) {
        firstPart()
        secondPart()
    }

    private static void firstPart() {
        def scrambles = readScrambles()
        def str = 'abcdefgh'.toCharArray().toList()
        for (scr in scrambles) {
            scr.scramble(str)
        }
        println str.join('')
    }

    private static void secondPart() {
        def scrambles = readScrambles(true).reverse()
        def str = 'fbgdceah'.toCharArray().toList()
        for (scr in scrambles) {
            scr.scramble(str)
        }
        println str.join('')
    }

    static List<Scramble> readScrambles(boolean reverse = false) {
        ScrambledLettersAndHash.class.getResource('/scramble-rules.txt')
                .readLines().collect { interpet(it, reverse) }
    }

    private static Scramble interpet(String line, boolean reverse = false) {
        if (line.startsWith('swap position ')) {
            def m = line =~ /swap position (\d+) with position (\d+)/
            int from = m[0][1] as int
            int to = m[0][2] as int
            return (chars) -> {
                swap(chars, from, to)
            }
        } else if (line.startsWith('swap letter ')) {
            def m = line =~ /swap letter (\w+) with letter (\w+)/
            char from = m[0][1] as char
            char to = m[0][2] as char
            return (chars) -> {
                swap(chars, chars.indexOf(from), chars.indexOf(to))
            }
        } else if (line.startsWith('rotate based on position of letter')) {
            def m = line =~ /rotate based on position of letter (\w+)/
            char c = m[0][1] as char
            return (chars) -> {
                if (reverse) {
                    reverseFromPosition(chars.indexOf(c)).times {
                        rotateLeft(chars)
                    }
                } else {
                    int idx = chars.indexOf(c)
                    (1 + idx + (idx >= 4 ? 1 : 0)).times {
                        rotateRight(chars)
                    }
                }
            }
        } else if (line.startsWith('rotate ')) {
            def m = line =~ /rotate (left|right) (\d+) step[s]?/
            boolean isLeft = m[0][1] == 'left'
            int steps = m[0][2] as int
            return (chars) -> {
                steps.times {
                    if (!reverse) {
                        if (isLeft) {
                            rotateLeft(chars)
                        } else {
                            rotateRight(chars)
                        }
                    } else {
                        if (isLeft) {
                            rotateRight(chars)
                        } else {
                            rotateLeft(chars)
                        }
                    }
                }
            }
        } else if (line.startsWith('reverse positions ')) {
            def m = line =~ /reverse positions (\d+) through (\d+)/
            int from = m[0][1] as int
            int to = m[0][2] as int
            int diff = to - from
            int steps = (diff / 2 + diff % 2)
            return (chars) -> {
                for (int i = 0; i < steps; ++i) {
                    swap(chars, i + from, to - i)
                }
            }
        } else if (line.startsWith('move position ')) {
            def m = line =~ /move position (\d+) to position (\d+)/
            int from = m[0][1] as int
            int to = m[0][2] as int
            return (chars) -> {
                if (!reverse) {
                    movePosition(chars, from, to)
                } else {
                    movePosition(chars, to, from)
                }
            }
        } else {
            throw new IllegalArgumentException("don't understand: '$line'")
        }
    }

    private static void swap(List<Character> chars, int from, int to) {
        char t = chars[from]
        chars[from] = chars[to]
        chars[to] = t
    }

    private static void movePosition(List<Character> chars, int from, int to) {
        Character c = chars.removeAt(from)
        chars.add(to, c)
    }

    private static void rotateLeft(List<Character> chars) {
        Character c = chars.removeAt(0)
        chars.add(c)
    }

    private static void rotateRight(List<Character> chars) {
        Character chr = chars.removeAt(chars.size() - 1)
        chars.add(0, chr)
    }

    @FunctionalInterface
    private static interface Scramble {
        void scramble(List<Character> chars)
    }

    @Memoized
    private static int reverseFromPosition(int i) {
        /*
        if x < 4 --> x+1+x --> 2x+1 --> odd (mod 8)
        if x >= 4 --> x+1+x+1 --> 2x+2 --> even (mod 8)
         */
        if (i % 2 == 1) {
            for (int j = 0; j < 8; ++j) {
                if ((j * 2 + 1) % 8 == i) {
                    return (8 + i - j) % 8
                }
            }
        } else {
            for (int j = 4; j < 8; ++j) {
                if ((j * 2 + 2) % 8 == i) {
                    return (8 + i - j) % 8
                }
            }
        }
    }
}
