package reader.usecase.port;

import reader.dto.Reader;

import java.util.List;
import java.util.Optional;

public interface ReaderService {
    /**
     * Load the Reader datastore generating ficticious readers
     * @param nReaders  the number of readers to be generated
     * @return          the list of generated readers
     * @see             ReaderService.cleanUpDatabase
     */
    public List<Reader> loadDatabase(Optional<Integer> nReaders);

    /**
     * Clean up the Reader datastore
     * @see ReaderService.loadDatabase
     */
    public void cleanUpDatabase();

    /**
     * Retrieve the list of readers currently stored in the Reader datastore
     * @param pageNum   the number of the page to be retrieved.
     *                  No pagination is to apply when this is not present
     * @param pageSize  the number of items per page when pagination is being applied
     * @return          the list of readers retrieved
     */
    public List<Reader> retrieveReaders(Optional<Integer> pageNum, Optional<Integer> pageSize);
   
    /**
     * Retrieve a reader by their id, if they exist.
     * @param id    the id of the reader
     * @return      the reader found, if any
     */
    public Optional<Reader> retrieveReader(long id);
}