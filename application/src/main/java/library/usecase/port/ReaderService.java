package library.usecase.port;

import library.dto.Reader;
//import library.usecase.exception.RequestException;

import java.util.List;
import java.util.Optional;

public interface ReaderService {
    /**
     * Load the Reader datastore generating ficticious readers
     * @param nReaders  the number of readers to be generated
     * @return          the list of generated readers
     * @throws RequestException 
     * @see             ReaderService.cleanUpDatabase
     */
    public List<Reader> loadDatabase(Optional<Integer> nReaders);// throws RequestException;

    /**
     * Clean up the Reader datastore
     * @see ReaderService.loadDatabase
     * @throws RequestException
     */
    public void cleanUpDatabase(); // throws RequestException;

   /**
     * Retrieve the list of readers currently stored in the Reader datastore
     * @param pageNum   the number of the page to be retrieved. Optional.
     *                  No pagination is to apply when this is not present
     * @param pageSize  the number of items per page when pagination is being applied. Optional
     * @param readerId  the id of the reader to returned, if any. Optional
     * @return          the list of readers retrieved
     * @throws RequestException
     */
    public List<Reader> retrieveReaders(Optional<Integer> pageNum, Optional<Integer> pageSize, Optional<Long> readerId);
}