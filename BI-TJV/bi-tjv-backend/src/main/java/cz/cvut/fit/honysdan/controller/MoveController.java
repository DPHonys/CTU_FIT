package cz.cvut.fit.honysdan.controller;

import cz.cvut.fit.honysdan.exception.EntityNotFoundException;
import cz.cvut.fit.honysdan.dto.MoveCreateDTO;
import cz.cvut.fit.honysdan.dto.MoveDTO;
import cz.cvut.fit.honysdan.service.MoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/move")
public class MoveController {
    private final MoveService moveService;

    @Autowired
    public MoveController(MoveService moveService) {
        this.moveService = moveService;
    }

//----------------------------------------------------------------------------------------------------------------------
// CRUD
//----------------------------------------------------------------------------------------------------------------------

    // CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MoveDTO> create(@RequestBody MoveCreateDTO move) {
        MoveDTO dto = moveService.create(move);
        return ResponseEntity.created(Link.of("http://localhost:8080/api/move/" + dto.getId()).toUri()).body(dto);
    }

    // GET MOVE BY ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MoveDTO getById(@PathVariable int id) {
        return moveService.getByIdAsDTO(id);
    }

    // GET ALL
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MoveDTO> getAll() {
        return moveService.getAll();
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<MoveDTO> updateById(@PathVariable int id, @RequestBody MoveCreateDTO newMove) {
        MoveDTO updated = moveService.update(id, newMove);
        return ResponseEntity.ok().header("Location",
                Link.of("http://localhost:8080/api/move/" + updated.getId()).toUri().toString()).body(updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable int id) {
        moveService.deleteById(id);
    }

//----------------------------------------------------------------------------------------------------------------------
// EXTRA
//----------------------------------------------------------------------------------------------------------------------

    // GET MOVE BY NAME
    @GetMapping(params = {"name"})
    @ResponseStatus(HttpStatus.OK)
    public MoveDTO getByName(@RequestParam String name) {
        return moveService.getByName(name)
                .orElseThrow(() -> new EntityNotFoundException("No such move with name '" + name + "'"));
    }
}
