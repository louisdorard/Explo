package explo.util;

public class JvmTester {

	public static void main(String[] args) throws InterruptedException {
		//Declare the size  
        int megaBytes = 1024*1024;  
  
        //Getting the runtime reference from system  
        Runtime runtime = Runtime.getRuntime();  
  
        System.out.println(" ------| JVM Memory Utilization Statistics (in MB) |-------");  
  
        //Print used memory  
        System.out.println("Used Memory: "  
               +(runtime.totalMemory() - runtime.freeMemory())/megaBytes);  
  
        //Print free memory  
        System.out.println("Free Memory: "  
               +runtime.freeMemory()/megaBytes);  
  
        //Print total available memory  
        System.out.println("Total Memory: "  
               +runtime.totalMemory()/megaBytes);  
  
        //Print Maximum available memory  
        System.out.println("Max Memory: "  
               +runtime.maxMemory()/megaBytes);
        
        Thread.sleep(1000);
	}

}
