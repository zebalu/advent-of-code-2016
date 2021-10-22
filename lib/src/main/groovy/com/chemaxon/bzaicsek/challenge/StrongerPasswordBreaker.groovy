package com.chemaxon.bzaicsek.challenge

class StrongerPasswordBreaker {

    final String password

    StrongerPasswordBreaker(String doorId) {
        Map<Integer, Character> passwdCollector = [:]
        def idx = 0L
        while(passwdCollector.size()<8) {
            String hash = (doorId+idx).md5()
            if(hash.startsWith('00000')) {
                int position = Integer.parseInt(hash.substring(5,6), 16);
                if(!passwdCollector.containsKey(position) && position in 0..7) {
                    passwdCollector.put(position, hash.charAt(6))
                }
            }
            ++idx
        }
        println passwdCollector
        String s = ""
        for(int i=0; i<=8; ++i) {
            if(passwdCollector.containsKey(i)) {
                s+=passwdCollector[i]
            }
        }
        password = s
    }

    static void main(String[] args) {
        def pb = new StrongerPasswordBreaker("ffykfhsq")
        println(pb.password)
    }
}
