package com.chemaxon.bzaicsek.challenge

class DragonChecksum {

    static void main(String[] args) {
        println checksum(dragonCodeUntil('10111100110001111', 272))
        println checksum(dragonCodeUntil('10111100110001111', 35651584))
    }

    private static String dragonCodeUntil(String bits, int size) {
        String curr = bits
        while(curr.size() < size) {
            curr = dragonCode(curr)
        }
        return curr.substring(0, size)
    }

    private static String dragonCode(String bits) {
        "${bits}0${inversInvers(bits)}"
    }
    private static String inversInvers(String bits) {
        StringBuilder sb = new StringBuilder()
        for(int i=bits.size()-1; i>=0; --i) {
            if('0' == bits[i]) {
                sb.append('1')
            } else {
                sb.append('0')
            }
        }
        return sb.toString()
    }

    private static String checksum(String dragonCode) {
        String curr = dragonCode
        while(curr.size() %2 == 0) {
            curr = zip(curr)
        }
        return curr
    }

    private static String zip(String dragonCode) {
        StringBuilder sb = new StringBuilder()
        for(int i=0; i<dragonCode.size()-1; i+=2) {
            if(dragonCode[i] == dragonCode[i+1]) {
                sb.append('1')
            } else {
                sb.append('0')
            }
        }
        return sb.toString()
    }
}
