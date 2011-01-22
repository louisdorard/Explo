package explo.util;

import java.util.zip.*;
import java.io.*;
 
// From http://code.hammerpig.com/how-to-gunzip-files-with-java.html
public class Zippie {

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