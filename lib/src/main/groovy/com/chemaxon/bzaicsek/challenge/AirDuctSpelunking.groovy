package com.chemaxon.bzaicsek.challenge

import groovy.transform.Canonical

class AirDuctSpelunking {
    static void main(String[] args) {
        def robot = new HvacRobot(readInputLines())
        println robot.findSorthestPathLength()
        def smartRobot = new HvacRobot(readInputLines(), true)
        println smartRobot.findSorthestPathLength()
    }

    private static class HvacRobot {
        private static final String OUT = '*'
        private static final String WALL = '#'
        private static final String PATH = '.'
        private static final String START = '0'

        private static final Set<String> NOT_WALKABLE = [OUT, WALL]
        private static final Set<String> NOT_INTERESTING = [PATH, START]

        final int width
        final int height
        final List<String> map
        final Set<String> goal
        final Coord startCoord
        final boolean returnHome

        HvacRobot(List<String> map, boolean returnHome = false) {
            this.map = map
            this.returnHome = returnHome
            this.height = map.size()
            this.width = map[0].size()
            goal = map.collect { it.toCharArray() as Set }.flatten() as Set - [WALL, PATH, START]
            startCoord = findStart(map)
        }

        int findSorthestPathLength() {
            Tuple2<State, Integer> start = new Tuple2<>(new State(startCoord, [] as Set), 0)
            Queue<Tuple2<State, Integer>> queue = new LinkedList<>()
            Set<State> seen = new HashSet<>()
            queue.add(start)
            seen.add(start)
            while(!queue.isEmpty()) {
                Tuple2<State, Integer> top = queue.poll()
                if(top.v1.isEndState()) {
                    return top.v2
                }
                top.v1.nextStates().each {
                    if(!seen.contains(it)) {
                        queue.add(new Tuple2<>(it, top.v2+1))
                        seen.add(it)
                    }
                }
            }
            throw new IllegalStateException("goal is unreachable")
        }

        String getAt(int x, int y) {
            if (y >= 0 && y < height && x >= 0 && x < width) {
                return map[y][x]
            }
            return OUT
        }

        String getAt(Coord c) {
            return getAt(c.x, c.y)
        }

        private static Coord findStart(List<String> map) {
            for (int y = 0; y < map.size(); ++y) {
                int x = map[y].indexOf(START)
                if (x >= 0) {
                    return new Coord(x, y)
                }
            }
            throw new IllegalArgumentException("There is no starting position on map!")
        }

        private static boolean isWalkable(String str) {
            return !(NOT_WALKABLE.contains(str))
        }

        private static boolean isInteresting(String str) {
            return !(NOT_INTERESTING.contains(str))
        }


        @Canonical
        private static class Coord {
            final int x
            final int y

            Coord diff(int x, int y) {
                return new Coord(this.x + x, this.y + y)
            }
        }

        @Canonical
        private class State {
            final Coord location
            final Set<String> inventory

            List<State> nextStates() {
                List<State> result = []
                def changes = [[-1, 0], [1, 0], [0, -1], [0, 1]]
                for (change in changes) {
                    def (difX, difY) = change
                    def diff = location.diff(difX, difY)
                    String at = HvacRobot.this[diff]
                    if (HvacRobot.isWalkable(at)) {
                        def set = inventory
                        if (HvacRobot.isInteresting(at)) {
                            set = set + at
                        }
                        result << new State(diff, set)
                    }
                }
                return result
            }

            boolean isEndState() {
                if(!returnHome) {
                    return inventory == HvacRobot.this.goal
                } else {
                    return location == startCoord && inventory == HvacRobot.this.goal
                }
            }
        }
    }

    private static List<String> readInputLines() {
        AirDuctSpelunking.getResource('/air-duct-spelunking-map.txt').readLines()
    }


}
