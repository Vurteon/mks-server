package test.dao.index;

import beans.index.RegisterInfoBean;
import dao.index.RegisteDao;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

/**
 * RegisteDao Tester.
 *
 * @author <Authors name>
 * @since <pre>07/28/2014</pre>
 * @version 1.0
 */
public class RegisteDaoTest extends TestCase {
    public RegisteDaoTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetUser() throws Exception {
        //TODO: Test goes here...
	    String r = RegisteDao.getUser("test");
	    System.out.println(r);


	    RegisterInfoBean registerInfoBean = new RegisterInfoBean();

		registerInfoBean.setName("康乐");
	    registerInfoBean.setEmail("1196139850@qq.com");
	    registerInfoBean.setPassword("kangLE520CJqaz?2");
	    RegisteDao.recordUser(registerInfoBean);
    }

    public static Test suite() {
        return new TestSuite(RegisteDaoTest.class);
    }
}
