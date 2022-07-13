package net.vinlabs.springboot.service;

import net.vinlabs.springboot.exception.ResourceNotFoundException;
import net.vinlabs.springboot.model.Employee;
import net.vinlabs.springboot.repository.EmployeeRepository;
import net.vinlabs.springboot.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTests {
    // Mocking the object
    @Mock
    private EmployeeRepository employeeRepository; // employeeRepository = Mockito.mock(EmployeeRepository.class);
    // Injecting another mock to the mocked object.
    @InjectMocks
    private EmployeeServiceImpl employeeService;// employeeService = new EmployeeServiceImpl(employeeRepository);

    private Employee employee;

    @BeforeEach
    public void setup() {

        employee = Employee.builder()
                .id(1L)
                .firstName("Vinod")
                .lastName("Vinod")
                .email("Vinod@gmail,com")
                .build();
    }

    @DisplayName("JUnite test for save employee method")
    @Test
    public void givenEmployeeObject_whenSaveEmployee_thenReturnEmployeeObject() {
        //Given.
        given(employeeRepository.findByEmail(employee.getEmail()))
                .willReturn(Optional.empty());
        given(employeeRepository.save(employee))
                .willReturn(employee);
        //When
        Employee savedEmployee = employeeService.saveEmployee(employee);

        //Then
        assertThat(savedEmployee).isNotNull();

    }

    @DisplayName("JUnite test for validating exception")
    @Test
    public void givenEmployeeObjectWithExistingEmailId_whenSaveEmployee_thenThrowsNoResourceFoundException() {
        //Given.
        given(employeeRepository.findByEmail(employee.getEmail()))
                .willReturn(Optional.of(employee));
        //When
        Assertions.assertThrows(ResourceNotFoundException.class, () -> employeeService.saveEmployee(employee));
        //Then
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @DisplayName("JUnite test for getallEmployees method")
    @Test
    public void givenEmployeeList_whenFindAllEmployee_thenReturnEmployeeList() {
        //Given.
        Employee employee1 = Employee.builder()
                .id(2L)
                .firstName("Nisha")
                .lastName("Vinod")
                .email("Vinod@gmail,com")
                .build();

        given(employeeRepository.findAll())
                .willReturn(List.of(employee, employee1));
        //When
        List<Employee> allEmployees = employeeService.getAllEmployees();

        //Then
        assertThat(allEmployees).isNotNull();
        assertThat(allEmployees.size()).isEqualTo(2);
    }


    @DisplayName("JUnite test for getallEmployees method (negative scenario)")
    @Test
    public void givenEmptyList_whenFindAllEmployee_thenReturnEmptyList() {
        //Given.
        given(employeeRepository.findAll())
                .willReturn(Collections.emptyList());
        //When
        List<Employee> allEmployees = employeeService.getAllEmployees();

        //Then
        assertThat(allEmployees).isEmpty();
        assertThat(allEmployees.size()).isEqualTo(0);
    }

    @DisplayName("JUnite test for getallEmployees method")
    @Test
    public void givenEmployeeId_whenFindAllEmployee_thenReturnEmployeeList() {
        //Given.
        Employee employee1 = Employee.builder()
                .id(2L)
                .firstName("Nisha")
                .lastName("Vinod")
                .email("Vinod@gmail,com")
                .build();

        given(employeeRepository.findAll())
                .willReturn(List.of(employee, employee1));
        //When
        List<Employee> allEmployees = employeeService.getAllEmployees();

        //Then
        assertThat(allEmployees).isNotNull();
        assertThat(allEmployees.size()).isEqualTo(2);
    }

    @DisplayName("Junit test for getEmployeeById method")
    @Test
    public void givenEmployeeId_whenGetEmployeeById_thenReturnEmployee() {
        //Given
        given(employeeRepository.findById(employee.getId()))
                .willReturn(Optional.of(employee));
        //When
        Employee savedEmployee = employeeService.getEmployeeById(employee.getId()).get();

        //Then
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getId()).isEqualTo(employee.getId());

    }

    @DisplayName("Junit test for updateEmployee method")
    @Test
    public void givenEmployeeObject_whenUpdate_thenReturnUpdatedEmployee() {
        //Given
        given(employeeRepository.save(employee)).willReturn(employee);
        employee.setFirstName("raj");
        //When
        Employee updatedEmployee = employeeService.updateEmployee(employee);

        //Then
        assertThat(updatedEmployee).isNotNull();
        assertThat(updatedEmployee.getId()).isEqualTo(employee.getId());
        assertThat(updatedEmployee.getFirstName()).isEqualTo("raj");

    }

    // willDoNothing is the one to mock the void methods
    @DisplayName("Junit test for deleteEmployee method")
    @Test
    public void givenEmployeeId_whenDelete_thenRemoveTheEmployee() {
        //Given
        willDoNothing().given(employeeRepository).deleteById(employee.getId());
        //When
        employeeService.deleteEmployeeById(employee.getId());

        //Then
        Optional<Employee> deletedEmployee = employeeService.getEmployeeById(employee.getId());
        assertThat(deletedEmployee.isPresent()).isFalse();
        verify(employeeRepository,times(1)).deleteById(employee.getId());

    }

}
