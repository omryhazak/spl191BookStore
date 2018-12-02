package bgu.spl.mics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {

    private Future fuTest;

    @Before
    public void setUp() throws Exception {
        fuTest = new Future<String> ();
    }

    @After
    public void tearDown() throws Exception {
        fuTest = null;
    }

    /**
     * testing if resolve generate a result;
     */
    @Test
    public void get() {
        fuTest.resolve("check");
        assertEquals(fuTest.get(), "check");
    }

    /**
     * positive test for resolve
     */
    @Test
    public void resolve() {
        String result = "check1";
        fuTest.resolve(result);
        assertEquals(fuTest.isDone(), true);

    }

    /**
     * positive test for isDone
     */
    @Test
    public void isDone() {
        assertFalse(fuTest.isDone());
        fuTest.resolve(3);
        assertTrue(fuTest.isDone());
    }

    @Test
    public void get1() {
        TimeUnit unit = TimeUnit.MILLISECONDS;
        fuTest.resolve("check");
        assertEquals(fuTest.get(), "check");

    }
}