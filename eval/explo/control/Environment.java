package explo.control;

import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import org.apache.log4j.Logger;

import explo.model.*;
import explo.util.CSVReader;

/**
 * @author Louis Dorard, University College London
 * As in the bandit terminology, the Environment is the object to be called to know which arms are available (here with 'getNewBatch()') and to play an arm.
 * This is done by calling the reward function, defined here as a list of arm-reward pairs (implemented in the 'data' variable), and by modifying the number of plays and the cumulated reward.
 * All fields are private, and all methods are protected so that they can only be called from the explo.control package.
 */
final class Environment {
	
	private static org.apache.log4j.Logger log = Logger.getLogger(Run.class);
	private HashMap<Arm,Integer> data = new HashMap<Arm, Integer>();
	private Iterator<Arm> it; // iterator which is used to know where to take data points from in order to evaluate the click predictor implementation
	private int R; // cumulated reward
	private int nplays; // number of plays
	
	protected int getR() {
		return R;
	}
	
	protected int getNplays() {
		return nplays;
	}

	/**
	 * Constructs an Environment from the data file in CSV format that contains the mapping (User,Option) to Reward.
	 * @param filepath: path to the CSV file, as a String
	 * @throws IOException
	 * @throws ParseException 
	 */
	protected Environment(String filepath) throws IOException, ParseException{
		
		// Import data from CSV file
		CSVReader reader = new CSVReader(new FileReader(filepath));
	    String [] nextLine;
	    Number[] featuresCON; // user features (continuous)
	    String[] featuresNOM; // user features (nominal)
	    User u; // user to be added to data
        Option o; // option...
        Integer r; // reward...
        nextLine = reader.readNext(); // discard first line (used to give table headers)
	    while ((nextLine = reader.readNext()) != null) {
	    	// Import current line
	        // nextLine[] is an array of values from the line
	    	featuresCON = new Number[99];
	    	featuresNOM = new String[20];
	    	// the first 3 entries of nextLine can be discarded (visitorID, sessionID and timestamp)
	    	for (int i=0; i<=98; i++)
	    		featuresCON[i] = parseCON(nextLine[i+3]);
	    	for (int i=217; i<=236; i++)
	    		featuresNOM[i-217] = readNull(nextLine[i-116]);
	        u = new User(featuresCON, featuresNOM);
	        o = Option.values()[Integer.parseInt(nextLine[122])-1];
	        r = Integer.parseInt(nextLine[123]);
	        data.put(new Arm(u,o), r);
	    }
	    
	    // Initialise other fields
	    it = data.keySet().iterator();
	    R = 0;
	    nplays = 0;
	    
	}
	
	/**
	 * @param string
	 * @return the number parsed from the string, or null if the string was "null"
	 * @throws ParseException
	 */
	private Number parseCON(String string) throws ParseException {
		NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH); // used to parse entries (strings) and get numbers from strings
		if (readNull(string)==null)
			return null;
		else
			return nf.parse(string);
	}

	/**
	 * @param string
	 * @return the string or a null pointer if the value of the string is "null"
	 */
	private String readNull(String string) {
		if (string.equals("null"))
			return null;
		else
			return string;
	}

	/**
	 * Play a given arm, update number of plays and cumulative reward, and return reward for this arm.
	 * @param a
	 * @return reward for a
	 */
	protected Integer play(Arm a) {
		int r = data.get(a).intValue(); // get reward
		R = R + r;
		nplays++;
		log.debug("Played and got a reward of " + r);
		return r; // return reward
	}

	/**
	 * Informs whether there are any data points left from the data set, that haven't been used yet.
	 * @return
	 */
	protected boolean hasBatchesLeft() {
		return it.hasNext();
	}
	
	/**
	 * If there are data points left, this takes at most explo.model.Batch.BATCH_MAX_SIZE of them and returns them as a Batch.
	 * @return
	 */
	protected Batch getNewBatch() {
		Batch b = new Batch();
		for (int i=0; i<Batch.BATCH_MAX_SIZE; i++)
			if (it.hasNext())
				b.add(it.next());
		return b;
	}
	
	/**
	 * @return total number of data points in the dataset, i.e. total number of arms
	 */
	protected int size(){
		return data.size();
	}
	
}
