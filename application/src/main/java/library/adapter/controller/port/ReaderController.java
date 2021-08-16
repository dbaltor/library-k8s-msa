package library.adapter.controller.port;

import java.util.Optional;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

public interface ReaderController {
    public static final String READERS_MODEL_NAME = "readers";
    public static final String READER_MODEL_NAME = "readerId";
    public static final String READERS_TEMPLATE = "ReadersList";

    @GetMapping("/listreaders")
    public String listReaders(
        @RequestParam("page") Optional<Integer> pageNum,
        @RequestParam("size") Optional<Integer> pageSize,
        @RequestParam("reader") Optional<Long> readerId,
        Model model);

    @PostMapping("/loadreaders")
    @ResponseBody
    public ResponseEntity<String> loadDatabase(@RequestParam Optional<Integer> count);
}
