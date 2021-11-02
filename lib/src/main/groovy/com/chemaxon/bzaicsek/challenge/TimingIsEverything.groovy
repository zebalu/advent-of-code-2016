package com.chemaxon.bzaicsek.challenge

import groovy.transform.Canonical

import java.time.Duration
import java.time.Instant

class TimingIsEverything {
    static void main(String[] args) {
        firstPart()
        secondPart()
    }

    private static void firstPart() {
        def dics = readDiscs()
        def nums = dics.collect { it.positions }
        def rems = dics.collect { it.desiredMod }
        int chRT = chineseRemiainderTheorem(nums, rems)
        println chRT
    }

    private static void secondPart() {
        def dics = (readDiscs() + new Disc('Disc #7 has 11 positions; at time=0, it is at position 0.'))
        def nums = dics.collect { it.positions }
        def rems = dics.collect { it.desiredMod }
        int chRT = chineseRemiainderTheorem(nums, rems)
        println chRT
    }

    private static int bruteForce(List<Disc> dics) {
        int i = 0
        while (!dics.inject(true) { acc, val -> acc && val.isDesired(i) }) {
            ++i
        }
        return i
    }

    // from: https://www.freecodecamp.org/news/how-to-implement-the-chinese-remainder-theorem-in-java-db88a3f1ffe0/
    private static int chineseRemiainderTheorem(List<Integer> nums, List<Integer> rems) {
        int product = nums.inject(1) { acc, val -> acc * val }
        List<Integer> partialProducts = nums.collect { (int) (product / it) }
        def inverses = (0..(nums.size() - 1)).collect { computeInverse(partialProducts[it], nums[it]) }
        return (0..(nums.size() - 1)).collect { partialProducts[it] * inverses[it] * rems[it] }.sum() % product
    }

    private static int computeInverse(int partialProduct, int num) {
        if (num == 1) {
            return 0
        }
        int a = partialProduct
        int b = num
        int x = 0
        int y = 1
        // Apply extended Euclid Algorithm
        while (a > 1) {
            // q is quotient
            int q = a / b
            // now proceed same as Euclid's algorithm
            int oldB = b
            b = a % b
            a = oldB
            int oldX = x
            x = y - q * x
            y = oldX
        }
        // Make x1 positive
        if (y < 0) {
            y += num
        }
        return y
    }

    private static List<Disc> readDiscs() {
        INPUT.readLines().collect { new Disc(it) }
    }

    @Canonical
    private static class Disc {
        final int id
        final int positions
        final int startPosition
        final int desiredMod

        Disc(String line) {
            def m = line =~ /Disc #(\d+) has (\d+) positions; at time=0, it is at position (\d+)\./
            id = m[0][1] as int
            positions = m[0][2] as int
            startPosition = m[0][3] as int
            desiredMod = (positions * 2 - id - startPosition) % positions
        }

        boolean isDesired(int time) {
            //println("$id, $startPosition, $positions, $desiredMod ==> ${((time + id + startPosition) % positions)}")
            (time + id + startPosition) % positions == 0
        }
    }

    private static final String INPUT =
            '''Disc #1 has 7 positions; at time=0, it is at position 0.
Disc #2 has 13 positions; at time=0, it is at position 0.
Disc #3 has 3 positions; at time=0, it is at position 2.
Disc #4 has 5 positions; at time=0, it is at position 2.
Disc #5 has 17 positions; at time=0, it is at position 0.
Disc #6 has 19 positions; at time=0, it is at position 7.'''
}
