package org.ninit.lucenesorted;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.sorter.SortingMergePolicy;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

public class RandomDocumentIndexer {

    private Directory directory;
    private IndexWriter indexWriter;
    private Logger logger = LoggerFactory.getLogger(RandomDocumentIndexer.class);
    private Analyzer analyzer;

    public void createIndex(String indexFile, String fieldName, Analyzer analyzer, boolean reverse) throws IOException {
        this.directory = FSDirectory.open(new File(indexFile));
        IndexWriterConfig indexWriterConfiguration = new IndexWriterConfig(Version.LUCENE_4_9, this.analyzer);
        indexWriterConfiguration.setRAMBufferSizeMB(48);

        Sort sort = new Sort(new SortField(fieldName, SortField.Type.FLOAT, reverse));
        SortingMergePolicy sorting = new SortingMergePolicy(indexWriterConfiguration.getMergePolicy(), sort);
        indexWriterConfiguration.setMergePolicy(sorting);

        indexWriter = new IndexWriter(directory, indexWriterConfiguration);
        logger.info("Index Created. It will be sorted by {}", fieldName);
    }

    public void createIndex(String indexFile, Analyzer analyzer) throws IOException {
        this.directory = FSDirectory.open(new File(indexFile));
        IndexWriterConfig indexWriterConfiguration = new IndexWriterConfig(Version.LUCENE_4_9, this.analyzer);
        indexWriterConfiguration.setRAMBufferSizeMB(48);

        indexWriter = new IndexWriter(directory, indexWriterConfiguration);
        logger.info("Index Created. No sorted");
    }

    private void forceMerge() throws IOException {
        Stopwatch watch = Stopwatch.createStarted();
        this.logger.info("Merging to only one segment");
        this.indexWriter.forceMerge(1);
        this.logger.info("Merged to only one segment in {}", watch);
    }

    public boolean closeIndex(boolean forceMerge) {

        this.logger.info("Closing index with {} documents", this.indexWriter.numDocs());
        try {
            if (forceMerge) {
                forceMerge();
            }
            this.indexWriter.close();
        } catch (IOException e) {
            this.logger.warn("Can't close index");
            return false;
        }
        return true;
    }

    public Runnable getIndexerWorker(RandomDocument rDoc) {
        return new IndexerWorker(rDoc, indexWriter);
    }

    static class IndexerWorker implements Runnable {
        private Logger logger = LoggerFactory.getLogger(RandomDocumentIndexer.class);
        private RandomDocument rDoc;
        private IndexWriter indexWriter;

        public IndexerWorker(RandomDocument rDoc, IndexWriter indexWriter) {
            this.rDoc = rDoc;
            this.indexWriter = indexWriter;
        }

        private void indexDocument(RandomDocument rDoc) {
            Document doc = new Document();
            doc.add(new IntField(RandomDocument.ID, rDoc.getId(), Field.Store.YES));
            doc.add(new FloatField(RandomDocument.SCORE, rDoc.getScore(), Field.Store.YES));
            try {
                this.indexWriter.addDocument(doc);
            } catch (IOException e) {
                this.logger.warn("Can't index rDoc");
            }
        }

        @Override
        public void run() {
            indexDocument(rDoc);
        }
    }
}
