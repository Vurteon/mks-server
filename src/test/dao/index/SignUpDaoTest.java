package test.dao.index;

import beans.index.SignUpInfoBean;
import dao.account.SignUpDao;
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
public class SignUpDaoTest extends TestCase {
    public SignUpDaoTest(String name) {
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
	    String r = SignUpDao.getUserEmail("test");
	    System.out.println(r);


	    SignUpInfoBean signUpInfoBean = new SignUpInfoBean();

		signUpInfoBean.setName("康乐");
	    signUpInfoBean.setUserAccount("1196139850@qq.com");
	    signUpInfoBean.setPassword("kangLE520CJqaz?2");
	    SignUpDao.recordUser(signUpInfoBean);
    }

    public static Test suite() {
        return new TestSuite(SignUpDaoTest.class);
    }
}
