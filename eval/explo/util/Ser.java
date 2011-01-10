package explo.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class Ser {

	public static void save(Serializable o, String path) throws IOException {
		// save to file
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		fos = new FileOutputStream(path);
		out = new ObjectOutputStream(fos);
		out.writeObject(o);
		out.close();
	}
	
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
