package explo.util;

import java.lang.management.*;

/**
 * A utility class to keep track of user time and calibrate its measure on the current JVM.
 * @author Louis Dorard, University College London
 *
*/

public final class TimeKeeper {	
	
	/**
	 * Get the user time (spent running your application's own code).
	 * See http://nadeausoftware.com/articles/2008/03/java_tip_how_get_cpu_and_user_time_benchmarking
	 * @return user time in nanoseconds
	 */
	public static long getUserTime( ) {
	    ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
	    return bean.isCurrentThreadCpuTimeSupported( ) ?
	        bean.getCurrentThreadUserTime( ) : 0L;
	}
	
    /**
     * Recursive Fibonacci implementation.
     * See http://www.cs.princeton.edu/introcs/23recursion/Fibonacci.java.html
     * @param n 
     * @return nth element in the Fibonacci sequence
     */
    private static long fib(int n) {
        if (n <= 1) return n;
        else return fib(n-1) + fib(n-2);
    }

    /**
	 * Calibrates the execution time on this JVM by running a fixed number of naive Fibonacci iterations.
	 * @return time taken, in nanoseconds (to be used to set a time budget for future computations)
	 */
    public static long calibrate() {
    	long time = 0;
    	int nb_run = 10;
    	for (int i = 1; i <= nb_run; i++) {
			long start = getUserTime(); // start user time in nano seconds
			fib(34); fib(36);
	        time += getUserTime() - start; // this is about 100ms on a UCL CS compute node
		}
    	return time/nb_run;
    }

}
