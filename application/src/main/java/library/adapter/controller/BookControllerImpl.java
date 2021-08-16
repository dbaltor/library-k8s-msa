package library.adapter.controller;

import library.LibraryApplication;
import library.adapter.controller.port.BookController;
import library.adapter.gateway.config.Propagate4xxFeignErrorDecoder.Feign4xxResponseException;
import library.usecase.port.BookService;
import library.usecase.port.BookService.BookServiceRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RefreshScope // Required for dynamically updating BaseController.FIXED_BACKGROUND property
@Controller
@RequiredArgsConstructor
public class BookControllerImpl extends BaseControllerImpl implements BookController {
    private final @NonNull BookService bookService;

    @Override
    public String listbooks(
        Optional<Integer> pageNum, 
        Optional<Integer> pageSize,
        Optional<Long> readerId,
        Model model) {
            // Set background color of UI
            model.addAttribute(LibraryApplication.UI_CONFIG_NAME, getUIConfig());

            if (readerId.isPresent()) {
                //Add reader ID to the Model object being returned to ViewResolver
                model.addAttribute(READER_MODEL_NAME, readerId.get());
            }
            // Retrieve books and add them to the Model object being returned to ViewResolver
            val books = bookService.retrieveBooks(pageNum, pageSize, readerId);
            model.addAttribute(BOOKS_MODEL_NAME, books);
            
            // Returns the name of the template view to reply this request
            return BOOKS_TEMPLATE;
    }

    @Override
    public ResponseEntity<String> loadDatabase(Optional<Integer> count) {
        try {
            // load database
            val books = bookService.loadDatabase(count);
            return ResponseEntity.ok(String.format("Book database loaded with %d records", books.size()));
        } catch (Feign4xxResponseException e) {
            return new ResponseEntity<>(e.getBody(), HttpStatus.valueOf(e.getStatus()));
        }
    }

    @Override
    public ResponseEntity<String> borrowBooks(BooksRequest booksRequest) {
        try {
            val books = bookService.borrowBooks(
                BookServiceRequest.of(
                    booksRequest.readerId,
                    booksRequest.bookIds));
            return ResponseEntity.ok(String.format(
                "The reader ID %d has borrowed %d book(s).", 
                booksRequest.readerId, 
                books.size()));
        } catch (Feign4xxResponseException e) {
            return new ResponseEntity<>(e.getBody(), HttpStatus.valueOf(e.getStatus()));
        }
    }

    @Override
    public  ResponseEntity<String> returnBooks(BooksRequest booksRequest) {
        try {
            val books = bookService.returnBooks(
                BookServiceRequest.of(
                    booksRequest.readerId,
                    booksRequest.bookIds));
            return ResponseEntity.ok(String.format(
                "The reader ID %d has returned %d book(s).", 
                booksRequest.readerId, 
                books.size()));
        } catch (Feign4xxResponseException e) {
            return new ResponseEntity<>(e.getBody(), HttpStatus.valueOf(e.getStatus()));
        }
    }
}

