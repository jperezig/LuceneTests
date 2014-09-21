package org.ninit.analyzers;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    private static Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);

    public static void main(String[] args) {
        RandomPhraseIterator phraseIterator = new RandomPhraseIterator(100);
        while (phraseIterator.hasNext()) {
            logger.debug(phraseIterator.next());
        }
    }

}
