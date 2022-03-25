package cz.cvut.fit.honysdan.resource;

import cz.cvut.fit.honysdan.entity.TrainerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Component
public class TrainerResource {

    private final RestTemplate restTemplate;

    private static final String ROOT_RESOURCE_URL = "http://localhost:8080/api/trainer";


    @Autowired
    public TrainerResource(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.rootUri(ROOT_RESOURCE_URL).build();
    }

    public TrainerDTO getTrainerById(Integer id) {
        return restTemplate.getForObject("/{id}", TrainerDTO.class, id);
    }

    public List<TrainerDTO> getAllTrainer() {
        TrainerDTO[] t = restTemplate.getForObject("/", TrainerDTO[].class);
        return Arrays.asList(t);
    }

    public URI createTrainer(TrainerDTO trainer) {
        return restTemplate.postForLocation("/", trainer);
    }

    public List<TrainerDTO> getTrainerByName(String name) {
        TrainerDTO[] p = restTemplate.getForObject("/?name={name}", TrainerDTO[].class, name);
        return Arrays.asList(p);
    }

    public TrainerDTO updateTrainer(Integer id, TrainerDTO trainer) {
        ResponseEntity<TrainerDTO> p = restTemplate.exchange("/{id}", HttpMethod.PUT,
                new HttpEntity<TrainerDTO>(trainer),
                TrainerDTO.class, id);

        return p.getBody();
    }

    public void deleteTrainer(Integer id) {
        restTemplate.delete("/{id}", id);
    }
}
