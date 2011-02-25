package explo.agent;

import java.util.HashMap;

import explo.model.Arm;
import explo.model.Batch;
//import explo.util.TimeKeeper;

/**
 * Implementation of a click predictor that randomly selects arms to play.
 * @author Louis Dorard, University College London
 */
public class ClickPredictor_Impl implements ClickPredictor {
	
	private static final long serialVersionUID = 1L; // first version of the ClickPredictor object
	// TODO: test when incrementing this
	
	private HashMap<Arm,Integer> training = new HashMap<Arm, Integer>();
	
	public void learn(Arm a, Integer reward) {
		// here we don't learn but just memorise the training examples
		if (Runtime.getRuntime().freeMemory()>2048) // if we have more than 2K left in memory
			training.put(a, reward);
	}

	public int choose(Batch b) {
		// here we choose an index randomly
		
		// the following two lines are used to test what happens when taking longer than allowed
		//TimeKeeper.calibrate();
		//TimeKeeper.calibrate();
		return b.getRandomIndex();
	}

}
