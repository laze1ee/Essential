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
}
