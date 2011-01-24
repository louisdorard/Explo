package explo.util;

import java.util.zip.*;
import java.io.*;
 
/**
 * A utility class to uncompress gunzipped files.
 * (from http://code.hammerpig.com/how-to-gunzip-files-with-java.html)
 * @author Hammerpig and Louis Dorard
 */
public class Zippie {

	/**
	 * Unzips given file and returns path where the result has been written to.
	 * @param inFilePath - file to be unzipped
	 * @return path to the unzipped file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String unzip(String inFilePath) throws FileNotFoundException, IOException
	{
	    GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(inFilePath));
	 
	    String outFilePath = inFilePath.replace(".gz", "");
	    OutputStream out = new FileOutputStream(outFilePath);
	 
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = gzipInputStream.read(buf)) > 0)
	        out.write(buf, 0, len);
	 
	    gzipInputStream.close();
	    out.close();
	 
	    return outFilePath;
	}

}