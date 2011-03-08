package explo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A batch contains up to BATCH_MAX_SIZE arms.
 * @author Louis Dorard, University College London
 */
final public class Batch {
	
	/**
	 * Maximum number of arms in any given batch.
	 */
	public static final int BATCH_MAX_SIZE = 6;
	
	private static Random rgen = new Random(); // random generator used to select arms at random in a batch
	
	private List<Arm> arms = new ArrayList<Arm>();

	/**
	 * Constructor.
	 */
	public Batch() {
		// initially, the batch is empty
	}

	/**
	 * Adds an arm to the current batch.
	 * @param a - arm to be added
	 */
	public void add(Arm a){
		arms.add(a); 
	}
	
	/**
	 * Returns the size of the batch as the number of arms it contains (lesser than or equal to BATCH_MAX_SIZE).
	 * @return number of arms
	 */
	public int size() {
		return arms.size();
	}
	
	public String toString(){
		String s = "";
		for(int i=0; i<arms.size(); i++){
			s = s + "[Entry " + i + "] \t " + getArm(i) + "\n";
		}
		return s;
	}

	/**
	 * Gets the ith arm in the current batch.
	 * @param i - index of the arm to be returned
	 * @return ith arm
	 */
	public Arm getArm(int i) {
		return arms.get(i);
	}
	
	/**
	 * Returns a randomly chosen arm index.
	 * @return index between 0 and the size of the batch (excluded)
	 */
	public int getRandomIndex() {
		return rgen.nextInt(arms.size());
	}
	
	/**
	 * Returns a randomly chosen arm.
	 * @return arm in the batch
	 */
	public Arm getRandomArm() {
		return getArm(getRandomIndex());
	}
	
}
