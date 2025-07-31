package mate.academy.carsharingapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import mate.academy.carsharingapp.dao.car.CarDto;
import mate.academy.carsharingapp.dao.car.CreateCarRequestDto;
import mate.academy.carsharingapp.model.Car;
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

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext context) {
        mockMvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private CreateCarRequestDto createCarRequestDto(String model, String brand, Car.Type type,
                                                    int inventory, BigDecimal dailyFee) {
        CreateCarRequestDto dto = new CreateCarRequestDto();
        dto.setModel(model);
        dto.setBrand(brand);
        dto.setType(type);
        dto.setInventory(inventory);
        dto.setDailyFee(dailyFee);
        return dto;
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "MANAGER")
    @DisplayName("Create car - success")
    void createCar_ValidRequestDto_Success() throws Exception {
        CreateCarRequestDto requestDto = createCarRequestDto("Toyota Camry",
                "Sedan", Car.Type.SEDAN, 5, new BigDecimal("100.00"));
        MvcResult result = mockMvc.perform(
                        post("/cars")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isCreated())
                .andReturn();
        CarDto actualDto = objectMapper.readValue(result
                .getResponse().getContentAsString(), CarDto.class);
        assertNotNull(actualDto.getId());
        assertEquals(requestDto.getModel(), actualDto.getModel());
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
        MvcResult result = mockMvc.perform(get("/cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CarDto[] actualCars = objectMapper.readValue(
                objectMapper.readTree(result.getResponse()
                        .getContentAsString()).get("content").toString(), CarDto[].class);
        assertEquals(1, actualCars.length);
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    @DisplayName("Get car by valid ID - success")
    void getCarById_ValidId_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/cars/{id}",
                        VALID_CAR_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CarDto actualDto = objectMapper.readValue(result
                .getResponse().getContentAsString(), CarDto.class);
        assertNotNull(actualDto);
        assertEquals(VALID_CAR_ID, actualDto.getId());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "MANAGER")
    @DisplayName("Update car (PUT) by valid ID - success")
    void updateCar_ValidId_Success() throws Exception {
        CreateCarRequestDto updateRequestDto = createCarRequestDto("Tesla Model S",
                "Electric", Car.Type.UNIVERSAL, 2, new BigDecimal("250.00"));
        MvcResult result = mockMvc.perform(
                        put("/cars/{id}", VALID_CAR_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequestDto))
                )
                .andExpect(status().isOk())
                .andReturn();
        CarDto actualDto = objectMapper.readValue(result
                .getResponse().getContentAsString(), CarDto.class);
        assertEquals(VALID_CAR_ID, actualDto.getId());
        assertEquals(updateRequestDto.getModel(), actualDto.getModel());
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
