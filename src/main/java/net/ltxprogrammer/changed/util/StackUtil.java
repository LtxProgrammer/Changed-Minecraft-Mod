package net.ltxprogrammer.changed.util;

public class StackUtil {
    public static boolean isRecursive() {
        return isRecursive(300000, 3);
    }

    public static boolean isRecursive(int stackDistance) {
        return isRecursive(stackDistance, 3);
    }

    /**
     * walks the stack back to check for a matching
     * @param stackDistance how far back from the caller to check for recursion. Specify if you know how far back to check.
     * @param callerIndex index in the stack referencing the caller method. Use 1 if calling this method directly
     * @return true - if the call stack contains a recursion to callerIndex. false otherwise.
     */
    public static boolean isRecursive(int stackDistance, int callerIndex) {
        var trace = Thread.currentThread().getStackTrace();
        var caller = trace[callerIndex];
        for (int i = callerIndex + 1; i < stackDistance + callerIndex + 1 && i < trace.length; ++i) {
            var element = trace[i];

            if (element.getClassName().equals(caller.getClassName()) &&
                    element.getMethodName().equals(caller.getMethodName()))
                return true;
        }

        return false;
    }

    public static boolean callStackContainsClass(String name) {
        return callStackContainsClass(300000, name);
    }

    /**
     * I know its not recommended to change function output depending on caller, but im doing it anyways
     * @param stackDistance how far back to check for a class
     * @param name partial class name
     * @return true - if the stack contains the given class name. false otherwise.
     */
    public static boolean callStackContainsClass(int stackDistance, String name) {
        var trace = Thread.currentThread().getStackTrace();
        for (var element : trace) {
            if (element.getClassName().contains(name))
                return true;
            stackDistance--;
            if (stackDistance <= 0)
                return false;
        }
        return false;
    }
}
