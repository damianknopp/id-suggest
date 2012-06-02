package dmk.suggest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class IdTokenizerTest {
	private static final Log log = LogFactory.getLog(IdTokenizerTest.class);
	private IdTokenizer idTokenizer;
	private static final String INPUT_FILE = "src/main/resources/my_tests.txt";
	private static final String INDEX_DIR = "test-data/";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void tester() throws Exception {
		idTokenizer = new IdTokenizer(3, 4);
		final String line = "6308e6b8-6fbd-4cbf-97e7-d7e91b6ba183";
		System.out.println("Found line = " + line);
		final Reader reader = new StringReader(line);
		List<String> triGrams = null;
		List<String> quadGrams = null;
		final TokenStream ts = idTokenizer.tokenStream("string", reader);
		triGrams = new ArrayList<String>(32);
		quadGrams = new ArrayList<String>(32);
		while (ts.incrementToken()) {
			final TermAttribute term = ts.getAttribute(TermAttribute.class);
			final String text = term.term();
			System.out.println(text.length() + " text=" + text);
			final int len = text.length();
			switch(len){
				case 3:
					triGrams.add(text);
					break;
				case 4:
					quadGrams.add(text);
					break;
				default:
					break;
			}
		}
//		System.out.println("triGrams.size =" + triGrams.size());
//		System.out.println("triGrams =" + triGrams);
//		System.out.println("quadGrams.size = " + quadGrams.size());
//		System.out.println("quadGrams = " + quadGrams);
		assertNotNull(triGrams);
		assertNotNull(quadGrams);
		assertEquals(triGrams.size(), 34);
		assertEquals(quadGrams.size(), 33);
		assertEquals(triGrams.get(0), "630");
		assertEquals(triGrams.get(triGrams.size() - 1), "183");
		assertEquals(quadGrams.get(0), "6308");
		assertEquals(quadGrams.get(quadGrams.size() - 1), "a183");

	}
}