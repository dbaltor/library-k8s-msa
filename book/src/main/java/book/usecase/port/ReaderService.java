package book.usecase.port;

import book.dto.Reader;

import java.util.List;
import java.util.Optional;

public interface ReaderService {
    /**
     * Retrieve the list of readers currently stored in the Reader datastore
     * @param pageNum   the number of the page to be retrieved.
     *                  No pagination is to apply when this is not present
     * @param pageSize  the number of items per page when pagination is being applied
     * @return          the list of readers retrieved
     */
    public List<Reader> retrieveReaders(Optional<Integer> pageNum, Optional<Integer> pageSize, Optional<Long> readerId);
}