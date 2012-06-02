package dmk.suggest;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.NGramTokenizer;

public class IdTokenizer extends Analyzer {
	private int minGram;
	private int maxGram;

	IdTokenizer(int minGram, int maxGram) {
		this.minGram = minGram;
		this.maxGram = maxGram;
	}

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return new NGramTokenizer(reader,
				minGram, maxGram);
	}
}