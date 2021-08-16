package library.adapter.controller;

import java.util.Optional;
import java.util.function.Function;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.*;


import org.springframework.beans.factory.annotation.Value;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import library.dto.UIConfig;

public abstract class BaseControllerImpl {
    @Value("#{new Boolean('${library.red-background:false}')}")
    private boolean FIXED_BACKGROUND;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    protected UIConfig getUIConfig() {
        var instanceIndex = FIXED_BACKGROUND ? -1 : 0;
        boolean isK8s = "kubernetes".equals(activeProfile);
        if (isK8s && !FIXED_BACKGROUND) {
            try {
                ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                CoreV1Api coreApi = new CoreV1Api();
                // AppsV1Api appApi = new AppsV1Api();
                    // appApi.listNamespacedDeployment("library", null, null, null, null, "app=library-msa", null, null, null, null)
                        // .getItems().get(0).getStatus().getAvailableReplicas();// .stream().forEach(System.out::println);
                var count= new AtomicInteger(0);    
                var pods = coreApi.listNamespacedPod("library", null, null, null, null, "app=library-msa", null, null, null, null)
                    .getItems().stream()
                        .map(pod -> pod.getMetadata().getName())
                        .collect(toMap(Function.identity(), value -> count.getAndIncrement()));
                
                var podName = Optional.ofNullable(System.getenv("HOSTNAME")).orElse("");
                instanceIndex = pods.getOrDefault(podName, 0);
            } catch (Exception ex){
                ex.printStackTrace();
                return UIConfig.of(false, Integer.valueOf(0));
            }

        }
        return UIConfig.of(isK8s, instanceIndex);
    } 
}