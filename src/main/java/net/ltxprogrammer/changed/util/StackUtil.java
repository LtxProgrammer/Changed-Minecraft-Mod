package net.ltxprogrammer.changed.util;

import net.minecraft.Util;
import net.minecraftforge.accesstransformer.INameHandler;
import net.minecraftforge.accesstransformer.service.ServiceNameHandler;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.function.Function;

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

    public static boolean callStackContainsClass(Class<?> clazz) {
        return callStackContainsClass(clazz, 300000);
    }

    /**
     * I know it's not recommended to change function output depending on caller, but im doing it anyway
     * @param stackDistance how far back to check for a class
     * @param clazz class or parent class to check for
     * @return true - if the stack contains the given class name. false otherwise.
     */
    public static boolean callStackContainsClass(Class<?> clazz, int stackDistance) {
        var trace = Thread.currentThread().getStackTrace();
        for (var element : trace) {
            try {
                Class<?> elementClass = Class.forName(element.getClassName());
                if (clazz.isAssignableFrom(elementClass))
                    return true;
            } catch (ClassNotFoundException ignored) {}
            stackDistance--;
            if (stackDistance <= 0)
                return false;
        }
        return false;
    }

    public static boolean callStackContainsMethod(Class<?> clazz, String method) {
        return callStackContainsMethod(clazz, method, 300000);
    }

    public static boolean callStackContainsMethod(Class<?> clazz, String method, int stackDistance) {
        var trace = Thread.currentThread().getStackTrace();
        for (var element : trace) {
            try {
                Class<?> elementClass = Class.forName(element.getClassName());
                if (clazz.isAssignableFrom(elementClass) && element.getMethodName().equals(method))
                    return true;
            } catch (ClassNotFoundException ignored) {}
            stackDistance--;
            if (stackDistance <= 0)
                return false;
        }
        return false;
    }

    private static Cacheable<INameHandler> MCP_TO_SRG = Cacheable.of(() -> {
        return new ServiceNameHandler(FMLLoader.getLaunchHandler().getNaming(), "srg");
    });

    private static Cacheable<INameHandler> SRG_TO_MCP = Cacheable.of(() -> {
        return new ServiceNameHandler("srg", "mcp");
    });

    private static Function<String, String> MCP_TO_SRG_METHODS = Util.memoize(name -> MCP_TO_SRG.get().translateMethodName(name));
    private static Function<String, String> SRG_TO_MCP_METHODS = Util.memoize(name -> SRG_TO_MCP.get().translateMethodName(name));

    public static String getSrgMethod(String mcpMethod) {
        return MCP_TO_SRG_METHODS.apply(mcpMethod);
    }

    public static String getMcpMethod(String srgMethod) {
        return SRG_TO_MCP_METHODS.apply(srgMethod);
    }
}
