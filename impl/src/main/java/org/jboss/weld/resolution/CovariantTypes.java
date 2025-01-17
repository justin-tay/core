/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.resolution;

import static org.jboss.weld.util.Types.boxedClass;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import org.jboss.weld.logging.ReflectionLogger;
import org.jboss.weld.util.Types;
import org.jboss.weld.util.reflection.HierarchyDiscovery;
import org.jboss.weld.util.reflection.Reflections;

/**
 * Utility class that captures standard covariant Java assignability rules.
 *
 * This class operates on all the possible Type subtypes: Class, ParameterizedType, TypeVariable, WildcardType, GenericArrayType.
 * To make this class easier to understand and maintain, there is a separate isAssignableFrom method for each combination
 * of possible types. Each of these methods compares two type instances and determines whether the first one is assignable from
 * the other.
 *
 * TypeVariables are considered a specific unknown type restricted by the upper bound. No inference of type variables is performed.
 *
 * @author Jozef Hartinger
 *
 */
public class CovariantTypes {

    private CovariantTypes() {
    }

    public static boolean isAssignableFromAtLeastOne(Type type1, Type[] types2) {
        for (Type type2 : types2) {
            if (isAssignableFrom(type1, type2)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAssignableFrom(Type type1, Type type2) {
        if (type1 instanceof Class<?>) {
            if (type2 instanceof Class<?>) {
                return isAssignableFrom((Class<?>) type1, (Class<?>) type2);
            }
            if (type2 instanceof ParameterizedType) {
                return isAssignableFrom((Class<?>) type1, (ParameterizedType) type2);
            }
            if (type2 instanceof TypeVariable<?>) {
                return isAssignableFrom((Class<?>) type1, (TypeVariable<?>) type2);
            }
            if (type2 instanceof WildcardType) {
                return isAssignableFrom((Class<?>) type1, (WildcardType) type2);
            }
            if (type2 instanceof GenericArrayType) {
                return isAssignableFrom((Class<?>) type1, (GenericArrayType) type2);
            }
            throw ReflectionLogger.LOG.unknownType(type2);
        }
        if (type1 instanceof ParameterizedType) {
            if (type2 instanceof Class<?>) {
                return isAssignableFrom((ParameterizedType) type1, (Class<?>) type2);
            }
            if (type2 instanceof ParameterizedType) {
                return isAssignableFrom((ParameterizedType) type1, (ParameterizedType) type2);
            }
            if (type2 instanceof TypeVariable<?>) {
                return isAssignableFrom((ParameterizedType) type1, (TypeVariable<?>) type2);
            }
            if (type2 instanceof WildcardType) {
                return isAssignableFrom((ParameterizedType) type1, (WildcardType) type2);
            }
            if (type2 instanceof GenericArrayType) {
                return isAssignableFrom((ParameterizedType) type1, (GenericArrayType) type2);
            }
            throw ReflectionLogger.LOG.unknownType(type2);
        }
        if (type1 instanceof TypeVariable<?>) {
            if (type2 instanceof Class<?>) {
                return isAssignableFrom((TypeVariable<?>) type1, (Class<?>) type2);
            }
            if (type2 instanceof ParameterizedType) {
                return isAssignableFrom((TypeVariable<?>) type1, (ParameterizedType) type2);
            }
            if (type2 instanceof TypeVariable<?>) {
                return isAssignableFrom((TypeVariable<?>) type1, (TypeVariable<?>) type2);
            }
            if (type2 instanceof WildcardType) {
                return isAssignableFrom((TypeVariable<?>) type1, (WildcardType) type2);
            }
            if (type2 instanceof GenericArrayType) {
                return isAssignableFrom((TypeVariable<?>) type1, (GenericArrayType) type2);
            }
            throw ReflectionLogger.LOG.unknownType(type2);
        }
        if (type1 instanceof WildcardType) {
            if (Types.isActualType(type2)) {
                return isAssignableFrom((WildcardType) type1, type2);
            }
            if (type2 instanceof TypeVariable<?>) {
                return isAssignableFrom((WildcardType) type1, (TypeVariable<?>) type2);
            }
            if (type2 instanceof WildcardType) {
                return isAssignableFrom((WildcardType) type1, (WildcardType) type2);
            }
            throw ReflectionLogger.LOG.unknownType(type2);
        }
        if (type1 instanceof GenericArrayType) {
            if (type2 instanceof Class<?>) {
                return isAssignableFrom((GenericArrayType) type1, (Class<?>) type2);
            }
            if (type2 instanceof ParameterizedType) {
                return isAssignableFrom((GenericArrayType) type1, (ParameterizedType) type2);
            }
            if (type2 instanceof TypeVariable<?>) {
                return isAssignableFrom((GenericArrayType) type1, (TypeVariable<?>) type2);
            }
            if (type2 instanceof WildcardType) {
                return isAssignableFrom((GenericArrayType) type1, (WildcardType) type2);
            }
            if (type2 instanceof GenericArrayType) {
                return isAssignableFrom((GenericArrayType) type1, (GenericArrayType) type2);
            }
            throw ReflectionLogger.LOG.unknownType(type2);
        }
        throw ReflectionLogger.LOG.unknownType(type1);
    }

    /*
     * Raw type
     */
    private static boolean isAssignableFrom(Class<?> type1, Class<?> type2) {
        return boxedClass(type1).isAssignableFrom(boxedClass(type2));
    }

    private static boolean isAssignableFrom(Class<?> type1, ParameterizedType type2) {
        return type1.isAssignableFrom(Reflections.getRawType(type2));
    }

    private static boolean isAssignableFrom(Class<?> type1, TypeVariable<?> type2) {
        for (Type type : type2.getBounds()) {
            if (isAssignableFrom(type1, type)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAssignableFrom(Class<?> type1, WildcardType type2) {
        return false;
    }

    private static boolean isAssignableFrom(Class<?> type1, GenericArrayType type2) {
        return type1.equals(Object.class) || type1.isArray()
                && isAssignableFrom(type1.getComponentType(), Reflections.getRawType(type2.getGenericComponentType()));
    }

    /*
     * ParameterizedType
     */
    private static boolean isAssignableFrom(ParameterizedType type1, Class<?> type2) {
        Class<?> rawType1 = Reflections.getRawType(type1);

        // raw types have to be assignable
        if (!isAssignableFrom(rawType1, type2)) {
            return false;
        }
        // this is a raw type with missing type arguments
        if (!Types.getCanonicalType(type2).equals(type2)) {
            return true;
        }

        return matches(type1, new HierarchyDiscovery(type2));
    }

    private static boolean isAssignableFrom(ParameterizedType type1, ParameterizedType type2) {
        // first, raw types have to be assignable
        if (!isAssignableFrom(Reflections.getRawType(type1), Reflections.getRawType(type2))) {
            return false;
        }
        if (matches(type1, type2)) {
            return true;
        }
        return matches(type1, new HierarchyDiscovery(type2));
    }

    private static boolean matches(ParameterizedType type1, HierarchyDiscovery type2) {
        for (Type type : type2.getTypeClosure()) {
            if (type instanceof ParameterizedType && matches(type1, (ParameterizedType) type)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matches(ParameterizedType type1, ParameterizedType type2) {
        final Class<?> rawType1 = Reflections.getRawType(type1);
        final Class<?> rawType2 = Reflections.getRawType(type2);

        if (!rawType1.equals(rawType2)) {
            return false;
        }

        final Type[] types1 = type1.getActualTypeArguments();
        final Type[] types2 = type2.getActualTypeArguments();

        if (types1.length != types2.length) {
            throw ReflectionLogger.LOG.invalidTypeArgumentCombination(type1, type2);
        }
        for (int i = 0; i < type1.getActualTypeArguments().length; i++) {
            // if `type1` is recursive in its type argument `types1[i]`, we treat types1[i] and types2[i] as assignable
            // (checking the same for type2 doesn't seem necessary)
            if (types1[i] instanceof TypeVariable && isTypeRecursiveIn(type1, (TypeVariable<?>) types1[i])) {
                continue;
            }
            // Generics are invariant
            if (!InvariantTypes.isAssignableFrom(types1[i], types2[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if given parameterized type is recursive in given type variable, which is
     * a type argument of the parameterized type.
     *
     * @param type a parameterized type
     * @param typeArgument a type variable which is a type argument of {@code type}
     * @return whether {@code type} is recursive in {@code typeArgument}
     */
    private static boolean isTypeRecursiveIn(ParameterizedType type, TypeVariable<?> typeArgument) {
        for (Type bound : AbstractAssignabilityRules.getUppermostTypeVariableBounds(typeArgument)) {
            if (bound instanceof ParameterizedType) {
                if (type.equals(bound)) {
                    // found bound equal to original type, this is recursive generic type
                    return true;
                } else {
                    ParameterizedType castBound = (ParameterizedType) bound;
                    // recursive search through all found type args
                    for (Type typeArg : castBound.getActualTypeArguments()) {
                        if (typeArg instanceof TypeVariable && isTypeRecursiveIn(castBound, (TypeVariable<?>) typeArg)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean isAssignableFrom(ParameterizedType type1, TypeVariable<?> type2) {
        for (Type type : type2.getBounds()) {
            if (isAssignableFrom(type1, type)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAssignableFrom(ParameterizedType type1, WildcardType type2) {
        return false;
    }

    private static boolean isAssignableFrom(ParameterizedType type1, GenericArrayType type2) {
        return false;
    }

    /*
     * Type variable
     */
    private static boolean isAssignableFrom(TypeVariable<?> type1, Class<?> type2) {
        return false;
    }

    private static boolean isAssignableFrom(TypeVariable<?> type1, ParameterizedType type2) {
        return false;
    }

    /**
     * Returns <tt>true</tt> if <tt>type2</tt> is a "sub-variable" of <tt>type1</tt>, i.e. if they are equal or if
     * <tt>type2</tt> (transitively) extends <tt>type1</tt>.
     */
    private static boolean isAssignableFrom(TypeVariable<?> type1, TypeVariable<?> type2) {
        if (type1.equals(type2)) {
            return true;
        }
        // if a type variable extends another type variable, it cannot declare other bounds
        if (type2.getBounds()[0] instanceof TypeVariable<?>) {
            return isAssignableFrom(type1, (TypeVariable<?>) type2.getBounds()[0]);
        }
        return false;
    }

    private static boolean isAssignableFrom(TypeVariable<?> type1, WildcardType type2) {
        return false;
    }

    private static boolean isAssignableFrom(TypeVariable<?> type1, GenericArrayType type2) {
        return false;
    }

    /*
     * Wildcard
     */

    /**
     * This logic is shared for all actual types i.e. raw types, parameterized types and generic array types.
     */
    private static boolean isAssignableFrom(WildcardType type1, Type type2) {
        if (!isAssignableFrom(type1.getUpperBounds()[0], type2)) {
            return false;
        }
        if (type1.getLowerBounds().length > 0 && !isAssignableFrom(type2, type1.getLowerBounds()[0])) {
            return false;
        }
        return true;
    }

    private static boolean isAssignableFrom(WildcardType type1, TypeVariable<?> type2) {
        if (type1.getLowerBounds().length > 0) {
            return isAssignableFrom(type2, type1.getLowerBounds()[0]);
        }
        return isAssignableFrom(type1.getUpperBounds()[0], type2);
    }

    private static boolean isAssignableFrom(WildcardType type1, WildcardType type2) {
        if (!isAssignableFrom(type1.getUpperBounds()[0], type2.getUpperBounds()[0])) {
            return false;
        }

        if (type1.getLowerBounds().length > 0) {
            // the first type defines a lower bound
            if (type2.getLowerBounds().length > 0) {
                return isAssignableFrom(type2.getLowerBounds()[0], type1.getLowerBounds()[0]);
            } else {
                return false;
            }
        } else if (type2.getLowerBounds().length > 0) {
            // only the second type defines a lower bound
            return type1.getUpperBounds()[0].equals(Object.class);
        }
        return true;
    }

    /*
     * GenericArrayType
     */
    private static boolean isAssignableFrom(GenericArrayType type1, Class<?> type2) {
        return type2.isArray() && isAssignableFrom(Reflections.getRawType(type1.getGenericComponentType()), type2.getComponentType());
    }

    private static boolean isAssignableFrom(GenericArrayType type1, ParameterizedType type2) {
        return false;
    }

    private static boolean isAssignableFrom(GenericArrayType type1, TypeVariable<?> type2) {
        /*
         * JLS does not allow array types to be used as bounds of type variables
         */
        return false;
    }

    private static boolean isAssignableFrom(GenericArrayType type1, WildcardType type2) {
        return false;
    }

    private static boolean isAssignableFrom(GenericArrayType type1, GenericArrayType type2) {
        return isAssignableFrom(type1.getGenericComponentType(), type2.getGenericComponentType());
    }
}
