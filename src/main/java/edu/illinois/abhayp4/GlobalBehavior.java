/**
 * GlobalBehavior.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4;

public final class GlobalBehavior {
    static final int timeout = 10;

    private GlobalBehavior() { }

    public static boolean delay() {
        try {
            Thread.sleep(timeout);
        }
        catch (InterruptedException e) {
            return false;
        }
        return true;
    }
}
