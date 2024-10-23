package essential;

import org.jetbrains.annotations.NotNull;


public class Share {

public static byte @NotNull [] trim(byte @NotNull [] big) {
    int bound = big.length - 1;
    if (big[0] == 0) {
        int k = 0;
        while (k < bound && big[k] == 0) {
            k += 1;
        }
        if (big[k] < 0) {
            k -= 1;
        }
        byte[] ooo = new byte[big.length - k];
        System.arraycopy(big, k, ooo, 0, big.length - k);
        return ooo;
    } else if (big[0] == -1) {
        int k = 0;
        while (k < bound && big[k] == -1) {
            k += 1;
        }
        if (big[k] >= 0) {
            k -= 1;
        }
        byte[] ooo = new byte[big.length - k];
        System.arraycopy(big, k, ooo, 0, big.length - k);
        return ooo;
    } else {
        return big;
    }
}

public static byte @NotNull [] integerToBinary(long n, int length, boolean little_endian) {
    byte[] bin = new byte[length];
    if (little_endian) {
        for (int i = 0; i < length; i = i + 1) {
            bin[i] = (byte) n;
            n = n >>> 8;
        }
    } else {
        for (int i = length - 1; i >= 0; i = i - 1) {
            bin[i] = (byte) n;
            n = n >>> 8;
        }
    }
    return bin;
}

public static byte @NotNull [] integerToBinary(int n, int length) {
    return integerToBinary(n, length, false);
}

public static long binaryToInteger(byte[] bin, int start, int bound, boolean little_endian) {
    long n = 0;
    if (little_endian) {
        for (int i = bound - 1; i >= start; i = i - 1) {
            n = n << 8;
            n = n | (bin[i] & 0xFF);
        }
    } else {
        for (int i = start; i < bound; i = i + 1) {
            n = n << 8;
            n = n | (bin[i] & 0xFF);
        }
    }
    return n;
}

public static long binaryToInteger(byte[] bin, int start, int length) {
    return binaryToInteger(bin, start, length, false);
}
}
