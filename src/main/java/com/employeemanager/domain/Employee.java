package com.employeemanager.domain;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import com.employeemanager.utilities.StatusTypeEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.security.password.Password;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.interfaces.BCryptPassword;
import org.wildfly.security.password.util.ModularCrypt;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = "email"), @UniqueConstraint(columnNames = "code"),
        @UniqueConstraint(columnNames = "phoneNumber"), @UniqueConstraint(columnNames = "nationalIdNumber") })
public class Employee extends PanacheEntity {

    @NotNull
    @Column(nullable = false)
    public String firstname;

    @NotNull
    @Column(nullable = false)
    public String lastname;

    @Column(nullable = true)
    public String password;

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

    public Employee(String firstname, String lastname, String othername, String nationalIdNumber, String phoneNumber,
            LocalDate dateOfBirth, String email, String position) {
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
     * Find an employee by their email
     * 
     * @param email
     * @return The employee whose email was provided or null if not found
     */
    public static Employee findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public static Employee findByPhoneNumber(String phoneNumber) {
        return find("phoneNumber", phoneNumber).firstResult();
    }

    public static Employee findByNationalIdNumber(String nationalIdNumber) {
        return find("nationalIdNumber", nationalIdNumber).firstResult();
    }

    /**
     * Search for employees based on given query parameters
     * 
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

    public static Employee login(String email, String password) {
        Optional<Employee> T = Employee.find("email", email).singleResultOptional();
        if (T.isPresent()) {
            Employee user = T.get();
            if (verifyPassword(password, user.password)) {
                return user;
            }
        }
        return null;
    }

    private static boolean verifyPassword(String originalPwd, String encryptedPwd) {
        Logger logger = LoggerFactory.getLogger(Employee.class);

        try {
            // convert encrypted password string to a password key
            Password rawPassword = ModularCrypt.decode(encryptedPwd);

            try {
                // create the password factory based on the bcrypt algorithm
                PasswordFactory factory = PasswordFactory.getInstance(BCryptPassword.ALGORITHM_BCRYPT);

                try {

                    // create encrypted password based on stored string
                    BCryptPassword restored = (BCryptPassword) factory.translate(rawPassword);

                    // verify restored password against original
                    return factory.verify(restored, originalPwd.toCharArray());

                } catch (InvalidKeyException e) {
                    logger.error("Invalid password key: {}", e.getMessage());

                }

            } catch (NoSuchAlgorithmException e) {
                logger.error("Invalid Algorithm: {}", e.getMessage());

            }

        } catch (InvalidKeySpecException e) {
            logger.error("Invalid key: {}", e.getMessage());

        }

        return false;

    }

    /**
     * Generates a code for a new employee. The code increments the latest
     * employee's code.
     * 
     * @return generated code for the new employee.
     */
    private String generateCode() {
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
