package ninit.org.lucenesorted;

import java.util.Iterator;
import java.util.Random;

public class RandomDocumentIterator implements Iterator<RandomDocument> {

    private int size;
    private int current = 1;
    private Random random;

    public RandomDocumentIterator(int size) {
        this.size = size;
        this.random = new Random();
    }

    public boolean hasNext() {
        return current <= size;
    }

    public RandomDocument next() {
        return new RandomDocument(current++, random.nextFloat() * 100000);
    }

    public void remove() {
        throw new UnsupportedOperationException("Remove method not implemented");
    }
}
