package pl.tscript3r.notify.monitor.utils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.FatalBeanException;
import org.springframework.context.ApplicationContext;
import pl.tscript3r.notify.monitor.utils.testPackage.ATest;
import pl.tscript3r.notify.monitor.utils.testPackage.C;
import pl.tscript3r.notify.monitor.utils.testPackage.TestInterface;
import pl.tscript3r.notify.monitor.utils.testPackage.TypicalClassName;

import java.lang.reflect.Modifier;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class PackageClassScannerTest {

    private static final String TEST_PACKAGE_PATH = "pl.tscript3r.notify.monitor.utils.testPackage";

    @Mock
    ApplicationContext context;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void scanWithoutPattern() {
        assertEquals(4, PackageClassScanner.scan(context, TEST_PACKAGE_PATH)
                .getBeanNames().size());
    }

    @Test
    public void scanWithPattern() {
        assertEquals(2, PackageClassScanner.scan(context,
                TEST_PACKAGE_PATH, Pattern.compile(".*Test"))
                .getBeanNames().size());
    }

    @Test
    public void getBeanName() throws ClassNotFoundException {
        assertEquals("ATest", PackageClassScanner.getBeanName(ATest.class.getName()));
        assertEquals("c", PackageClassScanner.getBeanName(C.class.getName()));
        assertEquals("typicalClassName", PackageClassScanner.getBeanName(TypicalClassName.class.getName()));
    }

    @Test(expected = ClassNotFoundException.class)
    public void getBeanNameNotFoundException() throws ClassNotFoundException {
        PackageClassScanner.getBeanName("xyz");
    }

    @Test
    public void filterWithInterface() {
        assertEquals(2, PackageClassScanner.scan(context, TEST_PACKAGE_PATH)
                .filterWithInterface(TestInterface.class)
                .getBeanNames().size());
    }

    @Test(expected = FatalBeanException.class)
    public void filterWithInterfaceException() {
        PackageClassScanner.scan(context, TEST_PACKAGE_PATH)
                .throwExceptions()
                .filterWithInterface(TestInterface.class)
                .getBeanNames().size();
    }

    @Test
    public void filterWithSpringComponents() {
        when(context.containsBeanDefinition(anyString())).thenReturn(true);
        assertEquals(4, PackageClassScanner.scan(context, TEST_PACKAGE_PATH)
                .filterWithSpringComponents()
                .getBeanNames().size());
    }

    @Test(expected = FatalBeanException.class)
    public void filterWithSpringComponentsException() {
        when(context.containsBeanDefinition(anyString())).thenReturn(false);
        PackageClassScanner.scan(context, TEST_PACKAGE_PATH)
                .throwExceptions()
                .filterWithSpringComponents()
                .getBeanNames().size();
    }

    @Test
    public void filterWithPrototypeComponents() {
        when(context.isPrototype(anyString())).thenReturn(true);
        assertEquals(4, PackageClassScanner.scan(context, TEST_PACKAGE_PATH)
                .filterWithPrototypeComponents()
                .getBeanNames().size());
    }

    @Test(expected = FatalBeanException.class)
    public void filterWithPrototypeComponentsException() {
        when(context.containsBeanDefinition(anyString())).thenReturn(false);
        PackageClassScanner.scan(context, TEST_PACKAGE_PATH)
                .throwExceptions()
                .filterWithPrototypeComponents()
                .getBeanNames().size();
    }

    @Test
    public void filterByModifier() {
        assertEquals(2, PackageClassScanner.scan(context, TEST_PACKAGE_PATH)
                .filterByModifier(Modifier.FINAL)
                .getBeanNames().size());
    }

    @Test(expected = FatalBeanException.class)
    public void filterByModifierException() {
        PackageClassScanner.scan(context, TEST_PACKAGE_PATH)
                .throwExceptions()
                .filterByModifier(Modifier.FINAL)
                .getBeanNames().size();
    }

    @Test
    public void filter() {
        assertEquals(1, PackageClassScanner.scan(context, TEST_PACKAGE_PATH)
                .filter(beanDefinition -> {
                    try {
                        return PackageClassScanner.getBeanName(beanDefinition.getBeanClassName()).equals("ATest");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return true;
                })
                .getBeanNames().size());
    }

}