package library.adapter.gateway.port;

import library.dto.Book;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@FeignClient(name = "library-book-service")
@RequestMapping("books")
public interface BookClient {
    @ToString
    @RequiredArgsConstructor(staticName = "of")
    public class BooksRequest {
        public @NonNull Long readerId;
        public @NonNull Long bookIds[];
    }

    @GetMapping
    public List<Book> getBooks(
        @RequestParam("page") Optional<Integer> pageNum,
        @RequestParam("size") Optional<Integer> pageSize,
        @RequestParam("reader") Optional<Long> readerId);

    @PostMapping("commands/load")
    public List<Book> loadDatabase(@RequestParam Optional<Integer> count);

    @PostMapping("commands/cleanup")
    public String cleanUp();

    @PostMapping("commands/borrow")
    public List<Book> borrowBooks(@RequestBody BooksRequest booksRequest);

    @PostMapping("commands/return")
    public List<Book> returnBooks(@RequestBody BooksRequest booksRequest);
}

