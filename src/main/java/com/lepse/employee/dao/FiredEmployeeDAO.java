package com.lepse.employee.dao;

import com.lepse.employee.models.FiredEmployee;
import com.lepse.employee.models.FiredEmployeeRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Component
public class FiredEmployeeDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FiredEmployeeDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Get all fired employees from the Spis_uv table
     * @return returns a list of fired employees
     * */
    public List<FiredEmployee> getFiredEmployee() {
        String query = "SELECT * FROM Spis_uv GROUP BY LOGIN";
        return jdbcTemplate.query(query, new FiredEmployeeRowMapper());
    }
}
