package com.chemaxon.bzaicsek.challenge

import groovy.transform.Canonical

class FirewallRules {
    private static final long MAX = 4_294_967_295L

    static void main(String[] args) {
        List<BlockRange> ranges = getSortedInput()
        println firstNotBlocked(ranges)
        println countNonBlocked(ranges)
    }

    static long firstNotBlocked(List<BlockRange> ranges) {
        return firstNotBlockedFrom(ranges, 0L)
    }

    static long countNonBlocked(List<BlockRange> ranges) {
        long next = firstNotBlockedFrom(ranges, 0L)
        List<BlockRange> filtered = ranges.findAll {next < it.from}
        long length = 0L
        while (!filtered.isEmpty()) {
            length += filtered.first().from - next
            next = firstNotBlockedFrom(filtered, filtered.first().to)
            filtered = ranges.findAll{next < it.from}
        }
        // last interwall is next..MAX (+1 as it is inclusive)
        return length + MAX - next + 1L
    }

    static long firstNotBlockedFrom(List<BlockRange> ranges, long start) {
        long next = start
        while(ranges.collect {it.contains(next)}.inject(false) {acc, v -> acc ||v }) {
            boolean broken = false
            for (int i = 0; i < ranges.size() && !broken; ++i) {
                if (ranges[i].contains(next)) {
                    next = ranges[i].to + 1L
                    broken = true
                }
            }
        }
        return next
    }

    static List<BlockRange> getSortedInput() {
        return FirewallRules.class.getResource('/firewall-rules.txt').readLines().collect {
            BlockRange.fromString(it)
        }.sort()
    }
    @Canonical
    private static class BlockRange implements Comparable<BlockRange> {

        private static final Comparator<BlockRange> BLOCK_RANGE_COMPARATOR =
                Comparator.comparing(BlockRange::getFrom)
                        .thenComparing(Comparator.comparing(BlockRange::getTo))

        final long from
        final long to
        BlockRange(long f, long t) {
            if(f<=t) {
                from = f
                to = t
            } else {
                from = t
                to = f
            }
        }

        boolean contains(long L) {
            from <= L && L <= to
        }

        @Override
        int compareTo(BlockRange o) {
            return BLOCK_RANGE_COMPARATOR.compare(this, o)
        }

        static BlockRange fromString(String str) {
            def m = str =~ /(\d+)-(\d+)/
            return new BlockRange(m[0][1] as long, m[0][2] as long)
        }
    }
}
