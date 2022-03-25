package cz.cvut.fit.honysdan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fit.honysdan.TestData;
import cz.cvut.fit.honysdan.dto.PokemonCreateDTO;
import cz.cvut.fit.honysdan.dto.PokemonDTO;
import cz.cvut.fit.honysdan.exception.*;
import cz.cvut.fit.honysdan.service.PokemonService;
import org.hamcrest.core.StringEndsWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;
import java.util.stream.Collectors;

import static cz.cvut.fit.honysdan.TestData.*;
import static cz.cvut.fit.honysdan.TestData.toDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class PokemonControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PokemonService pokemonService;

    private JacksonTester<PokemonDTO> jsonPokemon;
    private JacksonTester<List<PokemonDTO>> jsonPokemonList;

    @BeforeEach
    void setUp() {
       JacksonTester.initFields(this, new ObjectMapper());
    }

//----------------------------------------------------------------------------------------------------------------------
// CREATE TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Successful create > 201
    @Test
    void pokemonSuccessfulCreate() throws Exception {
        given(pokemonService.create(any(PokemonCreateDTO.class))).willReturn(toDTO(pokemon1));

        //Verifying HTTP Request Matching + Input Serialization
        MockHttpServletResponse rep = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/pokemon")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Pikachu\",\"type\": \"Electric\",\"moves\": [0,0],\"trainer\": 0}")
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"))
                .andExpect(MockMvcResultMatchers.header().string("Location",
                        new StringEndsWith("/api/pokemon/" + pokemon1.getId())))
                .andReturn().getResponse();

        //Verifying Output Serialization
        assertThat(rep.getContentAsString()).isEqualTo(
                jsonPokemon.write(toDTO(pokemon1)).getJson()
        );

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).create(any(PokemonCreateDTO.class));
    }

    // Missing trainer for create > 404
    @Test
    void createMissingTrainerException() throws Exception {
        given(pokemonService.create(any(PokemonCreateDTO.class)))
                .willThrow(new EntityNotFoundException("No such trainer with id " + pokemon1.getTrainer().getId()));

        //Verifying HTTP Request Matching + ERROR message
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/pokemon")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Pikachu\",\"type\": \"Electric\",\"moves\": [0,0],\"trainer\": 0}")
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("No such trainer with id " + pokemon1.getTrainer().getId())));

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).create(any(PokemonCreateDTO.class));
    }

    // Not all moves are present > 404
    @Test
    void createMissingMoveException() throws Exception {
        given(pokemonService.create(any(PokemonCreateDTO.class)))
                .willThrow(new EntityNotFoundException("Not all provided moves were found"));

        //Verifying HTTP Request Matching + ERROR message
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/pokemon")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Pikachu\",\"type\": \"Electric\",\"moves\": [0,0],\"trainer\": 0}")
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Not all provided moves were found")));

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).create(any(PokemonCreateDTO.class));
    }

    // Null value in request (name) > 400
    @Test
    void nullNameValueInCreate() throws Exception {
        given(pokemonService.create(any(PokemonCreateDTO.class)))
                .willThrow(new NullValueException("Value 'name' cant be null"));

        //Verifying HTTP Request Matching + ERROR message
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/pokemon")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": null,\"type\": \"Electric\",\"moves\": [0,0],\"trainer\": 0}")
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Value 'name' cant be null")));

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).create(any(PokemonCreateDTO.class));
    }

    // Null value in request (type) > 400
    @Test
    void nullTypeValueInCreate() throws Exception {
        given(pokemonService.create(any(PokemonCreateDTO.class)))
                .willThrow(new NullValueException("Value 'type' cant be null"));

        //Verifying HTTP Request Matching + ERROR message
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/pokemon")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Pikachu\",\"type\": null,\"moves\": [0,0],\"trainer\": 0}")
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Value 'type' cant be null")));

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).create(any(PokemonCreateDTO.class));
    }

//----------------------------------------------------------------------------------------------------------------------
// GET BY ID TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Pokemon is present > 200
    @Test
    void getByIdPokemonIsPresent() throws Exception {
        given(pokemonService.getByIdAsDTO(pokemon1.getId())).willReturn(toDTO(pokemon1));

        //Verifying HTTP Request Matching
        MockHttpServletResponse rep = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/pokemon/{id}", pokemon1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        //Verifying Output Serialization
        assertThat(rep.getContentAsString()).isEqualTo(
                jsonPokemon.write(toDTO(pokemon1)).getJson()
        );

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).getByIdAsDTO(pokemon1.getId());
    }

    // Pokemon is not present > 404
    @Test
    void getByIdPokemonIsNotPresent() throws Exception {
        given(pokemonService.getByIdAsDTO(pokemon1.getId()))
                .willThrow(new EntityNotFoundException("No such pokemon with id " + pokemon1.getId()));

        //Verifying HTTP Request Matching + ERROR message
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/pokemon/{id}", pokemon1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("No such pokemon with id " + pokemon1.getId())));

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).getByIdAsDTO(pokemon1.getId());
    }

//----------------------------------------------------------------------------------------------------------------------
// GET ALL TEST
//----------------------------------------------------------------------------------------------------------------------

    @Test
    void pokemonGetAll() throws Exception {
        List<PokemonDTO> allPokemonDTO = allPokemon.stream().map(TestData::toDTO).collect(Collectors.toList());
        given(pokemonService.getAll()).willReturn(allPokemonDTO);

        //Verifying HTTP Request Matching
        MockHttpServletResponse rep = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/pokemon")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$[*]", hasSize(allPokemonDTO.size())))
        .andReturn().getResponse();

        //Verifying Output Serialization
        assertThat(rep.getContentAsString()).isEqualTo(
                jsonPokemonList.write(allPokemonDTO).getJson()
        );

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).getAll();
    }

//----------------------------------------------------------------------------------------------------------------------
// UPDATE TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Successful update
    @Test
    void pokemonSuccessfulUpdate() throws Exception {
        given(pokemonService.update(any(Integer.class), any(PokemonCreateDTO.class))).willReturn(toDTO(pokemon2));

        //Verifying HTTP Request Matching + Input Serialization
        MockHttpServletResponse rep = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/pokemon/{id}", pokemon1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Bulbasaur\",\"type\": \"Grass\",\"moves\": [0,0],\"trainer\": 0}")
        )
                .andExpect(MockMvcResultMatchers.header().exists("Location"))
                .andExpect(MockMvcResultMatchers.header().string("Location",
                        new StringEndsWith("/api/pokemon/" + pokemon2.getId())))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse();

        //Verifying Output Serialization
        assertThat(rep.getContentAsString()).isEqualTo(
                jsonPokemon.write(toDTO(pokemon2)).getJson()
        );

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).update(any(Integer.class), any(PokemonCreateDTO.class));
    }

    // Pokemon to update not found
    @Test
    void updateMissingPokemonException() throws Exception {
        given(pokemonService.update(any(Integer.class), any(PokemonCreateDTO.class)))
                .willThrow(new EntityNotFoundException("No such pokemon with id " + pokemon1.getId()));

        //Verifying HTTP Request Matching + ERROR message
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/pokemon/{id}", pokemon1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Bulbasaur\",\"type\": \"Grass\",\"moves\": [0,0],\"trainer\": 0}")
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("No such pokemon with id " + pokemon1.getId())));

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).update(any(Integer.class), any(PokemonCreateDTO.class));
    }

    // Trainer is missing
    @Test
    void updateMissingTrainerException() throws Exception {
        given(pokemonService.update(any(Integer.class), any(PokemonCreateDTO.class)))
                .willThrow(new EntityNotFoundException("No such trainer with id " + pokemon1.getTrainer().getId()));

        //Verifying HTTP Request Matching + ERROR message
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/pokemon/{id}", pokemon1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Bulbasaur\",\"type\": \"Grass\",\"moves\": [0,0],\"trainer\": 0}")
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("No such trainer with id " + pokemon1.getTrainer().getId())));

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).update(any(Integer.class), any(PokemonCreateDTO.class));
    }

    // Not all moves are present
    @Test
    void updateMissingMoveException() throws Exception {
        given(pokemonService.update(any(Integer.class), any(PokemonCreateDTO.class)))
                .willThrow(new EntityNotFoundException("Not all provided moves were found"));

        //Verifying HTTP Request Matching + ERROR message
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/pokemon/{id}", pokemon1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Bulbasaur\",\"type\": \"Grass\",\"moves\": [0,0],\"trainer\": 0}")
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Not all provided moves were found")));

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).update(any(Integer.class), any(PokemonCreateDTO.class));
    }

    // Null value in request (name)
    @Test
    void nullNameValueInUpdate() throws Exception {
        given(pokemonService.update(any(Integer.class), any(PokemonCreateDTO.class)))
                .willThrow(new NullValueException("Value 'name' cant be null"));

        //Verifying HTTP Request Matching + ERROR message
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/pokemon/{id}", pokemon1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": null,\"type\": \"Grass\",\"moves\": [0,0],\"trainer\": 0}")
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Value 'name' cant be null")));

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).update(any(Integer.class), any(PokemonCreateDTO.class));
    }

    // Null value in request (type)
    @Test
    void nullTypeValueInUpdate() throws Exception {
        given(pokemonService.update(any(Integer.class), any(PokemonCreateDTO.class)))
                .willThrow(new NullValueException("Value 'type' cant be null"));

        //Verifying HTTP Request Matching + ERROR message
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/pokemon/{id}", pokemon1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Bulbasaur\",\"type\": null,\"moves\": [0,0],\"trainer\": 0}")
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Value 'type' cant be null")));

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).update(any(Integer.class), any(PokemonCreateDTO.class));
    }

//----------------------------------------------------------------------------------------------------------------------
// DELETE TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Successful delete
    @Test
    void pokemonSuccessfulDelete() throws Exception {
        //Verifying HTTP Request Matching
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/pokemon/{id}", pokemon1.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk());

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).deleteById(pokemon1.getId());
    }

    // Pokemon to delete not found
    @Test
    void deleteMissingPokemonException() throws Exception {
        doThrow(new EntityNotFoundException("No such pokemon with id " + pokemon1.getId()))
                .when(pokemonService).deleteById(any(Integer.class));

        //Verifying HTTP Request Matching
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/pokemon/{id}", pokemon1.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("No such pokemon with id " + pokemon1.getId())));

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).deleteById(any(Integer.class));
    }

//----------------------------------------------------------------------------------------------------------------------
// GET BY NAME TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Atleast one pokemon with name
    @Test
    void pokemonGetByName() throws Exception {
        List<PokemonDTO> pokemonWithNameDTO = pokemonWithName.stream().map(TestData::toDTO).collect(Collectors.toList());
        given(pokemonService.getByName(any(String.class))).willReturn(pokemonWithNameDTO);

        //Verifying HTTP Request Matching
        MockHttpServletResponse rep = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/pokemon?name={name}", pokemonWithNameDTO.get(0).getName())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*]", hasSize(pokemonWithNameDTO.size())))
                .andReturn().getResponse();

        //Verifying Output Serialization
        assertThat(rep.getContentAsString()).isEqualTo(
                jsonPokemonList.write(pokemonWithNameDTO).getJson()
        );

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).getByName(any(String.class));
    }

    // No pokemon with name exists
    @Test
    void pokemonGetByNameNoPokemonExists() throws Exception {
        //Verifying HTTP Request Matching
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/pokemon?name={name}", pokemon1.getName())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("No such pokemon with name " + pokemon1.getName())));

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).getByName(any(String.class));
    }

//----------------------------------------------------------------------------------------------------------------------
// ADD MOVE TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Successful addition of move to Pokemon's move list
    @Test
    void pokemonSuccessfullyAddedMove() throws Exception {
        PokemonDTO before = toDTO(pokemon1);
        List<Integer> list = before.getMovesIds();
        list.add(move3.getId());
        PokemonDTO after = new PokemonDTO(pokemon1.getId(),
                                          pokemon1.getName(),
                                          pokemon1.getType(),
                                          list,
                                          pokemon1.getTrainer().getId());

        given(pokemonService.addMove(any(Integer.class), any(Integer.class))).willReturn(after);

        MockHttpServletResponse rep = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/pokemon/{pokemonid}/add?id={moveid}", pokemon1.getId(), move3.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        //Verifying Output Serialization
        assertThat(rep.getContentAsString()).isEqualTo(
                jsonPokemon.write(after).getJson()
        );

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).addMove(any(Integer.class), any(Integer.class));
    }

    // Pokemon is not present
    @Test
    void addMoveMissingPokemonException() throws Exception {
        given(pokemonService.addMove(any(Integer.class), any(Integer.class)))
                .willThrow(new EntityNotFoundException("No such pokemon with id " + pokemon1.getId()));

        //Verifying HTTP Request Matching
        mockMvc.perform(MockMvcRequestBuilders.post("/api/pokemon/{pokemonid}/add?id={moveid}", pokemon1.getId(), move3.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("No such pokemon with id " + pokemon1.getId())));

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).addMove(any(Integer.class), any(Integer.class));
    }

    // Move to add is not present
    @Test
    void addMoveMissingMoveException() throws Exception {
        given(pokemonService.addMove(any(Integer.class), any(Integer.class)))
                .willThrow(new EntityNotFoundException("No such move with id " + move3.getId()));

        //Verifying HTTP Request Matching
        mockMvc.perform(MockMvcRequestBuilders.post("/api/pokemon/{pokemonid}/add?id={moveid}", pokemon1.getId(), move3.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("No such move with id " + move3.getId())));

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).addMove(any(Integer.class), any(Integer.class));
    }

    // Pokemon already knows the move
    @Test
    void addMoveAlreadyKnowsException() throws Exception {
        given(pokemonService.addMove(any(Integer.class), any(Integer.class)))
                .willThrow(new MoveKnowledgeException("Pokemon already knows move with id " + move3.getId()));

        //Verifying HTTP Request Matching
        mockMvc.perform(MockMvcRequestBuilders.post("/api/pokemon/{pokemonid}/add?id={moveid}", pokemon1.getId(), move3.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Pokemon already knows move with id " + move3.getId())));

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).addMove(any(Integer.class), any(Integer.class));
    }

//----------------------------------------------------------------------------------------------------------------------
// REMOVE MOVE TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Successful removal of move from Pokemon's move list
    @Test
    void pokemonSuccessfullyRemovedMove() throws Exception {
        PokemonDTO before = toDTO(pokemon1);
        List<Integer> list = before.getMovesIds();
        list.remove(move1.getId());
        PokemonDTO after = new PokemonDTO(pokemon1.getId(),
                pokemon1.getName(),
                pokemon1.getType(),
                list,
                pokemon1.getTrainer().getId());

        given(pokemonService.addMove(any(Integer.class), any(Integer.class))).willReturn(after);

        MockHttpServletResponse rep = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/pokemon/{pokemonid}/add?id={moveid}", pokemon1.getId(), move1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        //Verifying Output Serialization
        assertThat(rep.getContentAsString()).isEqualTo(
                jsonPokemon.write(after).getJson()
        );

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).addMove(any(Integer.class), any(Integer.class));
    }

    // Pokemon is not present
    @Test
    void removeMoveMissingPokemonException() throws Exception {
        given(pokemonService.removeMove(any(Integer.class), any(Integer.class)))
                .willThrow(new EntityNotFoundException("No such pokemon with id " + pokemon1.getId()));

        //Verifying HTTP Request Matching
        mockMvc.perform(MockMvcRequestBuilders.post("/api/pokemon/{pokemonid}/remove?id={moveid}", pokemon1.getId(), move3.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("No such pokemon with id " + pokemon1.getId())));

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).removeMove(any(Integer.class), any(Integer.class));
    }

    // Move to remove is not present
    @Test
    void removeMoveMissingMoveException() throws Exception {
        given(pokemonService.removeMove(any(Integer.class), any(Integer.class)))
                .willThrow(new EntityNotFoundException("No such move with id " + move3.getId()));

        //Verifying HTTP Request Matching
        mockMvc.perform(MockMvcRequestBuilders.post("/api/pokemon/{pokemonid}/remove?id={moveid}", pokemon1.getId(), move3.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("No such move with id " + move3.getId())));

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).removeMove(any(Integer.class), any(Integer.class));
    }

    // Pokemon already doesn't know the move
    @Test
    void removeMoveDoesntKnowException() throws Exception {
        given(pokemonService.removeMove(any(Integer.class), any(Integer.class)))
                .willThrow(new MoveKnowledgeException("Pokemon doesn't know move with id " + move3.getId()));

        //Verifying HTTP Request Matching
        mockMvc.perform(MockMvcRequestBuilders.post("/api/pokemon/{pokemonid}/remove?id={moveid}", pokemon1.getId(), move3.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Pokemon doesn't know move with id " + move3.getId())));

        //Verifying Business Logic Call
        verify(pokemonService, atLeastOnce()).removeMove(any(Integer.class), any(Integer.class));
    }

}