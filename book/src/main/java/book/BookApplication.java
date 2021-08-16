package book;

import book.usecase.port.BookService;
//import book.usecase.port.ReaderService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.Optional;

import javax.annotation.PreDestroy;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@RequiredArgsConstructor @Slf4j
public class BookApplication {
    private final @NonNull BookService bookService;
    //private final @NonNull ReaderService readerService;

    public static void main(String[] args){
        SpringApplication.run(BookApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) 
    {
        return args -> {
            Optional<Integer> nBooks = Optional.empty();
            
            // Reading command line parameters      
            // parameter key's length
            val PARAM_KEY_LENGHT = 3;
            if (args.length > 0) {
                for (String arg: args) {
                    if (arg.length() >= PARAM_KEY_LENGHT && arg.substring(0,PARAM_KEY_LENGHT).equals("-b="))
                        // get number of books
                        try {
                            nBooks = Optional.of(Integer.parseInt(arg.substring(PARAM_KEY_LENGHT)));
                        } catch (NumberFormatException nfe) {
                            log.info("Invalid format number provided! Will load the DEFAULT number of books");
                        }
                    else 
                        log.info(
                            String.format("Not recognised parameter ignored: %s", arg));
                }
            }
            /*val readers = readerService.retrieveReaders(
                Optional.empty(), 
                Optional.empty(), 
                Optional.empty());
            bookService.loadDatabase(nBooks, readers);*/
        };
    }

    @PreDestroy
    public void onExit() {
        bookService.cleanUpDatabase();
    }
}