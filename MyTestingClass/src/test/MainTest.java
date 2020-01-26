package test;

import testing.AfterSuite;
import testing.BeforeSuite;
import testing.MyTestingClass;
import testing.Test;

public class MainTest {
    public static void main(String[] args) {
        MyTestingClass.start("test.MainTest");
    }
    @Test
    public void m1_2(){
        System.out.println(1.2);
    }
    @Test(priority = 10)
    public void m10(){
        System.out.println(10.1);
    }
    @BeforeSuite
    public void before1() {
        System.out.println("before1");
    }
    //@BeforeSuite
    public void before2() {
        System.out.println("before2");
    }
    @Test
    public void m1(){
        System.out.println(1.1);
    }
    @Test(priority = 2)
    public void m2(){
        System.out.println(2.1);
    }
    @AfterSuite
    public void after(){
        System.out.println("after");
    }
    @Test(priority = 5)
    public void m5(){
        System.out.println(5.1);
    }
    //@Test(priority = 11)
    public void m11(){
        System.out.println(11.1);
    }


}
