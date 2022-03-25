package cz.cvut.fit.honysdan.controller;

import cz.cvut.fit.honysdan.dto.TrainerCreateDTO;
import cz.cvut.fit.honysdan.dto.TrainerDTO;
import cz.cvut.fit.honysdan.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/trainer")
public class TrainerController {

    private final TrainerService trainerService;

    @Autowired
    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

//----------------------------------------------------------------------------------------------------------------------
// CRUD
//----------------------------------------------------------------------------------------------------------------------

    // CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TrainerDTO> create(@RequestBody TrainerCreateDTO trainer) {
        TrainerDTO in = trainerService.create(trainer);
        return ResponseEntity.created(Link.of("http://localhost:8080/api/trainer/" + in.getId()).toUri()).body(in);
    }

    // GET TRAINER BY ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TrainerDTO getById(@PathVariable int id) {
        return trainerService.getByIdAsDTO(id);
    }

    // GET ALL
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TrainerDTO> getAll() {
        return trainerService.getAll();
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<TrainerDTO> updateById(@PathVariable int id, @RequestBody TrainerCreateDTO newTrainer) {
        TrainerDTO in = trainerService.update(id, newTrainer);
        return ResponseEntity.created(Link.of("http://localhost:8080/api/trainer/" + in.getId()).toUri()).body(in);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable int id) {
        trainerService.deleteById(id);
    }

//----------------------------------------------------------------------------------------------------------------------
// EXTRA
//----------------------------------------------------------------------------------------------------------------------

    // GET LIST OF TRAINERS BY NAME
    @GetMapping(params = {"name"})
    @ResponseStatus(HttpStatus.OK)
    public List<TrainerDTO> getByName(@RequestParam String name) {
        List<TrainerDTO> list = trainerService.getByName(name);
        if (list.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No such trainer with name " + name);
        }
        return list;
    }
}
