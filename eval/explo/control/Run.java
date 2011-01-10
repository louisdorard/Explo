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
import explo.agent.ClickPredictor_Impl;
import explo.model.*;
import explo.util.Ser;
import explo.util.TimeKeeper;

final public class Run {
	
	private static org.apache.log4j.Logger log = Logger.getLogger(Run.class);
	public static ClickPredictor cp; // instance of ClickPredictor to be used to choose arms to play
	public static Environment e; // used to evaluate this instance
	private static final long TIME_LIMIT = TimeKeeper.calibrate(); // time limit for each call to ClickPredictor.choose()
	private static String cpPath = "cp.ser"; // path to the file in which we save the ClickPredictor object, so that we can memorise the state of the algo from one run to the other
	private static ExecutorService executor = Executors.newFixedThreadPool(1); // used for running ClickPredictor in another thread with time restriction
	private static boolean testMode; // indicates whether we run in test mode or not
	
	/**
	 * Runs the participant's algorithm on the dataset, and logs the cumulative reward.
	 * First parameter is the path to the CSV data file
	 * Second parameter, if given, implies that the test mode is entered and specifies the number of iterations to be performed for tests
	 * @param args
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws NumberFormatException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws NumberFormatException, IOException, ParseException, ClassNotFoundException {
		
		// Initialisation
		if (args.length>1)
			testMode = true;
		else
			testMode = false;
		if (args.length>0)
			e = new Environment(args[0]);
		else
			e = new Environment("data.csv");
		cp = initAlgo();
		if (testMode)
			cpPath = "test/" + cpPath;
		
		if (testMode)
			iterate(Integer.parseInt(args[1]));
		else
			iterate();
		
		// All data from environment has been used, we can now save the algorithm
		Ser.save(cp, cpPath); // save the state of the ClickPredictor algorithm in a file, for retrieval later.
		log.info("Phase number: " + 1);
		log.info("Cumulative reward: " + e.getR());
		
	}
	
	private static void iterate() {
		iterate(-1);
	}
	
	/**
	 * Perform nit iterations with e and cp.
	 * An iteration consists of getting a batch from the environment, using cp in limited time to choose an arm to play in this batch, playing the selected arm, receiving a reward, and learning from this observation. 
	 * If nit is <=0, we run as many iterations as allowed by the data set.
	 * @param nit
	 */
	private static void iterate(int nit) {
		
		Batch b; // batch for current iteration
		int i; // index of chosen arm in b
		Arm a; // corresponding arm
		Integer reward; // reward for a
		int c = 0; // current batch number
		
		while (e.hasBatchesLeft()) {
			
			/*
			 * New iteration
			 * (stop if we are getting more batches than specified number of iterations)
			 */
			b = e.getNewBatch();
			c = c+1;
			if (nit>0 && c>nit)
				break;
			log.info("Batch #" + c);
			
			
			/*
			 * Choose which arm to play in this batch
			 * (measure and log execution time)
			 */
			long start = TimeKeeper.getUserTime();
			if (testMode)
				i = cp.choose(b);
			else
				// time is limited when not in test mode
				try {
					i = choose(b);
				} catch (TimeoutException ex) {
					i = b.getRandomIndex();
				    log.warn("\t Time limit exceeded. Random choice was made.");
				} catch (Exception ex) { // can be either InterruptedException or ExecutionException
					log.error(ex);
					break;
				}
			double prop = round((double) (TimeKeeper.getUserTime() - start) / TIME_LIMIT);
			log.info("\t" + prop + "% of allowed time");
			if (prop > 1)
				log.error("The algorithm took too long"); // this error is used by the webapp for tests, but it is also useful as a double check on the computation time when not in test mode
			
			
			/*
			 * Play the chosen arm and learn 
			 */
			a = b.getArm(i);
			reward = e.play(a);
			cp.learn(a, reward);
			
			log.trace("\t Chose entry number " + i + ":");
			log.trace("\t \t " + a);
			log.trace("\t \t Reward " + reward);
			
		}
	}

	/**
	 * Round a given double and return a double with only 2 decimals.
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
			cp = (ClickPredictor) Ser.load(cpPath);
		}
		else
			cp = new ClickPredictor_Impl();
		
		return cp;
	}


	/**
	 * Choose an arm to play among those proposed in a given batch. This method uses explo.agent.ClickPredictor.choose() and limits its execution time by executing it in a separate thread.
	 * @param b
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	private static int choose(final Batch b) throws InterruptedException, ExecutionException, TimeoutException {
		FutureTask<Integer> theTask = null;
	    // create new task
	    theTask = new FutureTask<Integer>(new Callable<Integer>() {
	        public Integer call() {
	        	return cp.choose(b);
	        }
	    });
	    executor.execute(theTask);
	    // wait for the execution to finish, timeout given by constant
	    return theTask.get(TIME_LIMIT, TimeUnit.SECONDS); 
	}

}
