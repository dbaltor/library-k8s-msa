package library.adapter.controller.port;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface BookController {
    public static final String BOOKS_MODEL_NAME = "books";
    public static final String READER_MODEL_NAME = "readerId";
    public static final String BOOKS_TEMPLATE = "BooksList";    

    @RequiredArgsConstructor(staticName = "of")
    public class BooksRequest {
        public @NonNull Long readerId;
        public @NonNull Long bookIds[];
    }
    
    @GetMapping("/listbooks")
    public String listbooks(
        @RequestParam("page") Optional<Integer> pageNum, 
        @RequestParam("size") Optional<Integer> pageSize,
        @RequestParam("reader") Optional<Long> readerId,
        Model model);

    @PostMapping("/loadbooks")
    @ResponseBody
    public ResponseEntity<String> loadDatabase(@RequestParam Optional<Integer> count);

    @PostMapping("/borrowbooks")
    @ResponseBody
    public ResponseEntity<String> borrowBooks(@RequestBody BooksRequest booksRequest);

    @PostMapping("/returnbooks")
    @ResponseBody
    public  ResponseEntity<String> returnBooks(@RequestBody BooksRequest booksRequest);
}

