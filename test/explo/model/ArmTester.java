package explo.model;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Test;
import explo.util.Ser;


public class ArmTester {
	
	private String filePath = "test/arm.ser";

	@Test
	public void testArm() throws IOException, ClassNotFoundException {
		// create Arm
		Number[] featuresCON = new Number[2];
		String[] featuresNOM = new String[2];
		User u = new User(featuresCON, featuresNOM);
		Option o = Option.D;
		Arm a = new Arm(u, o);
		
		Ser.save(a, filePath);
		a = (Arm) Ser.load(filePath);
		
		assertTrue(a.o.equals(o));
	}
	
	@After
	public void tearDown() throws Exception {
		new File(filePath).delete();
	}

}
