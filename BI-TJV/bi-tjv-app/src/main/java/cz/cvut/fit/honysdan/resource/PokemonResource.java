package cz.cvut.fit.honysdan.resource;

import cz.cvut.fit.honysdan.entity.PokemonDTO;
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
public class PokemonResource {

    private final RestTemplate restTemplate;

    private static final String ROOT_RESOURCE_URL = "http://localhost:8080/api/pokemon";


    @Autowired
    public PokemonResource(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.rootUri(ROOT_RESOURCE_URL).build();
    }

    public PokemonDTO getPokemonById(Integer id) {
        return restTemplate.getForObject("/{id}", PokemonDTO.class, id);
    }

    public List<PokemonDTO> getAllPokemon() {
        PokemonDTO[] p = restTemplate.getForObject("/", PokemonDTO[].class);
        return Arrays.asList(p);
    }

    public URI createPokemon(PokemonDTO pokemon) {
        return restTemplate.postForLocation("/", pokemon);
    }

    public List<PokemonDTO> getPokemonByName(String name) {
        PokemonDTO[] p = restTemplate.getForObject("/?name={name}", PokemonDTO[].class, name);
        return Arrays.asList(p);
    }

    public PokemonDTO updatePokemon(Integer id, PokemonDTO pokemon) {
        ResponseEntity<PokemonDTO> p = restTemplate.exchange("/{id}", HttpMethod.PUT,
                                                                new HttpEntity<PokemonDTO>(pokemon), PokemonDTO.class, id);
        return p.getBody();
    }

    public void deletePokemon(Integer id) {
        restTemplate.delete("/{id}", id);
    }

    public PokemonDTO addMove(Integer pokemonId, Integer moveId) {
        return restTemplate.postForObject("/{pokemonId}/add?id={moveId}", null, PokemonDTO.class, pokemonId, moveId);
    }

    public PokemonDTO removeMove(Integer pokemonId, Integer moveId) {
        return restTemplate.postForObject("/{pokemonId}/remove?id={moveId}", null, PokemonDTO.class, pokemonId, moveId);
    }
}
