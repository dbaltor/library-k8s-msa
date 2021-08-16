package library.adapter.gateway.port;

import library.dto.Reader;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "library-reader-service")
@RequestMapping("readers")
public interface ReaderClient {
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

    @PostMapping("commands/cleanup")
    public String cleanUp();
}