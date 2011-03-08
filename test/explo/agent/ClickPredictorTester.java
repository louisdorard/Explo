package explo.agent;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Test;

import explo.util.Ser;

public class ClickPredictorTester {

	private String filePath = "test/cp.ser";

	@Test
	public void testSer() throws IOException {
		ClickPredictor cp = new ClickPredictor();
		Ser.save(cp, filePath );
	}
	
	//@Test
	public void testChooseOption() {
		fail("Not yet implemented");
	}

	//@Test
	public void testLearn() {
		fail("Not yet implemented");
	}
	
	@After
	public void tearDown() throws Exception {
		new File(filePath).delete();
	}

}
