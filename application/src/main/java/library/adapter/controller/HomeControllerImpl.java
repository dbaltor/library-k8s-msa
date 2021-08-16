package library.adapter.controller;

import library.LibraryApplication;
import library.adapter.controller.port.HomeController;
import library.usecase.port.BookService;
import library.usecase.port.ReaderService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;

@RefreshScope // Required for dynamically updating BaseController.FIXED_BACKGROUND property
@Controller
@RequiredArgsConstructor
public class HomeControllerImpl extends BaseControllerImpl implements HomeController {
    private final @NonNull BookService bookService;
    private final @NonNull ReaderService readerService;
    
    @Override
    public String index(Model model) {
        // Set background color of response page
        model.addAttribute(LibraryApplication.UI_CONFIG_NAME, getUIConfig());
        return BOOKS_TEMPLATE;
    }
    
    @Override
    public ResponseEntity<String> cleanUp() {
        var error = false;
        var returnMsg = new StringBuilder();
        try {
            readerService.cleanUpDatabase();
            returnMsg.append("Reader data have been removed.\n");
        } catch (RuntimeException e) {
            error = true;
            returnMsg.append("Error detected when trying to remove reader data.\n");
        }
        try {
            bookService.cleanUpDatabase();
            returnMsg.append("Book data have been removed.\n");
        } catch (RuntimeException e) {
            error = true;
            returnMsg.append("Error detected when trying to remove book data.\n");
        }
        if (error) {
            return new ResponseEntity<>(returnMsg.toString(), HttpStatus.MULTI_STATUS);
        }
        else { 
            return ResponseEntity.ok("All data have been removed.");
        }
    }
}