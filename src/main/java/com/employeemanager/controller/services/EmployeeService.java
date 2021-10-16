package com.employeemanager.controller.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;

import com.employeemanager.auditTrail.AuditTrail;
import com.employeemanager.controller.services.payload.EmployeeRequest;
import com.employeemanager.domain.Employee;
import com.employeemanager.utilities.StatusTypeEnum;

@ApplicationScoped
public class EmployeeService {

    /**
     * Create an employee
     * @param request
     * @param employer
     * @return the created employee details
     */
    public Employee create(EmployeeRequest request, Employee employer) {
        Employee exists = Employee.findByEmail(request.email);
        if (exists != null)
            throw new WebApplicationException("Employee with email " + request.email + " already exists", 409);

        Employee employee = new Employee(request.firstname, request.lastname, request.othername,
                request.nationalIdNumber, request.phoneNumber, request.dateOfBirth, request.email, request.position);
        employee.persist();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail audit = new AuditTrail("SAVED", employee.id, null, jsonb.toJson(employee), employer);
        audit.persist();

        return employee;
    }

    /**
     * Update the details of a employee
     * @param id
     * @param request
     * @param employer
     * @return the updated employee details
     */
    public Employee update(Long id, EmployeeRequest request, Employee employer) {
        Employee employee = Employee.findById(id);
        if (employee == null)
            throw new WebApplicationException("Employee with id " + id + " does not exist", 404);

        Employee emp = employee;

        employee.dateOfBirth = request.dateOfBirth;
        employee.email = request.email;
        employee.firstname = request.firstname;
        employee.lastname = request.lastname;
        employee.nationalIdNumber = request.nationalIdNumber;
        employee.othername = request.othername;
        employee.phoneNumber = request.phoneNumber;
        employee.position = request.position;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail audit = new AuditTrail("UPDATED", employee.id, jsonb.toJson(emp), jsonb.toJson(employee), employer);
        audit.persist();

        return employee;
    }

    /**
     * Suspend an existing employee.
     * @param id
     * @param employer
     * @return the suspended employee
     */
    public Employee suspend(Long id, Employee employer) {
        Employee employee = Employee.findById(id);
        if (employee == null)
            throw new WebApplicationException("Employee with id " + id + " does not exist", 404);

        Employee emp = employee;

        employee.status = StatusTypeEnum.INACTIVE.toString();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail audit = new AuditTrail("SUSPENDED", employee.id, jsonb.toJson(emp), jsonb.toJson(employee),
                employer);
        audit.persist();

        return employee;
    }

    /**
     * Activate an employee
     * @param id
     * @param employer
     * @return the activated employee
     */
    public Employee activate(Long id, Employee employer) {
        Employee employee = Employee.findById(id);
        if (employee == null)
            throw new WebApplicationException("Employee with id " + id + " does not exist", 404);

        Employee emp = employee;

        employee.status = StatusTypeEnum.ACTIVE.toString();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail audit = new AuditTrail("ACTIVATED", employee.id, jsonb.toJson(emp), jsonb.toJson(employee),
                employer);
        audit.persist();

        return employee;
    }

    /**
     * Fetch a list of employees based on the provided query parameters.
     * @param firstname
     * @param lastname
     * @param email
     * @param position
     * @param phoneNumber
     * @param code
     * @return a list of employees that match the provided query parameters.
     */
    public List<Employee> search(String firstname, String lastname, String email, String position, String phoneNumber,
            String code) {
        return Employee.search(firstname, lastname, position, phoneNumber, email, code);
    }

    /**
     * Delete an existing employee
     * @param id
     * @param employer
     * @return details of the deleted employee
     */
    public Employee delete(Long id, Employee employer) {
        Employee employee = Employee.findById(id);
        if (employee == null)
            throw new WebApplicationException("Employee with id " + id + " does not exist", 404);

        Employee emp = employee;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail audit = new AuditTrail("ACTIVATED", employee.id, jsonb.toJson(emp), jsonb.toJson(employee),
                employer);
        audit.persist();

        employee.delete();

        return employee;
    }
}
