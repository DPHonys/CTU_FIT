package cz.cvut.fit.honysdan.service;

import cz.cvut.fit.honysdan.entity.Trainer;
import cz.cvut.fit.honysdan.dto.TrainerCreateDTO;
import cz.cvut.fit.honysdan.dto.TrainerDTO;
import cz.cvut.fit.honysdan.exception.EntityNotFoundException;
import cz.cvut.fit.honysdan.exception.NullValueException;
import cz.cvut.fit.honysdan.repository.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrainerService {

    private final TrainerRepository trainerRepository;

    @Autowired
    public TrainerService(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

//----------------------------------------------------------------------------------------------------------------------
// TO DTO
//----------------------------------------------------------------------------------------------------------------------
    private TrainerDTO toDTO(Trainer trainer) {
        return new TrainerDTO(trainer.getId(),
                              trainer.getName(),
                              trainer.getNumber(),
                              trainer.getAddress());
    }

//----------------------------------------------------------------------------------------------------------------------
// CRUD
//----------------------------------------------------------------------------------------------------------------------

    // CREATE
    public TrainerDTO create(TrainerCreateDTO trainerCreateDTO) {
        if (trainerCreateDTO.getName() == null) {
            throw new NullValueException("Value 'name' can't be null");
        }
        return toDTO (
                trainerRepository.save( new Trainer(trainerCreateDTO.getName(),
                                                    trainerCreateDTO.getNumber(),
                                                    trainerCreateDTO.getAddress())));
    }

    // GET ONE BY ID
    public Trainer getById(int id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No such trainer with id " + id));
    }

    public TrainerDTO getByIdAsDTO(int id) {
        return toDTO(getById(id));
    }

    // GET ALL
    public List<TrainerDTO> getAll() {
        return trainerRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // UPDATE
    public TrainerDTO update(int id, TrainerCreateDTO trainerDTO) {
        if (trainerDTO.getName() == null) {
            throw new NullValueException("Value 'name' can't be null");
        }

        Optional<Trainer> oldTrainer = trainerRepository.findById(id);
        if (oldTrainer.isEmpty()) {
            throw new EntityNotFoundException("No such trainer with id " + id);
        }

        Trainer newTrainer = oldTrainer.get();
        newTrainer.setName(trainerDTO.getName());
        newTrainer.setAddress(trainerDTO.getAddress());
        newTrainer.setNumber(trainerDTO.getNumber());
        return toDTO(newTrainer);
    }

    // DELETE
    public void deleteById(int id) {
        trainerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No such trainer with id " + id));
        trainerRepository.deleteById(id);
    }

//----------------------------------------------------------------------------------------------------------------------
// EXTRA
//----------------------------------------------------------------------------------------------------------------------

    // GET LIST BY NAME
    public List<TrainerDTO> getByName(String name) {
        return trainerRepository.findByName(name).stream().map(this::toDTO).collect(Collectors.toList());
    }
}
