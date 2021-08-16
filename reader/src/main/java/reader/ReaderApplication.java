package reader;

import reader.usecase.port.ReaderService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
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
public class ReaderApplication {

    private final @NonNull ReaderService readerService;

    public static void main(String[] args){
        SpringApplication.run(ReaderApplication.class, args);
    }

    @Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) 
    {
        return args -> {
            Optional<Integer> nReaders = Optional.empty();
            
            // Reading command line parameters      
            // parameter key's length
            val PARAM_KEY_LENGHT = 3;
            if (args.length > 0) {
                for (String arg: args) {
                    if (arg.length() >= PARAM_KEY_LENGHT && arg.substring(0,PARAM_KEY_LENGHT).equals("-r="))
                        // get number of readers
                        try{
                            nReaders = Optional.of(Integer.parseInt(arg.substring(PARAM_KEY_LENGHT)));
                        } catch (NumberFormatException nfe) {
                            log.info("Invalid format number provided! Will load the DEFAULT number of readers");
                        }
                    else 
                        log.info(
                            String.format("Not recognised parameter ignored: %s", arg));
                }
            }
            //val readers = readerService.loadDatabase(nReaders);
        };
    }

    @PreDestroy
    public void onExit() {
        readerService.cleanUpDatabase();
    }
}