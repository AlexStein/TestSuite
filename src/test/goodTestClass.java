package test;

import main.AfterSuite;
import main.BeforeSuite;
import main.Test;

public class goodTestClass {

    private static Object o;

    @BeforeSuite
    public static void beforeTest() {
        o = new Object();
        System.out.println("@BeforeSuite выполнен");
    }

    @Test(priority = 5)
    public static void testToString() {
        if (!o.getClass().getSimpleName().startsWith("Object")) {
            throw new AssertionError("Не Оbject");
        }
    }

    @Test(priority = 10)
    public static void testNotNull() {
        if (o == null) {
            throw new AssertionError("Значение null");
        }
    }

    @Test // приоритет по умолчанию 1
    public static void testHashCode() {
        if (o.hashCode() == 0) {
            throw new AssertionError("Значение хэшкода 0!");
        }
    }

    @AfterSuite
    public static void afterTest() {
        System.out.println("@AfterSuite выполнен");
        o = null;
    }

}
