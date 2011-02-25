package explo.util;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class ZippieTester {

	@Test
	public void testUnzip() throws FileNotFoundException, IOException {
		Zippie.gunzip("data/data.0.csv.gz");
		assertTrue(true);
	}
	
	@Test
	public void testZip() throws IOException {
		Zippie.zipFolder("data/", "tada.zip");
	}

}
