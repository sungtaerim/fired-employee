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
 * Surrogate model
 * */
@Data
@JsonRootName("surrogate")
public class Surrogate {

    @JsonProperty("uid")
    private String uid;
    @JsonProperty("id")
    private String userId;
    @JsonProperty("name")
    private String userName;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;

    public String getStartDate() {
        return dateFormat(startDate);
    }

    public String getEndDate() {
        return dateFormat(endDate);
    }

    private String dateFormat(String param) {
        if (param != null && !param.isEmpty()) {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("dd-MMM-yyyy HH:mm")
                    .toFormatter(Locale.ENGLISH);

            TemporalAccessor temporalAccessor = formatter.parse(param);
            return temporalAccessor.toString().substring(temporalAccessor.toString().lastIndexOf(' ') + 1).replace("T", " ");
        }
        return "unknown";
    }
}
