package com.chemaxon.bzaicsek.challenge

import groovy.transform.Canonical

class LikeARouge {
    private static final String FIRST_ROW = '^.^^^..^^...^.^..^^^^^.....^...^^^..^^^^.^^.^^^^^^^^.^^.^^^^...^^...^^^^.^.^..^^..^..^.^^.^.^.......'

    static void main(String[] args) {
        firstPart()
        secondPart()
    }

    private static void secondPart() {
        TileMap map = new TileMap(FIRST_ROW, 400_000)
        println map.countSafes()
    }

    private static void firstPart() {
        TileMap map = new TileMap(FIRST_ROW, 40)
        println map.countSafes()
    }

    private static class TileMap {
        private static final String TRAP = '^'
        private static final String SAFE = '.'
        private static final Set<String> TRAPS_ABOVE = [
                "${TRAP}${TRAP}${SAFE}".toString(),
                "${SAFE}${TRAP}${TRAP}".toString(),
                "${TRAP}${SAFE}${SAFE}".toString(),
                "${SAFE}${SAFE}${TRAP}".toString()
        ] as Set

        List<String> map = []
        int width
        int height

        TileMap(String firstRow, int height) {
            this.width = firstRow.size()
            this.height = height
            mapFirstRow(firstRow)
            for(int r=1; r<height; ++r) {
                StringBuilder sb = new StringBuilder()
                for(int c=0; c<width; ++c) {
                    sb.append(calcNewTile(r, c))
                }
                String line = sb.toString()
                map << line
            }
        }

        int countTraps() {
            map.collect{it.count(TRAP)}.sum()
        }

        int countSafes() {
            map.collect {it.count(SAFE)}.sum()
        }

        private void mapFirstRow(String firstRow) {
            map << firstRow
        }

        private String calcNewTile(int row, int col) {
            String above = "${getTile(row-1, col-1)}${getTile(row-1, col)}${getTile(row-1, col+1)}"
            // println "$row, $col: $above \t ${TRAPS_ABOVE.contains(above)}"
            if(TRAPS_ABOVE.contains(above)) {
                //map[new Coord(col, row)] = TRAP
                return TRAP
            } else {
                //map[new Coord(col, row)] = SAFE
                return SAFE
            }
        }

        private String getTile(int row, int col) {
            if(col<0 || col>=width) {
                return SAFE
            }
            return map[row][col]
        }

        @Override
        String toString() {
            StringBuilder sb = new StringBuilder()
            sb.append("width: $width,\theight: $height\n")
            for(int i =0; i<height; ++i) {
                sb.append(map[i]+"\n")
            }
            return sb.toString()
        }
    }

    @Canonical
    private static class Coord {
        int x
        int y
    }
}
