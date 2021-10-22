package com.chemaxon.bzaicsek.challenge

import sun.security.provider.MD5

class PasswordBreaker {

    final String password

    PasswordBreaker(String doorId) {
        StringBuilder passwdCollector = new StringBuilder()
        def idx = 0L
        while(passwdCollector.size()<8) {
            String hash = (doorId+idx).md5()
            if(hash.startsWith('00000')) {
                passwdCollector.append(hash.charAt(5))
            }
            ++idx
        }
        password = passwdCollector.toString()
    }

    static void main(String[] args) {
        def pb = new PasswordBreaker("ffykfhsq")
        println(pb.password)
    }
}
