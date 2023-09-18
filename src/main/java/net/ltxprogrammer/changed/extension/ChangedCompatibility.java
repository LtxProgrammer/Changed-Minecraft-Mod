package net.ltxprogrammer.changed.extension;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChangedCompatibility {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static Field findField(String className, String fieldName) {
        Field tmp;
        try {
            tmp = Class.forName(className).getField(fieldName);
            LOGGER.info("Found compatibility for class {}, field {}", className, fieldName);
        } catch (Exception ignored) {
            tmp = null;
        }

        return tmp;
    }

    private static Method findMethod(String className, String functionName, Class<?>... param) {
        Method tmp;
        try {
            tmp = Class.forName(className).getMethod(functionName, param);
            LOGGER.info("Found compatibility for class {}, method {}", className, functionName);
        } catch (Exception ignored) {
            tmp = null;
        }

        return tmp;
    }

    public static class ClassField<Clazz, T> implements Function<Clazz, T> {
        private final Field field;

        public ClassField(Field field) {
            this.field = field;
        }

        @Override
        public T apply(Clazz clazz) {
            try {
                return field != null ? (T) field.get(clazz) : null;
            } catch (IllegalAccessException e) {
                return null;
            }
        }

        public T applyOr(Clazz clazz, T value) {
            try {
                return field != null ? (T) field.get(clazz) : value;
            } catch (IllegalAccessException e) {
                return value;
            }
        }

        public static <Clazz, T> ClassField<Clazz, T> of(String className, String fieldName) {
            return new ClassField<>(findField(className, fieldName));
        }
    }

    public static class StaticField<T> implements Supplier<T> {
        private final Field field;

        public StaticField(Field field) {
            this.field = field;
        }

        @Override
        public T get() {
            try {
                return field != null ? (T) field.get(null) : null;
            } catch (IllegalAccessException e) {
                return null;
            }
        }

        public T getOr(T value) {
            try {
                return field != null ? (T) field.get(null) : value;
            } catch (IllegalAccessException e) {
                return value;
            }
        }

        public static <T> StaticField<T> of(String className, String fieldName) {
            return new StaticField<>(findField(className, fieldName));
        }
    }

    public static class StaticFunction<T, R> implements Function<T, R> {
        private final Method method;

        public StaticFunction(Method method) {
            this.method = method;
        }

        @Override
        public R apply(T input) {
            try {
                return method != null ? (R) method.invoke(null, input) : null;
            } catch (IllegalAccessException | InvocationTargetException e) {
                return null;
            }
        }

        public R applyOr(T input, R value) {
            try {
                return method != null ? (R) method.invoke(null, input) : value;
            } catch (IllegalAccessException | InvocationTargetException e) {
                return value;
            }
        }

        public static <T, R> StaticFunction<T, R> of(String className, String fieldName, Class<?>... param) {
            return new StaticFunction<>(findMethod(className, fieldName, param));
        }
    }

    public static final StaticField<Boolean> dev_tr7zw_firstperson$FirstPersonModelCore$isRenderingPlayer =
            StaticField.of("dev.tr7zw.firstperson.FirstPersonModelCore", "isRenderingPlayer");
    public static final StaticFunction<Entity, Boolean> by_dragonsurvivalteam_dragonsurvival_util$DragonUtils$isDragon =
            StaticFunction.of("by.dragonsurvivalteam.dragonsurvival.util.DragonUtils", "isDragon", Entity.class);

    public static boolean isFirstPersonRendering() {
        if (dev_tr7zw_firstperson$FirstPersonModelCore$isRenderingPlayer.getOr(false))
            return true;
        return false;
    }

    public static boolean isPlayerUsedByOtherMod(Player player) {
        if (by_dragonsurvivalteam_dragonsurvival_util$DragonUtils$isDragon.applyOr(player, false))
            return true;
        return false;
    }
}
