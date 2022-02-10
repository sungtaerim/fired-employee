package com.lepse.employee.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * FiredEmployee model
 * */
@Data
@JsonRootName("employee")
public class FiredEmployee {

    @JsonProperty("name")
    private String fio;
    @JsonProperty("id")
    private String login;
    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;

    public String getFio() {
        return fio.trim();
    }

    public String getLogin() {
        return login.trim();
    }
}
