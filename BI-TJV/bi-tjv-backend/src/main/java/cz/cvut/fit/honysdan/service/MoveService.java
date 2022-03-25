package cz.cvut.fit.honysdan.service;

import cz.cvut.fit.honysdan.exception.EntityNotFoundException;
import cz.cvut.fit.honysdan.exception.NullValueException;
import cz.cvut.fit.honysdan.exception.UniqueNameConflictException;
import cz.cvut.fit.honysdan.dto.MoveCreateDTO;
import cz.cvut.fit.honysdan.dto.MoveDTO;
import cz.cvut.fit.honysdan.entity.Move;
import cz.cvut.fit.honysdan.repository.MoveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MoveService {

    private final MoveRepository moveRepository;

    @Autowired
    public MoveService(MoveRepository moveRepository) {
        this.moveRepository = moveRepository;
    }

//----------------------------------------------------------------------------------------------------------------------
// TO DTO
//----------------------------------------------------------------------------------------------------------------------

    private MoveDTO toDTO(Move move) {
        return new MoveDTO(move.getId(),
                           move.getName(),
                           move.getDescription());
    }

    private Optional<MoveDTO> toDTO(Optional<Move> move) {
        if (move.isEmpty())
            return Optional.empty();
        return Optional.of(toDTO(move.get()));
    }

//----------------------------------------------------------------------------------------------------------------------
// CRUD
//----------------------------------------------------------------------------------------------------------------------

    // CREATE
    public MoveDTO create(MoveCreateDTO moveCreateDTO) {
        if (moveCreateDTO.getName() == null) {
            throw new NullValueException("Value 'name' can't be null");
        }
        Optional<MoveDTO> find = getByName(moveCreateDTO.getName());
        if (find.isPresent()) {
            throw new UniqueNameConflictException("Move with this name already exists");
        }
        return toDTO(moveRepository.save( new Move (moveCreateDTO.getName(),
                                                    moveCreateDTO.getDescription())));
    }

    // GET ONE BY ID
    public Move getById(int id) {
        return moveRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No such move with id " + id));
    }

    public MoveDTO getByIdAsDTO(int id) {
        return toDTO(getById(id));
    }

    // GET ALL
    public List<MoveDTO> getAll() {
        return moveRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // UPDATE
    public MoveDTO update(int id, MoveCreateDTO moveDTO) {
        if (moveDTO.getName() == null) {
            throw new NullValueException("Value 'name' can't be null");
        }

        Optional<MoveDTO> find = getByName(moveDTO.getName());
        if (find.isPresent()) {
            throw new UniqueNameConflictException("Move with this name already exists");
        }

        Optional<Move> oldMove = moveRepository.findById(id);
        if (oldMove.isEmpty()) {
            throw new EntityNotFoundException("No such move with id " + id);
        }

        Move newMove = oldMove.get();
        if (moveDTO.getName() != null) {
            newMove.setName(moveDTO.getName());
        }
        newMove.setDescription(moveDTO.getDescription());
        return toDTO(newMove);
    }

    // DELETE
    public void deleteById(int id) {
        moveRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No such move with id " + id));
        moveRepository.deleteById(id);
    }

//----------------------------------------------------------------------------------------------------------------------
// EXTRA
//----------------------------------------------------------------------------------------------------------------------

    // GET LIST BY IDS
    public List<Move> getAllByIds(List<Integer> ids) {
        return moveRepository.findAllById(ids);
    }

    // GET BY NAME
    public Optional<MoveDTO> getByName(String name) {
        return toDTO(moveRepository.findByName(name));
    }
}
