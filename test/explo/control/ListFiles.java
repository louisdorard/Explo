package explo.control;
import java.io.File;
import java.util.Arrays;


public class ListFiles {

	public static void main(String[] args) {
		String[] datafiles;
		String dirpath;
		if (args.length==0)
			dirpath = "./";
		else
			dirpath = args[0];
		File dir = new File(dirpath);
		datafiles = dir.list();
		Arrays.sort(datafiles);
		for (int i = 0; i < datafiles.length; i++) {
			System.out.println(datafiles[i]);
		}
	}
	
}
