package dao.index;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import javax.sql.rowset.CachedRowSet;

/**
 * LoginDao Tester.
 *
 * @author <Authors name>
 * @since <pre>08/01/2014</pre>
 * @version 1.0
 */
public class LoginDaoTest extends TestCase {
    public LoginDaoTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetID() throws Exception {
        //TODO: Test goes here...
    }

    public void testGetUserSettings() throws Exception {
        //TODO: Test goes here...


	    CachedRowSet cachedRowSet = LoginDao.getContactersID(27);

	    while (cachedRowSet.next()){
		    long ID = cachedRowSet.getLong("friend");
		    System.out.println(ID);
	    }

//		String bg_pic = cachedRowSet.getString();






    }

    public static Test suite() {
        return new TestSuite(LoginDaoTest.class);
    }
}
