package dao.cache;

import beans.status.PhotoDesBean;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import model.status.StatusRowSetManager;

import java.sql.SQLException;
import java.util.Random;

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

			PhotoDesBean photoDesBean = new PhotoDesBean();

			for (int i = 0; i < 10; i++) {
				photoDesBean.setID(new Random().nextInt());

				//photoDesBean.setPhotoLocation("asd");
				photoDesBean.setViewPhotoPath("aaa");
				photoDesBean.setDetailPhotoPath("aa2qwed");

				StatusRowSetManager.insertStatus(photoDesBean);
			}


		} catch (SQLException e) {
			e.printStackTrace();
		}
	}



    public static Test suite() {
        return new TestSuite(CacheRowSetDaoTest.class);
    }
}
