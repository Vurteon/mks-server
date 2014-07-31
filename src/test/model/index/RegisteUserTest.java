package model.index;

import beans.index.RegisterInfoBean;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

/**
 * RegisteUser Tester.
 *
 * @author <Authors name>
 * @since <pre>07/29/2014</pre>
 * @version 1.0
 */
public class RegisteUserTest extends TestCase {
    public RegisteUserTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite(RegisteUserTest.class);
    }

	public void testRegisteUser() throws Exception {

		RegisterInfoBean registerInfoBean = new RegisterInfoBean();

		registerInfoBean.setName("j林");
		registerInfoBean.setEmail("27982a2256@qq.com");
		registerInfoBean.setPassword("123456");

		System.out.println(RegisteUser.registeUser(registerInfoBean));


	}


}
