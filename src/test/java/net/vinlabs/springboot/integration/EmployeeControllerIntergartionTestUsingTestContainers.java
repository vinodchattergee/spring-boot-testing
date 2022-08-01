package net.vinlabs.springboot.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.vinlabs.springboot.model.Employee;
import net.vinlabs.springboot.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class EmployeeControllerIntergartionTestUsingTestContainers extends AstractionBaseTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();


    }

    @Test
    public void givenEmployeeObject_whenCreateEmployee_thenReturnSavedEmployee() throws Exception {
        //Given
        Employee employee = Employee.builder()
                .firstName("Vinod")
                .lastName("Chattergee")
                .email("Vinod@gmail.com")
                .build();

        //When
        ResultActions response = mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)));


        //Then
        //MockMvcResultHandlers.print() - Prints the request and the response.
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is(employee.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(employee.getLastName())))
                .andExpect(jsonPath("$.email", is(employee.getEmail())));

    }

    @Test
    public void givenListOfEmployees_whenGetAllEmployees_thenReturnListOfEmployees() throws Exception {
        //Given

        List<Employee> employees = List.of(
                Employee.builder()
                        .firstName("Vinod")
                        .lastName("Chattergee")
                        .email("Vinod@gmail.com")
                        .build(),
                Employee.builder()
                        .firstName("Gautham")
                        .lastName("Vinod")
                        .email("Gautham@gmail.com")
                        .build()
        );

        employeeRepository.saveAll(employees);

        //When
        ResultActions response = mockMvc.perform(get("/api/employees")
                .contentType(MediaType.APPLICATION_JSON));

        //Then
        //MockMvcResultHandlers.print() - Prints the request and the response.
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.size()", is(employees.size())))
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    public void givenEmployeeId_whenGetEmployeeByID_thenReturnEmployee() throws Exception {
        //Given
        Employee employee = Employee.builder()
                .firstName("Vinod")
                .lastName("Chattergee")
                .email("Vinod@gmail.com")
                .build();
        employeeRepository.save(employee);


        //When
        ResultActions response = mockMvc.perform(get("/api/employees/{id}", employee.getId()));

        //Then
        //MockMvcResultHandlers.print() - Prints the request and the response.
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.firstName", is(employee.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(employee.getLastName())))
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    public void givenWrongEmployeeId_whenGetEmployeeByID_thenReturnNotFound() throws Exception {
        //Given
        long employeeId = 1L;
        Employee employee = Employee.builder()
                .firstName("Vinod")
                .lastName("Chattergee")
                .email("Vinod@gmail.com")
                .build();

        employeeRepository.save(employee);

        //When
        ResultActions response = mockMvc.perform(get("/api/employees/{id}", employeeId));

        //Then
        //MockMvcResultHandlers.print() - Prints the request and the response.
        response.andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    public void givenEmployee_whenUpdateEmployee_thenReturnUpdatedEmployee() throws Exception {
        //Given
        Employee savedEmployee = Employee.builder()
                .firstName("Vinod")
                .lastName("Chattergee")
                .email("Vinod@gmail.com")
                .build();

        employeeRepository.save(savedEmployee);

        Employee updatedEmployee = Employee.builder()
                .firstName("Vinod")
                .lastName("Chattergee")
                .email("Vinodchattergee@gmail.com")
                .build();


        //When
        ResultActions response = mockMvc.perform(put("/api/employees/{id}", savedEmployee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        //Then
        //MockMvcResultHandlers.print() - Prints the request and the response.
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk()).andExpect(jsonPath("$.firstName", is(updatedEmployee.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(updatedEmployee.getLastName())))
                .andExpect(jsonPath("$.email", is(updatedEmployee.getEmail())));
        ;

    }

    @Test
    public void givenInvalidEmployee_whenUpdateEmployee_thenReturnNotFound() throws Exception {
        //Given
        long employeeId = 1L;
        Employee savedEmployee = Employee.builder()
                .firstName("Vinod")
                .lastName("Chattergee")
                .email("Vinod@gmail.com")
                .build();
        employeeRepository.save(savedEmployee);

        Employee updatedEmployee = Employee.builder()
                .firstName("Vinod")
                .lastName("Chattergee")
                .email("Vinodchattergee@gmail.com")
                .build();

        //When
        ResultActions response = mockMvc.perform(put("/api/employees/{id}", employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        //Then
        //MockMvcResultHandlers.print() - Prints the request and the response.
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());

    }

    @Test
    public void givenEmployeeId_whenDeleteEmployee_thenDeleteTheEmployee() throws Exception {
        //Given
        Employee savedEmployee = Employee.builder()
                .firstName("Vinod")
                .lastName("Chattergee")
                .email("Vinod@gmail.com")
                .build();
        employeeRepository.save(savedEmployee);
        //When
        ResultActions response = mockMvc.perform(delete("/api/employees/{id}", savedEmployee.getId()));

        //Then
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

}
