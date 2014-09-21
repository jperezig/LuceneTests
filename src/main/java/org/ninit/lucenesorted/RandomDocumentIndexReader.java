package org.ninit.lucenesorted;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomDocumentIndexReader {

    private IndexReader indexReader;
    private Logger logger = LoggerFactory.getLogger(RandomDocumentIndexReader.class);

    public void openIndex(String indexFile) throws IOException {
        this.indexReader = DirectoryReader.open(FSDirectory.open(new File(indexFile)));
    }

    private boolean areSorted(Document firstDoc, Document lastDocument, String fieldName, boolean reverse) {
        if (reverse) {
            return firstDoc.getField(fieldName).numericValue().floatValue() >= lastDocument.getField(fieldName)
                    .numericValue().floatValue();
        } else
            return firstDoc.getField(fieldName).numericValue().floatValue() < lastDocument.getField(fieldName)
                    .numericValue().floatValue();

    }

    public void isIndexSortedBy(String fieldName, boolean reverse) throws IOException {
        int numSegment = 1;
        for (AtomicReaderContext reader : indexReader.leaves()) {
            int numDocs = reader.reader().numDocs();
            this.logger.info("Checking segment {} of {} with size {}", numSegment++, indexReader.leaves().size(),
                    numDocs);
            for (int i = 1; i < numDocs; i++) {
                Document previousDoc = reader.reader().document(i - 1);
                Document currentDoc = reader.reader().document(i);
                if (!areSorted(previousDoc, currentDoc, fieldName, reverse)) {
                    this.logger.info("PREVIOUS: {}", previousDoc);
                    this.logger.info("CURRENT: {}", currentDoc);
                    break;
                }
            }
            this.logger.info("All documents sorted by '{}'", fieldName);
        }
    }

    public boolean closeIndex() {

        try {
            this.indexReader.close();
        } catch (IOException e) {
            this.logger.warn("Can't close index");
            return false;
        }
        return true;
    }
}
