/**
 * 
 */
package explo.control;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Louis Dorard, University College London
 *
 */
public class RunTester {

	public static String dataPath = "data";
	
	@Before
	public void setUp() throws Exception {
		delete();
	}

	@Test
	public void testRun() throws NumberFormatException, IOException, ParseException, ClassNotFoundException {
		String[] args = new String[2];
		args[0] = dataPath;
		args[1] = "5";
		Run.main(args);
	}
	
	@After
	public void tearDown() throws Exception {
		delete();
	}
	
	private void delete() {
		new File("test/cp.ser").delete();
	}

}
