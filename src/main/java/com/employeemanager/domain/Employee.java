package com.employeemanager.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import com.employeemanager.utilities.StatusTypeEnum;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Employee extends PanacheEntity {

    @NotNull
    @Column(nullable = false)
    public String firstname;

    @NotNull
    @Column(nullable = false)
    public String lastname;

    @NotNull
    @Column(nullable = true)
    public String othername;

    @NotNull
    @Column(nullable = false)
    public String nationalIdNumber;

    @NotNull
    @Column(nullable = false)
    public String code;

    @NotNull
    @Column(nullable = false)
    public String phoneNumber;

    @NotNull
    @JsonbDateFormat("dd/MM/yyyy")
    @Column(nullable = false)
    public LocalDate dateOfBirth;

    @NotNull
    @Email
    @Column(nullable = false)
    public String email;

    @NotNull
    @Column(nullable = false)
    public String status = StatusTypeEnum.ACTIVE.toString();

    @NotNull
    @Column(nullable = false)
    public String position;

    @NotNull
    @JsonbDateFormat("dd/MM/yyyy HH/mm/SS")
    @Column(nullable = false)
    public LocalDateTime creationDate = LocalDateTime.now();

    public Employee() {
    }

    public Employee(String firstname, String lastname, String othername, String nationalIdNumber, 
            String phoneNumber, LocalDate dateOfBirth, String email, String position) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.othername = othername;
        this.nationalIdNumber = nationalIdNumber;
        this.code = generateCode();
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.position = position;
    }

    /**
     * Find an employee by thier email
     * @param email
     * @return The employee whose email was provided or null if not found
     */
    public static Employee findByEmail(String email){
        return find("email", email).firstResult();
    }

    /**
     * Search for employees based on given query parameters
     * @param firstname
     * @param lastname
     * @param position
     * @param phoneNumber
     * @param email
     * @param code
     * @return a list of employees that match given query parameters
     */
    public static List<Employee> search(String firstname, String lastname, String position, String phoneNumber,
            String email, String code) {
        return list(
                "(?1 is null or firstname=?1) and (?2 is null or lastname=?2) and (?3 is null or position=?3) "
                        + "and (?4 is null or phoneNumber=?4) and (?5 is null or email=?5) and (?6 is null or code=?6)",
                firstname, lastname, position, phoneNumber, email, code);
    }

    /**
     * Generates a code for a new employee. The code increments the latest employee's code.
     * @return generated code for the new employee.
     */
    private String generateCode(){
        List<Employee> employeeList = Employee.listAll();

        int latestAccount = 0;
        for (Employee employee : employeeList) {
            String employeeCode = employee.code.replace("EMP", "");
            int late = Integer.parseInt(employeeCode);

            if (late >= latestAccount) {
                latestAccount = late;
            }
        }

        return "EMP" + (latestAccount + 1);
    }

}
