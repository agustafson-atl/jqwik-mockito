package net.jqwik.mockito;

import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockingDetails;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.internal.configuration.plugins.Plugins;
import org.mockito.plugins.MemberAccessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper class which extracts mocked fields from a given test instance.
 */
class MockitoAnnotationFinder {
    private static final MemberAccessor MEMBER_ACCESSOR = Plugins.getMemberAccessor();

    /**
     * Retrieve mocking fields for a given test instance object.
     *
     * @param testInstance The test instance which contains the mocked fields
     * @return The list of mock objects which have been injected into the test instance
     * @throws IllegalAccessException If it is not possible to get access to the fields in the test instance
     */
    static List<Object> getMocks(Object testInstance) throws IllegalAccessException {
        final List<Object> mocks = new ArrayList<>();
        for (final Field field : testInstance.getClass().getDeclaredFields()) {
            final Object fieldValue = MEMBER_ACCESSOR.get(field, testInstance);
            final MockingDetails mockingDetails = Mockito.mockingDetails(fieldValue);
            if (mockingDetails.isMock()) {
                mocks.add(fieldValue);
            }
        }
        return mocks;
    }
}
