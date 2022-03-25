package cz.cvut.fit.honysdan.service;

import cz.cvut.fit.honysdan.dto.MoveCreateDTO;
import cz.cvut.fit.honysdan.dto.MoveDTO;
import cz.cvut.fit.honysdan.entity.Move;
import cz.cvut.fit.honysdan.exception.EntityNotFoundException;
import cz.cvut.fit.honysdan.exception.NullValueException;
import cz.cvut.fit.honysdan.exception.UniqueNameConflictException;
import cz.cvut.fit.honysdan.repository.MoveRepository;
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
class MoveServiceTest {

    @Autowired
    private MoveService moveService;

    @MockBean
    private MoveRepository moveRepository;

//----------------------------------------------------------------------------------------------------------------------
// CREATE TEST
//----------------------------------------------------------------------------------------------------------------------

    // Successful create
    @Test
    void moveSuccessfulCreate() throws UniqueNameConflictException, NullValueException {
        given(moveRepository.save(move1)).willReturn(move1);

        MoveCreateDTO moveInserted = createDTOInstance(move1);
        MoveDTO moveExpected = toDTO(move1);

        assertEquals(moveExpected, moveService.create(moveInserted));
        verify(moveRepository, atLeastOnce()).save(move1);
    }

    // Not unique > expecting exception
    @Test
    void notUniqueCreate() {
        given(moveRepository.findByName(move1.getName())).willReturn(Optional.of(move1));

        MoveCreateDTO moveInserted = createDTOInstance(move1);

        assertThrows(UniqueNameConflictException.class, () -> moveService.create(moveInserted));
    }

    // Null value in request
    @Test
    void nullNameValueInCreate() {
        Move dummy = new Move(null, "not null description");
        MoveCreateDTO dummyDTO = createDTOInstance(dummy);

        NullValueException exception =
                assertThrows(NullValueException.class, () -> moveService.create(dummyDTO));
        assertEquals("Value 'name' can't be null", exception.getMessage());
    }

//----------------------------------------------------------------------------------------------------------------------
// GET BY ID TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Move is present > successful find
    @Test
    void getByIdMoveIsPresent() {
        given(moveRepository.findById(move1.getId())).willReturn(Optional.of(move1));

        assertEquals(move1, moveService.getById(move1.getId()));
        verify(moveRepository, atLeastOnce()).findById(move1.getId());
    }

    // Move is not present > expecting exception
    @Test
    void getByIdMoveIsNotPresent() {
        given(moveRepository.findById(any(Integer.class))).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> moveService.getById(any(Integer.class)));
        verify(moveRepository, atLeastOnce()).findById(any(Integer.class));
    }

//----------------------------------------------------------------------------------------------------------------------
// GET BY ID AS DTO TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Move is present > successful find
    @Test
    void getByIdAsDTOMoveIsPresent() {
        given(moveRepository.findById(move1.getId())).willReturn(Optional.of(move1));

        assertEquals(toDTO(move1), moveService.getByIdAsDTO(move1.getId()));
        verify(moveRepository, atLeastOnce()).findById(move1.getId());
    }

    // Move is not present > expecting exception
    @Test
    void getByIdAsDTOMoveIsNotPresent() {
        given(moveRepository.findById(any(Integer.class))).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> moveService.getByIdAsDTO(any(Integer.class)));
        verify(moveRepository, atLeastOnce()).findById(any(Integer.class));
    }

//----------------------------------------------------------------------------------------------------------------------
// GET ALL TEST
//----------------------------------------------------------------------------------------------------------------------

    @Test
    void moveGetAll() {
        given(moveRepository.findAll()).willReturn(allMoves);
        List<MoveDTO> returned = moveService.getAll();

        assertEquals(allMoves.size(), returned.size());
        for (int i = 0; i < returned.size(); i++)
            assertEquals(toDTO(allMoves.get(i)), returned.get(i));

        verify(moveRepository, atLeastOnce()).findAll();
    }

//----------------------------------------------------------------------------------------------------------------------
// UPDATE TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Successful update
    @Test
    void moveSuccessfulUpdate() throws UniqueNameConflictException,
            EntityNotFoundException,
                                       NullValueException {
        MoveCreateDTO newMove = createDTOInstance(move2);
        given(moveRepository.findById(move1.getId())).willReturn(Optional.of(move1));

        MoveDTO updatedMove = moveService.update(move1.getId(), newMove);

        assertEquals(move1.getId(), updatedMove.getId());
        assertEquals(newMove.getName(), updatedMove.getName());
        assertEquals(newMove.getDescription(), updatedMove.getDescription());

        verify(moveRepository, atLeastOnce()).findById(move1.getId());
    }

    // Move to update not found > expecting exception
    @Test
    void updateMissingMoveException() {
        MoveCreateDTO newMove = createDTOInstance(move2);
        assertThrows(EntityNotFoundException.class, () -> moveService.update(move1.getId(), newMove));
    }

    // Not unique > expecting exception
    @Test
    void notUniqueUpdate() {
        given(moveRepository.findByName(move1.getName())).willReturn(Optional.of(move1));

        MoveCreateDTO newMove = createDTOInstance(move1);
        assertThrows(UniqueNameConflictException.class, () -> moveService.update(move1.getId(), newMove));
    }

    // Null value in request
    @Test
    void nullNameValueInUpdate() {
        Move dummy = new Move(null, "not null description");
        MoveCreateDTO dummyDTO = createDTOInstance(dummy);

        NullValueException exception =
                assertThrows(NullValueException.class, () -> moveService.update(move1.getId(), dummyDTO));
        assertEquals("Value 'name' can't be null", exception.getMessage());
    }

//----------------------------------------------------------------------------------------------------------------------
// DELETE BY ID TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Successful delete
    @Test
    void trainerSuccessfulDelete() throws EntityNotFoundException {
        given(moveRepository.findById(move1.getId())).willReturn(Optional.of(move1));
        moveService.deleteById(move1.getId());
        verify(moveRepository, atLeastOnce()).deleteById(move1.getId());
    }

    // Move to delete not found > expecting exception
    @Test
    void deleteMissingMoveException() {
        assertThrows(EntityNotFoundException.class, () -> moveService.deleteById(move1.getId()));
    }

//----------------------------------------------------------------------------------------------------------------------
// GET ALL BY IDS TEST
//----------------------------------------------------------------------------------------------------------------------

    @Test
    void moveGetAllByIds() {
        List<Move> expected = List.of(move1, move2);
        List<Integer> inserted = List.of(move1.getId(), move2.getId());

        given(moveRepository.findAllById(inserted)).willReturn(expected);

        List<Move> returned = moveService.getAllByIds(inserted);

        assertEquals(expected.size(), returned.size());
        for (int i = 0; i < returned.size(); i++)
            assertEquals(expected.get(i), returned.get(i));

        verify(moveRepository, atLeastOnce()).findAllById(inserted);
    }

//----------------------------------------------------------------------------------------------------------------------
// GET BY NAME TEST
//----------------------------------------------------------------------------------------------------------------------

    @Test
    void moveGetByName() {
        given(moveRepository.findByName(move1.getName())).willReturn(Optional.of(move1));

        assertEquals(Optional.of(toDTO(move1)), moveService.getByName(move1.getName()));
        verify(moveRepository, atLeastOnce()).findByName(move1.getName());
    }

}