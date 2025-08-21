package com.ellen.sucurity.lab.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

    @Bean
    @ConfigurationProperties("datasource.auth-server")
    public HikariConfig dataSourceProperties(){
        return new HikariConfig();
    }

    @Bean
    public DataSource dataSource(HikariConfig config){
        return new HikariDataSource(config);
    }

    @Bean
    public JdbcTemplate registeredClientJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}