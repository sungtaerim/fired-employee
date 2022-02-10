package com.lepse.employee.config;

import com.lepse.employee.service.EmployeeCredentialManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@PropertySource({"classpath:application.properties"})
public class Config {

    final Environment environment;

    @Autowired
    public Config(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(environment.getProperty("spring.datasource.driver-class-name")));
        dataSource.setUrl(environment.getProperty("spring.datasource.url"));
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(this.dataSource());
    }

    @Bean
    public EmployeeCredentialManager credentialManagerConfig() {
        final String user = environment.getProperty("tc.server.login");
        final String password = environment.getProperty("tc.server.password");
        final String tcServer = environment.getProperty("tc.server.address");

        return new EmployeeCredentialManager(user, password, tcServer);
    }
}
