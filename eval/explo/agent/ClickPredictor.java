package explo.agent;

import java.io.Serializable;

import explo.model.*;

/**
 * The ClickPredictor interface is used to predict which arm in a given batch is most likely to yield a reward of 1. It typically implements a learning algorithm.
 * The class is Serializable so that a ClickPredictor instance can be saved in a file and its learnings can be reused later. 
 * @author Louis Dorard, University College London
 */
public interface ClickPredictor extends Serializable{
	
	/**
	 * Returns the index of the Arm in the Batch that is most likely to yield a click (indices start at 0). The arm is to be played in the environment.
	 * @param b - batch of arms
	 * @return index of the chosen arm
	 */
	int choose(Batch b);
	
	/**
	 * Learns from feedback received from the environment
	 * @param a - arm that was played
	 * @param reward that was observed for a
	 */
	void learn(Arm a, Integer reward);
	
}
