package explo.util;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class ZippieTester {

	@Test
	public void testUnzip() throws FileNotFoundException, IOException {
		Zippie.unzip("data/data.0.csv.gz");
		assertTrue(true);
	}

}
