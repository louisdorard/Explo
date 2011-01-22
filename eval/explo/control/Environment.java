package explo.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import org.apache.log4j.Logger;

import explo.model.*;
import explo.util.CSVReader;
import explo.util.Zippie;

/**
 * @author Louis Dorard, University College London
 * As in the bandit terminology, the Environment is the object to be called to know which arms are available (here with 'getNewBatch()') and to play an arm.
 * This is done by calling the reward function, defined here as a list of arm-reward pairs (implemented in the 'data' variable), and by modifying the number of plays and the cumulated reward.
 * All fields are private, and all methods are protected so that they can only be called from the explo.control package.
 */
final class Environment {
	
	private static org.apache.log4j.Logger log = Logger.getLogger(Environment.class);
	private CSVReader reader;
	private HashMap<Arm,Integer> data = new HashMap<Arm, Integer>(); // contains the data from the last batch we read from the data files
	private String dirpath; // path to directory where the data files are
	private String[] datafiles; // list of data files
	private String unzipped = ""; // path where we unzip datafiles to, if necessary
	private int it = -1; // iterator for datafiles
	private int R = 0; // cumulated reward
	private int nplays = 0; // number of plays
	
	protected int getR() {
		return R;
	}
	
	protected int getNplays() {
		return nplays;
	}

	/**
	 * Constructs an Environment from a directory containing data files in CSV format; these files are used to define the mapping (User,Option) to Reward.
	 * @param dirpath: path to the directory of CSV files, as a String
	 * @throws IOException
	 * @throws ParseException 
	 */
	protected Environment(String dirpath) throws IOException, ParseException{
		
		this.dirpath = dirpath;
		
		// Get the paths to the data files contained in dirpath
		File dir = new File(dirpath);
		FilenameFilter filter = new FilenameFilter() {
			// Defines a filter to be used on the list of returned files, so that we only retain the CSV ones (and the zipped CSV files)
			// see http://www.exampledepot.com/egs/java.io/GetFiles.html
		    public boolean accept(File dir, String name) {
		        return (name.endsWith(".csv") || name.endsWith(".csv.gz"));
		    }
		};
		datafiles = dir.list(filter);
		if (datafiles == null) {
		    // Either dir does not exist or is not a directory
			datafiles[0] = dirpath; // if it's not a directory, it might be a file
		}
		
		// Initialise reader
		nextReader(); // get ready to tap into the right file in order to read data
	    
	}
	
	/**
	 * Load the contents of a line read from CSV file, into data
	 * @param nextLine
	 * @throws ParseException
	 */
	private void loadLine(String[] nextLine) throws ParseException {

	    Number[] featuresCON = new Number[99]; // user features (continuous)
	    String[] featuresNOM = new String[20]; // user features (nominal)
	    User u; // user to be added to data
        Option o; // option...
        Integer r; // reward...
        
        // nextLine is an array of values from the line
    	// its first 3 entries can be discarded (visitorID, sessionID and timestamp)
        // it has the following format:
        // 0 1 2     3 ... 101          102 ... 121      122 133
        //       |< featuresCON >|   |< featuresNOM >|    o   r
    	if (nextLine==null || nextLine.length!=124)
    		log.error("Data file doesn't have the right format");
    	for (int i=0; i<=98; i++)
    		featuresCON[i] = parseCON(nextLine[i+3]);
    	for (int i=218; i<=237; i++)
    		featuresNOM[i-218] = readNull(nextLine[i-116]);
        u = new User(featuresCON, featuresNOM);
        o = Option.values()[Integer.parseInt(nextLine[122])-1];
        r = Integer.parseInt(nextLine[123]);
        data.put(new Arm(u,o), r);
        
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
		int r = data.get(a).intValue(); // get reward; we will get a null exception if a is not present in the data
		R = R + r;
		nplays++;
		log.debug("Played and got a reward of " + r);
		return r; // return reward
	}
	
	/**
	 * If there are data points left, this takes at most explo.model.Batch.BATCH_MAX_SIZE of them, saves them in data and returns them as a Batch.
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	protected Batch getNewBatch() throws IOException, ParseException {
		
		// 1. data must contain the (arm, reward) pairs for the new batch; we read these from the CSV reader; data will be used later, in play(), when we have decided which arm to play in that batch
		
		data.clear(); // purge data: we don't need to remember past data
		String [] line; // variable to store line taken from CSV file and that contains the next data point
		while (hasNext() && data.size()<Batch.BATCH_MAX_SIZE) {
			// We haven't reached the maximum size for the batch yet, and there are data points left.
			line = nextLine();
	    	loadLine(line); // load this new line into the data object
	    }
		
		// 2. we prepare a batch object to return; it is built by adding all the arms from data
		
		Batch b = new Batch();
		Iterator<Arm> it = data.keySet().iterator();
		while (it.hasNext())
			b.add(it.next());
		return b;
		
	}
	
	
	private String[] nextLine() throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		// Next data point is either in the current CSV file, or in the next CSV file
		String[] line;
		if (reader.hasNext())
			line = reader.readNext(); // should not be null
		else { // means we've hit the end of children[it], so we have to increment "it" and load up the new file
			nextReader(); // take next file
			line = reader.readNext();
		}
		return line;
	}

	/**
	 * Change reader to a new CSVReader constructed from the  
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void nextReader() throws FileNotFoundException, IOException {
		// TODO write doc
		
		// get path to next data file
		it++;
		String filepath = this.dirpath + "/" + datafiles[it];
		if (it==0)
			log.info("First CSV file: " + datafiles[it]);
		else
			log.info("Switched to next CSV file: " + datafiles[it]);
		
		// delete previously unzipped file, in order to save disk space:
		File f = new File(unzipped);
		if (f.exists())
			f.delete();
		
		if (filepath.endsWith(".gz")) {
			// unzip next data file (given by filepath) and load the CSV reader
			unzipped = Zippie.unzip(filepath);
			reader = new CSVReader(new FileReader(unzipped));
		}
		else
			reader = new CSVReader(new FileReader(filepath));
		
		reader.readNext(); // discard first line (used to give table headers)
		
	}

	/**
	 * @return boolean that indicates whether there is a next data point to get from the data files, or not
	 * @throws IOException
	 */
	public boolean hasNext() throws IOException {
		boolean b = (reader.hasNext() || it<(datafiles.length-1));
		if (!b) {
			// if there's no more data points left, we are not going to be using this Environment anymore, so we can clean up:
			// delete previously unzipped file, if it exists
			File f = new File(unzipped);
			if (f.exists())
				f.delete();
		}
		return b; // the next line is either in the current file (children[it]), or in a new file, children[it+1], if it exists
	}
	
}
