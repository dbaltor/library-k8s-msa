package reader.adapter.controller.port;

import reader.domain.port.ReaderEntity.BorrowingErrors;
import reader.domain.port.ReaderEntity.ReturningErrors;
import reader.dto.Book;
import reader.dto.Reader;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@RequestMapping(value = "readers",  produces = MediaType.APPLICATION_JSON_VALUE)
public interface ReaderServiceController {
     /**
     * Retrieve the list of readers currently stored in the Reader datastore
     * @param pageNum   the number of the page to be retrieved.
     *                  No pagination is to apply when this is not present
     * @param pageSize  the number of items per page when pagination is being applied
     * @param readerId  the id of the reader currently borrowing the books, if any.
     * @return          the list of readers retrieved
     */
    @GetMapping
    public List<Reader> getReaders(
        @RequestParam("page") Optional<Integer> pageNum,
        @RequestParam("size") Optional<Integer> pageSize,
        @RequestParam("reader") Optional<Long> readerId);

    @PostMapping("commands/load")
    public List<Reader> loadDatabase(@RequestParam Optional<Integer> count);

    @PostMapping(value = "commands/cleanup", produces = MediaType.TEXT_PLAIN_VALUE)
    public String cleanUp();

    @PostMapping("{id}/commands/validatebookborrowing")
    public Set<BorrowingErrors> validateBookBorrowing(
        @PathVariable(name = "id") Long readerId, 
        @RequestBody ValidationRequest validationRequest);

    @PostMapping("{id}/commands/validatebookreturning")
    public Set<ReturningErrors> validateBookReturning(
        @PathVariable(name = "id") Long readerId, 
        @RequestBody ValidationRequest validationRequest);

    @Data
    @NoArgsConstructor @AllArgsConstructor
    public class ValidationRequest {
        private List<Book> booksToValidate;
        private List<Book> borrowedBooksByReader;
    }
}