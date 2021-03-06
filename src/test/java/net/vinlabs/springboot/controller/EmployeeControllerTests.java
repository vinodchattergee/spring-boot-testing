package net.vinlabs.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.vinlabs.springboot.model.Employee;
import net.vinlabs.springboot.service.EmployeeService;
import org.aspectj.weaver.ast.ITestVisitor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class EmployeeControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EmployeeService employeeService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void givenEmployeeObject_whenCreateEmployee_thenReturnSavedEmployee() throws Exception {
        //Given
        Employee employee = Employee.builder()
                .firstName("Vinod")
                .lastName("Chattergee")
                .email("Vinod@gmail.com")
                .build();

        given(employeeService.saveEmployee(any(Employee.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));
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
        given(employeeService.getAllEmployees())
                .willReturn(employees);
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
        long employeeId = 1L;
        Employee employee = Employee.builder()
                .firstName("Vinod")
                .lastName("Chattergee")
                .email("Vinod@gmail.com")
                .build();

        given(employeeService.getEmployeeById(employeeId))
                .willReturn(Optional.of(employee));
        //When
        ResultActions response = mockMvc.perform(get("/api/employees/{id}", employeeId));

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

        given(employeeService.getEmployeeById(employeeId))
                .willReturn(Optional.empty());
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
        long employeeId = 1L;
        Employee savedEmployee = Employee.builder()
                .firstName("Vinod")
                .lastName("Chattergee")
                .email("Vinod@gmail.com")
                .build();

        Employee updatedEmployee = Employee.builder()
                .firstName("Vinod")
                .lastName("Chattergee")
                .email("Vinodchattergee@gmail.com")
                .build();

        given(employeeService.getEmployeeById(employeeId))
                .willReturn(Optional.of(savedEmployee));

        given(employeeService.updateEmployee(any(Employee.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        //When
        ResultActions response = mockMvc.perform(put("/api/employees/{id}", employeeId)
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

        Employee updatedEmployee = Employee.builder()
                .firstName("Vinod")
                .lastName("Chattergee")
                .email("Vinodchattergee@gmail.com")
                .build();

        given(employeeService.getEmployeeById(employeeId))
                .willReturn(Optional.empty());

        given(employeeService.updateEmployee(any(Employee.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

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
        long employeeId = 1L;
        willDoNothing().given(employeeService).deleteEmployeeById(employeeId);// for mpcking void methed we shud use willDoNothing

        //When
        ResultActions response = mockMvc.perform(delete("/api/employees/{id}", employeeId));

        //Then
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }
}
