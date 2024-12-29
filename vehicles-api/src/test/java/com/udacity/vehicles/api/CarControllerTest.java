package com.udacity.vehicles.api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.service.CarService;

import java.net.URI;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Implements testing of the CarController class.
 */
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Car> json;


    /**
     * Tests for successful creation of new car in the system.
     * @throws Exception when car creation fails in the system.
     */
    @Test
    public void createCar() throws Exception {
        Car car = getCar();
        car.setId(1L);

        try (MockedStatic<CarService> mockedPricingService = mockStatic(CarService.class)) {
            mockedPricingService.when(() -> CarService.save(any())).thenReturn(car);
            mvc.perform(post(new URI("/cars"))
                            .content(json.write(car).getJson())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());
        }
    }

    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     * @throws Exception if the read operation of the vehicle list fails.
     */
    @Test
    public void listCars() throws Exception {
        Car car = getCar();
        car.setId(1L);

        try (MockedStatic<CarService> mockedPricingService = mockStatic(CarService.class)) {
            mockedPricingService.when(CarService::list).thenReturn(Collections.singletonList(car));

            mvc.perform(get("/cars")
                            .accept("application/hal+json"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/hal+json"))
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].id", is(1)))
                    .andExpect(jsonPath("$.content[0].details.model", is("Impala")));
        }
    }

    /**
     * Tests the read operation for a single car by ID.
     * @throws Exception if the read operation for a single car fails.
     */
    @Test
    public void findCar() throws Exception {
        Car car = getCar();
        car.setId(1L);

        try (MockedStatic<CarService> mockedPricingService = mockStatic(CarService.class)) {
            mockedPricingService.when(() -> CarService.findById(any())).thenReturn(car);

            mvc.perform(get("/cars/1")
                            .accept("application/hal+json")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/hal+json"))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.details.model", is("Impala")));
        }
    }

    /**
     * Tests the deletion of a single car by ID.
     * @throws Exception if the delete operation of a vehicle fails.
     */
    @Test
    public void deleteCar() throws Exception {
        Car car = getCar();
        car.setId(1L);
        try (MockedStatic<CarService> mockedCarService = mockStatic(CarService.class)) {
            mockedCarService.when(() -> CarService.delete(any())).thenAnswer(invocation -> {
                // Simulate successful deletion
                return null;
            });
            mvc.perform(delete("/cars/1")
                            .accept("application/hal+json"))
                    .andExpect(status().isNoContent());
        }
    }

    /**
     * Creates an example Car object for use in testing.
     * @return an example Car object.
     */
    private Car getCar() {
        Car car = new Car();
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        return car;
    }
}
