package library.adapter.controller.port;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

public interface HomeController {
    public static final String BOOKS_TEMPLATE = "Index";
    
    @GetMapping("/")
    public String index(Model model);
    
    @PostMapping("/cleanup")
    @ResponseBody
    public ResponseEntity<String> cleanUp();    
}