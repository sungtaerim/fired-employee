package com.lepse.employee.dao;

import sun.reflect.CallerSensitive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TC Properties
 * */
public enum Properties {
    uid,
    user_id,
    user_name,
    status,
    is_out_of_office,
    login_group,
    last_login_time,
    inbox_delegate,
    taskinbox,
    end_date,
    start_date;

    public static String[] getEmployeeProperties() {
        List<String> values = new ArrayList<>();

        Arrays.stream(Properties.values()).forEach(property -> {
            String name = property.name();
            values.add(name);
        });
        values.remove(Properties.end_date.name());
        values.remove(Properties.start_date.name());

        return values.toArray(new String[0]);
    }

    public static String[] getSurrogateProperties() {
        List<String> values = new ArrayList<>();
        values.add(Properties.uid.name());
        values.add(Properties.user_id.name());
        values.add(Properties.user_name.name());

        return values.toArray(new String[0]);
    }

    public static String[] getUserInboxProperties() {
        return new String[] {
                Properties.end_date.name(), Properties.start_date.name()
        };
    }
}
