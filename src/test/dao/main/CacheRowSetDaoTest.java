package dao.main;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;

/**
 * CacheRowSetDao Tester.
 *
 * @author <Authors name>
 * @since <pre>08/22/2014</pre>
 * @version 1.0
 */
public class CacheRowSetDaoTest extends TestCase {
    public CacheRowSetDaoTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

	public void testCacheRowSetDao() {
		try {
			CacheRowSetDao.buildNewCacheRowSet();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}



    public static Test suite() {
        return new TestSuite(CacheRowSetDaoTest.class);
    }
}
