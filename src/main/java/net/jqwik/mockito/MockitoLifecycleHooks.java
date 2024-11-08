package net.jqwik.mockito;

import net.jqwik.api.lifecycle.AroundPropertyHook;
import net.jqwik.api.lifecycle.AroundTryHook;
import net.jqwik.api.lifecycle.PropagationMode;
import net.jqwik.api.lifecycle.PropertyExecutionResult;
import net.jqwik.api.lifecycle.PropertyExecutor;
import net.jqwik.api.lifecycle.PropertyLifecycleContext;
import net.jqwik.api.lifecycle.TryExecutionResult;
import net.jqwik.api.lifecycle.TryExecutor;
import net.jqwik.api.lifecycle.TryLifecycleContext;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides {@link net.jqwik.api.lifecycle.LifecycleHook}s to enable usage of Mockito with jqwik.
 * <ul>
 *   <li>Creates mocks for any fields with Mockito annotations such as {@link org.mockito.Mock} or {@link org.mockito.Spy}.</li>
 *   <li>Resets all mocks between each try, whether those mocks were created programmatically (eg: via calls to {@code Mockito.mock()})
 *   or via annotations.</li>
 * </ul>
 *
 * <pre>
 * import net.jqwik.api.lifecycle.AddLifecycleHook;
 * import org.mockito.InjectMocks;
 * import org.mockito.Mock;
 * import org.mockito.Mockito;
 * import org.mockito.Spy;
 * {@literal @}AddLifecycleHook(MockitoLifecycleHooks.class)
 * class OrderServiceTest {
 *     {@literal @}Mock
 *     private OrderRepository orderRepository;
 *     private OrderRepository orderRepository2 = Mockito.mock();
 *     private LoggingService loggingService = Mockito.spy(new ConsoleLoggingServiceImpl());
 *     {@literal @}Spy
 *     private ConsoleLoggingServiceImpl loggingService2;
 *     {@literal @}InjectMock
 *     private OrderService orderService;
 * }
 * </pre>
 *
 * @see net.jqwik.api.lifecycle.AddLifecycleHook
 */
public class MockitoLifecycleHooks implements AroundPropertyHook, AroundTryHook {
    private Object[] mocks;

    @Override
    public PropagationMode propagateTo() {
        return PropagationMode.ALL_DESCENDANTS;
    }

    @Override
    public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor propertyExecutor)
            throws Throwable {
        // finds all mocked fields within the test instance object
        final List<AutoCloseable> mockitoCloseables = new ArrayList<>();
        try {
            final List<Object> mockList = new ArrayList<>();

            for (final Object testInstance : context.testInstances()) {
                // open all the annotated mocks, keeping track of the handle so that we can close them later
                mockitoCloseables.add(MockitoAnnotations.openMocks(testInstance));
                // find all of the mocks in each of the test instances and store them in a list, so that we can reset them between tries.
                mockList.addAll(MockFinder.getMocks(testInstance));
            }

            mocks = mockList.toArray(new Object[0]);
            return propertyExecutor.execute();
        } finally {
            mockitoCloseables.forEach(mockitoCloseable -> {
                try {
                    mockitoCloseable.close();
                } catch (final Exception ignored) {
                }
            });
        }
    }

    @Override
    public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor tryExecutor, List<Object> parameters) {
        try {
            return tryExecutor.execute(parameters);
        } finally {
            Mockito.reset(mocks);
        }
    }
}
