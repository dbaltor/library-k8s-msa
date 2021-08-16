package reader.adapter.gateway.config;

import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OptionalFeignFormatterRegistrar implements FeignFormatterRegistrar {
    
    @Override
    public void registerFormatters(FormatterRegistry registry) {
        registry.addConverter(
            Optional.class, 
            String.class, 
            optional -> {
                if (optional.isPresent())
                    return optional.get().toString();
                else
                    return "";
            });
    }
}