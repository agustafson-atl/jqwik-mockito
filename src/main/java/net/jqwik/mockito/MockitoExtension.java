package net.jqwik.mockito;

import java.util.*;

import net.jqwik.api.lifecycle.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.progress.ThreadSafeMockingProgress;

class MockitoExtension implements AroundTryHook /*, AroundPropertyHook*/ {

//    private static final Object IDENTIFIER = MockitoExtension.class;
    private static final Object SHARED_LIFECYCLE_AWARE_MOCKITO = new Object();

//    final Store<AutoCloseable> autoCloseableStore = Store.create(SHARED_LIFECYCLE_AWARE_MOCKITO, Lifespan.TRY, () -> () -> {});

    @Override
    public PropagationMode propagateTo() {
        return PropagationMode.ALL_DESCENDANTS;
    }

//    @Override
//    public void beforeContainer(ContainerLifecycleContext context) {
//        Store.create(SHARED_LIFECYCLE_AWARE_MOCKITO, Lifespan.PROPERTY, () -> MockitoAnnotations.openMocks(propertyLifecycleContext.testInstance()));
//        AroundContainerHook.super.beforeContainer(context);
//    }
//
//    @Override
//    public void afterContainer(ContainerLifecycleContext context) {
//        AroundContainerHook.super.afterContainer(context);
//    }

//    @Override
//    public PropertyExecutionResult aroundProperty(PropertyLifecycleContext propertyLifecycleContext, PropertyExecutor propertyExecutor) throws Throwable {
        //Store.create(SHARED_LIFECYCLE_AWARE_MOCKITO, Lifespan.PROPERTY, () -> MockitoAnnotations.openMocks(propertyLifecycleContext.testInstance()));
//        try (AutoCloseable ignored = MockitoAnnotations.openMocks(propertyLifecycleContext.testInstance())) {
//            System.out.println("BEFORE PROPERTY EXECUTION");
//            return propertyExecutor.execute();
//        } finally {
//            System.out.println("AFTER PROPERTY EXECUTION");
//        }
//        return propertyExecutor.execute();
//    }

//    @Override
//    public TryExecutionResult aroundTry(TryLifecycleContext tryLifecycleContext, TryExecutor tryExecutor, List<Object> parameters) throws Throwable {
//        final AtomicReference<TryExecutionResult> tryExecutionResultAtomicReference = new AtomicReference<>();
//        try {
//            autoCloseableStore.update(previousAutoCloseable -> {
//                try {
//                    previousAutoCloseable.close();
//                } catch (Exception ignored) {
//                }
//
//                return MockitoAnnotations.openMocks(tryLifecycleContext.testInstance());
//            });
//            System.out.println("BEFORE TRY EXECUTION");
//            return tryExecutor.execute(parameters);
//        } finally {
//            autoCloseableStore.get().close();
//            autoCloseableStore.reset();
//            System.out.println("AFTER TRY EXECUTION");
//        }

//        try (AutoCloseable ignored = MockitoAnnotations.openMocks(tryLifecycleContext.testInstance())) {
//            System.out.println("BEFORE TRY EXECUTION");
//            return tryExecutor.execute(parameters);
//        } finally {
//            System.out.println("AFTER TRY EXECUTION");
//        }

//        return tryExecutor.execute(parameters);
//  }

    @Override
    public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) throws Throwable {
        Object testInstance = context.testInstance();
//        Store<ClosableStartables> store = getOrCreateContainerClosingStore(aTry.hashCode(), Lifespan.TRY);
        final Store<ClosableStartables> store =
                Store.getOrCreate(aTry.hashCode(), Lifespan.TRY, () -> ClosableStartables.of(MockitoAnnotations.openMocks(context.testInstance())));

//        List<TestLifecycleAware> lifecycleAwareContainers = startContainersAndFindLifeCycleAwareOnes(store, findRestartContainersPerTry(testInstance));
//
//        TestDescription testDescription = testDescriptionFrom(context);
//        signalBeforeTestToContainers(lifecycleAwareContainers, testDescription);

        store.get();

        //final List<>
//        context.findAnnotationsInContainer(Mock.class);
//        context.testInstances();
        Mockito.reset(testInstance);

        ThreadSafeMockingProgress.mockingProgress().reset();

//        store.update(previousAutoCloseable -> {
//            try {
//                previousAutoCloseable.close();
//            } catch (Exception ignored) {
//            }
//
//            System.out.println("OPENING MOCKS FOR testInstance: " + testInstance);
//
//            return ClosableStartables.of(MockitoAnnotations.openMocks(context.testInstance()));
//        });
        System.out.println("BEFORE TRY EXECUTION");

        TryExecutionResult executionResult = aTry.execute(parameters);
//        signalAfterTestToContainersFor(lifecycleAwareContainers, testDescription, executionResult);

        System.out.println("AFTER TRY EXECUTION");

        return executionResult;
    }

//    @Override
//    public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
//        Object testInstance = context.testInstance();
//        Store<ClosableStartables> store = getOrCreateContainerClosingStore(property.hashCode(), Lifespan.PROPERTY);
//
////        List<TestLifecycleAware> lifecycleAwareContainers = startContainersAndFindLifeCycleAwareOnes(store, findRestartContainersPerTry(testInstance));
////
////        TestDescription testDescription = testDescriptionFrom(context);
////        signalBeforeTestToContainers(lifecycleAwareContainers, testDescription);
//
//        store.update(previousAutoCloseable -> {
//            try {
//                previousAutoCloseable.close();
//            } catch (Exception ignored) {
//            }
//
//            System.out.println("OPENING MOCKS FOR testInstance: " + testInstance);
//
//            return ClosableStartables.of(MockitoAnnotations.openMocks(context.testInstance()));
//        });
//        System.out.println("BEFORE PROPERTY EXECUTION");
//
//        PropertyExecutionResult executionResult = property.execute();
////        signalAfterTestToContainersFor(lifecycleAwareContainers, testDescription, executionResult);
//
//        System.out.println("AFTER PROPERTY EXECUTION");
//
//        return executionResult;
//    }

//
//    private static Predicate<Field> restartPerTry() {
//        return field -> AnnotationSupport.findAnnotation(field, Container.class)
//                .filter(Container::restartPerTry)
//                .isPresent();
//    }
//
//    private static Predicate<Field> isContainer() {
//        return field -> {
//            boolean isAnnotatedWithContainer = AnnotationSupport.isAnnotated(field, Container.class);
//            if (isAnnotatedWithContainer) {
//                boolean isStartable = Startable.class.isAssignableFrom(field.getType());
//
//                if (!isStartable) {
//                    throw new JqwikException(String.format("FieldName: %s does not implement Startable", field.getName()));
//                }
//                return true;
//            }
//            return false;
//        };
//    }
//
//    private static Startable getContainerInstance(final Object testInstance, final Field field) {
//        try {
//            field.setAccessible(true);
//            return Preconditions.notNull((Startable) field.get(testInstance), "Container " + field.getName() + " needs to be initialized");
//        } catch (IllegalAccessException e) {
//            throw new JqwikException("Can not access container defined in field " + field.getName());
//        }
//    }
//
//    @Override
//    public void beforeContainer(ContainerLifecycleContext context) {
//        Class<?> testClass = context.optionalContainerClass()
//                .orElseThrow(() -> new IllegalStateException("MockitoExtension is only supported for classes."));
//        Store<ClosableStartables> store = getOrCreateContainerClosingStore(IDENTIFIER, Lifespan.RUN);
//
//        List<TestLifecycleAware> lifecycleAwareContainers = startContainersAndFindLifeCycleAwareOnes(store, findSharedContainers(testClass));
//
//        Store.getOrCreate(SHARED_LIFECYCLE_AWARE_MOCKITO, Lifespan.RUN, () -> lifecycleAwareContainers);
//        signalBeforeTestToContainers(lifecycleAwareContainers, testDescriptionFrom(context));
//    }
//
//    @Override
//    public void afterContainer(ContainerLifecycleContext context) {
//        Store<List<TestLifecycleAware>> containers = Store
//                .getOrCreate(SHARED_LIFECYCLE_AWARE_MOCKITO, Lifespan.RUN, ArrayList::new);
//        signalAfterTestToContainersFor(containers.get(), testDescriptionFrom(context));
//    }
//
//    @Override
//    public int proximity() {
//        // must be run before the @BeforeContainer annotation and after @AfterContainer annotation
//        return -11;
//    }
//
//    @Override
//    public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) {
//        Object testInstance = context.testInstance();
//        Store<ClosableStartables> store = getOrCreateContainerClosingStore(property.hashCode(), Lifespan.PROPERTY);
//
//        List<TestLifecycleAware> lifecycleAwareContainers = startContainersAndFindLifeCycleAwareOnes(store, findRestartContainers(testInstance));
//
//        TestDescription testDescription = testDescriptionFrom(context);
//        signalBeforeTestToContainers(lifecycleAwareContainers, testDescription);
//        PropertyExecutionResult executionResult = property.execute();
//        signalAfterTestToContainersFor(lifecycleAwareContainers, testDescription, executionResult);
//
//        return executionResult;
//    }
//
//    @Override
//    public int aroundPropertyProximity() {
//        return -11; // Run before BeforeProperty and after AfterProperty
//    }
//
//    @Override
//    public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) {
//        Object testInstance = context.testInstance();
//        Store<ClosableStartables> store = getOrCreateContainerClosingStore(aTry.hashCode(), Lifespan.TRY);
//
//        List<TestLifecycleAware> lifecycleAwareContainers = startContainersAndFindLifeCycleAwareOnes(store, findRestartContainersPerTry(testInstance));
//
//        TestDescription testDescription = testDescriptionFrom(context);
//        signalBeforeTestToContainers(lifecycleAwareContainers, testDescription);
//        TryExecutionResult executionResult = aTry.execute(parameters);
//        signalAfterTestToContainersFor(lifecycleAwareContainers, testDescription, executionResult);
//
//        return executionResult;
//    }
//
//    @Override
//    public int aroundTryProximity() {
//        return -11; // Run before BeforeTry and after AfterTry
//    }
//
    private Store<ClosableStartables> getOrCreateContainerClosingStore(Object identifier, Lifespan lifespan) {
        return Store.getOrCreate(identifier, lifespan, ClosableStartables::empty);
    }
//
//    private List<TestLifecycleAware> startContainersAndFindLifeCycleAwareOnes(Store<ClosableStartables> store, Stream<Startable> containers) {
//        return containers
//                .peek(startable -> store.update(closableStartables -> {
//                    startable.start();
//                    closableStartables.add(startable);
//                    return closableStartables;
//                }))
//                .filter(this::isTestLifecycleAware)
//                .map(startable -> (TestLifecycleAware) startable)
//                .collect(toList());
//    }
//
//    private void signalBeforeTestToContainers(List<TestLifecycleAware> lifecycleAwareContainers, TestDescription testDescription) {
//        lifecycleAwareContainers.forEach(container -> container.beforeTest(testDescription));
//    }
//
//    private void signalAfterTestToContainersFor(List<TestLifecycleAware> containers, TestDescription testDescription) {
//        containers.forEach(container -> container.afterTest(testDescription, Optional.empty()));
//    }
//
//    private void signalAfterTestToContainersFor(
//            List<TestLifecycleAware> containers,
//            TestDescription testDescription,
//            PropertyExecutionResult executionResult
//    ) {
//        containers.forEach(container -> {
//            if (executionResult.status() == PropertyExecutionResult.Status.ABORTED) {
//                container.afterTest(testDescription, Optional.of(new TestAbortedException()));
//            } else {
//                container.afterTest(testDescription, executionResult.throwable());
//            }
//        });
//    }
//
//    private void signalAfterTestToContainersFor(
//            List<TestLifecycleAware> containers,
//            TestDescription testDescription,
//            TryExecutionResult executionResult
//    ) {
//        containers.forEach(container -> container.afterTest(testDescription, executionResult.throwable()));
//    }
//
//    private TestDescription testDescriptionFrom(LifecycleContext context) {
//        return new MockitoTestDescription(
//                context.label(),
//                FilesystemFriendlyNameGenerator.filesystemFriendlyNameOf(context)
//        );
//    }
//
//    private boolean isTestLifecycleAware(Startable startable) {
//        return startable instanceof TestLifecycleAware;
//    }
//
//    @Override
//    public SkipResult shouldBeSkipped(LifecycleContext context) {
//        return findMockito(context)
//                .map(this::evaluateSkipResult)
//                .orElseThrow(() -> new JqwikException("@Mockito not found"));
//    }
//
//    private Optional<Mockito> findMockito(LifecycleContext context) {
//        // Find closest Mockito annotation
//        Optional<Mockito> first = context.findAnnotationsInContainer(Mockito.class).stream().findFirst();
//        if (first.isPresent())
//            return first;
//        else
//            return context.findAnnotation(Mockito.class);
//    }
//
//    private SkipResult evaluateSkipResult(Mockito mockito) {
//        return SkipResult.doNotSkip();
//    }
//
//    private Stream<Startable> findSharedContainers(Class<?> testClass) {
//        final Predicate<Field> isSharedContainer = ReflectionUtils::isStatic;
//        return findContainers(null, isSharedContainer, testClass);
//    }
//
//    private Stream<Startable> findRestartContainers(Object testInstance) {
//        final Predicate<Field> isRestartContainer = ReflectionUtils::isNotStatic;
//        return findContainers(testInstance, isRestartContainer.and(restartPerTry().negate()), testInstance.getClass());
//    }
//
//    private Stream<Startable> findRestartContainersPerTry(Object testInstance) {
//        final Predicate<Field> isRestartContainer = ReflectionUtils::isNotStatic;
//        return findContainers(testInstance, isRestartContainer.and(restartPerTry()), testInstance.getClass());
//    }
//
//    private Stream<Startable> findContainers(Object testInstance, Predicate<Field> containerCondition, Class<?> testClass) {
//        return ReflectionUtils.findFields(
//                        testClass,
//                        isContainer().and(containerCondition),
//                        ReflectionUtils.HierarchyTraversalMode.TOP_DOWN
//                )
//                .stream()
//                .map(f -> getContainerInstance(testInstance, f));
//    }
//
//    private static class ClosableStartables implements Store.CloseOnReset {
//
//        private final List<Startable> startables;
//
//        private ClosableStartables(List<Startable> startables) {
//            this.startables = startables;
//        }
//
//        private static ClosableStartables empty(){
//            return of(new ArrayList<>());
//        }
//
//        private static ClosableStartables of(List<Startable> startables){
//            return new ClosableStartables(startables);
//        }
//
//        public void add(Startable startable){
//            startables.add(startable);
//        }
//
//        @Override
//        public void close() {
//            startables.forEach(Startable::close);
//        }
//
//    }

    private static class ClosableStartables implements Store.CloseOnReset {

        public static final AutoCloseable EMPTY = () -> {};
        private final AutoCloseable autoCloseable;

        private ClosableStartables(AutoCloseable autoCloseable) {
            this.autoCloseable = autoCloseable;
        }

        private static ClosableStartables empty() {
            System.out.println("Empty ClosableStartables");
            return of(EMPTY);
        }

        private static ClosableStartables of(AutoCloseable startable){
            return new ClosableStartables(startable);
        }

        @Override
        public void close() {
            System.out.println("CLOSING MOCKS FOR testInstance");
            System.out.println();
            try {
                autoCloseable.close();
            } catch (Exception ignored) {
            }
        }
    }
}