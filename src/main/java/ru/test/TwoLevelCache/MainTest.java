package ru.test.TwoLevelCache;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class MainTest {
    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("appContext.xml");


        RequestThreads threads = context.getBean("ObjectsThreads", RequestThreads.class);
        threads.start();
        ResponseThreads responseObject = context.getBean("ResponseObject",ResponseThreads.class);
        responseObject.start();
    }
}
