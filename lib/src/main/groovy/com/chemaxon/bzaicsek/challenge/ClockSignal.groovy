package com.chemaxon.bzaicsek.challenge

class ClockSignal {
    /*
cpy a d
cpy 11 c
cpy 231 b
inc d
dec b
jnz b -2
dec c
jnz c -5
// this is a + (11 * 231) in d
cpy d a
jnz 0 0 //fancy SKIP
cpy a b
cpy 0 a
cpy 2 c
jnz b 2
jnz 1 6
dec b
dec c
jnz c -4
inc a
jnz 1 -7
// this is a = b / 2 (int div, where b was (initial)a + 11 * 231) AND c = b % 2
cpy 2 b
jnz c 2
jnz 1 4
dec b
dec c
jnz 1 -4
jnz 0 0 // fancy SKIP
out b
// if a + 11 * 231 is even --> outputs 2 | if odd --> outputs 1
// then restart the loop with b / 2 (int div) /instead of initial_a + 231 * 11
jnz a -19
// then restart the loop with b / 2 (int div) /instead of initial_a + 231 * 11
jnz 1 -21
// this means restart the full program with (initial_a + 231 * 11)
// so we are looking for the smallest integer (a) that is added to valueAtLine(2')*valueAtLine(3') (': index is less with 1)
// gives the binary representation ...10101010... or ...01010101...
     */

    static void main(String[] args) {
        def code = ClockSignal.getResource('/clock-signal-assembuny.txt').readLines()
        def mul = extractVal(code[1]) * extractVal(code[2])
        println findSmallestIntToAlternateBits(mul)
    }

    private static int extractVal(String str) {
        str.split(' ')[1] as int
    }

    private static int findSmallestIntToAlternateBits(int base) {
        for (int i = 0; i < Integer.MAX_VALUE; ++i) {
            if (isAlternating(base + i)) {
                return i
            }
        }
        throw new IllegalStateException("thi code should not be reached")
    }

    private static boolean isAlternating(int val) {
        int curr = val.intdiv(2)
        def bits = [val % 2]
        while (curr > 0) {
            def bit = curr % 2
            if (bit == bits.last()) {
                return false
            }
            bits << bit
            curr = curr.intdiv(2)
        }
        return true
    }
}