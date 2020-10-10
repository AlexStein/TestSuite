package test;

import main.AfterSuite;
import main.BeforeSuite;

public class badTestClass {

    @BeforeSuite
    public void beforeTest() {
    }

    @BeforeSuite
    public void before2Test() {
    }

    @AfterSuite
    public void afterTest() {
    }

}
