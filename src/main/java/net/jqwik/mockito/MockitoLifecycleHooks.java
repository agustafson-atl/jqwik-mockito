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

import java.util.List;

/**
 * Provides {@link net.jqwik.api.lifecycle.LifecycleHook}s to enable usage of Mockito with jqwik.
 * <br/>
 * <ul>
 *   <li>Creates mocks for any fields with Mockito annotations such as {@link org.mockito.Mock} or {@link org.mockito.Spy}.</li>
 *   <li>Resets all mocks between each try, whether those mocks were created programmatically (eg: via calls to {@code Mockito.mock()})
 *   or via annotations.</li>
 * </ul>
 *
 * <pre>{@code
 * import net.jqwik.api.lifecycle.AddLifecycleHook;
 * import org.mockito.InjectMocks;
 * import org.mockito.Mock;
 * import org.mockito.Mockito;
 * import org.mockito.Spy;
 * @AddLifecycleHook(MockitoLifecycleHooks.class)
 * class OrderServiceTest {
 *     @Mock
 *     private OrderRepository orderRepository;
 *     private OrderRepository orderRepository2 = Mockito.mock();
 *     private LoggingService loggingService = Mockito.spy(new ConsoleLoggingServiceImpl());
 *     @Spy
 *     private ConsoleLoggingServiceImpl loggingService2;
 *     @InjectMock
 *     private OrderService orderService;
 * }
 * }</pre>
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
        try (AutoCloseable ignored = MockitoAnnotations.openMocks(context.testInstance())) {
            // finds all mocked fields within the test instance object
            mocks = MockFinder.getMocks(context.testInstance()).toArray(new Object[0]);
            return propertyExecutor.execute();
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
