package cz.cvut.fit.honysdan.resource;

import cz.cvut.fit.honysdan.entity.MoveDTO;
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
public class MoveResource {

    private final RestTemplate restTemplate;

    private static final String ROOT_RESOURCE_URL = "http://localhost:8080/api/move";


    @Autowired
    public MoveResource(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.rootUri(ROOT_RESOURCE_URL).build();
    }

    public MoveDTO getMoveById(Integer id) {
        return restTemplate.getForObject("/{id}", MoveDTO.class, id);
    }

    public List<MoveDTO> getAllMove() {
        MoveDTO[] p = restTemplate.getForObject("/", MoveDTO[].class);
        return Arrays.asList(p);
    }

    public URI createMove(MoveDTO move) {
        return restTemplate.postForLocation("/", move);
    }

    public MoveDTO getMoveByName(String name) {
        return restTemplate.getForObject("/?name={name}", MoveDTO.class, name);
    }

    public MoveDTO updateMove(Integer id, MoveDTO move) {
        ResponseEntity<MoveDTO> p = restTemplate.exchange("/{id}", HttpMethod.PUT, new HttpEntity<MoveDTO>(move), MoveDTO.class, id);
        return p.getBody();
    }

    public void deleteMove(Integer id) {
        restTemplate.delete("/{id}", id);
    }
}
