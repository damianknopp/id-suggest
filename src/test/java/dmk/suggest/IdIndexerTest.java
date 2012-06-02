package dmk.suggest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class IdIndexerTest {
	private static final Log log = LogFactory.getLog(IdIndexerTest.class);
	private static final String indexDir = "test-data/";
	private IdIndexer idIndexer;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		idIndexer = new IdIndexer(indexDir);
	}

	@Test
	public void testIndexDir() throws Exception {
		try {
			this.idIndexer.indexDir();
		} finally {
			this.idIndexer.optimize();
			this.idIndexer.close();
		}
	}
}
