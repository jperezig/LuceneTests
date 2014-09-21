package org.ninit.analyzers;

import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;

public class TermIterator implements Iterator<Term> {
    private static String FIELD = "";
    private TokenStream tokenStream;

    public TermIterator(Analyzer analyzer, String input) {
        try {
            this.tokenStream = analyzer.tokenStream(FIELD, input);
            tokenStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasNext() {
        try {
            if (tokenStream.incrementToken()) {
                return true;
            } else {
                tokenStream.close();
                return false;
            }

        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Term next() {
        return new Term(FIELD, tokenStream.getAttribute(CharTermAttribute.class).toString());
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove method not implemented");
    }

}
