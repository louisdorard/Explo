package explo.util;

import java.util.zip.*;
import java.io.*;
 
/**
 * A utility class to compress and uncompress files.
 * @author Louis Dorard
 */
public class Zippie {

	/**
	 * Gunzips given file and returns path where the result has been written to.
	 * (from http://code.hammerpig.com/how-to-gunzip-files-with-java.html)
	 * @param inFilePath - file to be unzipped, assumed to be a .gz archive containing only one element
	 * @return path to the unzipped content of the archive
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @author Hammerpig
	 */
	public static String gunzip(String inFilePath) throws FileNotFoundException, IOException
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
	
	/**
	 * Creates zip archive from given directory, and saves it to the enclosing directory
	 * @param folder - directory to be zipped 
	 * @param zipName - name of the zip archive to be created
	 * @return a file pointing to the newly created archive
	 * @throws IOException 
	 */
	public static File zipFolder(String folderPath, String zipName) throws IOException {
		
		File folder = new File(folderPath);
		File[] files = folder.listFiles();
		
	    // Create a buffer for reading the files
	    int bufLength = 1024;
		byte[] buf = new byte[bufLength];
		
		// Create the ZIP file
        String fileZip = folder.getParent()+"/"+zipName;
        if (folder.getParent()!=null) // absolute path was given
    		fileZip = folder.getParent()+"/"+zipName;
    	else
    		fileZip = zipName;

		ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(fileZip)));
		
		for (int i=0; i<files.length; i++) {

        	File file = files[i];

			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream buffInputStream = new BufferedInputStream(fis, bufLength);
        
            // Add ZIP entry to output stream.
            zip.putNextEntry(new ZipEntry(file.getName()));
    
            // Transfer bytes from the file to the ZIP file
            int len;
            while((len = buffInputStream.read(buf, 0, bufLength)) != -1) {
                zip.write(buf, 0, len);
            }
    
            // Complete the entry
            zip.closeEntry();
            buffInputStream.close();
            
		}

    	// Complete the ZIP file
    	zip.close();
    	
    	return new File(fileZip);
	        
	}

}