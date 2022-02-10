package com.lepse.employee.controllers;

import com.lepse.employee.dao.EmployeeDAO;
import com.lepse.employee.dao.FiredEmployeeDAO;
import com.lepse.employee.models.EmplModel;
import com.lepse.employee.models.Employee;
import com.lepse.employee.models.FiredEmployee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping({"/"})
public class EmployeeController {

    private final FiredEmployeeDAO firedEmployeeDAO;
    private final EmployeeDAO employeeDAO;

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    private HttpHeaders responseHeaders = setHttpHeaders("xml");

    @Autowired
    public EmployeeController(FiredEmployeeDAO firedEmployeeDAO, EmployeeDAO employeeDAO) {
        this.firedEmployeeDAO = firedEmployeeDAO;
        this.employeeDAO = employeeDAO;
    }

    /**
     * GET request controller to get all fired employees from the Spis_uv table
     * @return returns a list of fired employees
     * */
    @GetMapping({"fired{extend}"})
    @ResponseBody
    public ResponseEntity<EmplModel<FiredEmployee>> getFiredEmployees(@PathVariable String extend) {
        if (extend.contains(".")) {
            responseHeaders = setHttpHeaders(extend.substring(1));
        }
        try {
            EmplModel<FiredEmployee> firedEmployees = new EmplModel<>();
            firedEmployees.setEmployee(firedEmployeeDAO.getFiredEmployee());
            return new ResponseEntity<EmplModel<FiredEmployee>>(firedEmployees, responseHeaders, HttpStatus.OK);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        }
        return new ResponseEntity<EmplModel<FiredEmployee>>(new EmplModel<>(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * GET request controller to get information from TC about user by user id
     * @param userId user id
     * @return returns Employee instance
     * */
    @GetMapping({"user/{userId}"})
    public ResponseEntity<Employee> findUser(@PathVariable String userId, @RequestParam(required = false) String status) {
        if (userId.contains(".")) {
            responseHeaders = setHttpHeaders(userId.substring(userId.indexOf('.') + 1));
            userId = userId.substring(0, userId.indexOf('.'));
        }
        try {
            return new ResponseEntity<Employee>(employeeDAO.getEmployee(userId, status), responseHeaders, HttpStatus.OK);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        }
        return new ResponseEntity<Employee>(employeeDAO.getEmployee(userId, status), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * GET request controller to get information from TC about fired employee who is still active in TC
     * @return returns a list of fired employees who are still active in TC
     * */
    @GetMapping({"all{extend}"})
    public ResponseEntity<EmplModel<Employee>> findAllUser(@PathVariable(required = false) String extend, @RequestParam(required = false) String status) {
        if (extend.contains(".")) {
            responseHeaders = setHttpHeaders(extend.substring(1));
        }
        try {
            EmplModel<FiredEmployee> firedEmployees = getFiredEmployees(extend).getBody();

            List<Employee> employees = new ArrayList<>();
            for (FiredEmployee firedEmployee : firedEmployees.getEmployee()) {
                if (!firedEmployee.getLogin().isEmpty()) {
                    Employee employee = findUser(firedEmployee.getLogin().toLowerCase(Locale.ROOT), status).getBody();
                    if (employee != null) {
                        employee.setFiredDate(firedEmployee.getDate());
                        employees.add(employee);
                    }
                }
            }
            EmplModel<Employee> employeeList = new EmplModel<>();
            employeeList.setEmployee(employees);

            return new ResponseEntity<EmplModel<Employee>>(employeeList, responseHeaders, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return new ResponseEntity<EmplModel<Employee>>(new EmplModel<>(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private HttpHeaders setHttpHeaders(String path) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", MediaType.APPLICATION_XML_VALUE + ";charset=utf-8");
        if (path != null && path.matches("json")) {
            httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8");
        }
        return httpHeaders;
    }
}