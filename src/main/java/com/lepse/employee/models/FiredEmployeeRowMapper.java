package com.lepse.employee.models;

import org.springframework.jdbc.core.RowMapper;

import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FiredEmployeeRowMapper implements RowMapper<FiredEmployee> {

    private ResultSet resultSet;

    @Override
    public FiredEmployee mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        this.resultSet = resultSet;

        FiredEmployee firedEmployee = new FiredEmployee();
        firedEmployee.setFio(decodeResultSetMember("FIO"));
        firedEmployee.setLogin(decodeResultSetMember("LOGIN"));
        firedEmployee.setDate(resultSet.getDate("DATA_UV"));

        return firedEmployee;
    }

    private String decodeResultSetMember(String memberName) throws SQLException {
        Charset decodingCharset = Charset.forName("CP1251");
        return new String(this.resultSet.getBytes(memberName), decodingCharset);
    }
}
