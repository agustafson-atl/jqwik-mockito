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

public class MockitoLifecycleHooks implements AroundPropertyHook, AroundTryHook {
    private Object[] mocks;

    @Override
    public PropagationMode propagateTo() {
        return PropagationMode.ALL_DESCENDANTS;
    }

    @Override
    public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property)
            throws Throwable {
        try (AutoCloseable ignored = MockitoAnnotations.openMocks(context.testInstance())) {
            // finds all mocked fields within the test instance object
            mocks = MockFinder.getMocks(context.testInstance()).toArray(new Object[0]);
            return property.execute();
        }
    }

    @Override
    public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) {
        try {
            return aTry.execute(parameters);
        } finally {
            Mockito.reset(mocks);
        }
    }
}
