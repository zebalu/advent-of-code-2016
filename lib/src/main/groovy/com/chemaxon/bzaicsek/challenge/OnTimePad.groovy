package com.chemaxon.bzaicsek.challenge

class OnTimePad {

    static void main(String[] args) {
        println findUntil('ngcjuoqr', 64, false).last()
        println findUntil('ngcjuoqr', 64, true).last()
    }

    private static List<Integer> findUntil(String salt, int end, boolean stretch = false) {
        int num = 0
        def res = []
        Map<Integer, String> hashCache = new HashMap<>()
        while (res.size() < end) {
            if (isValidNum(salt, num, stretch, hashCache)) {
                res << num
            }
            ++num
            /*
            if(num%1_000 == 0) {
                println "num: $num \t res: ${res.size()}"
            }
             */
        }
        return res
    }

    private static Boolean isValidNum(String salt, int num, boolean stretch, Map<Integer, String> hashCache) {
        Character c = containedTriplet(findHash(salt, num, stretch, hashCache))
        if (c && contains5of(salt, c, num + 1..num + 1000, stretch, hashCache)) {
            return true
        }
        return false
    }

    private static Character containedTriplet(String candidate) {
        String key = candidate
        for (int i = 0; i < key.size() - 2; ++i) {
            if (key[i] == key[i + 1] &&
                    key[i + 1] == key[i + 2]) {
                return key.charAt(i)
            }
        }
        return null
    }

    private static boolean contains5of(String salt, int num, Character c, boolean stretch, Map<Integer, String> hashCache) {
        StringBuilder sb = new StringBuilder()
        5.times { sb.append(c) }
        return findHash(salt, num, stretch, hashCache).contains(sb.toString())
    }

    private static boolean contains5of(String salt, Character c, IntRange range, boolean stretch, Map<Integer, String> hashCache) {
        for (num in range) {
            if (contains5of(salt, num, c, stretch, hashCache)) {
                return true
            }
        }
        return false
    }

    private static findHash(String salt, int num, boolean stretch, Map<Integer, String> hashCache) {
        def hash = hashCache[num]
        if(!hash) {
            hash = "$salt$num".md5()
            if(stretch) {
                2016.times {hash = hash.md5()}
            }
            hashCache[num]=hash
        }
        return hash
    }

}
