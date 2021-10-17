package com.employeemanager.controller.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;

import com.employeemanager.auditTrail.AuditTrail;
import com.employeemanager.controller.services.payload.EmployeeRequest;
import com.employeemanager.domain.Employee;
import com.employeemanager.utilities.EmailServices;
import com.employeemanager.utilities.StatusTypeEnum;

@ApplicationScoped
public class EmployeeService {

    @Inject
    EmailServices emailService;

    /**
     * Create an employee
     * 
     * @param request
     * @param employer
     * @return the created employee details
     */
    public Employee create(EmployeeRequest request, Employee employer) {
        Employee exists = Employee.findByEmail(request.email);
        if (exists != null)
            throw new WebApplicationException("Employee with email " + request.email + " already exists", 409);

        Employee exists1 = Employee.findByNationalIdNumber(request.nationalIdNumber);
        if (exists1 != null)
            throw new WebApplicationException("Employee with that national id number already exists", 409);

        Employee exists2 = Employee.findByPhoneNumber(request.phoneNumber);
        if (exists2 != null)
            throw new WebApplicationException("Employee with phone number " + request.phoneNumber + " already exists",
                    409);

        if (!Employee.checkPhoneNumberFormat(request.phoneNumber))
            throw new WebApplicationException("Invalid phone number format", 403);

        if (request.nationalIdNumber.length() != 16)
            throw new WebApplicationException("National ID Number must be 16 characters", 403);

        if (ChronoUnit.YEARS.between(LocalDateTime.now(), request.dateOfBirth.atStartOfDay()) >= 18)
            throw new WebApplicationException("The person is below the minimum age of 18 years", 403);

        Employee employee = new Employee(request.firstname, request.lastname, request.othername,
                request.nationalIdNumber, request.phoneNumber, request.dateOfBirth, request.email, request.position);
        employee.persist();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail audit = new AuditTrail("SAVED", employee.id, null, jsonb.toJson(employee), employer);
        audit.persist();

        emailService.createuser(employee.firstname + " " + employee.lastname, request.email);

        return employee;
    }

    /**
     * Update the details of a employee
     * 
     * @param id
     * @param request
     * @param employer
     * @return the updated employee details
     */
    public Employee update(Long id, EmployeeRequest request, Employee employer) {
        Employee employee = Employee.findById(id);
        if (employee == null)
            throw new WebApplicationException("Employee with id " + id + " does not exist", 404);

        if (!Employee.checkPhoneNumberFormat(request.phoneNumber))
            throw new WebApplicationException("Invalid phone number format", 403);

        if (request.nationalIdNumber.length() != 16)
            throw new WebApplicationException("National ID Number must be 16 characters", 403);

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
     * 
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
     * 
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
     * 
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
     * 
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
