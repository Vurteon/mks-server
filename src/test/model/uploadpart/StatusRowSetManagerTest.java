package model.status.uploadpart;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import model.status.StatusRowSetManager;
import utils.json.JSONArray;

import java.util.HashSet;

/**
 * StatusRowSetManger Tester.
 *
 * @author <Authors name>
 * @since <pre>09/06/2014</pre>
 * @version 1.0
 */
public class StatusRowSetManagerTest extends TestCase {
    public StatusRowSetManagerTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSetGetStatusRowSet() throws Exception {
        //TODO: Test goes here...

	    HashSet<Integer> hashSet = new HashSet<Integer>();

	    hashSet.add(2);
	    hashSet.add(3);
	    hashSet.add(15);
	    hashSet.add(1785);
	    hashSet.add(89);
	    hashSet.add(11);


	    JSONArray jsonArray = StatusRowSetManager.selectStatus(hashSet, 61, false);


		int i = 0;

	    while (i < jsonArray.length()) {
		    System.out.println(jsonArray.get(i));
		    i++;
	    }


    }

    public void testGetRs_id() throws Exception {
        //TODO: Test goes here...
    }

    public static Test suite() {
        return new TestSuite(StatusRowSetManagerTest.class);
    }
}
