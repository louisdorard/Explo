package explo.agent;

import java.util.HashMap;
import java.io.Serializable;

import explo.model.Arm;
import explo.model.Batch;


/**
 * ClickPredictors are used to predict which arm in a given batch is most likely to yield a reward of 1. They typically implement a learning algorithm.
 * The class is Serializable so that a ClickPredictor instance can be saved in a file and its learnings can be reused later. 
 * @author Louis Dorard, University College London
 */
public class ClickPredictor implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * This can be used for storing training data. You may change the type of this object depending on your needs.
	 */
	private HashMap<Arm,Integer> training;
	
	/**
	 * Default constructor.
	 */
	public ClickPredictor() {
		
		training = new HashMap<Arm, Integer>();
		
	}
	
	/**
	 * Copy constructor: copies all fields of the class.
	 * @param cp - original click predictor to copy from
	 */
	public ClickPredictor(ClickPredictor cp) {
		
		training = cp.training;
		
		// TODO adapt to the structure of your click predictor so that all fields are copied
		
	}
	
	/**
	 * Learns from feedback received from the environment.
	 * @param a - arm that was played
	 * @param reward that was observed for a
	 */
	public void learn(Arm a, Integer reward) {
		
		// TODO implement your own code here
		// Be careful not to use more than half of the total available memory (explo.control.Run needs to keep a copy of your ClickPredictor instance).
		// You may want to call the garbage collector (System.gc();) for accurate memory usage reporting in the logs, but it might be very expensive if called at each iteration...
		
		/*
		 * Sample code:
		 * here we don't really "learn" but we just memorise the training examples
		 */
		Runtime runtime = Runtime.getRuntime();
		long free = runtime.freeMemory();
		long total = runtime.totalMemory();
		long used = total - free;
		training.put(a, reward);
		if (used > 0.9*total/2) { // making sure we're not using more than 90% of half the total memory: you may want to leave some breathing space for running your computations
			training.remove(training.keySet().iterator().next());
		}
		
	}
	
	/**
	 * Returns the index of the Arm in the Batch that is most likely to yield a click (indices start at 0). The arm is to be played in the environment.
	 * @param b - batch of arms
	 * @return index of the chosen arm
	 */
	public int choose(Batch b) {
		
		// TODO implement your own code here
		
		/*
		 * Sample code:
		 * here we choose an index randomly
		 */
		return b.getRandomIndex();
		
	}
	
}
