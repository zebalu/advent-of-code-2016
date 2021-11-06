package com.chemaxon.bzaicsek.challenge

import groovy.transform.Canonical

class GridComputing {

    static void main(String[] args) {
        def nodes = readNodes()
        firstPart(nodes)
        secondPart(nodes)
    }

    private static void firstPart(List<GridNode> nodes) {
        println countViablePairs(nodes)
    }

    private static void secondPart(List<GridNode> nodes) {
        println new ComputerGrid(nodes).countMinimalStepsToGetData()
    }


    private static int countViablePairs(List<GridNode> nodes) {
        int result = 0
        for (int i = 0; i < nodes.size(); ++i) {
            GridNode a = nodes[i]
            for (int j = i + 1; j < nodes.size(); ++j) {
                GridNode b = nodes[j]
                if (!a.empty && a.fits(b)) {
                    ++result
                }
                if (!b.empty && b.fits(a)) {
                    ++result
                }
            }
        }
        return result
    }

    private static List<GridNode> readNodes() {
        GridComputing.getResource('/grid-computing-df.txt')
                .readLines().drop(2).collect { new GridNode(it) }
    }

    private static class GridNode {
        final int x
        final int y
        final int size
        int used
        int avail
        int usePercent

        GridNode(String desc) {
            def m = desc =~ $//dev/grid/node-x(\d+)-y(\d+)\s+(\d+)T\s+(\d+)T\s+(\d+)T\s+(\d+)%/$
            x = m[0][1] as int
            y = m[0][2] as int
            size = m[0][3] as int
            used = m[0][4] as int
            avail = m[0][5] as int
            usePercent = m[0][6] as int
            assert (size == used + avail)
        }

        boolean isEmpty() {
            return used == 0
        }

        boolean isFull() {
            return avail == 0
        }

        boolean canStore(GridNode other) {
            return other.used <= avail
        }

        boolean fits(GridNode other) {
            return used <= other.avail
        }
    }

    private static class ComputerGrid {
        private static final String EMPTY = "_"
        private static final String FIX = "#"
        private static final String MOVABLE = "."

        private final Map<Coord, String> grid = new HashMap<>()
        private final int width
        private final int height

        private Coord empty
        private Coord goal

        ComputerGrid(List<GridNode> nodes) {
            def empties = nodes.findAll { it.empty }
            assert (empties.size() == 1)
            empty = c(empties[0])
            int maxX = Integer.MIN_VALUE
            int maxY = Integer.MIN_VALUE
            for (node in nodes) {
                grid[c(node)] = node.empty ? EMPTY : node.used > empties[0].size ? FIX : MOVABLE
                if (node.x > maxX) {
                    maxX = node.x
                }
                if (node.y > maxY) {
                    maxY = node.y
                }
            }
            height = maxY + 1
            width = maxX + 1
            goal = c(maxX, 0)
        }

        String getAt(Coord coord) {
            grid[coord]
        }

        String getAt(int x, int y) {
            this[c(x, y)]
        }

        boolean isFixed(int x, int y) {
            this[x, y] == FIX
        }

        int countMinimalStepsToGetData() {
            State start = new State(empty, goal)
            Queue<Tuple2<State, Integer>> queue = new LinkedList<>()
            Set<State> seen = new HashSet<>()
            queue.add(new Tuple2<>(start, 0))
            seen.add(start)
            while(!queue.isEmpty() && !queue.peek().v1.end) {
                def current = queue.poll()
                current.v1.nextStates(this).each {
                    if(!seen.contains(it)) {
                        queue.add(new Tuple2(it, current.v2+1))
                        seen.add(it)
                    }
                }
            }
            return queue.peek().v2
        }

        private static Coord c(GridNode node) {
            c(node.x, node.y)
        }

        private static Coord c(int x, int y) {
            new Coord(x, y)
        }

        @Canonical
        private static class Coord {
            final int x
            final int y
        }

        @Canonical
        private static class State {
            final Coord empty
            final Coord goal

            boolean isEnd() {
                goal.x == 0 && goal.y == 0
            }

            List<State> nextStates(ComputerGrid grid) {
                List<State> res = []
                if (empty.x > 0 && !grid.isFixed(empty.x - 1, empty.y)) {
                    res << moveEmptyTo(empty.x - 1, empty.y)
                }
                if (empty.x + 1 < grid.width && !grid.isFixed(empty.x + 1, empty.y)) {
                    res << moveEmptyTo(empty.x + 1, empty.y)
                }
                if (empty.y > 0 && !grid.isFixed(empty.x, empty.y - 1)) {
                    res << moveEmptyTo(empty.x, empty.y - 1)
                }
                if (empty.y + 1 < grid.height && !grid.isFixed(empty.x, empty.y + 1)) {
                    res << moveEmptyTo(empty.x, empty.y + 1)
                }
                return res
            }

            private State moveEmptyTo(int x, int y) {
                if (x == goal.x && y == goal.y) {
                    return new State(goal, empty)
                } else {
                    return new State(new Coord(x, y), goal)
                }
            }
        }
    }
}
