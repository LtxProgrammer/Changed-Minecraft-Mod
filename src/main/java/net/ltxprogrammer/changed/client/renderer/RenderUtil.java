package net.ltxprogrammer.changed.client.renderer;

import com.mojang.logging.LogUtils;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.function.Function;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class RenderUtil {
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

    private static final StaticField<Boolean> dev_tr7zw_firstperson$FirstPersonModelCore$isRenderingPlayer;

    static {
        dev_tr7zw_firstperson$FirstPersonModelCore$isRenderingPlayer = StaticField.of("dev.tr7zw.firstperson.FirstPersonModelCore", "isRenderingPlayer");
    }

    /**
     * Used to determine whether to render head armor, for first person mods
     * @param renderSubject entity that the camera may be attached to
     * @return true - if minecraft is rendering the entity while in first person mode
     */
    public static boolean isFirstPerson(LatexEntity renderSubject) {
        return dev_tr7zw_firstperson$FirstPersonModelCore$isRenderingPlayer.getOr(false);
    }
}
