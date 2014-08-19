package utils;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.util.Arrays;

/**
 * PartFactory Tester.
 *
 * @author <Authors name>
 * @since <pre>08/19/2014</pre>
 * @version 1.0
 */
public class PartFactoryTest extends TestCase {
    public PartFactoryTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }


	public void testPartFactory () {

		byte[] asd = "asdasdasdasd".getBytes();

		byte[] result = PartFactory.PartBuilder("test","test.jpg","asd",asd,true);

		String re = new String(result);

		System.out.println(re);
	}


    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite(PartFactoryTest.class);
    }
}
