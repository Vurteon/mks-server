package model.index;

import beans.index.UserAccountBean;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

/**
 * LoginUser Tester.
 *
 * @author <Authors name>
 * @since <pre>08/01/2014</pre>
 * @version 1.0
 */
public class LoginUserTest extends TestCase {
    public LoginUserTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSetSession() throws Exception {


	    UserAccountBean userAccountBean = new UserAccountBean();
	    userAccountBean.setEmail("test@test.com");
	    userAccountBean.setPassword("123456");

	    LoginUser.setSession(userAccountBean,new HttpSession() {
		    @Override
		    public long getCreationTime() {
			    return 0;
		    }

		    @Override
		    public String getId() {
			    return null;
		    }

		    @Override
		    public long getLastAccessedTime() {
			    return 0;
		    }

		    @Override
		    public ServletContext getServletContext() {
			    return null;
		    }

		    @Override
		    public void setMaxInactiveInterval(int i) {

		    }

		    @Override
		    public int getMaxInactiveInterval() {
			    return 0;
		    }

		    @Override
		    public HttpSessionContext getSessionContext() {
			    return null;
		    }

		    @Override
		    public Object getAttribute(String s) {
			    return null;
		    }

		    @Override
		    public Object getValue(String s) {
			    return null;
		    }

		    @Override
		    public Enumeration<String> getAttributeNames() {
			    return null;
		    }

		    @Override
		    public String[] getValueNames() {
			    return new String[0];
		    }

		    @Override
		    public void setAttribute(String s, Object o) {

		    }

		    @Override
		    public void putValue(String s, Object o) {

		    }

		    @Override
		    public void removeAttribute(String s) {

		    }

		    @Override
		    public void removeValue(String s) {

		    }

		    @Override
		    public void invalidate() {

		    }

		    @Override
		    public boolean isNew() {
			    return false;
		    }
	    });



        //TODO: Test goes here...
    }

    public static Test suite() {
        return new TestSuite(LoginUserTest.class);
    }
}
