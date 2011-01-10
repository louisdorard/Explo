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
	
	public static String dataPath = "data.csv";
	public static Environment e;
	public static ClickPredictor cp;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		e = new Environment(dataPath);
		cp = new ClickPredictor_Impl();
		assertTrue(e.size()>0);
	}
	
	@Test
	public void testGetNewBatch() throws NumberFormatException, IOException, ParseException {
		System.out.println("### \t testGetNewBatch");
		Batch b;
		while (e.hasBatchesLeft()) {
			b = e.getNewBatch();
			System.out.println(b);
		}
	}
	
	@Test
	public void testPlay() throws NumberFormatException, IOException, ParseException {
		System.out.println("### \t testPlay");
		Batch b;
		int i;
		Arm a;
		while (e.hasBatchesLeft()) {
			b = e.getNewBatch();
			i = cp.choose(b);
			a = b.getArm(i);
			cp.learn(a, e.play(a));
		}
		System.out.println("Total Reward = " + e.getR());
	}
	
}
