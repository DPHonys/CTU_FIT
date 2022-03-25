package cz.cvut.fit.honysdan.controller;

import cz.cvut.fit.honysdan.dto.PokemonCreateDTO;
import cz.cvut.fit.honysdan.dto.PokemonDTO;
import cz.cvut.fit.honysdan.service.PokemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {
    private final PokemonService pokemonService;

    @Autowired
    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

//----------------------------------------------------------------------------------------------------------------------
// CRUD
//----------------------------------------------------------------------------------------------------------------------

    // CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PokemonDTO> create(@RequestBody PokemonCreateDTO pokemon) {
        PokemonDTO created = pokemonService.create(pokemon);
        return ResponseEntity.created(Link.of("http://localhost:8080/api/pokemon/" +
                created.getId()).toUri()).body(created);
    }

    // GET POKEMON BY ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PokemonDTO getById(@PathVariable int id) {
        return pokemonService.getByIdAsDTO(id);
    }

    // GET ALL
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PokemonDTO> getAll() {
        return pokemonService.getAll();
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<PokemonDTO> updateById(@PathVariable int id,
                                                 @RequestBody PokemonCreateDTO newPokemon) {
        PokemonDTO dto = pokemonService.update(id, newPokemon);
        return ResponseEntity.created(Link.of("http://localhost:8080/api/pokemon/" + dto.getId()).toUri()).body(dto);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable int id) {
        pokemonService.deleteById(id);
    }

//----------------------------------------------------------------------------------------------------------------------
// EXTRA
//----------------------------------------------------------------------------------------------------------------------

    // GET LIST OF POKEMON BY NAME
    @GetMapping(params = {"name"})
    @ResponseStatus(HttpStatus.OK)
    public List<PokemonDTO> getByName(@RequestParam String name) {
        List<PokemonDTO> list = pokemonService.getByName(name);
        if (list.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No such pokemon with name " + name);
        }
        return list;
    }

    // ADD MOVE
    @PostMapping("/{pokemonId}/add")
    @ResponseStatus(HttpStatus.OK)
    public PokemonDTO addMove(@PathVariable int pokemonId, @RequestParam int id) {
        return pokemonService.addMove(pokemonId, id);
    }

    // REMOVE MOVE
    @PostMapping("/{pokemonId}/remove")
    @ResponseStatus(HttpStatus.OK)
    public PokemonDTO removeMove(@PathVariable int pokemonId, @RequestParam int id) {
        return pokemonService.removeMove(pokemonId, id);
    }

}
