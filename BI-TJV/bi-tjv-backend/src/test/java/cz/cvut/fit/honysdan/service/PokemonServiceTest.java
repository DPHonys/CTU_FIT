package cz.cvut.fit.honysdan.service;

import cz.cvut.fit.honysdan.dto.PokemonCreateDTO;
import cz.cvut.fit.honysdan.dto.PokemonDTO;
import cz.cvut.fit.honysdan.entity.Pokemon;
import cz.cvut.fit.honysdan.exception.*;
import cz.cvut.fit.honysdan.repository.MoveRepository;
import cz.cvut.fit.honysdan.repository.PokemonRepository;
import cz.cvut.fit.honysdan.repository.TrainerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cz.cvut.fit.honysdan.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@SpringBootTest
class PokemonServiceTest {

    @Autowired
    private PokemonService pokemonService;

    @MockBean
    private PokemonRepository pokemonRepository;
    @MockBean
    private TrainerRepository trainerRepository;
    @MockBean
    private MoveRepository moveRepository;

//----------------------------------------------------------------------------------------------------------------------
// CREATE TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Successful create
    @Test
    void pokemonSuccessfulCreate() throws EntityNotFoundException,
                                          NullValueException {
        given(trainerRepository.findById(pokemon1.getTrainer().getId())).willReturn(Optional.of(trainer1));
        given(moveRepository.findAllById(anyList())).willReturn(pokemon1.getMoves());
        given(pokemonRepository.save(pokemon1)).willReturn(pokemon1);

        PokemonCreateDTO pokemonInserted = createDTOInstance(pokemon1);
        PokemonDTO pokemonExpected = toDTO(pokemon1);

        assertEquals(pokemonExpected, pokemonService.create(pokemonInserted));
        verify(pokemonRepository, atLeastOnce()).save(pokemon1);
    }

    // Missing trainer for create > expecting exception
    @Test
    void createMissingTrainerException() {
        assertThrows(EntityNotFoundException.class, () -> pokemonService.create(createDTOInstance(pokemon1)));
    }

    // Not all moves are present > expecting exception
    @Test
    void createMissingMoveException() {
        given(trainerRepository.findById(pokemon1.getTrainer().getId())).willReturn(Optional.of(trainer1));

        assertThrows(EntityNotFoundException.class, () -> pokemonService.create(createDTOInstance(pokemon1)));
    }

    // Null value in request (name)
    @Test
    void nullNameValueInCreate() {
        Pokemon dummy = new Pokemon(null, "Type", new ArrayList<>(), null);
        PokemonCreateDTO dummyDTO = createDTOInstance(dummy);

        NullValueException exception =
                assertThrows(NullValueException.class, () -> pokemonService.create(dummyDTO));
        assertEquals("Value 'name' can't be null", exception.getMessage());
    }

    // Null value in request (type)
    @Test
    void nullTypeValueInCreate() {
        Pokemon dummy = new Pokemon("Name", null, new ArrayList<>(), null);
        PokemonCreateDTO dummyDTO = createDTOInstance(dummy);

        NullValueException exception =
                assertThrows(NullValueException.class, () -> pokemonService.create(dummyDTO));
        assertEquals("Value 'type' can't be null", exception.getMessage());
    }

//----------------------------------------------------------------------------------------------------------------------
// GET BY ID TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Pokemon is present > successful find
    @Test
    void getByIdPokemonIsPresent() {
        given(pokemonRepository.findById(pokemon1.getId())).willReturn(Optional.of(pokemon1));

        assertEquals(pokemon1, pokemonService.getById(pokemon1.getId()));
        verify(pokemonRepository, atLeastOnce()).findById(pokemon1.getId());
    }

    // Pokemon is not present > expecting exception
    @Test
    void getByIdPokemonIsNotPresent() {
        given(pokemonRepository.findById(any(Integer.class))).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> pokemonService.getById(any(Integer.class)));
        verify(pokemonRepository, atLeastOnce()).findById(any(Integer.class));
    }

//----------------------------------------------------------------------------------------------------------------------
// GET BY ID AS DTO TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Pokemon is present > successful find
    @Test
    void getByIdAsDTOPokemonIsPresent() {
        given(pokemonRepository.findById(pokemon1.getId())).willReturn(Optional.of(pokemon1));

        assertEquals(toDTO(pokemon1), pokemonService.getByIdAsDTO(pokemon1.getId()));
        verify(pokemonRepository, atLeastOnce()).findById(pokemon1.getId());
    }

    // Pokemon is not present > expecting empty optional
    @Test
    void getByIdAsDTOPokemonIsNotPresent() {
        given(pokemonRepository.findById(any(Integer.class))).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> pokemonService.getByIdAsDTO(any(Integer.class)));
        verify(pokemonRepository, atLeastOnce()).findById(any(Integer.class));
    }

//----------------------------------------------------------------------------------------------------------------------
// GET ALL TEST
//----------------------------------------------------------------------------------------------------------------------

    @Test
    void pokemonGetAll() {
        given(pokemonRepository.findAll()).willReturn(allPokemon);
        List<PokemonDTO> returned = pokemonService.getAll();

        assertEquals(allPokemon.size(), returned.size());
        for (int i = 0; i < returned.size(); i++)
            assertEquals(toDTO(allPokemon.get(i)), returned.get(i));

        verify(pokemonRepository, atLeastOnce()).findAll();
    }

//----------------------------------------------------------------------------------------------------------------------
// UPDATE TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Successful update
    @Test
    void pokemonSuccessfulUpdate() throws EntityNotFoundException,
                                          NullValueException {
        PokemonCreateDTO newPokemon = createDTOInstance(pokemon2);
        given(pokemonRepository.findById(pokemon1.getId())).willReturn(Optional.of(pokemon1));
        given(trainerRepository.findById(pokemon2.getTrainer().getId())).willReturn(Optional.of(trainer2));
        given(moveRepository.findAllById(anyList())).willReturn(pokemon2.getMoves());

        PokemonDTO updatedPokemon = pokemonService.update(pokemon1.getId(), newPokemon);

        assertEquals(pokemon1.getId(), updatedPokemon.getId());
        assertEquals(newPokemon.getName(), updatedPokemon.getName());
        assertEquals(newPokemon.getType(), updatedPokemon.getType());
        assertEquals(newPokemon.getMovesIds(), updatedPokemon.getMovesIds());
        assertEquals(newPokemon.getTrainerId(), updatedPokemon.getTrainerId());

        verify(pokemonRepository, atLeastOnce()).findById(pokemon1.getId());
    }

    // Pokemon to update not found > expecting exception
    @Test
    void updateMissingPokemonException() {
        PokemonCreateDTO newPokemon = createDTOInstance(pokemon2);
        assertThrows(EntityNotFoundException.class, () -> pokemonService.update(pokemon1.getId(), newPokemon));
    }

    // Trainer is missing > expecting exception
    @Test
    void updateMissingTrainerException() {
        PokemonCreateDTO newPokemon = createDTOInstance(pokemon2);
        given(pokemonRepository.findById(pokemon1.getId())).willReturn(Optional.of(pokemon1));

        assertThrows(EntityNotFoundException.class, () -> pokemonService.update(pokemon1.getId(), newPokemon));
    }

    // Not all moves are present > expecting exception
    @Test
    void updateMissingMoveException() {
        PokemonCreateDTO newPokemon = createDTOInstance(pokemon2);
        given(pokemonRepository.findById(pokemon1.getId())).willReturn(Optional.of(pokemon1));
        given(trainerRepository.findById(pokemon2.getTrainer().getId())).willReturn(Optional.of(trainer2));

        assertThrows(EntityNotFoundException.class, () -> pokemonService.update(pokemon1.getId(), newPokemon));
    }

    // Null value in request (name)
    @Test
    void nullNameValueInUpdate() {
        Pokemon dummy = new Pokemon(null, "Type", new ArrayList<>(), null);
        PokemonCreateDTO dummyDTO = createDTOInstance(dummy);

        NullValueException exception =
                assertThrows(NullValueException.class, () -> pokemonService.update(pokemon1.getId(), dummyDTO));
        assertEquals("Value 'name' can't be null", exception.getMessage());
    }

    // Null value in request (type)

    @Test
    void nullTypeValueInUpdate() {
        Pokemon dummy = new Pokemon("Name", null, new ArrayList<>(), null);
        PokemonCreateDTO dummyDTO = createDTOInstance(dummy);

        NullValueException exception =
                assertThrows(NullValueException.class, () -> pokemonService.update(pokemon1.getId(), dummyDTO));
        assertEquals("Value 'type' can't be null", exception.getMessage());
    }

//----------------------------------------------------------------------------------------------------------------------
// DELETE BY ID TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Successful delete
    @Test
    void pokemonSuccessfulDelete() throws EntityNotFoundException {
        given(pokemonRepository.findById(pokemon1.getId())).willReturn(Optional.of(pokemon1));
        pokemonService.deleteById(pokemon1.getId());
        verify(pokemonRepository, atLeastOnce()).deleteById(pokemon1.getId());
    }

    // Pokemon to delete not found > expecting exception
    @Test
    void deleteMissingPokemonException() {
        assertThrows(EntityNotFoundException.class, () -> pokemonService.deleteById(pokemon1.getId()));
    }

//----------------------------------------------------------------------------------------------------------------------
// GET BY NAME TEST
//----------------------------------------------------------------------------------------------------------------------

    @Test
    void pokemonGetByName() {
        given(pokemonRepository.findByName(pokemonWithName.get(0).getName())).willReturn(pokemonWithName);
        List<PokemonDTO> returned = pokemonService.getByName(pokemonWithName.get(0).getName());

        assertEquals(pokemonWithName.size(), returned.size());
        for (int i = 0; i < returned.size(); i++)
            assertEquals(toDTO(pokemonWithName.get(i)), returned.get(i));

        verify(pokemonRepository, atLeastOnce()).findByName(pokemonWithName.get(0).getName());
    }

//----------------------------------------------------------------------------------------------------------------------
// ADD MOVE TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Successful addition of move to Pokemon's move list
    @Test
    void pokemonSuccessfullyAddedMove() throws EntityNotFoundException,
                                               MoveKnowledgeException {
        given(pokemonRepository.findById(pokemon5.getId())).willReturn(Optional.of(pokemon5));
        given(moveRepository.findById(move2.getId())).willReturn(Optional.of(move2));

        List<Integer> expected = List.of(move3.getId(), move2.getId());
        PokemonDTO returned = pokemonService.addMove(pokemon1.getId(), move2.getId());

        assertEquals(expected.size(), returned.getMovesIds().size());
        for (int i = 0; i < returned.getMovesIds().size(); i++)
            assertEquals(expected.get(i), returned.getMovesIds().get(i));
        verify(pokemonRepository, atLeastOnce()).findById(pokemon1.getId());
    }

    // Pokemon is not present > expecting exception
    @Test
    void addMoveMissingPokemonException() {
        assertThrows(EntityNotFoundException.class, () -> pokemonService.addMove(pokemon1.getId(), move2.getId()));
    }

    // Move to add is not present > expecting exception
    @Test
    void addMoveMissingMoveException() {
        given(pokemonRepository.findById(pokemon5.getId())).willReturn(Optional.of(pokemon5));

        assertThrows(EntityNotFoundException.class, () -> pokemonService.addMove(pokemon1.getId(), move2.getId()));
    }

    // Pokemon already knows the move > expecting exception
    @Test
    void addMoveAlreadyKnowsException() {
        given(pokemonRepository.findById(pokemon5.getId())).willReturn(Optional.of(pokemon5));
        given(moveRepository.findById(move2.getId())).willReturn(Optional.of(move2));

        assertThrows(MoveKnowledgeException.class, () -> pokemonService.addMove(pokemon5.getId(), move2.getId()));
    }

//----------------------------------------------------------------------------------------------------------------------
// REMOVE MOVE TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Successful removal of move from Pokemon's move list
    @Test
    void pokemonSuccessfullyRemovedMove() throws EntityNotFoundException,
                                                 MoveKnowledgeException {
        given(pokemonRepository.findById(pokemon2.getId())).willReturn(Optional.of(pokemon2));
        given(moveRepository.findById(move2.getId())).willReturn(Optional.of(move2));

        List<Integer> expected = List.of(move5.getId());
        PokemonDTO returned = pokemonService.removeMove(pokemon2.getId(), move2.getId());

        assertEquals(expected.size(), returned.getMovesIds().size());
        for (int i = 0; i < returned.getMovesIds().size(); i++)
            assertEquals(expected.get(i), returned.getMovesIds().get(i));
        verify(pokemonRepository, atLeastOnce()).findById(pokemon2.getId());
    }

    // Pokemon is not present > expecting exception
    @Test
    void removeMoveMissingPokemonException() {
        assertThrows(EntityNotFoundException.class, () -> pokemonService.removeMove(pokemon1.getId(), move2.getId()));
    }

    // Move to remove is not present > expecting exception
    @Test
    void removeMoveMissingMoveException() {
        given(pokemonRepository.findById(pokemon1.getId())).willReturn(Optional.of(pokemon1));

        assertThrows(EntityNotFoundException.class, () -> pokemonService.removeMove(pokemon1.getId(), move2.getId()));
    }

    // Pokemon already doesn't know the move > expecting exception
    @Test
    void removeMoveDoesntKnowException() {
        given(pokemonRepository.findById(pokemon1.getId())).willReturn(Optional.of(pokemon1));
        given(moveRepository.findById(move3.getId())).willReturn(Optional.of(move3));

        assertThrows(MoveKnowledgeException.class, () -> pokemonService.removeMove(pokemon1.getId(), move3.getId()));
    }

}