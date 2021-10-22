package com.chemaxon.bzaicsek.challenge

class GridWalk {

    List<String> commands;

    int coordX = 0
    int coordY = 0
    Facing facing = Facing.UP

    GridWalk() {
        commands = gridInput.split(',').collect { it.trim() }
        commands.each {
            TURN turn = TURN.valueOf(it.substring(0, 1))
            int length = java.lang.Integer.parseInt(it.substring(1))
            move(turn, length)
        }
    }

    public int distance() {
        return Math.abs(coordX) + Math.abs(coordY);
    }

    private def move(TURN turn, length) {
        facing = transit(facing, turn)
        switch (facing) {
            case Facing.UP: coordY -= length; break;
            case Facing.RIGHT: coordX += length; break;
            case Facing.DOWN: coordY += length; break;
            case Facing.LEFT: coordX -= length; break;
        }
    }


    static String gridInput = 'R1, L3, R5, R5, R5, L4, R5, R1, R2, L1, L1, R5, R1, L3, L5, L2, R4, L1, R4, R5, L3, R5, L1, R3, L5, R1, L2, R1, L5, L1, R1, R4, R1, L1, L3, R3, R5, L3, R4, L4, R5, L5, L1, L2, R4, R3, R3, L185, R3, R4, L5, L4, R48, R1, R2, L1, R1, L4, L4, R77, R5, L2, R192, R2, R5, L4, L5, L3, R2, L4, R1, L5, R5, R4, R1, R2, L3, R4, R4, L2, L4, L3, R5, R4, L2, L1, L3, R1, R5, R5, R2, L5, L2, L3, L4, R2, R1, L4, L1, R1, R5, R3, R3, R4, L1, L4, R1, L2, R3, L3, L2, L1, L2, L2, L1, L2, R3, R1, L4, R1, L1, L4, R1, L2, L5, R3, L5, L2, L2, L3, R1, L4, R1, R1, R2, L1, L4, L4, R2, R2, R2, R2, R5, R1, L1, L4, L5, R2, R4, L3, L5, R2, R3, L4, L1, R2, R3, R5, L2, L3, R3, R1, R3'

    enum Facing {
        UP, RIGHT, DOWN, LEFT
    }

    enum TURN {
        R, L
    }

    def transit(Facing facing, TURN turn) {
        switch (turn) {
            case TURN.R:
                switch (facing) {
                    case Facing.UP: return Facing.RIGHT;
                    case Facing.RIGHT: return Facing.DOWN;
                    case Facing.DOWN: return Facing.LEFT;
                    case Facing.LEFT: return Facing.UP;
                }
                break;
            case TURN.L:
                switch (facing) {
                    case Facing.UP: return Facing.LEFT;
                    case Facing.RIGHT: return Facing.UP;
                    case Facing.DOWN: return Facing.RIGHT;
                    case Facing.LEFT: return Facing.DOWN;
                }
                break
        }
        throw new IllegalStateException();
    }

    static void main(String[] args) {
        println new GridWalk().distance()
    }
}

