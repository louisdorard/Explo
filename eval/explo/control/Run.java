package explo.control;

import org.apache.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import explo.agent.ClickPredictor;
import explo.model.*;
import explo.util.Ser;
import explo.util.TimeKeeper;
import explo.util.Zippie;

/**
 * Main class for running the evaluation of a click predictor on a dataset.
 * @author Louis Dorard, University College London
 */
final public class Run {
	
	/**
	 * Instance of the algorithm to be evaluated. Chooses arms to play.
	 */
	private static ClickPredictor cp;
	
	/**
	 * Used to feed data to this.cp and to evaluate it through its cumulative reward.
	 */
	private static Environment e;
	
	private static org.apache.log4j.Logger log = Logger.getLogger(Run.class);
	private static final long TIME_LIMIT = TimeKeeper.calibrate(); // time limit for each iteration, in nanoseconds
	private static String cpPath = "cp.ser"; // path to the file in which we save the ClickPredictor object, so that we can memorise the state of the algo from one run to the other
	private static ExecutorService executor = Executors.newFixedThreadPool(1); // used for running ClickPredictor in another thread with time restriction
	private static boolean testMode; // indicates whether we run in test mode or not  
    private static Runtime runtime = Runtime.getRuntime(); // getting the runtime reference from system
	private static double timeSpent; // time spent for the last execution of choosePlayLearn, measured relatively to TIME_LIMIT
    
	
	/**
	 * Runs the participant's algorithm on the dataset and logs the cumulative reward.
	 * @param args (opt.) - the first argument is the path to the CSV data file; the second argument (if given) specifies the number of iterations to be performed for tests
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		// Initialisation
		if (args.length>1 && args[1]!=null) {
			testMode = true;
			log.info("Test mode");
		}
		else
			testMode = false;
		
		log.info("Phase number: " + 1);
		
		try {
			
			if (args.length>0)
				e = new Environment(args[0]);
			else
				e = new Environment("data");
			if (testMode)
				cpPath = "test/" + cpPath;
			cp = initAlgo();
			
			if (testMode)
				iterate(Integer.parseInt(args[1]));
			else
				iterate();
			
			// All data from environment has been used, we can now save the algorithm
			// Ser.save(cp, cpPath); // save the state of the ClickPredictor algorithm in a file, for retrieval later.
			log.info("Cumulative reward: " + e.getR());
			
		} catch (Exception e) { 
			java.io.StringWriter sw = new java.io.StringWriter(); 
			e.printStackTrace(new java.io.PrintWriter(sw)); 
			log.error(sw.toString());
		}
		
		// Package the logs in a zip archive
		if (!testMode)
			Zippie.zipFolder("logs", "logs-" + e.getR() + ".zip"); // zips the logs/ directory, with an archive name given in second parameter
		System.exit(0); // stops ant from possibly "hanging"
		
	}
	
	/**
	 * Performs as many iterations as there are data points.
	 */
	private static void iterate() throws IOException, ParseException {
		iterate(-1);
	}
	
	/**
	 * Performs nit iterations with this.e and this.cp.
	 * An iteration consists of getting a batch from the environment, using cp in limited time to choose an arm to play in this batch, playing the selected arm, receiving a reward, and learning from this observation. 
	 * If nit is <=0, we run as many iterations as allowed by the data set.
	 * @param nit
	 * @throws ParseException 
	 * @throws IOException 
	 */
	private static void iterate(int nit) throws IOException, ParseException {
		
		Batch b; // batch for current iteration
		Integer reward; // reward for a
		int c = 0; // current batch number
		
		while (e.hasNext()) {
			
			/*
			 * New iteration
			 * (stop if we are getting more batches than specified number of iterations)
			 */
			b = e.getNewBatch();
			c = c+1;
			if (nit>0 && c>nit)
				break;
			
			
			/*
			 * Choose which arm to play in this batch, play and learn
			 * (measure and log execution time)
			 */
			if (testMode) {
				reward = choosePlayLearn(b);
				if (timeSpent>1)
					log.warn("Batch #" + c + ": time limit exceeded.");
			} else { // time is limited when not in test mode
				ClickPredictor cp2 = new ClickPredictor(cp); // saves the current state of the click predictor
				try {
					reward = choosePlayLearnTimelimited(b);
				} catch (TimeoutException ex) {
					log.warn("Batch #" + c + ": time limit exceeded, now making random choice.");
					cp = cp2; // this acts as a clean-up of the click predictor, which computations were not finished
					reward = e.play(b.getRandomArm());
					timeSpent = 1; // the execution was interrupted, hence all the allowed time was used
				} catch (Exception ex) { // can be either InterruptedException or ExecutionException
					log.error(ex);
					break;
				}
			}
			
			double memory = round((double) (runtime.totalMemory() - runtime.freeMemory()) / runtime.totalMemory());
			log.info("Batch #" + c + ": " + timeSpent*100 + "% of allowed time" + " ; " + memory*100 + "% memory used.");
			
		}
	}
	
	/**
	 * Chooses an arm to play, plays it, gets reward and learns from this observation.
	 * @param b - batch of arms to choose from
	 * @return reward obtained for the chosen arm
	 */
	private static Integer choosePlayLearn(Batch b) {
		long startTime = TimeKeeper.getUserTime();
		int i = cp.choose(b); // index of chosen arm in b
		Arm a = b.getArm(i); // corresponding arm
		Integer reward = e.play(a);
		cp.learn(a, reward);
		log.trace("\t Chose entry number " + i + ":");
		log.trace("\t \t " + a);
		timeSpent = round((double) (TimeKeeper.getUserTime() - startTime) / TIME_LIMIT); // this has to be done in the same thread as where we set the value for startTime
		return reward;
	}
	

	/**
	 * Same as choosePlayLearn(Batch b) but limits its execution time by executing in a separate thread.
	 * @param b - batch of arms to choose from
	 * @return reward obtained for the chosen arm
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	private static Integer choosePlayLearnTimelimited(final Batch b) throws InterruptedException, ExecutionException, TimeoutException {
		FutureTask<Integer> theTask = null;
	    // create new task
	    theTask = new FutureTask<Integer>(new Callable<Integer>() {
	        public Integer call() {
	        	return choosePlayLearn(b);
	        }
	    });
	    executor.execute(theTask);
	    // wait for the execution to finish, timeout given by constant
	    return theTask.get(TIME_LIMIT, TimeUnit.NANOSECONDS);
	}

	/**
	 * Rounds a given double and returns a double with only 2 decimals.
	 * This is called before displaying the proportion of allowed time spent for one iteration.
	 * @param d
	 * @return
	 */
	private static double round(double d) {
		BigDecimal bd = new BigDecimal(d);
	    bd = bd.setScale(2, BigDecimal.ROUND_UP);
	    return bd.doubleValue();
	}

	/**
	 * Initialises the ClickPredictor algorithm by creating a new, empty instance, or restoring a serialised object if a file exists at the path given by cpPath.
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static ClickPredictor initAlgo() throws IOException, ClassNotFoundException {
		ClickPredictor cp;
		
		// if cpPath file exists, load it; otherwise, load new Click Predictor
		File f = new File(cpPath);
		if (f.exists()) {
			log.info("Loading existing ClickPredictor in cp.ser");
			cp = (ClickPredictor) Ser.load(cpPath);
		}
		else {
			log.info("Creating new ClickPredictor");
			cp = new ClickPredictor();
		}
		
		return cp;
	}

}
