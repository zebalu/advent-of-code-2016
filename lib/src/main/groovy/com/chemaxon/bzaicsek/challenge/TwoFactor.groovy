package com.chemaxon.bzaicsek.challenge

import groovy.transform.Canonical

import java.util.regex.Matcher

class TwoFactor {
    private static final int maxX = 50
    private static final int maxY = 6

    static void main(String[] args) {
        final int maxX = TwoFactor.maxX
        final int maxY = TwoFactor.maxY
        /*
        def maxX = 7
        def maxY = 3
         */
        def screen = Instruction.initScreen(maxX, maxY)
        def fromString = { String s -> build(s, maxX, maxY)}
        //getExample()
        getInput().collect {fromString(it)}.each {
            screen = it.transform(screen)
        }
        printScreen(screen, maxX, maxY)
        println('litCount: '+countLit(screen))
    }

    static int countLit(Map<Pos, Character> screen) {
        screen.values().findAll {it == Instruction.Y}.size()
    }

    static void printScreen(Map<Pos, Character> screen, int maxX, int maxY) {
        for(int y=0;y<maxY; ++y) {
            for(int x=0; x<maxX; ++x) {
                print screen.get(new Pos(x,y))
            }
            println ''
        }
    }

    static Instruction build(String instruction, int maxX, int maxY) {
        if (instruction.startsWith('rect')) {
            Matcher m = instruction =~ /(rect )(\d+)(x)(\d+)/
            return new Rect(m[0][2] as int, m[0][4] as int)
        } else if (instruction.contains('column')) {
            def m = instruction =~ /(rotate column x=)(\d+)( by )(\d+)/
            return new ColumnRotate(m[0][2] as int, m[0][4] as int, maxX, maxY)
        } else if (instruction.contains('row')) {
            def m = instruction =~ /(rotate row y=)(\d+)( by )(\d+)/
            return new RowRotate(m[0][2] as int, m[0][4] as int, maxX, maxY)
        } else {
            throw new IllegalArgumentException("don't understand '$instruction'")
        }
    }

    static abstract class Instruction {
        protected static final char Y = '#' as char
        protected static final char F = '.' as char

        abstract Map<Pos, Character> transform(Map<Pos, Character> input)

        static Map<Pos, Character> initScreen(int maxX, int maxY) {
            def screen = new HashMap<Pos, Character>()
            for(int i=0; i<maxX; ++i) {
                for(int j=0; j<maxY; ++j) {
                    screen[new Pos(i,j)] = F
                }
            }
            return screen
        }
    }

    static class Rect extends Instruction {
        private final int x
        private final int y

        Rect(int x, int y) {
            this.x = x
            this.y = y
        }

        @Override
        Map<Pos, Character> transform(Map<Pos, Character> input) {
            def result = new HashMap<>(input)
            for (int i = 0; i < x; ++i) {
                for (int j = 0; j < y; ++j) {
                    result.put(new Pos(i, j), Y)
                }
            }
            return result
        }

        @Override
        String toString() {
            return "rect ${x}x$y"
        }
    }

    static abstract class Rotate extends Instruction {
        protected final int maxX
        protected final int maxY

        Rotate(int maxX, int maxY) {
            this.maxX = maxX
            this.maxY = maxY
        }

        protected int newCoord(int from, int with, int limit) {
            int res =
            (from + with) % limit
            return res
        }
    }

    static class ColumnRotate extends Rotate {

        final int col
        final int length

        ColumnRotate(int col, int length, int maxX, int maxY) {
            super(maxX, maxY)
            this.col = col
            this.length = length
        }

        @Override
        Map<Pos, Character> transform(Map<Pos, Character> input) {
            def result = new HashMap<>(input)
            for (int i = 0; i < maxY; ++i) {
                result[new Pos(col, newCoord(i, length, maxY))] = input[new Pos(col, i)]
            }
            return result
        }

        @Override
        String toString() {
            return "rotate column x=$col by $length"
        }
    }

    static class RowRotate extends Rotate {

        final int row
        final int length

        RowRotate(int row, int length, int maxX, int maxY) {
            super(maxX, maxY)
            this.row = row
            this.length = length
        }

        @Override
        Map<Pos, Character> transform(Map<Pos, Character> input) {
            def result = new HashMap<>(input)
            for (int i = 0; i < maxX; ++i) {
                result[new Pos(newCoord(i, length, maxX), row)] = input[new Pos(i, row)]
            }
            return result
        }

        @Override
        String toString() {
            return "rotate row y=$row by $length"
        }
    }

    @Canonical
    static class Pos {
        int x
        int y
    }

    static List<String> getInput() {
        new File(TwoFactor.getResource("/small-screen-instructions.txt").toURI()).text.readLines()
    }

    static List<String> getExample() {
        '''rect 3x2
rotate column x=1 by 1
rotate row y=0 by 4
rotate column x=1 by 1'''.readLines()
    }
}
