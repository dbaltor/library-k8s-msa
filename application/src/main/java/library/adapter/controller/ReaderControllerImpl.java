package library.adapter.controller;

import library.LibraryApplication;
import library.adapter.controller.port.ReaderController;
import library.adapter.gateway.config.Propagate4xxFeignErrorDecoder.Feign4xxResponseException;
import library.usecase.port.ReaderService;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RefreshScope // Required for dynamically updating BaseController.FIXED_BACKGROUND property
@Controller
@RequiredArgsConstructor
public class ReaderControllerImpl extends BaseControllerImpl implements ReaderController {
    private final @NonNull ReaderService readerService;

    @Override
    public String listReaders(
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
            // Retrieve readers and add them to the Model object being returned to ViewResolver
            val readers = readerService.retrieveReaders(pageNum, pageSize, readerId);
            model.addAttribute(READERS_MODEL_NAME, readers);
            
            // Returns the name of the template view to reply this request
            return READERS_TEMPLATE;
    }

    @Override
    public ResponseEntity<String> loadDatabase(Optional<Integer> count) {
        try {
            // load database
            val readers = readerService.loadDatabase(count);
            return ResponseEntity.ok(String.format("Reader database loaded with %d records", readers.size()));
        } catch (Feign4xxResponseException e) {
            return new ResponseEntity<>(e.getBody(), HttpStatus.valueOf(e.getStatus()));
        }
    }
}
