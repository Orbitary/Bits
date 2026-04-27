/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.util.reflection;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.jetbrains.annotations.Nullable;

import xyz.bitsquidd.bits.log.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Utility class for common reflection operations and classpath scanning.
 * Provides caching for methods, fields, and constructors to improve performance,
 * as well as helper methods for primitive type conversions and package scanning.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class ReflectionUtils {
    private ReflectionUtils() {}

    private static volatile ClassLoader[] CLASSLOADERS = {Thread.currentThread().getContextClassLoader()};
    private static final Function<? super String, ? extends ClassGraph> CLASSGRAPH_SUPPLIER =
      packageName -> new ClassGraph()
        .enableClassInfo()
        .enableAnnotationInfo()
        .overrideClassLoaders(CLASSLOADERS)
        .acceptPackages(packageName);


    /**
     * Sets the classloader used for all reflection operations.
     * Useful for Minecraft Paper plugins to ensure compatibility with the server's classloader.
     *
     * @since 0.0.12
     */
    public static void setClassloader(ClassLoader... classLoaders) {
        if (classLoaders == null || classLoaders.length == 0) throw new IllegalArgumentException("At least one ClassLoader required");
        CLASSLOADERS = classLoaders;
    }


    /**
     * Converts between Java primitive types and their corresponding wrapper classes.
     */
    public static final class Primitive {
        private Primitive() {}

        public static final HashBiMap<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE = HashBiMap.create(Map.of(
          Boolean.class, boolean.class,
          Byte.class, byte.class,
          Character.class, char.class,
          Short.class, short.class,
          Integer.class, int.class,
          Long.class, long.class,
          Float.class, float.class,
          Double.class, double.class
        ));
        public static final BiMap<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = WRAPPER_TO_PRIMITIVE.inverse();

        public static Class<?> to(Class<?> wrapper) {
            Class<?> primitive = WRAPPER_TO_PRIMITIVE.get(wrapper);
            if (primitive == null) throw new IllegalArgumentException("Not a wrapper class: " + wrapper);
            return primitive;
        }

        public static Class<?> toOrSelf(Class<?> clazz) {
            return WRAPPER_TO_PRIMITIVE.getOrDefault(clazz, clazz);
        }

        public static Class<?> from(Class<?> primitive) {
            Class<?> wrapper = PRIMITIVE_TO_WRAPPER.get(primitive);
            if (wrapper == null) throw new IllegalArgumentException("No wrapper for primitive: " + primitive);
            return wrapper;
        }

        public static Class<?> fromOrSelf(Class<?> clazz) {
            return PRIMITIVE_TO_WRAPPER.getOrDefault(clazz, clazz);
        }

    }

    /**
     * Invokes instance and static methods via reflection with caching.
     */
    public static final class Method {
        private Method() {}

        private static final Map<MethodKey, java.lang.reflect.Method> METHOD_CACHE = new ConcurrentHashMap<>();

        private record MethodKey(
          Class<?> clazz,
          String methodName,
          Class<?>[] parameterTypes
        ) {
            @Override
            public boolean equals(Object obj) {
                if (this == obj) return true;
                if (!(obj instanceof MethodKey(Class<?> c, String n, Class<?>[] t))) return false;
                return clazz.equals(c) && methodName.equals(n) && Arrays.equals(parameterTypes, t);
            }

            @Override
            public int hashCode() {
                return Objects.hash(clazz, methodName, Arrays.hashCode(parameterTypes));
            }

        }

        private static java.lang.reflect.Method resolveMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) throws ReflectionException {
            if (clazz == null || methodName == null || methodName.isEmpty()) {
                throw new IllegalArgumentException("Class and methodName must be non-null and non-empty");
            }

            MethodKey key = new MethodKey(clazz, methodName, parameterTypes);
            java.lang.reflect.Method cached = METHOD_CACHE.get(key);
            if (cached != null) return cached;

            Class<?> search = clazz;
            while (search != null) {
                try {
                    java.lang.reflect.Method m = search.getDeclaredMethod(methodName, parameterTypes);
                    m.setAccessible(true);
                    METHOD_CACHE.put(key, m);
                    return m;
                } catch (NoSuchMethodException ignored) {
                    search = search.getSuperclass();
                }
            }
            throw new ReflectionException("Method not found: " + methodName + " in " + clazz.getName());
        }

        /**
         * Invokes a non-static method on {@code object}.
         * The {@code returnType} parameter is not used at runtime; callers rely on the
         * unchecked cast.
         */
        @SuppressWarnings("unchecked")
        public static <T> @Nullable T invoke(Object object, String methodName, Class<?>[] paramTypes, Class<T> returnType, Object... args) throws ReflectionException {
            if (object == null) throw new IllegalArgumentException("Object cannot be null");
            try {
                return (T)resolveMethod(object.getClass(), methodName, paramTypes).invoke(object, args);
            } catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
                throw new ReflectionException("Unable to invoke method: " + methodName, e);
            }
        }

        public static <T> Optional<T> tryInvoke(Object object, String methodName, Class<?>[] paramTypes, Class<T> returnType, Object... args) {
            try {
                return Optional.ofNullable(invoke(object, methodName, paramTypes, returnType, args));
            } catch (ReflectionException e) {
                Logger.warn("Failed to invoke method: " + methodName + " on " + object.getClass().getName());
                return Optional.empty();
            }
        }

        /**
         * Invokes a static method on {@code clazz}.
         */
        @SuppressWarnings("unchecked")
        public static <T> T invokeStatic(Class<?> clazz, String methodName, Class<?>[] paramTypes, Class<T> returnType, Object... args) throws ReflectionException {
            if (clazz == null) throw new IllegalArgumentException("Class cannot be null");
            try {
                return (T)resolveMethod(clazz, methodName, paramTypes).invoke(null, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ReflectionException("Unable to invoke static method: " + methodName, e);
            }
        }

    }

    /**
     * Reads and writes fields via reflection with caching.
     */
    public static final class Value {
        private Value() {}

        private static final Map<FieldKey, Field> FIELD_CACHE = new ConcurrentHashMap<>();

        private record FieldKey(
          Class<?> clazz,
          String fieldName
        ) {
            @Override
            public boolean equals(Object obj) {
                if (this == obj) return true;
                if (!(obj instanceof FieldKey(Class<?> c, String n))) return false;
                return clazz.equals(c) && fieldName.equals(n);
            }

            @Override
            public int hashCode() {
                return Objects.hash(clazz, fieldName);
            }

        }

        private static Field resolveField(Class<?> clazz, String fieldName) throws ReflectionException {
            if (clazz == null || fieldName == null || fieldName.isEmpty()) {
                throw new IllegalArgumentException("Class and fieldName must be non-null and non-empty");
            }

            FieldKey key = new FieldKey(clazz, fieldName);
            Field cached = FIELD_CACHE.get(key);
            if (cached != null) return cached;

            Class<?> search = clazz;
            while (search != null) {
                try {
                    Field f = search.getDeclaredField(fieldName);
                    f.setAccessible(true);
                    FIELD_CACHE.put(key, f);
                    return f;
                } catch (NoSuchFieldException ignored) {
                    search = search.getSuperclass();
                }
            }
            throw new ReflectionException("Field not found: " + fieldName + " in " + clazz.getName());
        }

        @SuppressWarnings("unchecked")
        public static <T> T get(Object object, String fieldName, Class<T> fieldType) throws ReflectionException {
            if (object == null) throw new IllegalArgumentException("Object cannot be null");
            try {
                return (T)resolveField(object.getClass(), fieldName).get(object);
            } catch (IllegalAccessException e) {
                throw new ReflectionException("Unable to access field: " + fieldName, e);
            }
        }

        @SuppressWarnings("unchecked")
        public static <T> T getStatic(Class<?> clazz, String fieldName, Class<T> fieldType) throws ReflectionException {
            try {
                return (T)resolveField(clazz, fieldName).get(null);
            } catch (IllegalAccessException e) {
                throw new ReflectionException("Unable to access static field: " + fieldName, e);
            }
        }

        public static <T> void set(Object object, String fieldName, T value) throws ReflectionException {
            if (object == null) throw new IllegalArgumentException("Object cannot be null");
            try {
                resolveField(object.getClass(), fieldName).set(object, value);
            } catch (IllegalAccessException e) {
                throw new ReflectionException("Unable to set field: " + fieldName, e);
            }
        }

        public static <T> void setStatic(Class<?> clazz, String fieldName, T value) throws ReflectionException {
            try {
                resolveField(clazz, fieldName).set(null, value);
            } catch (IllegalAccessException e) {
                throw new ReflectionException("Unable to set static field: " + fieldName, e);
            }
        }

    }

    /**
     * Instantiates objects via reflection with constructor caching.
     */
    public static final class Instance {
        private Instance() {}

        private static final Map<ConstructorKey, Constructor<?>> CONSTRUCTOR_CACHE = new ConcurrentHashMap<>();

        private record ConstructorKey(
          Class<?> clazz,
          Class<?>[] parameterTypes
        ) {
            @Override
            public boolean equals(Object obj) {
                if (this == obj) return true;
                if (!(obj instanceof ConstructorKey(Class<?> c, Class<?>[] t))) return false;
                return clazz.equals(c) && Arrays.equals(parameterTypes, t);
            }

            @Override
            public int hashCode() {
                return Objects.hash(clazz, Arrays.hashCode(parameterTypes));
            }

        }

        /**
         * Derives parameter types from the supplied args, guarding against null elements.
         */
        private static Class<?>[] paramTypesFrom(Object... args) {
            Class<?>[] types = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] == null) throw new IllegalArgumentException("Null argument at index " + i + " — cannot infer type");
                types[i] = Primitive.fromOrSelf(args[i].getClass());
            }
            return types;
        }

        @SuppressWarnings("unchecked")
        private static <T> Constructor<T> resolveConstructor(Class<T> clazz, Class<?>[] parameterTypes) throws ReflectionException {
            if (clazz == null) throw new IllegalArgumentException("Class must be non-null");

            ConstructorKey key = new ConstructorKey(clazz, parameterTypes);
            Constructor<?> cached = CONSTRUCTOR_CACHE.get(key);
            if (cached != null) return (Constructor<T>)cached;

            try {
                Constructor<T> ctor = clazz.getDeclaredConstructor(parameterTypes);
                ctor.setAccessible(true);
                CONSTRUCTOR_CACHE.put(key, ctor);
                return ctor;
            } catch (NoSuchMethodException e) {
                throw new ReflectionException("Constructor not found: " + clazz.getName() + " with params: " + Arrays.toString(parameterTypes), e);
            }
        }

        public static <T> T create(Class<T> clazz, Object... args) throws ReflectionException {
            try {
                return resolveConstructor(clazz, paramTypesFrom(args)).newInstance(args);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                throw new ReflectionException("Constructor threw for " + clazz.getName(), cause != null ? cause : e);
            } catch (ReflectionException e) {
                throw e;
            } catch (Exception e) {
                throw new ReflectionException("Failed to create instance of " + clazz.getName(), e);
            }
        }

        public static <T> Optional<T> tryCreate(Class<T> clazz, Object... args) {
            try {
                return Optional.of(create(clazz, args));
            } catch (ReflectionException e) {
                Logger.warn("Failed to create instance of " + clazz.getName() + " with args: " + Arrays.toString(args));
                return Optional.empty();
            }
        }

        /**
         * Returns a {@link Supplier} that can create instances using a pre-resolved constructor.
         * Any exception thrown during instantiation includes the original cause.
         */
        public static <T> Supplier<T> supplier(Class<T> clazz, Object... args) {
            return () -> tryCreate(clazz, args).orElseThrow(() -> new RuntimeException("Failed to create instance of " + clazz.getName() + " with args: " + Arrays.toString(args)));
        }

    }

    /**
     * Scans the classpath using the classgraph library.
     */
    public static final class Scanner {
        private Scanner() {}

        private static Class<?> getCorrectLoader(ClassInfo info, Class<?> base) throws ClassNotFoundException {
            // We prefer the loader that loaded the base type - this should generally guarantee constraint compatibility
            ClassLoader baseLoader = base.getClassLoader();
            if (baseLoader != null) {
                try {
                    return Class.forName(info.getName(), true, baseLoader);
                } catch (ClassNotFoundException ignored) {}
            }

            for (ClassLoader cl : CLASSLOADERS) {
                try {
                    return Class.forName(info.getName(), true, cl);
                } catch (ClassNotFoundException ignored) {}
            }
            throw new ClassNotFoundException("Cannot load " + info.getName());
        }


        public static <T> List<Class<? extends T>> getClasses(String packageName, Class<? extends T> clazz, ScannerFlags flags) throws ReflectionException {
            List<Class<? extends T>> classes = new ArrayList<>();

            try (ScanResult scanResult = CLASSGRAPH_SUPPLIER.apply(packageName).scan()) {
                ClassInfoList classInfoList = clazz.isInterface()
                                              ? scanResult.getClassesImplementing(clazz.getName())
                                              : scanResult.getSubclasses(clazz.getName());

                for (ClassInfo info : classInfoList) {
                    if (!flags.isValid(info)) continue;
                    classes.add(getCorrectLoader(info, clazz).asSubclass(clazz));
                }
            } catch (Exception e) {
                throw new ReflectionException("Failed to scan package: " + packageName, e);
            }

            return classes;
        }

        public static <T> List<Class<? extends T>> tryGetClasses(String packageName, Class<? extends T> clazz, ScannerFlags flags) {
            try {
                return getClasses(packageName, clazz, flags);
            } catch (ReflectionException e) {
                Logger.warn("Failed to get classes in package: " + packageName + " extending/implementing " + clazz.getName());
                return Collections.emptyList();
            }
        }


        public static List<Class<?>> getAnnotatedClasses(String packageName, Class<? extends Annotation> clazz, ScannerFlags flags) throws ReflectionException {
            List<Class<?>> classes = new ArrayList<>();

            try (ScanResult scanResult = CLASSGRAPH_SUPPLIER.apply(packageName).scan()) {
                ClassInfoList classInfoList = scanResult.getClassesWithAnnotation(clazz.getName());

                for (ClassInfo info : classInfoList) {
                    if (!flags.isValid(info)) continue;
                    classes.add(getCorrectLoader(info, clazz));
                }
            } catch (Exception e) {
                throw new ReflectionException("Failed to scan package: " + packageName, e);
            }

            return classes;
        }

        public static List<Class<?>> tryGetAnnotatedClasses(String packageName, Class<? extends Annotation> clazz, ScannerFlags flags) {
            try {
                return getAnnotatedClasses(packageName, clazz, flags);
            } catch (ReflectionException e) {
                Logger.warn("Failed to get annotated classes in package: " + packageName + " with annotation " + clazz.getName());
                return Collections.emptyList();
            }
        }

    }

    /**
     * General useful utility methods.
     */
    public static final class General {
        private General() {}

        public static <T> Set<T> createClassesInDir(String packageName, Class<T> clazz, ScannerFlags flags) {
            return Scanner.tryGetClasses(packageName, clazz, flags)
              .stream().map(ReflectionUtils.Instance::tryCreate)
              .map(optional -> optional.orElse(null))
              .filter(Objects::nonNull)
              .collect(HashSet::new, Set::add, Set::addAll);
        }

        public static <T, A extends Annotation> Set<T> createAnnotatedClassesInDir(String packageName, Class<A> annotationClazz, Class<T> clazz, ScannerFlags flags) {
            return Scanner.tryGetAnnotatedClasses(packageName, annotationClazz, flags)
              .stream().map(ReflectionUtils.Instance::tryCreate)
              .map(optional -> optional.orElse(null))
              .filter(Objects::nonNull)
              .map(clazz::cast)
              .collect(HashSet::new, Set::add, Set::addAll);
        }

        @SuppressWarnings("unchecked")
        public static <T> Map<Class<?>, T> createClassesInDirMapped(String packageName, Class<? extends T> clazz, ScannerFlags flags) {
            return Scanner.tryGetClasses(packageName, clazz, flags)
              .stream().map(ReflectionUtils.Instance::tryCreate)
              .filter(Optional::isPresent)
              .map(Optional::get)
              .map(i -> Map.entry(i.getClass(), i))
              .collect(Collectors.toMap(e -> (Class<? extends T>)e.getKey(), Map.Entry::getValue));
        }

    }

}