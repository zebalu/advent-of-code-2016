package com.chemaxon.bzaicsek.challenge

import groovy.transform.ToString

class TwoStepsForward {
    static void main(String[] args) {
        String key = 'bwnlcvfs'
        firstPart(key)
        secondPart(key)
    }

    private static void secondPart(String key) {
        State start = new State(key, 0, 0)
        Queue<State> queue = new LinkedList<>()
        Set<State> seen = new HashSet<>()
        queue.add(start)
        seen.add(start)
        int max = Integer.MIN_VALUE
        while (!queue.isEmpty()) {
            State state = queue.poll()
            if(state.isEnd()) {
                int length = state.path.size()-key.size()
                if(length>max) {
                    max = length
                }
            }
            state.neighbours().each {
                if (!seen.contains(it)) {
                    queue.add(it)
                    seen.add(it)
                }
            }
        }
        println max
    }

    private static void firstPart(String key) {
        State start = new State(key, 0, 0)
        Queue<State> queue = new LinkedList<>()
        Set<State> seen = new HashSet<>()
        queue.add(start)
        seen.add(start)
        while (!queue.isEmpty() && !queue.peek().isEnd()) {
            State state = queue.poll()
            state.neighbours().each {
                if (!seen.contains(it)) {
                    queue.add(it)
                    seen.add(it)
                }
            }
        }
        println queue.peek().path.substring(key.size())
    }

    @ToString
    private static class State {
        final String path
        final int x
        final int y
        final List<Boolean> doors = []

        State(String path, int x, int y) {
            this.path = path
            this.x = x
            this.y = y
            for (char c in path.md5().substring(0, 4)) {
                doors << (c ==~ /[b|c|d|e|f]/)
            }
        }

        List<State> neighbours() {
            List<State> result = []
            if(isEnd()) {
                return result
            }
            if (y - 1 >= 0 && doors[0]) {
                result << new State(path + 'U', x, y - 1)
            }
            if (y + 1 < 4 && doors[1]) {
                result << new State(path + 'D', x, y + 1)
            }
            if (x - 1 >= 0 && doors[2]) {
                result << new State(path + 'L', x - 1, y)
            }
            if (x + 1 < 4 && doors[3]) {
                result << new State(path + 'R', x + 1, y)
            }
            return result
        }

        boolean isEnd() {
            return x == 3 && y == 3
        }

        @Override
        boolean equals(Object obj) {
            if (this.is(obj)) {
                return true
            }
            if (obj instanceof State) {
                return path == obj.path
            }
            return false
        }

        @Override
        int hashCode() {
            return path.hashCode()
        }
    }
}
