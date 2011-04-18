package explo.control;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.apache.log4j.Logger;

import explo.model.*;

/**
 * Similar to explo.control.Environment but generates random data points instead of reading from data files.
 * @author Louis Dorard, University College London
 */
final class EnvironmentRandom {
	
	private static org.apache.log4j.Logger log = Logger.getLogger(EnvironmentRandom.class);
	private HashMap<Arm,Integer> data = new HashMap<Arm, Integer>(); // contains the data from the last batch we read from the data files
	private int R = 0; // cumulative reward
	private int nplays = 0; // number of plays
	private int nmax; // maximum number of batches to be seen

	/**
	 * Constructs an Environment from a directory containing data files in CSV format. These files are used to define the mapping (User,Option) to Reward.
	 * @param dirpath - path to the directory of CSV files, as a String
	 * @throws IOException
	 * @throws ParseException 
	 */
	protected EnvironmentRandom(int nmax) throws IOException, ParseException{
		this.nmax = nmax;
	}
	
	/**
	 * Gets the cumulative reward
	 * @return cumulative reward
	 */
	protected int getR() {
		return R;
	}
	
	/**
	 * Gets the number of plays
	 * @return number of plays
	 */
	protected int getNplays() {
		return nplays;
	}
	
	/**
	 * Indicates whether there is a next data point to get from the data files
	 * @return true if there is a next data point, false otherwise
	 * @throws IOException
	 */
	protected boolean hasNext() throws IOException {
		if (nplays>=nmax*Batch.MAX_SIZE)
			return false;
		else
			return true; // the next line is either in the current file (children[it]), or in a new file, children[it+1], if it exists
	}
	
	/**
	 * Plays a given arm, updates the number of plays and cumulative reward, and returns the reward observed for this arm.
	 * @param a - given arm to play
	 * @return observed reward for a
	 */
	protected Integer play(Arm a) {
		int r = data.get(a).intValue(); // get reward; we will get a null exception if a is not present in the data
		R = R + r;
		nplays++;
		log.debug("Played and got a reward of " + r);
		return r; // return reward
	}
	
	/**
	 * Tries to read at most explo.model.Batch.BATCH_MAX_SIZE data points, returns them as a Batch and saves them in the data field of the environment.
	 * @return new batch of data points
	 * @throws IOException - if can't read data
	 * @throws ParseException - ditto
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	protected Batch getNewBatch() throws IOException, ParseException, InstantiationException, IllegalAccessException {
		
		// 1. data must contain the (arm, reward) pairs for the new batch; we read these from the CSV reader; data will be used later, in play(), when we have decided which arm to play in that batch
		
		data.clear(); // purge data: we don't need to remember past data
		while (hasNext() && data.size()<Batch.MAX_SIZE) {
	    	loadLine(); // load this new line into the data object
	    }
		
		// 2. we prepare a batch object to return; it is built by adding all the arms from data
		
		Batch b = new Batch();
		Iterator<Arm> it = data.keySet().iterator();
		while (it.hasNext())
			b.add(it.next());
		return b;
		
	}
	
	/**
	 * Loads the contents of a line read from CSV file, into data
	 * @param nextLine
	 * @throws ParseException
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private void loadLine() throws ParseException, InstantiationException, IllegalAccessException {

	    Number[] featuresCON = new Number[99]; // user features (continuous)
	    String[] featuresNOM = new String[20]; // user features (nominal)
	    User u; // user to be added to data
        Option o; // option...
        int r; // reward...
        
        // nextLine is an array of values from the line
    	// its first 3 entries can be discarded (visitorID, sessionID and timestamp)
        // it has the following format:
        // 0 1 2     3 ... 101          102 ... 121      122 133
        //       |< featuresCON >|   |< featuresNOM >|    o   r
        
        Random rand = java.util.Random.class.newInstance();
        
    	for (int i=0; i<=98; i++)
    		featuresCON[i] = rand.nextLong();
    	for (int i=218; i<=237; i++)
    		featuresNOM[i-218] = "";
        u = new User(featuresCON, featuresNOM);
        o = Option.values()[rand.nextInt(6)];
        boolean b = rand.nextBoolean();
        if (b) r = 1; else r = 0;
        
        data.put(new Arm(u,o), r);
        
	}
	
}
