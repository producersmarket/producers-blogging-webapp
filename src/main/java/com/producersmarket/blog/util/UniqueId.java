package com.producersmarket.blog.util;

import java.util.Random;

/**
 * Since the granulaty of a PC can be as high as 55ms (down to 10ms),
 * you can't use the System time to generate a unique ID because of
 * the risk of getting duplicated IDs.
 * This can be solved by using the following technique to make sure
 * that the number returned is unique (in a single JVM).
 *
 * @author  Dermot Doherty
 * @version 0.1 - 5/7/2012 1:34AM
 */

public class UniqueId {

    private static final Random RANDOM = new Random();
    static long current = System.currentTimeMillis();

    public static synchronized long getUniqueLong() {
        return current++;
    }

    /**
     * Generates unique ID based on timestamp, thread ID, random seed and random double.
     * Each part is converted to hexadecimal, padded left with zeros to a length of 8
     * chars and then concatentated to each other.
     * @return Unique 32-char hexadecimal string.
     */
    public static synchronized String getUniqueId() {

        final String pad = "0";
        final int padLength = 8;
        final int padDirection = StringUtils.PAD_LEFT;

        String timestamp = Integer.toHexString((int) System.currentTimeMillis());
        String threadHashCode = Integer.toHexString(Thread.currentThread().hashCode());
        String randomInt = Integer.toHexString(RANDOM.nextInt());
        String mathRandom = Integer.toHexString((int) (Math.random() * Integer.MAX_VALUE));

        // You may remove this try block if you want to increase performance.
        // But the generated ID might not be fully 100% guaranteed to be unique.
        // If you want to keep this block, then you can "almost" safely remove the
        // randomInt and mathRandom parts of the unique ID.
        try {
            Thread.currentThread().join(1); // System.currentTimeMillis()++
        } catch (InterruptedException e) {
            // Do nothing. However very, very, very dubious.
        }

        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.pad(timestamp, pad, padLength, padDirection))
          .append(StringUtils.pad(threadHashCode, pad, padLength, padDirection))
          .append(StringUtils.pad(randomInt, pad, padLength, padDirection))
          .append(StringUtils.pad(mathRandom, pad, padLength, padDirection))
        ;

        return sb.toString();

    }


}


