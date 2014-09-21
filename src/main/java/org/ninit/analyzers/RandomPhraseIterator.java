package org.ninit.analyzers;

import java.util.Iterator;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomPhraseIterator implements Iterator<String> {

    private static int MAX_PHRASE_LENGTH = 10;
    private static int MIN_PHRASE_LENGTH = 2;
    private static int MAX_WORD_LENGTH = 10;
    private static int MIN_WORD_LENGTH = 3;
    private static String SEPARATOR = " ";

    private int size;
    private int current = 1;
    private Random random = new Random();

    public RandomPhraseIterator(int size) {
        this.size = size;
    }

    @Override
    public boolean hasNext() {
        return current <= size;
    }

    @Override
    public String next() {
        int numWords = random.nextInt(MAX_PHRASE_LENGTH) + MIN_PHRASE_LENGTH;
        String phrase = "";
        for (int i = 0; i < numWords; i++) {
            phrase += SEPARATOR + RandomStringUtils.randomAlphabetic(random.nextInt(MAX_WORD_LENGTH) + MIN_WORD_LENGTH);

        }
        this.current ++;
        return phrase.trim().toLowerCase();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove method not implemented");

    }

}
