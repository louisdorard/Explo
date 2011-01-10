package explo.agent;

import java.util.HashMap;

import explo.model.Arm;
import explo.model.Batch;
import explo.util.TimeKeeper;

public class ClickPredictor_Impl implements ClickPredictor {
	
	private static final long serialVersionUID = 1L; // first version of the ClickPredictor object
	
	HashMap<Arm,Integer> training = new HashMap<Arm, Integer>();
	
	public void learn(Arm a, Integer reward) {
		// here we don't learn but just memorise the training examples
		training.put(a, reward);
	}

	public int choose(Batch b) {
		// here we choose an index randomly
		TimeKeeper.calibrate(); // this is used to test what happens when taking longer than allowed
		TimeKeeper.calibrate();
		return b.getRandomIndex();
	}

}
