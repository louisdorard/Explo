package explo.agent;

import java.io.Serializable;

import explo.model.*;

/**
 * A click predictor is used to predict which arm in a given batch is most likely to yield a reward of 1. It typically implements a learning algorithm.
 * The class is Serializable so that a ClickPredictor instance can be saved in a file and its learnings can be reused later. 
 * @author Louis Dorard, University College London
 */
public interface ClickPredictor extends Serializable{
	
	/**
	 * Return the index of the Arm in the Batch that is most likely to yield a click (indices start at 0)
	 * @param b
	 * @return index
	 */
	int choose(Batch b);
	
	/**
	 * Learn from feedback received from the environment
	 * @param a
	 * @param reward
	 */
	void learn(Arm a, Integer reward);
	
}
