package dmk.suggest;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

public class BloomFilterTest {
	private static final Log log = LogFactory.getLog(IdIndexerTest.class);
	private static final String indexDir = "test-data/";

	@Before
	public void setUp() {

	}

	@Test
	public void bloomFilterTest() throws Exception {
		BufferedReader br = null;
		try {
			final int million = 1000000;
			final BloomFilter bf = new BloomFilter(32 * 5 * million,2);
			final File inputDir = new File(indexDir);
			final File[] files = inputDir.listFiles();
			final long startTime = System.currentTimeMillis();
			int totalLoaded = 0;
			for (final File f : files) {
				log.warn("indexing contents of file " + f.getName());
				br = new BufferedReader(new FileReader(f));
				String line = null;
				int loaded = 0;
				while ((line = br.readLine()) != null) {
					line = line.trim();
					line = line.toLowerCase();
					bf.addToFilter(line);
					loaded++;
				}//16,000,000
				log.warn("loaded " + loaded + " items.");
				totalLoaded += loaded;
				br.close();
				br = null;
			}
			log.warn("total loaded " + totalLoaded);
			
			for (final File f : files) {
				log.warn("checking contents of file " + f.getName());
				// test filter
				br = new BufferedReader(new FileReader(f));
				String line = null;
				int found = 0;
				int missed = 0;
				while ((line = br.readLine()) != null) {
					if (bf.isInFilter(line)) {
						found++;
					} else {
						missed++;
					}
				}
				br.close();
				br = null;
				System.out.println("Correctly found: " + found
						+ " entries. Missed " + missed + " entries");
				assertEquals(missed, 0);
			}
			
			
			int badIds = million;
			int falsePositiveHits = 0;
			System.out.println("quering for " + badIds + " random uuids (all of which are more then likely not in the filter, so false positive should be real low)...");
			for(int i = 0; i < badIds; i++){
				final String uuid = UUID.randomUUID().toString();
				if(bf.isInFilter(uuid)){
					falsePositiveHits++;
					//log.error("false positive hit on string " + uuid);
				}
			}
			
			final double errRate = (double)falsePositiveHits / (double)totalLoaded;
			System.out.println("totalLoaded=" + totalLoaded + " falsePositiveHits=" + falsePositiveHits + " (" + errRate + ")");
			final long endTime = System.currentTimeMillis();
			System.out.println("total time to index and query = " + (endTime-startTime)/1000 + " secs.");
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}
}
