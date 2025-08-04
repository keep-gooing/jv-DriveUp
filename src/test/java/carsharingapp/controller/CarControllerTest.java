package carsharingapp.controller;

import static carsharingapp.util.TestUtil.createCarDto;
import static carsharingapp.util.TestUtil.createCarRequestDto;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import carsharingapp.dto.car.CarDto;
import carsharingapp.dto.car.CreateCarRequestDto;
import carsharingapp.model.Car;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

@Sql(scripts = {"classpath:database/add-cars-to-cars-table.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:database/delete-cars-from-cars-table.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerTest {
    protected static MockMvc mockMvc;
    private static final Long VALID_CAR_ID = 7L;
    private static final Long VALID_CAR_ID_ONE = 1L;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext context) {
        mockMvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "MANAGER")
    @DisplayName("Create car - success")
    void createCar_ValidRequestDto_Success() throws Exception {
        CarDto expectedDto = createCarDto(VALID_CAR_ID_ONE, "Toyota Camry", 5,
                new BigDecimal("100.00"));
        MvcResult result = mockMvc.perform(
                        post("/cars")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(expectedDto))
                )
                .andExpect(status().isCreated())
                .andReturn();
        CarDto actualDto = objectMapper.readValue(result
                .getResponse().getContentAsString(), CarDto.class);
        assertNotNull(actualDto.getId());
        assertEquals(expectedDto, actualDto);
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    @DisplayName("Create car - forbidden for CUSTOMER")
    void createCar_ForbiddenForCustomer() throws Exception {
        CreateCarRequestDto requestDto = createCarRequestDto("Ford Focus",
                "Hatchback", Car.Type.HATCHBACK, 3, new BigDecimal("70.00"));
        mockMvc.perform(
                        post("/cars")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    @DisplayName("Get all cars - success")
    void getAllCars_Success() throws Exception {
        CarDto expectedCar = new CarDto()
                .setId(7L)
                .setModel("Toyota Corolla")
                .setBrand("Toyota")
                .setType(Car.Type.SEDAN)
                .setInventory(5)
                .setDailyFee(new BigDecimal("80.0"));
        CarDto[] expectedCars = new CarDto[] { expectedCar };
        MvcResult result = mockMvc.perform(get("/cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CarDto[] actualCars = objectMapper.readValue(
                objectMapper.readTree(result.getResponse()
                        .getContentAsString()).get("content").toString(), CarDto[].class);
        assertArrayEquals(expectedCars, actualCars);
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    @DisplayName("Get car by valid ID - success")
    void getCarById_ValidId_Success() throws Exception {
        CarDto expectedDto = createCarDto(VALID_CAR_ID, "Toyota Corolla", 5,
                new BigDecimal("80.00"));
        MvcResult result = mockMvc.perform(get("/cars/{id}",
                        VALID_CAR_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CarDto actualDto = objectMapper.readValue(result
                .getResponse().getContentAsString(), CarDto.class);
        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "MANAGER")
    @DisplayName("Update car (PUT) by valid ID - success")
    void updateCar_ValidId_Success() throws Exception {
        CarDto expectedDto = new CarDto()
                .setId(VALID_CAR_ID)
                .setModel("Tesla Model S")
                .setBrand("Electric")
                .setType(Car.Type.UNIVERSAL)
                .setInventory(2)
                .setDailyFee(new BigDecimal("250.00"));
        MvcResult result = mockMvc.perform(
                        put("/cars/{id}", VALID_CAR_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(expectedDto))
                )
                .andExpect(status().isOk())
                .andReturn();
        CarDto actualDto = objectMapper.readValue(result
                .getResponse().getContentAsString(), CarDto.class);
        assertEquals(expectedDto, actualDto);
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    @DisplayName("Delete car - forbidden for CUSTOMER")
    void deleteCar_ForbiddenForCustomer() throws Exception {
        mockMvc.perform(delete("/cars/{id}", VALID_CAR_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
