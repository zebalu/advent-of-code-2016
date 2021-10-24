package com.chemaxon.bzaicsek.challenge

class Decompresser {

    static void main(String[] args) {
        /*
        String example = 'abc(6x3)d(1x3)efg'
        println decompress(example)
        println countDecompression(example)*/

        String input = readInput()
        println "simple decopmress length: ${decompress(input).length()}"
        println "recursive decompressLength: ${countDecompression(input)}"
    }

    private static String decompress(String compressed) {
        StringBuilder decompressed = new StringBuilder()
        Iterator<Character> charIterator = compressed.chars.iterator()
        while (charIterator.hasNext()) {
            char c = charIterator.next()
            if (c == '(' as char) {
                def rl = new RepeateLength(charIterator)
                String repeat = readMany(charIterator, rl.length)
                rl.repeat.times { decompressed.append(repeat) }
            } else {
                decompressed.append(c)
            }
        }
        return decompressed.toString()
    }

    private static long countDecompression(String compressed) {
        long decompressedLength = 0L
        Iterator<Character> charIterator = compressed.chars.iterator()
        while (charIterator.hasNext()) {
            char c = charIterator.next()
            if (c == '(' as char) {
                def rl = new RepeateLength(charIterator)
                String repeat = readMany(charIterator, rl.length)
                long subLength = countDecompression(repeat)
                decompressedLength += (rl.repeat * subLength)
            } else {
                decompressedLength += 1L
            }
        }
        return decompressedLength
    }

    private static String readMany(Iterator<Character> iterator, int length) {
        StringBuilder sb = new StringBuilder()
        for (int i = 0; i < length; ++i) {
            sb.append(iterator.next())
        }
        return sb.toString()
    }

    static class RepeateLength {
        final int length
        final int repeat

        RepeateLength(Iterator<Character> iterator) {
            StringBuilder lengthStr = new StringBuilder()
            StringBuilder repeateStr = new StringBuilder()
            Character curr = iterator.next()
            while (iterator.hasNext() & 'x' as char != curr) {
                lengthStr.append(curr)
                curr = iterator.next()
            }
            curr = iterator.next()
            while (iterator.hasNext() & ')' as char != curr) {
                repeateStr.append(curr)
                curr = iterator.next()
            }
            length = lengthStr.toString() as int
            repeat = repeateStr.toString() as int
        }
    }

    private static String readInput() {
        new File(Decompresser.getResource("/compressed-data.txt").toURI()).readLines().join('')
    }
}
