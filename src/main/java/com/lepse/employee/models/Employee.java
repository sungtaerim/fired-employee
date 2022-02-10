package com.lepse.employee.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;

/**
 * Employee model
 * */
@Data
@JsonRootName("active_user")
public class Employee {

    @JsonProperty("fired_date")
    private Date firedDate;
    @JsonProperty("uid")
    private String uid;
    @JsonProperty("name")
    private String name;
    @JsonProperty("id")
    private String id;
    @JsonProperty("status")
    private String status;
    @JsonProperty("last_login")
    private String lastLogin;
    @JsonProperty("is_out")
    private String isOut;
    @JsonProperty("group")
    private String group;
    @JsonProperty("surrogate")
    private Surrogate surrogate;

    public String getIsOut() {
        if (isOut.toLowerCase(Locale.ROOT).equals("false")) {
            return "Нет";
        } else {
            return "Да";
        }
    }

    public String getLastLogin() {
        if (lastLogin != null && !lastLogin.isEmpty()) {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("dd-MMM-yyyy HH:mm")
                    .toFormatter(Locale.ENGLISH);

            TemporalAccessor temporalAccessor = formatter.parse(lastLogin);
            return temporalAccessor.toString().substring(temporalAccessor.toString().lastIndexOf(' ') + 1).replace("T", " ");
        }
        return "unknown";
    }
}
