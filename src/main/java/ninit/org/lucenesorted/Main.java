package ninit.org.lucenesorted;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    private static int NUM_THREADS = 24;
    private static int MAX_QUEUE_SIZE = 1000000;

    public static void main(String[] args) throws IOException {
        RandomDocumentIndexer indexer = new RandomDocumentIndexer();
        indexer.createIndex(args[0], RandomDocument.SCORE, new StandardAnalyzer(Version.LUCENE_4_9), true);

        RandomDocumentIterator iterator = new RandomDocumentIterator(10000000);
        int i = 1;
        Stopwatch watch = Stopwatch.createStarted();
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUM_THREADS);

        while (iterator.hasNext()) {
            while (threadPool.getActiveCount() == NUM_THREADS && threadPool.getQueue().size() > MAX_QUEUE_SIZE) {
                try {
                    logger.info("Thread sleeping for 1 sec. Actives Threads {}, Task Queue size {}",
                            threadPool.getActiveCount(), threadPool.getQueue().size());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            threadPool.execute(indexer.getIndexerWorker(iterator.next()));
            if (i % 100000 == 0) {
                logger.info("Indexed {} documents in {}", i, watch);
            }
            i++;
        }

        while (!(threadPool.getQueue().size() == 0)) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        threadPool.shutdown();
        logger.info("Indexed {} documents in {}", i, watch);
        indexer.closeIndex(true);
        RandomDocumentIndexReader reader = new RandomDocumentIndexReader();
        reader.openIndex(args[0]);
        reader.isIndexSortedBy(RandomDocument.SCORE, true);
        reader.closeIndex();
    }
}
