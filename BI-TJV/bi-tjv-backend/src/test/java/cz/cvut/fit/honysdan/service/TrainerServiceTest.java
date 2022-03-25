package cz.cvut.fit.honysdan.service;

import cz.cvut.fit.honysdan.dto.TrainerCreateDTO;
import cz.cvut.fit.honysdan.dto.TrainerDTO;
import cz.cvut.fit.honysdan.entity.Trainer;
import cz.cvut.fit.honysdan.exception.EntityNotFoundException;
import cz.cvut.fit.honysdan.exception.NullValueException;
import cz.cvut.fit.honysdan.repository.TrainerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static cz.cvut.fit.honysdan.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@SpringBootTest
class TrainerServiceTest {

    @Autowired
    private TrainerService trainerService;

    @MockBean
    private TrainerRepository trainerRepository;

//----------------------------------------------------------------------------------------------------------------------
// CREATE TEST
//----------------------------------------------------------------------------------------------------------------------

    // Successful create
    @Test
    void trainerSuccessfulCreate() throws NullValueException {
        given(trainerRepository.save(trainer1)).willReturn(trainer1);

        TrainerCreateDTO trainerInserted = createDTOInstance(trainer1);
        TrainerDTO trainerExpected = toDTO(trainer1);

        assertEquals(trainerExpected, trainerService.create(trainerInserted));
        verify(trainerRepository, atLeastOnce()).save(trainer1);
    }

    // Null value in request
    @Test
    void nullNameValueInCreate() {
        Trainer dummy = new Trainer(null, 555, "address");
        TrainerCreateDTO dummyDTO = createDTOInstance(dummy);

        NullValueException exception =
                assertThrows(NullValueException.class, () -> trainerService.create(dummyDTO));
        assertEquals("Value 'name' can't be null", exception.getMessage());
    }

//----------------------------------------------------------------------------------------------------------------------
// GET BY ID TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Trainer is present > successful find
    @Test
    void getByIdTrainerIsPresent() {
        given(trainerRepository.findById(trainer1.getId())).willReturn(Optional.of(trainer1));

        assertEquals(trainer1, trainerService.getById(trainer1.getId()));
        verify(trainerRepository, atLeastOnce()).findById(trainer1.getId());
    }

    // Trainer is not present > expecting exception
    @Test
    void getByIdTrainerIsNotPresent() {
        given(trainerRepository.findById(any(Integer.class))).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> trainerService.getById(any(Integer.class)));
        verify(trainerRepository, atLeastOnce()).findById(any(Integer.class));
    }

//----------------------------------------------------------------------------------------------------------------------
// GET BY ID AS DTO TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Trainer is present > successful find
    @Test
    void getByIdAsDTOTrainerIsPresent() {
        given(trainerRepository.findById(trainer1.getId())).willReturn(Optional.of(trainer1));

        assertEquals(toDTO(trainer1), trainerService.getByIdAsDTO(trainer1.getId()));
        verify(trainerRepository, atLeastOnce()).findById(trainer1.getId());
    }

    // Trainer is not present > expecting exception
    @Test
    void getByIdAsDTOTrainerIsNotPresent() {
        given(trainerRepository.findById(any(Integer.class))).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> trainerService.getByIdAsDTO(any(Integer.class)));
        verify(trainerRepository, atLeastOnce()).findById(any(Integer.class));
    }

//----------------------------------------------------------------------------------------------------------------------
// GET ALL TEST
//----------------------------------------------------------------------------------------------------------------------

    @Test
    void trainerGetAll() {
        given(trainerRepository.findAll()).willReturn(allTrainers);
        List<TrainerDTO> returned = trainerService.getAll();

        assertEquals(allTrainers.size(), returned.size());
        for (int i = 0; i < returned.size(); i++)
            assertEquals(toDTO(allTrainers.get(i)), returned.get(i));

        verify(trainerRepository, atLeastOnce()).findAll();
    }

//----------------------------------------------------------------------------------------------------------------------
// UPDATE TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Successful update
    @Test
    void trainerSuccessfulUpdate() throws EntityNotFoundException,
                                          NullValueException {
        TrainerCreateDTO newTrainer = createDTOInstance(trainer2);
        given(trainerRepository.findById(trainer1.getId())).willReturn(Optional.of(trainer1));

        TrainerDTO updatedTrainer = trainerService.update(trainer1.getId(), newTrainer);

        assertEquals(trainer1.getId(), updatedTrainer.getId());
        assertEquals(newTrainer.getName(), updatedTrainer.getName());
        assertEquals(newTrainer.getNumber(), updatedTrainer.getNumber());
        assertEquals(newTrainer.getAddress(), updatedTrainer.getAddress());

        verify(trainerRepository, atLeastOnce()).findById(trainer1.getId());
    }

    // Trainer to update not found > expecting exception
    @Test
    void updateMissingTrainerException() {
        TrainerCreateDTO newTrainer = createDTOInstance(trainer2);
        assertThrows(EntityNotFoundException.class, () -> trainerService.update(trainer1.getId(), newTrainer));
    }

    // Null value in request
    @Test
    void nullNameValueInUpdate() {
        Trainer dummy = new Trainer(null, 555, "address");
        TrainerCreateDTO dummyDTO = createDTOInstance(dummy);

        NullValueException exception =
                assertThrows(NullValueException.class, () -> trainerService.update(trainer1.getId(), dummyDTO));
        assertEquals("Value 'name' can't be null", exception.getMessage());
    }

//----------------------------------------------------------------------------------------------------------------------
// DELETE BY ID TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Successful delete
    @Test
    void trainerSuccessfulDelete() {
        given(trainerRepository.findById(trainer1.getId())).willReturn(Optional.of(trainer1));
        trainerService.deleteById(trainer1.getId());
        verify(trainerRepository, atLeastOnce()).deleteById(trainer1.getId());
    }

    // Trainer to delete not found > expecting exception
    @Test
    void deleteMissingTrainerException() {
        assertThrows(EntityNotFoundException.class, () -> trainerService.deleteById(trainer1.getId()));
    }

//----------------------------------------------------------------------------------------------------------------------
// GET BY NAME TEST
//----------------------------------------------------------------------------------------------------------------------

    @Test
    void trainerGetByName() {
        given(trainerRepository.findByName(trainerWithName.get(0).getName())).willReturn(trainerWithName);
        List<TrainerDTO> returned = trainerService.getByName(trainerWithName.get(0).getName());

        assertEquals(trainerWithName.size(), returned.size());
        for (int i = 0; i < returned.size(); i++)
            assertEquals(toDTO(trainerWithName.get(i)), returned.get(i));

        verify(trainerRepository, atLeastOnce()).findByName(trainerWithName.get(0).getName());
    }

}