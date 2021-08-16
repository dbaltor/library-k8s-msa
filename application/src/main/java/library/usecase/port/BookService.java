package library.usecase.port;

import library.dto.Book;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

public interface BookService {

    @RequiredArgsConstructor(staticName = "of")
    public class BookServiceRequest {
        public @NonNull Long readerId;
        public @NonNull Long bookIds[];
    }

    /**
     * Load the Book datastore generating ficticious exemplars of books.
     * @param nBooks    the number of books to be generated
     * @return          the list of generated books
     * @throws RequestException 
     * @see             BookService.cleanUpDatabase   
     */
    public List<Book> loadDatabase(Optional<Integer> nBooks);

    /**
     * Clean up the Book datastore
     * @throws RequestException 
     * @see     BookService.loadDatabase
     */
    public void cleanUpDatabase();

    /**
     * Retrieve the list of books currently stored in the Book datastore.
     * @param pageNum   the number of the page to be retrieved. Optional. 
     *                  No pagination is to apply when this is not present
     * @param pageSize  the number of items per page when pagination is being applied. Optional
     * @param readerId  the id of the reader borrowing the books. Opitional
     * @return          list of books retrieved
     * @throws RequestException 
     */
    public List<Book> retrieveBooks(Optional<Integer> pageNum, Optional<Integer> pageSize, Optional<Long> readerId);

    /**
     * Try to borrow books to the reader applying business rules.
     * @param booksToBorrow the list of books to borrow
     * @param readerId      the id of the reader borrowing the books
     * @return              the list of books effectively borrowed
     * @throws RequestException 
     */
    public List<Book> borrowBooks(BookServiceRequest booksRequest);
    
    /**
     * Try to return books from the reader applying business rules.
     * @param booksToReturn the list of books being returned
     * @param readerId      the id of the reader returning the books
     * @return              the list of books effectively returned
     * @throws RequestException 
     */
    public List<Book> returnBooks(BookServiceRequest booksRequest); 
}