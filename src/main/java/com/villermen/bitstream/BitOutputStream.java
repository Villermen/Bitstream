package com.villermen.bitstream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Writes bits to an existing output stream.
 * Writes whenever a full byte is available, or when finishByte() is called.
 *
 * @author Villermen
 */
public class BitOutputStream extends OutputStream {
    private byte writingByte = 0;
    private int bitsWritten = 0;
    private OutputStream out;

    public BitOutputStream(OutputStream out) {
        this.out = out;
    }

    /**
     * Writes a single bit to the output stream.
     *
     * @param value The bit to write
     * @throws IOException
     */
    public void write(boolean value) throws IOException {
        if (bitsWritten == 8) {
            bitsWritten = 0;
            writingByte = 0;
        }

        if (value) {
            writingByte |= 1 << (7 - bitsWritten);
        }

        if (++bitsWritten == 8) {
            write(writingByte);
        }
    }

    /**
     * Writes out the uncompleted byte.
     * It will result in a byte right padded with zeroes.
     *
     * @return The amount of bits discarded by finishing the byte.
     * @throws IOException
     */
    public int finishByte() throws IOException {
        int bitsDiscarded = 0;

        if (bitsWritten > 0 && bitsWritten < 8) {
            bitsDiscarded = 8 - bitsWritten;
            bitsWritten = 8;
            write(writingByte);
        }

        return bitsDiscarded;
    }

    /**
     * Write a regular byte to the stream.
     * If an byte is still "under construction" by having written bits, it will be finished before writing the requested byte.
     *
     * @param b The byte to write.
     * @throws IOException
     */
    @Override
    public void write(int b) throws IOException {
        finishByte();
        out.write(b);
    }

    /**
     * Writes bits for a string of zeroes and ones to the stream.
     * Any character not '0' is considered a 1, even the null character.
     *
     * @param bitString
     * @throws IOException
     */
    public void write(String bitString) throws IOException {
        for (int i = 0; i < bitString.length(); i++) {
            write(bitString.charAt(i) != '0');
        }
    }

    @Override
    public void flush() throws IOException {
        finishByte();
        out.flush();
    }
}
