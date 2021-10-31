package com.chemaxon.bzaicsek.challenge

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.transform.Memoized

import java.time.Duration
import java.time.Instant

class MazeOfTwistyLittleCubicles {

    static void main(String[] args) {
        shortestPathTo_31_39()
        reachableIn50Steps()
    }

    private static void reachableIn50Steps() {
        Coordinate start = new Coordinate(1, 1)
        Queue<CoordStep> queue = new LinkedList<>()
        Set<Coordinate> seen = new HashSet<>()
        queue.add(new CoordStep(start, 0, null))
        seen.add(start)
        int i = 0
        while (!queue.isEmpty() && queue.peek().step<51) {
            ++i
            CoordStep at = queue.poll()
            at.coord.neighbours().findAll { !seen.contains(it) && it.space }.each { n ->
                if(at.step<50) {
                    queue.add(new CoordStep(n, at.step + 1, at))
                    seen.add(n)
                }
            }
            if (i % 10_000 == 0) {
                println "$i\t${queue.size()}\t${at.step}"
            }
        }
        println "reachable in 50 steps: ${seen.size()}"
    }

    private static void shortestPathTo_31_39() {
        Coordinate start = new Coordinate(1, 1)
        Coordinate end = new Coordinate(31, 39)
        Queue<CoordStep> queue = new LinkedList<>()
        Set<Coordinate> seen = new HashSet<>()
        queue.add(new CoordStep(start, 0, null))
        seen.add(start)
        int i = 0
        while (!queue.isEmpty() && queue.peek().coord != end) {
            ++i
            CoordStep at = queue.poll()
            at.coord.neighbours().findAll { !seen.contains(it) && it.space }.each { n ->
                queue.add(new CoordStep(n, at.step + 1, at))
                seen.add(n)
            }
            if (i % 10_000 == 0) {
                println "$i\t${queue.size()}\t${at.step}"
            }
        }
        /*
        def path = []
        def curr = queue.peek()
        while (curr.prev != null) {
            path.add(0, curr.coord)
            curr = curr.prev
        }
        path.each {
            println "$it"
        }
         */
        println "required steps: ${queue.peek().step}"
    }

    @Canonical
    private static class CoordStep {
        final Coordinate coord
        final int step
        final CoordStep prev
    }

    @Canonical
    private static class Coordinate {

        static int input = 1364

        final int x
        final int y
        final boolean valid
        final boolean wall
        final boolean space

        Coordinate(int x, int y) {
            this.x = x
            this.y = y
            this.valid = x >= 0 && y >= 0
            this.wall = (countOnes(x, y) % 2 == 1)
            this.space = !wall
        }

        @CompileStatic
        List<Coordinate> neighbours() {
            List<Coordinate> result = []
            for (dif in [[-1, 0], [1, 0], [0, -1], [0, 1]]) {
                Coordinate c = new Coordinate(x + dif[0], y + dif[1])
                if (c.isValid() && c.isSpace()) {
                    result << c
                }
            }
            return result
        }

        @CompileStatic
        private static int applyFormula(int x, int y) {
            return x * x + 3 * x + 2 * x * y + y + y * y + input
        }

        @CompileStatic
        private static int countOnes(int x, int y) {
            return Integer.bitCount(applyFormula(x, y))
        }
    }
}
