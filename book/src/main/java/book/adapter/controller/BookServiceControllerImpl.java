package book.adapter.controller;

import book.dto.Book;
import book.usecase.port.BookService;
import book.usecase.port.ReaderService;
import book.adapter.controller.port.BookServiceController;
import book.domain.port.ReaderEntity;
import book.usecase.exception.BorrowingException;
import book.usecase.exception.ReturningException;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RestController
@RequiredArgsConstructor
public class BookServiceControllerImpl implements BookServiceController{

    private final @NonNull BookService bookService;
    private final @NonNull ReaderService readerService;

    @Override
    public List<Book> getBooks(
        @RequestParam(name = "page") Optional<Integer> pageNum, 
        @RequestParam(name = "size") Optional<Integer> pageSize,
        @RequestParam(name = "reader") Optional<Long> readerId) {
            if (readerId.isPresent()) {
                //retrieve books borrowed by reader
                return bookService.findBooksByReaderId(readerId.get());
            }
            else {
                // Retrieve books
                return bookService.retrieveBooks(pageNum, pageSize);
            }
    }

    @Override
    public List<Book> loadDatabase(@RequestParam Optional<Integer> count) {
        return  bookService.loadDatabase(
            count,
            readerService.retrieveReaders(
                Optional.empty(),
                Optional.empty(),
                Optional.empty())
        );
    }

    @Override
    public String cleanUp() {
        bookService.cleanUpDatabase();
        return "The data have been removed";
    }

    @Override
    public ResponseEntity<Object> borrowBooks(@RequestBody BooksRequest booksRequest) {
            val readerId = booksRequest.readerId;
            val bookIds = booksRequest.bookIds;
            if (bookIds == null || bookIds.length == 0) {
                return ResponseEntity
                        .badRequest()
                        .body("No books provided. Nothing to do.");
            }
            val booksToBorrow = bookService.findBooksByIds(Arrays.asList(bookIds));
            try{ 
                val borrowedBooks = bookService.borrowBooks(booksToBorrow, readerId);
                return ResponseEntity.ok(borrowedBooks);
            } catch(BorrowingException e) {
                val errorMsg = new StringBuilder("Errors found:");
                for(ReaderEntity.BorrowingErrors error : e.errors) {
                    switch (error) {
                        case READER_NOT_FOUND:
                            errorMsg.append(String.format(" *No reader with ID %d has been found.", readerId));
                            break;
                        case MAX_BORROWED_BOOKS_EXCEEDED:
                            errorMsg.append(" *Maximum allowed borrowed books exceeded.");
                            break;
                        default:
                            errorMsg.append(" *Unexpected error.");
                    }
                }
                return ResponseEntity
                        .badRequest()
                        .body(errorMsg.toString());
            }
    }

    @Override
    public ResponseEntity<Object> returnBooks(
        @RequestBody BooksRequest booksRequest) {
            val readerId = booksRequest.readerId;
            val bookIds = booksRequest.bookIds;
            if (bookIds == null || bookIds.length == 0) {
                return ResponseEntity
                    .badRequest()
                    .body("No books provided. Nothing to do.");
            }    
            val booksToReturn = bookService.findBooksByIds(Arrays.asList(bookIds));
            try{ 
                val returnedBooks = bookService.returnBooks(booksToReturn, readerId);
                return ResponseEntity.ok(returnedBooks);
            } catch(ReturningException e) {
                val errorMsg = new StringBuilder("Errors found:");
                for(ReaderEntity.ReturningErrors error : e.errors) {
                    switch (error) {
                        case READER_NOT_FOUND:
                            errorMsg.append(String.format(" *No reader with ID %d has been found.", readerId));
                            break;
                        default:
                            errorMsg.append(" *Unexpected error.");
                    }
                }
                return ResponseEntity
                    .badRequest()
                    .body(errorMsg.toString());
            }
    }
}