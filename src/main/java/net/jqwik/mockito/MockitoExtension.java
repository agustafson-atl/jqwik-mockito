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

public class MockitoExtension implements AroundPropertyHook, AroundTryHook {
    @Override
    public PropagationMode propagateTo() {
        return PropagationMode.ALL_DESCENDANTS;
    }

    @Override
    public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
        try (AutoCloseable ignored = MockitoAnnotations.openMocks(context.testInstance())) {
            return property.execute();
        }
    }

    @Override
    public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) throws Throwable {
        final List<Object> mocks = MockitoAnnotationFinder.getMocks(context.testInstance());
        final Object[] mocksArray = mocks.toArray(mocks.toArray(new Object[0]));
        try {
            return aTry.execute(parameters);
        } finally {
            Mockito.reset(mocksArray);
        }
    }
}
