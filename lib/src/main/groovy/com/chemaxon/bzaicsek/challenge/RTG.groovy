package com.chemaxon.bzaicsek.challenge

import groovy.transform.Canonical
import groovy.transform.CompileStatic

@CompileStatic
class RTG {

    static void main(String[] args) {
        firstPart()
        // takes 18 minutes
        //secondPart()
    }

    private static void firstPart() {
        List<Set<RtgElement>> floors = readFloors()
        moveUpFromFloors(floors, 'first part')
    }

    private static void secondPart() {
        List<Set<RtgElement>> floors = readFloors()
        floors[0].addAll([RtgElement.ELERIUM_CHIP, RtgElement.ELERIUM_GENERATOR, RtgElement.DILITHIUM_GENERATOR, RtgElement.DILITHIUM_CHIP])
        moveUpFromFloors(floors, 'second part')
    }

    private static void moveUpFromFloors(List<Set<RtgElement>> floors, String message) {
        FloorState init = new FloorState(
                floors[0],
                floors[1],
                floors[2],
                floors[3],
                0,
                0,
                []
        )
        Set<String> seen = new HashSet<>()
        Queue<FloorState> queue = new LinkedList<>()
        queue.add(init)
        seen.add(init.id)
        int i = 0
        while (!queue.isEmpty() && !(((FloorState) queue.peek()).isEndState())) {
            ++i
            FloorState state = queue.poll()
            seen.add(state.id)
            state.generateNextStates().each { newState ->
                if (!seen.contains(newState.id) && !newState.isFried()) {
                    queue.add(newState)
                    seen.add(newState.id)
                }
            }
            if (i % 1000 == 0) {
                println "$i\t\t${queue.size()} \t ${state.steps}"
            }
        }

        FloorState fs = (FloorState) (queue.peek())
        for (int j = 0; j < fs.history.size(); ++j) {
            println "${j + 1}: ${fs.history[j]}"
        }
        println "$message costed: ${fs.steps}"
    }

    private static List<Set<RtgElement>> readFloors() {
        List<Set<RtgElement>> floors = (List<Set<RtgElement>>)([new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>()])
        List<String> input = getInput()
        for (int i = 0; i < input.size(); ++i) {
            for (RtgElement e : RtgElement.values()) {
                if (input[i].contains(e.toString())) {
                    floors.get(i).add(e)
                }
            }
        }
        floors
    }

    static class FloorState {
        final List<Set<RtgElement>> floors
        final int level
        final int steps
        final String id
        final List<String> history

        FloorState(Set<RtgElement> firstFloor,
                   Set<RtgElement> secondFloor,
                   Set<RtgElement> thirdFloor,
                   Set<RtgElement> fourthFloor,
                   int level,
                   int steps,
                   List<String> history
        ) {
            floors = Collections.unmodifiableList([
                    Collections.unmodifiableSet(firstFloor),
                    Collections.unmodifiableSet(secondFloor),
                    Collections.unmodifiableSet(thirdFloor),
                    Collections.unmodifiableSet(fourthFloor)
            ])
            this.level = level
            this.steps = steps
            id = level + " " + generateId(floors)
            this.history = history
        }

        private static String generateId(List<Set<RtgElement>> floors) {
            def f = []
            def p = []
            for (int i = 0; i < floors.size(); ++i) {
                def fi = floors[i].sort()
                f.add(fi)
                def pairs = 0
                for (RtgElement rtge : floors[i]) {
                    if (rtge.chip && floors[i].contains(rtge.generator)) {
                        ++pairs
                        fi.removeAll([rtge, rtge.generator])
                    }
                }
                p << pairs
            }
            StringBuilder sb = new StringBuilder()
            for (int i = 0; i < f.size(); ++i) {
                def f2 = f[i].collect { it.toString() }
                f2.add('pairs: ' + (p[i]))
                sb.append(f2.toString())
            }
            return sb.toString()
        }

        boolean isFried() {
            return floors.collect { isFriedFloor(it) }.inject(false) { a, v -> a || v }
        }

        boolean isEndState() {
            return floors.last().size() == floors.flatten().size()
        }

        List<FloorState> generateNextStates() {
            if (isEndState() || isFried()) {
                return []
            }
            return (List<FloorState>) generateNextSteps().collect { step ->
                List<FloorState> res = []
                if (level < 3) {
                    List<Set<RtgElement>> newFloors = toFloorList(step, level + 1)
                    res += new FloorState(newFloors[0], newFloors[1], newFloors[2], newFloors[3], level + 1, steps + 1, history + ["${step.elevator} up to ${level + 1}".toString()])
                }
                if (level > 0 && !subFloorsAreEmpty()) {
                    List<Set<RtgElement>> newFloors = toFloorList(step, level - 1)
                    res += new FloorState(newFloors[0], newFloors[1], newFloors[2], newFloors[3], level - 1, steps + 1, history + ["${step.elevator} down to ${level - 1}".toString()])
                }
                return res
            }.flatten()
        }

        private boolean subFloorsAreEmpty() {
            boolean result = true
            for (int i = level - 1; i >= 0; --i) {
                result &= floors[i].isEmpty()
            }
            return result
        }

        @Override
        String toString() {
            return "l: $level, s: $steps; \t $floors"
        }

        @Override
        boolean equals(Object obj) {
            if (this.is(obj)) {
                return true
            }
            if (obj instanceof FloorState) {
                return id == obj.id
            }
            return false
        }

        @Override
        int hashCode() {
            return id.hashCode()
        }

        private List<Set<RtgElement>> toFloorList(Carry carry, int target) {
            List<Set<RtgElement>> res = []
            for (int i = 0; i < floors.size(); ++i) {
                res << toFloor(carry, target, i)
            }
            return res
        }

        private Set<RtgElement> toFloor(Carry carry, int target, int from) {
            if (from == level) {
                return carry.remaining
            } else if (target == from) {
                return carry.elevator + floors[from]
            } else {
                return floors[from]
            }
        }

        private Set<Carry> generateNextSteps() {
            Set<Carry> result = [] as Set
            Set<RtgElement> curr = floors[level]
            for (RtgElement first : curr) {
                for (RtgElement second : curr) {
                    Set<RtgElement> elevator = [first, second] as Set
                    Carry carry = new Carry(elevator, curr - elevator)
                    if (!carry.isFried()) {
                        result << carry
                    }
                }
            }
            return result
        }

        private static boolean isFriedFloor(Set<RtgElement> floor) {
            return floor.collect { it.isFried(floor) }.inject(false) { a, v -> a || v }
        }

        @Canonical
        private static final class Carry {
            private final Set<RtgElement> elevator
            private final Set<RtgElement> remaining

            Carry(Set<RtgElement> elevator, Set<RtgElement> remaining) {
                this.elevator = Collections.unmodifiableSet(elevator)
                this.remaining = Collections.unmodifiableSet(remaining)
            }

            @Override
            int hashCode() {
                int e = elevator.inject(31) { a, v -> a * 31 + v.hashCode() }
                int r = remaining.inject(71) { a, v -> a * 71 + v.hashCode() }
                return e * 17 + r
            }

            @Override
            boolean equals(Object obj) {
                if (obj instanceof Carry) {
                    Carry other = (Carry) obj
                    return elevator.containsAll(other.elevator) && other.elevator.containsAll(elevator)
                            && remaining.containsAll(other.remaining) && other.remaining.containsAll(remaining)
                }
                return false
            }

            boolean isFried() {
                return isFriedFloor(elevator) || isFriedFloor(remaining)
            }
        }

    }

    static enum RtgElement {
        STRONTIUM_GENERATOR('strontium generator', null, 3),
        PLUTONIUM_GENERATOR('plutonium generator', null, 5),
        THULIUM_GENERATOR('thulium generator', null, 7),
        RUTHENIUM_GENERATOR('ruthenium generator', null, 11),
        CURIUM_GENERATOR('curium generator', null, 13),
        ELERIUM_GENERATOR('elerium generator', null, 17),
        DILITHIUM_GENERATOR('dilithium generator', null, 19),

        STRONTIUM_CHIP('strontium-compatible microchip', STRONTIUM_GENERATOR, 23),
        PLUTONIUM_CHIP('plutonium-compatible microchip', PLUTONIUM_GENERATOR, 29),
        THULIUM_CHIP('thulium-compatible microchip', THULIUM_GENERATOR, 31),
        RUTHENIUM_CHIP('ruthenium-compatible microchip', RUTHENIUM_GENERATOR, 37),
        CURIUM_CHIP('curium-compatible microchip', CURIUM_GENERATOR, 41),
        ELERIUM_CHIP('elerium-compatible microchip', ELERIUM_GENERATOR, 43),
        DILITHIUM_CHIP('dilithium-compatible microchip', DILITHIUM_GENERATOR, 47);

        final String name
        RtgElement generator
        final boolean chip
        final int id

        RtgElement(String name, RtgElement generator, int id) {
            this.name = name
            this.generator = generator
            this.chip = generator != null
            this.id = id
        }

        boolean isFried(Set<RtgElement> level) {
            if (chip) {
                if (level.contains(generator)) {
                    return false
                }
                return !(level.findAll { !it.chip }.isEmpty())
            }
            return false
        }

        @Override
        String toString() {
            return name
        }
    }

    static List<String> getInput() {
        return RTG.class.getResource('/microchip-generator-floor-description.txt').readLines()
    }
}
