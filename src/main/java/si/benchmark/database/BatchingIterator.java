package si.benchmark.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterator.ORDERED;

/**
 * An iterator which returns batches of items taken from another iterator
 * https://stackoverflow.com/questions/30641383/java-8-stream-with-batch-processing/31642381
 */
public class BatchingIterator<T> implements Iterator<List<T>> {
    /**
     * Given a stream, convert it to a stream of batches no greater than the
     * batchSize.
     *
     * @param originalStream to convert
     * @param batchSize      maximum size of a batch
     * @param <T>            type of items in the stream
     * @return a stream of batches taken sequentially from the original stream
     */
    public static <T> Stream<List<T>> batchedStreamOf(Stream<T> originalStream, int batchSize) {
        return asStream(new BatchingIterator<>(originalStream.iterator(), batchSize));
    }

    private static <T> Stream<T> asStream(Iterator<T> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, ORDERED),
                false);
    }

    private final int batchSize;
    private final Iterator<T> sourceIterator;
    private List<T> currentBatch;

    public BatchingIterator(Iterator<T> sourceIterator, int batchSize) {
        this.batchSize = batchSize;
        this.sourceIterator = sourceIterator;
    }

    @Override
    public boolean hasNext() {
        prepareNextBatch();
        return currentBatch != null && !currentBatch.isEmpty();
    }

    @Override
    public List<T> next() {
        return currentBatch;
    }

    private void prepareNextBatch() {
        currentBatch = new ArrayList<>(batchSize);
        while (sourceIterator.hasNext() && currentBatch.size() < batchSize) {
            currentBatch.add(sourceIterator.next());
        }
    }
}