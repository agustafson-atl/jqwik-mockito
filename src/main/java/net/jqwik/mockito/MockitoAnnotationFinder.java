package net.jqwik.mockito;

import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.configuration.plugins.Plugins;
import org.mockito.plugins.MemberAccessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MockitoAnnotationFinder {
    private static final Set<Class<? extends Annotation>> MOCKING_ANNOTATION_TYPES;

    static {
        MOCKING_ANNOTATION_TYPES = new HashSet<>();
        MOCKING_ANNOTATION_TYPES.add(Mock.class);
        MOCKING_ANNOTATION_TYPES.add(Spy.class);
        MOCKING_ANNOTATION_TYPES.add(Captor.class);
    }

    private static final MemberAccessor MEMBER_ACCESSOR = Plugins.getMemberAccessor();

    public static List<Object> getMocks(Object testInstance) throws IllegalAccessException {
        final List<Object> mocks = new ArrayList<>();
        for (final Field field : testInstance.getClass().getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                if (MOCKING_ANNOTATION_TYPES.contains(annotation.annotationType())) {
                    mocks.add(MEMBER_ACCESSOR.get(field, testInstance));
                }
            }
        }
        return mocks;
    }
}
