package testing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class MyTestingClass {
    public static void start (Class TestClass) {
        Method[] arrayMethods = TestClass.getDeclaredMethods();
        Method before = null;
        Method after = null;
        List<Method> tests = new ArrayList<>();

        //проверка на исключения
        boolean isNotValidBefore = false;
        boolean isNotValidAfter = false;
        boolean isNotValidPriority = false;
        for (Method m: arrayMethods) {
            if (m.isAnnotationPresent(BeforeSuite.class)) {
                if (before == null) {
                    before = m;
                } else isNotValidBefore = true;
            }
            if (m.isAnnotationPresent(AfterSuite.class)) {
                if (after == null) {
                    after = m;
                } else isNotValidAfter = true;
            }
            if (m.isAnnotationPresent(Test.class)) {
                int priority = m.getAnnotation(Test.class).priority();
                if (priority > 0 && priority <= 10) {
                    tests.add(m);
                } else isNotValidPriority = true;
            }
        }
        if (isNotValidAfter||isNotValidBefore||isNotValidPriority) {
            throw new RuntimeException(String.format("Ошибка%s%s%s!",
                    isNotValidBefore ? ", несколько аннотаций BeforeSuite" : "",
                    isNotValidAfter ? ", несколько аннотаций AfterSuite" : "",
                    isNotValidPriority ? ", указан неверный уровень приоритета в аннотации Test" : ""));
        }

        //основная часть
        tests.sort(new Comparator<Method>() {
                       @Override
                       public int compare(Method o1, Method o2) {
                           return o1.getAnnotation(Test.class).priority() -
                                   o2.getAnnotation(Test.class).priority();
                       }
        });
        try {
            if (before!=null) {
                    before.invoke(TestClass.newInstance());
            }
            for (Method m: tests) {
                m.invoke(TestClass.newInstance());
            }
            if (after!=null) {
                after.invoke(TestClass.newInstance());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void start (String testClassName) {
        try {
            start(Class.forName(testClassName));
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
    }
}