Provides `net.jqwik.api.lifecycle.LifecycleHook`s to enable usage of Mockito with jqwik.
 - Creates mocks for any fields with Mockito annotations such as `org.mockito.Mock` or `org.mockito.Spy`.
 - Resets all mocks between each try, whether those mocks were created programmatically (eg: via calls to `Mockito.mock()`}) or via annotations.

## Installation
```
implementation("net.jqwik:jqwik-mockito:${jqwikMockitoVersion}")
```

## Usage
```java
import net.jqwik.api.lifecycle.AddLifecycleHook;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

@AddLifecycleHook(MockitoLifecycleHooks.class)
class OrderServiceTest {
    // Either @Mock or Mockito.mock() can be used.
    @Mock
    private OrderRepository orderRepository;
    private OrderRepository orderRepository2 = Mockito.mock();

    // Either @Spy or Mockito.spy() can be used.
    @Spy
    private ConsoleLoggingServiceImpl loggingService;
    private LoggingService loggingService2 = Mockito.spy(new ConsoleLoggingServiceImpl());
    
    @InjectMock
    private OrderService orderService;
}
```