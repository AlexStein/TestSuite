package main;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;

import test.*;

public class TestSuite {

    public static void main(String[] args) {

        // Bad, Good & Ugly
        try {
            System.out.printf("Тестирование класса с некорректными аннотациями %s...\n", badTestClass.class.getName());
            start(badTestClass.class);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.printf("Тестирование класса %s завершено\n\n", badTestClass.class.getName());
        }

        try {
            System.out.printf("Тестирование класса с корректными аннотациями %s...\n", goodTestClass.class.getName());
            start(goodTestClass.class);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.printf("Тестирование класса %s завершено\n\n", goodTestClass.class.getName());
        }

        try {
            System.out.printf("Тестирование класса без аннотаций %s...\n", uglyTestClass.class.getName());
            start(uglyTestClass.class);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.printf("Тестирование класса %s завершено\n\n", uglyTestClass.class.getName());
        }

    }

    public static void start(Class testClass) throws IllegalAccessException, InvocationTargetException, RuntimeException, NoSuchMethodException, NoSuchFieldException {

        Method[] methods = testClass.getDeclaredMethods();

        Method beforeSuite = null;
        Method afterSuite = null;

        // Проверим наличие аннотированных методов before и after
        // А так же что они в единственном экземпляре в классе
        int a = 0;
        int b = 0;

        for (Method m : methods) {
            if (m.isAnnotationPresent(BeforeSuite.class)) {
                beforeSuite = m;
                b++;
            }

            if (m.isAnnotationPresent(AfterSuite.class)) {
                afterSuite = m;
                a++;
            }
        }

        if (a > 1 || b > 1) {
            throw new RuntimeException("Некорректное количество анннотаций Before или After");
        }

        // Выполняем метод before
        if (beforeSuite != null) {
            beforeSuite.invoke(null);
        }

        // Получим все тестовые методы и отсортируем по приоритетам от 10 до 1
        Comparator<Method> prioritySort = new Comparator<Method>() {
            @Override
            public int compare(Method m1, Method m2) {
                return m1.getAnnotation(Test.class).priority() - m2.getAnnotation(Test.class).priority();
            }
        };

        ArrayList<Method> testMethods = new ArrayList<>();
        for (Method m : methods) {
            if (m.isAnnotationPresent(Test.class)) {
                testMethods.add(m);
            }
        }
        testMethods.sort(prioritySort.reversed());

        if (testMethods.size() > 0) {
            Field field = testClass.getDeclaredField("o");
            field.setAccessible(true);
            Object testObj = field.get(null);
            for (Method m : testMethods) {
                System.out.printf("@Test %d.: ", m.getAnnotation(Test.class).priority());
                try {
                    m.invoke(testObj);
                } catch (Exception e) {
                    System.out.printf("Тест метода %s: провален. Причина: %s\n",
                            m.getName(), e.getMessage());
                    continue;
                }

                System.out.printf("Тест метода %s: ОК!\n", m.getName());
            }
        }

        // Выполняем метод after
        if (afterSuite != null) {
            afterSuite.invoke(null);
        }
    }
}
