package net.vinlabs.springboot.service.impl;

import net.vinlabs.springboot.exception.ResourceNotFoundException;
import net.vinlabs.springboot.model.Employee;
import net.vinlabs.springboot.repository.EmployeeRepository;
import net.vinlabs.springboot.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        Optional<Employee> employeeObject = employeeRepository.findByEmail(employee.getEmail());
        if (employeeObject.isPresent()) {
            throw new ResourceNotFoundException("The emlpoye with the given email already exists : " + employee.getEmail());
        }
        return employeeRepository.save(employee);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> getEmployeeById(long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployeeById(long id) {
        employeeRepository.deleteById(id);
    }
}
