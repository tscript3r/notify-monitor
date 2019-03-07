package pl.tscript3r.notify.monitor.utils;

import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import java.beans.Introspector;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PackageClassScanner {

    private ApplicationContext context;
    private Set<BeanDefinition> packageClasses;
    private Boolean throwExceptions = false;

    /**
     * Found at:
     * https://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection
     *
     * @return all classes in the given package
     */
    private Set<BeanDefinition> loadPackageClasses(String packagePath, Pattern classNameFilterPattern) {
        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new RegexPatternTypeFilter(classNameFilterPattern));
        final Set<BeanDefinition> classes = provider.findCandidateComponents(packagePath);
        return classes;
    }

    private PackageClassScanner(ApplicationContext context, String packagePath, Pattern classNamePattern) {
        this.context = context;
        packageClasses = loadPackageClasses(packagePath, classNamePattern);
    }

    public synchronized static PackageClassScanner scan(ApplicationContext context, String packagePath) {
        return new PackageClassScanner(context, packagePath, Pattern.compile(".*"));
    }

    public synchronized static PackageClassScanner scan(ApplicationContext context, String packagePath,
                                                        Pattern classNamePattern) {
        return new PackageClassScanner(context, packagePath, classNamePattern);
    }

    public PackageClassScanner throwExceptions() {
        throwExceptions = true;
        return this;
    }

    public static String getBeanName(String className) throws ClassNotFoundException {
        Class foundClass = Class.forName(className);
        return Introspector.decapitalize(foundClass.getSimpleName());
    }

    public PackageClassScanner filterWithInterface(Class interfaceClass) {
        packageClasses = packageClasses.stream()
                .filter(foundBeanClass -> {
                    Boolean interfaceFound = false;
                    try {
                        Class foundClass = Class.forName(foundBeanClass.getBeanClassName());
                        Class[] foundInterfaces = foundClass.getInterfaces();
                        if (foundInterfaces.length >= 1) {
                            for (Class foundInterface : foundInterfaces) {
                                interfaceFound = foundInterface.getName().equals(interfaceClass.getName());
                                if (interfaceFound)
                                    break;
                            }
                        } else
                            throwException("Class " + foundClass.getClass().getName() +
                                    " does not implement any interface");
                        if (!interfaceFound)
                            throwException("Class " + foundBeanClass.getBeanClassName() +
                                    " does not implement " + interfaceClass.getName());
                    } catch (ClassNotFoundException e) {
                        throwException(e.getMessage());
                    }

                    return interfaceFound;
                }).collect(Collectors.toSet());
        return this;
    }

    public PackageClassScanner filterWithSpringComponents() {
        packageClasses = packageClasses.stream()
                .filter(foundBeanClass -> {
                    String beanName = null;
                    try {
                        beanName = getBeanName(foundBeanClass.getBeanClassName());
                    } catch (ClassNotFoundException e) {
                        throwException(e.getMessage());
                    }
                    Boolean result = context.containsBeanDefinition(beanName);
                    if (!result)
                        throwException("Class " + foundBeanClass.getBeanClassName() +
                                " is not a spring component");
                    return result;
                })
                .collect(Collectors.toSet());
        return this;
    }

    public PackageClassScanner filterWithPrototypeComponents() {
        packageClasses = packageClasses.stream()
                .filter(foundBeanClass -> {
                    String beanName = null;
                    try {
                        beanName = getBeanName(foundBeanClass.getBeanClassName());
                    } catch (ClassNotFoundException e) {
                        throwException(e.getMessage());
                    }
                    Boolean result = context.isPrototype(beanName);
                    if (!result)
                        throwException("Class " + foundBeanClass.getBeanClassName() +
                                " is not a prototype component");
                    return result;
                })
                .collect(Collectors.toSet());
        return this;
    }

    public PackageClassScanner filterByModifier(int modifier) {
        packageClasses = packageClasses.stream()
                .filter(foundBeanClass -> {
                    try {
                        Class foundClass = Class.forName(foundBeanClass.getBeanClassName());
                        Boolean result = (foundClass.getModifiers() & modifier) == modifier;
                        if (!result)
                            throwException("Class " + foundClass.getName() + " has wrong access modifier.");
                        return result;
                    } catch (ClassNotFoundException e) {
                        throwException(e.getMessage());
                    }
                    return false;
                })
                .collect(Collectors.toSet());
        return this;
    }

    public PackageClassScanner filter(Predicate<BeanDefinition> predicate) {
        packageClasses = packageClasses.stream()
                .filter(predicate)
                .collect(Collectors.toSet());
        return this;
    }

    public PackageClassScanner forEach(Consumer<BeanDefinition> action) {
        packageClasses.forEach(action);
        return this;
    }

    public Set<String> getBeanNames() {
        return packageClasses.stream()
                .map(beanDefinition -> {
                    try {
                        return getBeanName(beanDefinition.getBeanClassName());
                    } catch (ClassNotFoundException e) {
                        throwException(e.getMessage());
                    }
                    return null;
                })
                .collect(Collectors.toSet());
    }

    private void throwException(String message) {
        if (throwExceptions)
            throw new FatalBeanException(message);
    }


}
