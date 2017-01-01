package test;
import static org.junit.Assert.*;  

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
//import MPD.ValidateStream;

public class ValidateTest {
	
		private ValidateStream validate;
		@BeforeClass
		public static void setUpBeforeClass() throws Exception {
		}

		@AfterClass
		public static void tearDownAfterClass() throws Exception {
		}

		@Before
		public void setUp() throws Exception {
			validate = new ValidateStream();
		}

		@After
		public void tearDown() throws Exception {
		}

		@Test
		public void testParsing() throws Exception {
			URL input = new URL("http://1click.ps/files/readme.txt.segments");
	        List<String> expectedResult=new ArrayList<String>();
	        expectedResult.add("http://1click.ps/here/readme.txt.part1;http://1click.ps/there/readme.txt.part1;");
	        expectedResult.add("http://1click.ps/here/readme.txt.part2;http://1click.ps/there/readme.txt.part2;");
	        expectedResult.add("http://1click.ps/here/readme.txt.part10;http://1click.ps/there/readme.txt.part10;");
			List<String> result  = validate.readManifset(input);
			assertEquals(result,expectedResult);
		}

	}
