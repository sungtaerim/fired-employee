package com.lepse.employee.models;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for correct display of employees in xml format
 * */
@Data
@JsonRootName("employees")
public class EmplModel<T> {

    @JacksonXmlElementWrapper(useWrapping = false)
    private List<T> employee = new ArrayList<>();
}
