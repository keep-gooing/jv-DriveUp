package carsharingapp.controller;

import static carsharingapp.util.TestUtil.createRental;
import static carsharingapp.util.TestUtil.createRentalResponseDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import carsharingapp.dto.rental.RentalRequestDto;
import carsharingapp.dto.rental.RentalResponseDto;
import carsharingapp.model.Rental;
import carsharingapp.notification.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

@Sql(scripts = {
        "classpath:database/add-users-to-users-table.sql",
        "classpath:database/add-cars-to-cars-table.sql",
        "classpath:database/add-rentals-to-rentals-table.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/delete-rentals-from-rentals-table.sql",
        "classpath:database/delete-cars-from-cars-table.sql",
        "classpath:database/delete-users-from-users-table.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RentalControllerTest {
    protected static MockMvc mockMvc;
    private static final Long VALID_USER_ID = 1L;
    private static final Long VALID_RENTAL_ID = 7L;
    private static final Long VALID_CAR_ID = 7L;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext context) {
        mockMvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    @DisplayName("Add new rental - success")
    void addRental_ValidRequestDto_Success() throws Exception {
        RentalRequestDto expectedDto = new RentalRequestDto()
                .setCarId(VALID_CAR_ID)
                .setRentalDate(LocalDate.now())
                .setReturnDate(LocalDate.now().plusDays(7));
        MvcResult result = mockMvc.perform(
                        post("/rentals")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(expectedDto))
                )
                .andExpect(status().isCreated())
                .andReturn();
        RentalResponseDto actualDto = objectMapper.readValue(result
                .getResponse().getContentAsString(), RentalResponseDto.class);
        assertNotNull(actualDto.getId());
        assertThat(actualDto).usingRecursiveComparison()
                .comparingOnlyFields("carId", "rentalDate", "returnDate")
                .isEqualTo(expectedDto);

    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "MANAGER")
    @DisplayName("Add new rental - forbidden for MANAGER")
    void addRental_ForbiddenForManager() throws Exception {
        RentalRequestDto requestDto = new RentalRequestDto()
                .setCarId(VALID_CAR_ID)
                .setRentalDate(LocalDate.now())
                .setReturnDate(LocalDate.now().plusDays(7));
        mockMvc.perform(
                        post("/rentals")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    @DisplayName("Get rentals by user ID and isActive=true - success")
    void getRentalsByUserId_Active_Success() throws Exception {
        // Arrange
        Rental rental = createRental(
                VALID_RENTAL_ID,
                VALID_CAR_ID,
                VALID_USER_ID,
                LocalDate.of(2025, 7, 15),
                LocalDate.of(2025, 7, 22),
                null
        );
        RentalResponseDto expectedDto = createRentalResponseDto(rental);

        // Act
        MvcResult result = mockMvc.perform(
                        get("/rentals")
                                .param("userId", String.valueOf(VALID_USER_ID))
                                .param("isActive", "true")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<RentalResponseDto> actualRentals = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, RentalResponseDto.class)
        );

        // Assert
        assertEquals(1, actualRentals.size());
        assertEquals(expectedDto, actualRentals.get(0));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    @DisplayName("Get specific rental by valid user and rental ID - success")
    void getSpecificRental_ValidIds_Success() throws Exception {
        // Arrange
        Rental rental = createRental(
                VALID_RENTAL_ID,
                VALID_CAR_ID,
                VALID_USER_ID,
                LocalDate.of(2025, 7, 15),
                LocalDate.of(2025, 7, 22),
                null
        );
        RentalResponseDto expectedDto = createRentalResponseDto(rental);

        // Act
        MvcResult result = mockMvc.perform(
                        get("/rentals/users/{userId}/rentals/{rentalId}",
                                VALID_USER_ID, VALID_RENTAL_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        RentalResponseDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                RentalResponseDto.class
        );

        // Assert
        assertEquals(expectedDto, actualDto);
    }
}
