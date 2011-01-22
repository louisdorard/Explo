package explo.control;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import explo.agent.ClickPredictor;
import explo.agent.ClickPredictor_Impl;
import explo.control.Environment;
import explo.model.Arm;
import explo.model.Batch;


public class EnvironmentTester {
	
	public static String dataPath = "data"; // data files are in the current directory
	public static Environment e;
	public static ClickPredictor cp;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		e = new Environment(dataPath);
		cp = new ClickPredictor_Impl();
	}
	
	@Test
	public void testGetNewBatch() throws NumberFormatException, IOException, ParseException {
		System.out.println("### \t testGetNewBatch");
		Batch b;
		int c = 0;
		while (e.hasNext()) {
			b = e.getNewBatch();
			c += b.size();
			// System.out.println(b);
		}
		assertTrue(c==71); // should have read 71 lines of data: 69 lines in data.0.csv, minus the first line, plus 4 lines in data.1.csv, minus the first line 
	}
	
	@Test
	public void testPlay() throws NumberFormatException, IOException, ParseException {
		System.out.println("### \t testPlay");
		Batch b;
		int i;
		Arm a;
		while (e.hasNext()) {
			b = e.getNewBatch();
			i = cp.choose(b);
			a = b.getArm(i);
			cp.learn(a, e.play(a));
		}
		System.out.println("Total Reward = " + e.getR());
	}
	
}
