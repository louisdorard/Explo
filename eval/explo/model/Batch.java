package explo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A batch contains up to BATCH_MAX_SIZE arms.
 * @author Louis Dorard, University College London
 */
final public class Batch {
	
	public static final int BATCH_MAX_SIZE = 6;
	
	private static Random rgen = new Random(); // random generator used to select arms at random in a batch
	
	private List<Arm> arms = new ArrayList<Arm>();

	public Batch() {
		// initially, the batch is empty
	}

	/**
	 * Arms can be added to the batch owing to this method.
	 * @param a
	 */
	public void add(Arm a){
		arms.add(a); 
	}
	
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

	public Arm getArm(int i) {
		return arms.get(i);
	}
	
	public int getRandomIndex() {
		return rgen.nextInt(arms.size()-1);
	}

}
