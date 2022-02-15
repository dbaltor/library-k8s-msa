package library.adapter.controller;

import java.util.Optional;
import java.util.function.Function;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.*;


import org.springframework.beans.factory.annotation.Value;

// Fabric8 Kubernetes client
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
// Classic Kubernetes client
/*import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;*/
import library.dto.UIConfig;

public abstract class BaseControllerImpl {
    @Value("#{new Boolean('${library.red-background:false}')}")
    private boolean FIXED_BACKGROUND;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    protected UIConfig getUIConfig() {
        var instanceIndex = FIXED_BACKGROUND ? -1 : 0;
        boolean isK8s = "kubernetes".equals(activeProfile);
        // Fabric8 Kubernetes client
        if (isK8s && !FIXED_BACKGROUND) {
            try (final KubernetesClient k8s = new DefaultKubernetesClient()) {
                var count= new AtomicInteger(0);
                var pods = k8s.pods().inNamespace("library")
                  .withLabel("app", "library-msa")
                  .list()
                  .getItems()
                  .stream()
                  .map(Pod::getMetadata)
                  .map(ObjectMeta::getName)
                  .collect(toMap(Function.identity(), value -> count.getAndIncrement()));
                var podName = Optional.ofNullable(System.getenv("HOSTNAME")).orElse("");
                instanceIndex = pods.getOrDefault(podName, 0);
            } catch (Exception ex){
                ex.printStackTrace();
                return UIConfig.of(false, Integer.valueOf(0));
            }

            // Classic Kubernetes client
            /*try {
                ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                CoreV1Api coreApi = new CoreV1Api();
                // AppsV1Api appApi = new AppsV1Api();
                    // appApi.listNamespacedDeployment("library", null, null, null, null, "app=library-msa", null, null, null, null)
                        // .getItems().get(0).getStatus().getAvailableReplicas();// .stream().forEach(System.out::println);
                var count= new AtomicInteger(0);
                // var pods = client.pods().inNamespace("library").withLabel("app", "library-msa").list()
                var pods = coreApi.listNamespacedPod("library", null, null, null, null, "app=library-msa", null, null, null, null,null)
                    .getItems().stream()
                        .map(pod -> pod.getMetadata().getName())
                        .collect(toMap(Function.identity(), value -> count.getAndIncrement()));
                
                var podName = Optional.ofNullable(System.getenv("HOSTNAME")).orElse("");
                instanceIndex = pods.getOrDefault(podName, 0);
            } catch (Exception ex){
                ex.printStackTrace();
                return UIConfig.of(false, Integer.valueOf(0));
            }*/

        }
        return UIConfig.of(isK8s, instanceIndex);
    } 
}