package com.villermen.bitstream;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Reads bits from an existing input stream.
 *
 * @author Villermen
 */
public class BitInputStream extends InputStream {
    private byte readingByte;
    private int bitsRead = 8;
    private InputStream in;

    public BitInputStream(InputStream in) {
        this.in = in;
    }

    /**
     * Reads a bit from the input stream.
     * Will request a new byte only when done with the previous one.
     * Reading regular bytes will still have readBit() finish its own byte before continuing.
     *
     * @return The read bit.
     * @throws IOException
     */
    public boolean readBit() throws IOException {
        //request a new byte if done with the last one
        if (bitsRead == 8) {
            int readValue = read();

            if (readValue == -1) {
                throw new EOFException("End of file reached while trying to read next bit.");
            }

            readingByte = (byte) readValue;
            bitsRead = 0;
        }

        bitsRead++;

        return (readingByte >> 8 - bitsRead & 1) != 0;
    }

    /**
     * Reads the specified amount of bits into a string.
     *
     * @param amount The amount of bits to read.
     * @return A bitstring where bits are represented with '0' and '1'.
     * @throws IOException
     */
    public String readBits(int amount) throws IOException {
        String result = "";
        for (int i = 0; i < amount; i++) {
            result += readBit() ? "1" : "0";
        }

        return result;
    }

    /**
     * Disregards the progress into the current byte, and starts with a new one on next read.
     *
     * @return The amount of bits discarded.
     */
    public int finishByte() {
        int bitsDiscarded = 8 - bitsRead;

        bitsRead = 8;

        return bitsDiscarded;
    }

    public int read() throws IOException {
        return in.read();
    }
}
