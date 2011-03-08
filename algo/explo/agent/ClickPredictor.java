package explo.agent;

import java.util.HashMap;
import java.io.Serializable;

import org.apache.log4j.Logger;

import explo.model.Arm;
import explo.model.Batch;


/**
 * ClickPredictors are used to predict which arm in a given batch is most likely to yield a reward of 1. They typically implement a learning algorithm.
 * The class is Serializable so that a ClickPredictor instance can be saved in a file and its learnings can be reused later. 
 * @author Louis Dorard, University College London
 */
public class ClickPredictor implements Serializable {

	private static org.apache.log4j.Logger log = Logger.getLogger(ClickPredictor.class);
	private static final long serialVersionUID = 1L;
	/**
	 * This can be used for storing training data.
	 */
	private HashMap<Arm,Integer> training;
	
	/**
	 * Default constructor; initialises training to an empty HashMap.
	 */
	public ClickPredictor() {
		log.info("Now using CP-Random 1.0 by Louis Dorard.");
		training = new HashMap<Arm, Integer>();
	}
	
	/**
	 * Copy constructor: copies all fields of the class.
	 * @param cp - original click predictor to copy from
	 */
	public ClickPredictor(ClickPredictor cp) {
		training = cp.training;
		
		// TODO adapt to the structure of your click predictor
		
	}
	
	/**
	 * Learns from feedback received from the environment.
	 * @param a - arm that was played
	 * @param reward that was observed for a
	 */
	public void learn(Arm a, Integer reward) {
		
		// TODO implement your own code here
		
		/*
		 * Sample code:
		 * here we don't really "learn" but we just memorise the training examples
		 */
		if (Runtime.getRuntime().freeMemory()>2048) { // if we have more than 2K left in memory
			training.put(a, reward);
			log.info("Added arm-reward to training set.");
		} else {
			log.info("Not enough memory to add to training set.");
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
