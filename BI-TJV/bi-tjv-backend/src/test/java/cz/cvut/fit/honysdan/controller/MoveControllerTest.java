package cz.cvut.fit.honysdan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fit.honysdan.TestData;
import cz.cvut.fit.honysdan.dto.*;
import cz.cvut.fit.honysdan.dto.MoveCreateDTO;
import cz.cvut.fit.honysdan.exception.NullValueException;
import cz.cvut.fit.honysdan.exception.EntityNotFoundException;
import cz.cvut.fit.honysdan.exception.UniqueNameConflictException;
import cz.cvut.fit.honysdan.service.MoveService;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static cz.cvut.fit.honysdan.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class MoveControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MoveService moveService;

    private JacksonTester<MoveDTO> jsonMove;
    private JacksonTester<List<MoveDTO>> jsonMoveList;

    @BeforeEach
    void setUp() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

//----------------------------------------------------------------------------------------------------------------------
// CREATE TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Successful create
    @Test
    void moveSuccessfulCreate() throws Exception {
        given(moveService.create(any(MoveCreateDTO.class))).willReturn(toDTO(move1));

        //Verifying HTTP Request Matching + Input Serialization
        MockHttpServletResponse rep = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Bolt Strike\",\"description\": \"May paralyze opponent\"}")
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"))
                .andExpect(MockMvcResultMatchers.header().string("Location",
                        new StringEndsWith("/api/move/" + move1.getId())))
                .andReturn().getResponse();

        //Verifying Output Serialization
        assertThat(rep.getContentAsString()).isEqualTo(
                jsonMove.write(toDTO(move1)).getJson()
        );

        //Verifying Business Logic Call
        verify(moveService, atLeastOnce()).create(any(MoveCreateDTO.class));
    }

    // Not unique
    @Test
    void notUniqueCreate() throws Exception {
        given(moveService.create(any(MoveCreateDTO.class)))
                .willThrow(new UniqueNameConflictException("Move with this name already exists"));

        //Verifying HTTP Request Matching + ERROR message
        var x = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Bolt Strike\",\"description\": \"May paralyze opponent\"}")
        )
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Move with this name already exists")));

        //Verifying Business Logic Call
        verify(moveService, atLeastOnce()).create(any(MoveCreateDTO.class));
    }

    // Null value in request
    @Test
    void nullNameValueInCreate() throws Exception {
        given(moveService.create(any(MoveCreateDTO.class)))
                .willThrow(new NullValueException("Value 'name' can't be null"));

        //Verifying HTTP Request Matching + ERROR message
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": null,\"description\": \"May paralyze opponent\"}")
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Value 'name' can't be null")));

        //Verifying Business Logic Call
        verify(moveService, atLeastOnce()).create(any(MoveCreateDTO.class));
    }

//----------------------------------------------------------------------------------------------------------------------
// GET BY ID TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Move is present
    @Test
    void getByIdMoveIsPresent() throws Exception {
        given(moveService.getByIdAsDTO(move1.getId())).willReturn((toDTO(move1)));

        //Verifying HTTP Request Matching
        MockHttpServletResponse rep = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/move/{id}", move1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        //Verifying Output Serialization
        assertThat(rep.getContentAsString()).isEqualTo(
                jsonMove.write(toDTO(move1)).getJson()
        );

        //Verifying Business Logic Call
        verify(moveService, atLeastOnce()).getByIdAsDTO(move1.getId());
    }

    // Move is not present
    @Test
    void getByIdMoveIsNotPresent() throws Exception {
        given(moveService.getByIdAsDTO(move1.getId()))
                .willThrow(new EntityNotFoundException("No such move with id " + move1.getId()));

        //Verifying HTTP Request Matching + ERROR message
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/move/{id}", move1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("No such move with id " + move1.getId())));

        //Verifying Business Logic Call
        verify(moveService, atLeastOnce()).getByIdAsDTO(move1.getId());
    }

//----------------------------------------------------------------------------------------------------------------------
// GET ALL TEST
//----------------------------------------------------------------------------------------------------------------------

    @Test
    void moveGetAll() throws Exception {
        List<MoveDTO> allMoveDTO = allMoves.stream().map(TestData::toDTO).collect(Collectors.toList());
        given(moveService.getAll()).willReturn(allMoveDTO);

        //Verifying HTTP Request Matching
        MockHttpServletResponse rep = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*]", hasSize(allMoveDTO.size())))
                .andReturn().getResponse();

        //Verifying Output Serialization
        assertThat(rep.getContentAsString()).isEqualTo(
                jsonMoveList.write(allMoveDTO).getJson()
        );

        //Verifying Business Logic Call
        verify(moveService, atLeastOnce()).getAll();
    }

//----------------------------------------------------------------------------------------------------------------------
// UPDATE TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Successful update
    @Test
    void moveSuccessfulUpdate() throws Exception {
        given(moveService.update(any(Integer.class), any(MoveCreateDTO.class))).willReturn(toDTO(move2));

        //Verifying HTTP Request Matching + Input Serialization
        MockHttpServletResponse rep = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/move/{id}", move1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Slam\",\"description\":null}"))
                .andExpect(MockMvcResultMatchers.header().exists("Location"))
                .andExpect(MockMvcResultMatchers.header().string("Location",
                        new StringEndsWith("/api/move/" + move2.getId())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        //Verifying Output Serialization
        assertThat(rep.getContentAsString()).isEqualTo(
                jsonMove.write(toDTO(move2)).getJson()
        );

        //Verifying Business Logic Call
        verify(moveService, atLeastOnce()).update(any(Integer.class), any(MoveCreateDTO.class));
    }

    // Move to update not found
    @Test
    void updateMissingMoveException() throws Exception {
        given(moveService.update(any(Integer.class), any(MoveCreateDTO.class)))
                .willThrow(new EntityNotFoundException("No such move with id " + move1.getId()));

        //Verifying HTTP Request Matching + ERROR message
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/move/{id}", move1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Slam\",\"description\":null}")
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("No such move with id " + move1.getId())));

        //Verifying Business Logic Call
        verify(moveService, atLeastOnce()).update(any(Integer.class), any(MoveCreateDTO.class));
    }

    // Not unique
    @Test
    void notUniqueUpdate() throws Exception {
        given(moveService.update(any(Integer.class), any(MoveCreateDTO.class)))
                .willThrow(new UniqueNameConflictException("Move with this name already exists"));

        //Verifying HTTP Request Matching + ERROR message
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/move/{id}", move1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Slam\",\"description\":null}")
        )
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Move with this name already exists")));

        //Verifying Business Logic Call
        verify(moveService, atLeastOnce()).update(any(Integer.class), any(MoveCreateDTO.class));
    }

    // Null value in request
    @Test
    void nullNameValueInUpdate() throws Exception {
        given(moveService.update(any(Integer.class), any(MoveCreateDTO.class)))
                .willThrow(new NullValueException("Value 'name' can't be null"));

        //Verifying HTTP Request Matching + ERROR message
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/move/{id}", move1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":null,\"description\":null}")
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Value 'name' can't be null")));

        //Verifying Business Logic Call
        verify(moveService, atLeastOnce()).update(any(Integer.class), any(MoveCreateDTO.class));
    }
    
//----------------------------------------------------------------------------------------------------------------------
// DELETE TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Successful delete
    @Test
    void trainerSuccessfulDelete() throws Exception {
        //Verifying HTTP Request Matching
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/move/{id}", move1.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNoContent());

        //Verifying Business Logic Call
        verify(moveService, atLeastOnce()).deleteById(move1.getId());
    }

    // Move to delete not found
    @Test
    void deleteMissingMoveException() throws Exception {
        doThrow(new EntityNotFoundException("No such move with id " + move1.getId())).when(moveService).deleteById(any(Integer.class));

        //Verifying HTTP Request Matching
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/move/{id}", move1.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("No such move with id " + move1.getId())));

        //Verifying Business Logic Call
        verify(moveService, atLeastOnce()).deleteById(any(Integer.class));
    }

//----------------------------------------------------------------------------------------------------------------------
// GET BY NAME TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Move is present
    @Test
    void moveGetByName() throws Exception {
        given(moveService.getByName(any(String.class))).willReturn(Optional.of(toDTO(move1)));

        //Verifying HTTP Request Matching + Input Serialization
        MockHttpServletResponse rep = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/move?name={name}", move1.getName())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        //Verifying Output Serialization
        assertThat(rep.getContentAsString()).isEqualTo(
                jsonMove.write(toDTO(move1)).getJson()
        );

        //Verifying Business Logic Call
        verify(moveService, atLeastOnce()).getByName(any(String.class));
    }

    // Move is not present
    @Test
    void moveGetByNameNotPresent() throws Exception {
        //Verifying HTTP Request Matching + Input Serialization
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/move?name={name}", move1.getName())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("No such move with name '" + move1.getName() + "'")));

        //Verifying Business Logic Call
        verify(moveService, atLeastOnce()).getByName(any(String.class));
    }
}
