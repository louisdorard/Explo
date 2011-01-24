package explo.util;

import java.lang.management.*;

/**
 * A utility class to keep track of user time and calibrate its measure on the current JVM.
 * @author Louis Dorard, University College London
 * 
 * Fibonacci programme by http://www.cs.princeton.edu/introcs/23recursion/Fibonacci.java.html
 *
 *  Compilation:  javac luigi/TestAlgo.java
 *  Execution:    java luigi/TestAlgo
 *
 *  Computes and prints the first N Fibonacci numbers.
 *
 *  WARNING:  this program is spectacularly inefficient and is meant
 *            to illustrate a performance bug, e.g., set N = 45.
 *
 *
 *   % java Fibonacci 7
 *   1: 1
 *   2: 1
 *   3: 2
 *   4: 3
 *   5: 5
 *   6: 8
 *   7: 13
 *
 *   Remarks
 *   -------
 *    - The 93rd Fibonacci number would overflow a long, but this
 *      will take so long to compute with this function that we
 *      don't bother to check for overflow.
 *
 *************************************************************************/
public final class TimeKeeper {	
	
	public static long getUserTime( ) {
		// see http://nadeausoftware.com/articles/2008/03/java_tip_how_get_cpu_and_user_time_benchmarking
	    ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
	    return bean.isCurrentThreadCpuTimeSupported( ) ?
	        bean.getCurrentThreadUserTime( ) : 0L;
	}
	
    private static long fib(int n) {
        if (n <= 1) return n;
        else return fib(n-1) + fib(n-2);
    }

    /**
	 * Calibrates the execution time of on this JVM by running a fixed number of naive Fibonacci iterations.
	 * @return time taken (to be used to set a time budget for further computations)
	 */
    public static long calibrate() {
    	long start = getUserTime(); // start user time in nano seconds
        int N = 38; // 38 iterations of Fibonacci
        for (int i = 1; i <= N; i++)
            fib(i);
        return getUserTime() - start;
    }

}
