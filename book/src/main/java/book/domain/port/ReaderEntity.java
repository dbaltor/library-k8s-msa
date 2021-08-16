package book.domain.port;

import book.dto.Book;

import java.util.List;
import java.util.Set;

public interface ReaderEntity {
    public enum BorrowingErrors {
        UNEXPECTED_ERROR,
        READER_NOT_FOUND,
        MAX_BORROWED_BOOKS_EXCEEDED
        // Future error codes    
    }
    public enum ReturningErrors {
        UNEXPECTED_ERROR,
        READER_NOT_FOUND
        // Future error codes
     }

    /**
     * Validate whether the list of books can be borrowed by the reader.
     * @param reader        The reader trying to borrow the books
     * @param booksToBorrow The list of books to borrow
     * @param borrowedBooks The list of already borrowed books
     * @return              The set of validation failures, is any. Otherwise, an empty set.         
     */
    public Set<BorrowingErrors> bookBorrowingValidator(Long readerId, List<Book> booksToBorrow, List<Book> borrowedBooks);
    
    /**
     * Validate whether the list of books can be returned by the reader.
     * @param reader        The reader trying to return the books
     * @param booksToReturn The list of books to return
     * @param borrowedBooks The list of already borrowed books
     * @return              The set of validation failures, if any. Otherwise, an empty set.        
     */
    public Set<ReturningErrors> bookReturningValidator(Long readerId, List<Book> booksToReturn, List<Book> borrowedBooks); 
}