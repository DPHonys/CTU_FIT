package cz.cvut.fit.honysdan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fit.honysdan.TestData;
import cz.cvut.fit.honysdan.dto.TrainerCreateDTO;
import cz.cvut.fit.honysdan.dto.TrainerDTO;
import cz.cvut.fit.honysdan.exception.EntityNotFoundException;
import cz.cvut.fit.honysdan.exception.NullValueException;
import cz.cvut.fit.honysdan.service.TrainerService;
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
class TrainerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainerService trainerService;

    private JacksonTester<TrainerDTO> jsonTrainer;
    private JacksonTester<List<TrainerDTO>> jsonTrainerList;

    @BeforeEach
    void setUp() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

//----------------------------------------------------------------------------------------------------------------------
// CREATE TEST
//----------------------------------------------------------------------------------------------------------------------

    // Successful create
    @Test
    void trainerSuccessfulCreate() throws Exception {
        given(trainerService.create(any(TrainerCreateDTO.class))).willReturn(toDTO(trainer1));

        //Verifying HTTP Request Matching + Input Serialization
        MockHttpServletResponse rep = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/trainer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Ash Ketchum\",\"numer\": 123,\"address\": \"Pallet Town\"}")
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"))
                .andExpect(MockMvcResultMatchers.header().string("Location",
                        new StringEndsWith("/api/trainer/" + trainer1.getId())))
                .andReturn().getResponse();

        //Verifying Output Serialization
        assertThat(rep.getContentAsString()).isEqualTo(
                jsonTrainer.write(toDTO(trainer1)).getJson()
        );

        //Verifying Business Logic Call
        verify(trainerService, atLeastOnce()).create(any(TrainerCreateDTO.class));
    }

    // Null value in request
    @Test
    void nullNameValueInCreate() throws Exception {
        given(trainerService.create(any(TrainerCreateDTO.class)))
                .willThrow(new NullValueException("Value 'name' cant be null"));

        //Verifying HTTP Request Matching + ERROR message
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/trainer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": null,\"numer\": 123,\"address\": \"Pallet Town\"}")
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Value 'name' cant be null")));

        //Verifying Business Logic Call
        verify(trainerService, atLeastOnce()).create(any(TrainerCreateDTO.class));
    }

//----------------------------------------------------------------------------------------------------------------------
// GET BY ID TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Trainer is present
    @Test
    void getByIdTrainerIsPresent() throws Exception {
        given(trainerService.getByIdAsDTO(trainer1.getId())).willReturn(toDTO(trainer1));

        //Verifying HTTP Request Matching
        MockHttpServletResponse rep = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/trainer/{id}", trainer1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        //Verifying Output Serialization
        assertThat(rep.getContentAsString()).isEqualTo(
                jsonTrainer.write(toDTO(trainer1)).getJson()
        );

        //Verifying Business Logic Call
        verify(trainerService, atLeastOnce()).getByIdAsDTO(trainer1.getId());
    }

    // Trainer is not present
    @Test
    void getByIdTrainerIsNotPresent() throws Exception {
        given(trainerService.getByIdAsDTO(trainer1.getId()))
                .willThrow(new EntityNotFoundException("No such trainer with id " + trainer1.getId()));

        //Verifying HTTP Request Matching + ERROR message
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/trainer/{id}", trainer1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("No such trainer with id " + trainer1.getId())));

        //Verifying Business Logic Call
        verify(trainerService, atLeastOnce()).getByIdAsDTO(trainer1.getId());
    }

//----------------------------------------------------------------------------------------------------------------------
// GET ALL TEST
//----------------------------------------------------------------------------------------------------------------------

    @Test
    void trainerGetAll() throws Exception {
        List<TrainerDTO> allTrainerDTO = allTrainers.stream().map(TestData::toDTO).collect(Collectors.toList());
        given(trainerService.getAll()).willReturn(allTrainerDTO);

        //Verifying HTTP Request Matching
        MockHttpServletResponse rep = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/trainer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*]", hasSize(allTrainerDTO.size())))
                .andReturn().getResponse();

        //Verifying Output Serialization
        assertThat(rep.getContentAsString()).isEqualTo(
                jsonTrainerList.write(allTrainerDTO).getJson()
        );

        //Verifying Business Logic Call
        verify(trainerService, atLeastOnce()).getAll();
    }
    
//----------------------------------------------------------------------------------------------------------------------
// UPDATE TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Successful update
    @Test
    void trainerSuccessfulUpdate() throws Exception {
        given(trainerService.update(any(Integer.class), any(TrainerCreateDTO.class))).willReturn(toDTO(trainer2));

        //Verifying HTTP Request Matching + Input Serialization
        MockHttpServletResponse rep = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/trainer/{id}", trainer1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Ash Ketchum\",\"numer\": 123,\"address\": \"Pallet Town\"}")
        )
                .andExpect(MockMvcResultMatchers.header().exists("Location"))
                .andExpect(MockMvcResultMatchers.header().string("Location",
                        new StringEndsWith("/api/trainer/" + trainer2.getId())))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse();

        //Verifying Output Serialization
        assertThat(rep.getContentAsString()).isEqualTo(
                jsonTrainer.write(toDTO(trainer2)).getJson()
        );

        //Verifying Business Logic Call
        verify(trainerService, atLeastOnce()).update(any(Integer.class), any(TrainerCreateDTO.class));
    }

    // Trainer to update not found
    @Test
    void updateMissingTrainerException() throws Exception {
        given(trainerService.update(any(Integer.class), any(TrainerCreateDTO.class)))
                .willThrow(new EntityNotFoundException("No such trainer with id " + trainer1.getId()));

        //Verifying HTTP Request Matching + ERROR message
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/trainer/{id}", trainer1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Ash Ketchum\",\"numer\": 123,\"address\": \"Pallet Town\"}")
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("No such trainer with id " + trainer1.getId())));

        //Verifying Business Logic Call
        verify(trainerService, atLeastOnce()).update(any(Integer.class), any(TrainerCreateDTO.class));
    }

    // Null value in request
    @Test
    void nullNameValueInUpdate() throws Exception {
        given(trainerService.update(any(Integer.class), any(TrainerCreateDTO.class)))
                .willThrow(new NullValueException("Value 'name' cant be null"));

        //Verifying HTTP Request Matching + ERROR message
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/trainer/{id}", trainer1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": null,\"numer\": 123,\"address\": \"Pallet Town\"}")
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Value 'name' cant be null")));

        //Verifying Business Logic Call
        verify(trainerService, atLeastOnce()).update(any(Integer.class), any(TrainerCreateDTO.class));
    }

//----------------------------------------------------------------------------------------------------------------------
// DELETE TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Successful delete
    @Test
    void trainerSuccessfulDelete() throws Exception {
        //Verifying HTTP Request Matching
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/trainer/{id}", trainer1.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk());

        //Verifying Business Logic Call
        verify(trainerService, atLeastOnce()).deleteById(trainer1.getId());
    }

    // Trainer to delete not found
    @Test
    void deleteMissingTrainerException() throws Exception {
        doThrow(new EntityNotFoundException("No such trainer with id " + trainer1.getId()))
                .when(trainerService).deleteById(any(Integer.class));

        //Verifying HTTP Request Matching
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/trainer/{id}", trainer1.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("No such trainer with id " + trainer1.getId())));

        //Verifying Business Logic Call
        verify(trainerService, atLeastOnce()).deleteById(any(Integer.class));
    }
    
//----------------------------------------------------------------------------------------------------------------------
// GET BY NAME TESTS
//----------------------------------------------------------------------------------------------------------------------

    // Atleast one trainer with name
    @Test
    void trainerGetByName() throws Exception {
        List<TrainerDTO> trainerWithNameDTO = trainerWithName.stream().map(TestData::toDTO).collect(Collectors.toList());
        given(trainerService.getByName(any(String.class))).willReturn(trainerWithNameDTO);

        //Verifying HTTP Request Matching
        MockHttpServletResponse rep = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/trainer?name={name}", trainerWithNameDTO.get(0).getName())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*]", hasSize(trainerWithNameDTO.size())))
                .andReturn().getResponse();

        //Verifying Output Serialization
        assertThat(rep.getContentAsString()).isEqualTo(
                jsonTrainerList.write(trainerWithNameDTO).getJson()
        );

        //Verifying Business Logic Call
        verify(trainerService, atLeastOnce()).getByName(any(String.class));
    }

    // No trainer with name exists
    @Test
    void trainerGetByNameNoTrainerExists() throws Exception {
        //Verifying HTTP Request Matching
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/trainer?name={name}", trainer1.getName())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("No such trainer with name " + trainer1.getName())));

        //Verifying Business Logic Call
        verify(trainerService, atLeastOnce()).getByName(any(String.class));
    }

}
