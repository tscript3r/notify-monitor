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
    private Boolean throwExceptions = true;

    public synchronized static String getBeanName(String className) throws ClassNotFoundException {
        Class foundClass = Class.forName(className);
        return Introspector.decapitalize(foundClass.getSimpleName());
    }

    public synchronized static PackageClassScanner scan(ApplicationContext context, String packagePath) {
        return new PackageClassScanner(context, packagePath, Pattern.compile(".*"));
    }

    public synchronized static PackageClassScanner scan(ApplicationContext context, String packagePath,
                                                        Pattern classNamePattern) {
        return new PackageClassScanner(context, packagePath, classNamePattern);
    }

    private PackageClassScanner(ApplicationContext context, String packagePath, Pattern classNamePattern) {
        this.context = context;
        packageClasses = loadBeanDefinitionsClassesInPackage(packagePath, classNamePattern);
    }

    public PackageClassScanner ignoreExceptions() {
        throwExceptions = false;
        return this;
    }

    public PackageClassScanner filterByInterface(Class requiredInterface) {
        packageClasses = packageClasses.stream()
                .filter(foundBeanDefinition -> {
                    try {
                        if (implementsInterface(loadClass(foundBeanDefinition).getInterfaces(),
                                requiredInterface))
                            return true;
                        else {
                            throwException("Class " + foundBeanDefinition.getBeanClassName() +
                                    " does not implement required interface");
                            return false;
                        }

                    } catch (ClassNotFoundException e) {
                        throwException(e);
                        return false;
                    }
                })
                .collect(Collectors.toSet());
        return this;
    }

    public PackageClassScanner filterSpringComponents() {
        packageClasses = packageClasses.stream()
                .filter(foundBeanClass -> {
                    String beanName = null;
                    try {
                        beanName = getBeanName(foundBeanClass.getBeanClassName());
                    } catch (ClassNotFoundException e) {
                        throwException(e);
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

    public PackageClassScanner filterPrototypeComponents() {
        packageClasses = packageClasses.stream()
                .filter(foundBeanClass -> {
                    try {
                        String beanName = getBeanName(foundBeanClass.getBeanClassName());
                        if (!context.isPrototype(beanName)) {
                            throwException("Class " + foundBeanClass.getBeanClassName() +
                                    " is not a prototype component");
                            return false;
                        } else
                            return true;
                    } catch (ClassNotFoundException e) {
                        throwException(e.getMessage());
                        return false;
                    }
                })
                .collect(Collectors.toSet());

        return this;
    }

    public PackageClassScanner filterByModifier(int modifier) {
        packageClasses = packageClasses.stream()
                .filter(foundBeanClass -> {
                    try {
                        Class foundClass = loadClass(foundBeanClass);
                        if (hasModifier(foundClass, modifier))
                            return true;
                        else {
                            throwException("Class " + foundClass.getName() + " does not have the required modifier.");
                            return false;
                        }
                    } catch (ClassNotFoundException e) {
                        throwException(e.getMessage());
                        return false;
                    }
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

    /**
     * Found at:
     * https://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection
     *
     * @return all classes in the given package
     */
    private Set<BeanDefinition> loadBeanDefinitionsClassesInPackage(String packagePath, Pattern classNameFilterPattern) {
        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new RegexPatternTypeFilter(classNameFilterPattern));
        final Set<BeanDefinition> classes = provider.findCandidateComponents(packagePath);
        return classes;
    }

    private Class loadClass(BeanDefinition beanDefinition) throws ClassNotFoundException {
        return Class.forName(beanDefinition.getBeanClassName());
    }

    private Boolean implementsInterface(Class[] interfaces, Class searchedInterface) {
        if (interfaces.length > 0) {
            for (Class iteratedInterface : interfaces) {
                if (compareInterfaces(iteratedInterface, searchedInterface))
                    return true;
            }
        }
        return false;
    }

    private Boolean compareInterfaces(Class source, Class compared) {
        return source.getName().equals(compared.getName());
    }

    private void throwException(Exception e) {
        if (throwExceptions)
            throw new FatalBeanException(e.getMessage());
    }

    private void throwException(String message) {
        if (throwExceptions)
            throw new FatalBeanException(message);
    }

    private Boolean hasModifier(Class source, int modifier) {
        return (source.getModifiers() & modifier) == modifier;
    }

}
