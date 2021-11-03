package com.chemaxon.bzaicsek.challenge

class AnElephantNamedJoseph {
    static void main(String[] args) {
        println josephusProblem(3014603)
        println crossJosephus(3014603)
    }

    // From: https://en.wikipedia.org/wiki/Josephus_problem
    private static int josephusProblem(int n) {
        int valueOfL = n - Integer.highestOneBit(n);
        return 2 * valueOfL + 1;
    }

    private static int crossJosephus(int n) {
        LinkedList<Integer> list = new LinkedList<>()
        for (int i = 0; i < n; ++i) {
            list << (i + 1)
        }
        Iterator<Integer> to = list.iterator()
        if (n % 2 == 0 && n > 1) {
            to.next()
        }
        for (int i = 0; i < list.size() / 2 && to.hasNext(); ++i) {
            to.next()
        }
        while (list.size() > 1) {
            to.remove()
            int repeat = list.size() % 2 == 1 ? 1 : 2
            repeat.times {
                if (!to.hasNext()) {
                    to = list.iterator()
                }
                to.next()
            }
        }
        return list[0]
    }

}
