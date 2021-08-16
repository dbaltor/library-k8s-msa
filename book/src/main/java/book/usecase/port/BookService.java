package book.usecase.port;

import book.dto.Book;
import book.dto.Reader;
import book.usecase.exception.BorrowingException;
import book.usecase.exception.ReturningException;

import java.util.List;
import java.util.Optional;

public interface BookService {
    /**
     * Load the Book datastore generating ficticious exemplars of books.
     * @param nBooks    the number of books to be generated
     * @param readers   the list of readers to borrow the generated books across
     * @return          the list of generated books
     * @see             BookService.cleanUpDatabase   
     */
    public List<Book> loadDatabase(Optional<Integer> nBooks, List<Reader> readers);

    /**
     * Clean up the Book datastore
     * @see     BookService.loadDatabase
     */
    public void cleanUpDatabase();

    /**
     * Retrieve a book by its id, if it exist.
     * @param id    the id of the book
     * @return      the book found, if any
     */
    public Optional<Book> retrieveBook(long id);

    /**
     * Retrieve the list of books currently stored in the Book datastore.
     * @param pageNum   the number of the page to be retrieved. 
     *                  No pagination is to apply when this is not present
     * @param pageSize  the number of items per page when pagination is being applied
     * @return          list of books retrieved
     */
    public List<Book> retrieveBooks(Optional<Integer> pageNum, Optional<Integer> pageSize);

    /**
     * Find books by their ids, if they exist.
     * @param ids   the ids of the books
     * @return      list of books found, if any.
     */
    public List<Book> findBooksByIds(List<Long> ids);

    /**
     * Find books borrowed by a reader.
     * @param id    reader id
     * @return      list of books borrowed by the reader
     */
    public List<Book> findBooksByReaderId(long id);

    /**
     * Try to borrow books to the reader applying business rules.
     * @param booksToBorrow The list of books to borrow
     * @param reader        The reader borrowing the books
     * @return              The list of books effectively borrowed
     * @throws BorrowingExcecption
     */
    public List<Book> borrowBooks(List<Book> booksToBorrow, Long readerId) throws BorrowingException;

    /**
     * Try to return books from the reader applying business rules.
     * @param booksToReturn The list of books being returned
     * @param reader        The reader returning the books
     * @return              The list of books effectively returned
     * @throws ReturningException
     */
    public List<Book> returnBooks(List<Book> booksToReturn, Long readerId) throws ReturningException;
}