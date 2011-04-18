package explo.control;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import explo.agent.ClickPredictor;
import explo.control.EnvironmentRandom;
import explo.model.Arm;
import explo.model.Batch;


public class EnvironmentRandomTester {
	
	public static String dataPath = "data"; // data files are in the current directory
	public static EnvironmentRandom e;
	public static ClickPredictor cp;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		e = new EnvironmentRandom(5000);
		cp = new ClickPredictor();
	}
	
	@Test
	public void testPlay() throws NumberFormatException, IOException, ParseException, InstantiationException, IllegalAccessException {
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
