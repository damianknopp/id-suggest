package dmk.suggest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.NIOFSDirectory;

public class IdIndexer {
	private static final Log log = LogFactory.getLog(IdIndexer.class);

	protected final int minGramSize = 3;
	protected final int maxGramSize = 4;
	protected final IdTokenizer idTokenizer;
	protected final String inputDir;
	protected final IndexWriter indexWriter;

	protected IdIndexer(final String dir) throws Exception {
		super();
		this.idTokenizer = new IdTokenizer(minGramSize, maxGramSize);
		this.inputDir = dir;
		this.indexWriter = new IndexWriter(new NIOFSDirectory(new File(
				Constants.indexDir)), new WhitespaceAnalyzer(),
				IndexWriter.MaxFieldLength.UNLIMITED);
	}

	/**
	 * Loop thru a directory of id files, write the ids to the inverted index
	 * 
	 * @throws Exception
	 */
	public void indexDir() throws Exception {
		final File inputDir = new File(this.inputDir);
		final File[] files = inputDir.listFiles();
		final long startTime = System.currentTimeMillis();
		for (final File f : files) {
			log.warn("indexing contents of file " + f.getName());
			final BufferedReader br = new BufferedReader(new FileReader(f));
			String line = null;
			long idCount = 0;
			List<String> triGrams = null;
			List<String> quadGrams = null;
			while ((line = br.readLine()) != null) {
				// System.out.println("Found line = " + line);
				line = line.trim();
				line = line.toLowerCase();
				triGrams = new ArrayList<String>(32);
				quadGrams = new ArrayList<String>(32);
				final Reader reader = new StringReader(line);
				final TokenStream ts = idTokenizer
						.tokenStream("string", reader);
				while (ts.incrementToken()) {
					final TermAttribute term = ts
							.getAttribute(TermAttribute.class);
					final String text = term.term();
					final int len = text.length();
					switch (len) {
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
				this.addIdToIndex(line, triGrams, quadGrams);
				idCount++;
				if (idCount % 50000 == 0) {
					log.warn("Indexed " + idCount + " ids");
				}
			}
		}
		final long endTime = System.currentTimeMillis();
		System.out.println("indexer took " + (endTime - startTime) + " millis");

	}

	/**
	 * Add the given id to the inverted index structure
	 */
	public void addIdToIndex(final String originalString,
			final List<String> triGrams, final List<String> quadGrams)
			throws Exception {
		if(log.isDebugEnabled()){
		log.debug("adding " + originalString + " " + triGrams.size()
				+ " tri grams " + quadGrams.size() + " quad grams.");
		}
		final Document doc = new Document();
		doc.add(new Field("id", originalString, Store.YES, Index.ANALYZED));
		//35 Mb worth of uuid indexed to 754Mb in lucene. Removing quadGrams, end4
		// first and last tri grams in gram3 list, since they arleady show up in the end3 and start3
		//to cut down on size
		//		final String gram3 : triGrams) {
		for (int i = 1; i < triGrams.size() - 2; i++){
			final String gram3 = triGrams.get(i);
			doc.add(new Field("gram3", gram3, Store.YES, Index.ANALYZED));
		}
//		for (final String gram4 : quadGrams) {
//			doc.add(new Field("gram4", gram4, Store.YES, Index.ANALYZED));
//		}
		doc
				.add(new Field("start3", triGrams.get(0), Store.YES,
						Index.ANALYZED));
//		doc
//				.add(new Field("start4", quadGrams.get(0), Store.YES,
//						Index.ANALYZED));
		doc.add(new Field("end3", triGrams.get(triGrams.size() - 1), Store.YES,
				Index.ANALYZED));
//		doc.add(new Field("end4", quadGrams.get(quadGrams.size() - 1),
//				Store.YES, Index.ANALYZED));
		this.indexWriter.addDocument(doc);
	}

	/**
	 * Call optimize on the the handle to the lucene index Recommended to call
	 * this after bulk indexes and before searching.
	 * 
	 * @throws IOException
	 */
	public void optimize() throws IOException {
		final long startTime = System.currentTimeMillis();
		this.indexWriter.optimize();
		final long endTime = System.currentTimeMillis();
		System.out.println("optimization took " + (endTime - startTime) + " millis");
	}
	
	/**
	 * Calls close on the underlaying inverted index resources.
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	public void close() throws CorruptIndexException, IOException{
		this.idTokenizer.close();
		this.indexWriter.close();
	}
}
