package com.chemaxon.bzaicsek.challenge

import java.time.Duration
import java.time.Instant

class IPSnooper {

    static class Ip {
        private final String repr;

        Ip(String repr) {
            this.repr = repr;
        }

        def supportsAbba() {
            boolean foundOut = false
            boolean foundIn = false
            int squareDepth = 0
            for (int i = 0; i < repr.length() - 3; ++i) {
                char c = repr.charAt(i)
                if (c == '[' as char) {
                    ++squareDepth
                } else if (c == ']' as char) {
                    --squareDepth
                } else if (squareDepth == 0) {
                    if (c != repr.charAt(i + 1) && repr[i + 1] == repr[i + 2] && c == repr[i + 3]) {
                        foundOut = true
                    }
                } else {
                    if (c != repr.charAt(i + 1) && repr[i + 1] == repr[i + 2] && c == repr[i + 3]) {
                        foundIn = true
                    }
                }
            }
            return foundOut && !foundIn
        }

        def supportsSsl() {
            Set<String> superNets = [] as Set
            Set<String> hyperNets = [] as Set
            int squareDepth = 0
            for (int i = 0; i < repr.length() - 2; ++i) {
                char c = repr.charAt(i)
                if (c == '[' as char) {
                    ++squareDepth
                } else if (c == ']' as char) {
                    --squareDepth
                } else if (squareDepth == 0) {
                    if (c != repr.charAt(i + 1) && c == repr[i + 2]) {
                        superNets += "${c}${repr[i + 1]}${repr[i + 2]}"
                    }
                } else {
                    if (c != repr.charAt(i + 1) && c == repr[i + 2]) {
                        hyperNets += "${c}${repr[i + 1]}${repr[i + 2]}"
                    }
                }
            }
            return hyperNets.collect { mirror(it) }.collect { superNets.contains(it) }.inject(false) { acc, val -> acc || val }
        }

        private def mirror(String ssl) {
            return "${ssl[1]}${ssl[0]}${ssl[1]}"
        }
    }

    static void main(String[] args) {
        String IPS = new File(IPSnooper.class.getResource("/ips.txt").toURI()).text
        def ips = IPS.split('\n').collect { new Ip(it) }
        def supportsAbba = ips.findAll { it.supportsAbba() }
        def supportsSsl = ips.findAll { it.supportsSsl() }
        println "supports abba: ${supportsAbba.size()}"
        println "supports ssl: ${supportsSsl.size()}"
    }

}
