package org.ethelred.leveldb;

/**
 * just functions so I can remind myself how various binary operations work
 */
public class TestBinaryManipulation {

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            String oneBits = Integer.toBinaryString((2 << (i - 1)) - 1);
            System.out.println(i + " => " + oneBits);
        }
    }
}
