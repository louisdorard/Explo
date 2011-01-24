package explo.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 * A utility class to save and load serializable objects, to and from files. 
 * @author Louis Dorard, University College London
 */
public class Ser {

	/**
	 * Saves the given object to the given location.
	 * @param o - object to be saved
	 * @param path - location where o should be saved
	 * @throws IOException
	 */
	public static void save(Serializable o, String path) throws IOException {
		// save to file
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		fos = new FileOutputStream(path);
		out = new ObjectOutputStream(fos);
		out.writeObject(o);
		out.close();
	}
	
	/**
	 * Loads object from given location.
	 * @param path - location where the object was saved
	 * @return the object
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Serializable load(String path) throws IOException, ClassNotFoundException {
		Serializable o;
		FileInputStream fis = null;
		ObjectInputStream in = null;
		fis = new FileInputStream(path);
		in = new ObjectInputStream(fis);
		o = (Serializable) in.readObject();
		in.close();
		return o;
	}
	
}
