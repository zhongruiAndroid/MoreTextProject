package com.example.moretext;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void asd() {
        long time=1635166800000l;
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        int i = instance.get(Calendar.HOUR_OF_DAY);
        System.out.println(i);
    }
    @Test
    public void adsd() {
       int a=1;
       int b=2;
       int d=4;
       int aa=a=b=d=9;
        System.out.println(aa);
        System.out.println(a);
        System.out.println(b);
        System.out.println(d);
    }
}