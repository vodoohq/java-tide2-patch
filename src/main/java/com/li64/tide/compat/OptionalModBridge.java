package com.li64.tide.compat;

import com.li64.tide.Tide;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Keeps optional integrations out of the mandatory Minecraft 26.3 classpath. */
public final class OptionalModBridge {
    private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();

    private OptionalModBridge() {}

    public static Object callStatic(String className, String method, Object... args) {
        try {
            return invoke(Class.forName(className), null, method, args);
        } catch (ReflectiveOperationException | LinkageError e) {
            warn(className + "." + method, e);
            return null;
        }
    }

    public static Object call(Object receiver, String method, Object... args) {
        if (receiver == null) return null;
        try {
            return invoke(receiver.getClass(), receiver, method, args);
        } catch (ReflectiveOperationException | LinkageError e) {
            warn(receiver.getClass().getName() + "." + method, e);
            return null;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object enumValue(String className, String name) {
        try {
            return Enum.valueOf((Class) Class.forName(className), name);
        } catch (ReflectiveOperationException | IllegalArgumentException | LinkageError e) {
            warn(className, e);
            return null;
        }
    }

    private static Object invoke(Class<?> owner, Object receiver, String name, Object[] args) throws ReflectiveOperationException {
        for (Method method : owner.getMethods()) {
            if (!method.getName().equals(name) || method.getParameterCount() != args.length) continue;
            Class<?>[] types = method.getParameterTypes();
            boolean matches = true;
            for (int i = 0; i < args.length; i++) {
                if (args[i] == null) { matches &= !types[i].isPrimitive(); continue; }
                Class<?> type = types[i] == boolean.class ? Boolean.class : types[i];
                matches &= type.isInstance(args[i]);
            }
            if (matches) return method.invoke(receiver, args);
        }
        throw new NoSuchMethodException(owner.getName() + "." + name);
    }

    private static void warn(String integration, Throwable e) {
        if (WARNED.add(integration)) Tide.LOG.warn("Optional integration '{}' is unavailable with the installed addon version: {}", integration, e.toString());
    }
}
