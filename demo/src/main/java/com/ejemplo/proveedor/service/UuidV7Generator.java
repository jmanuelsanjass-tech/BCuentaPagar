package com.ejemplo.proveedor.service;

import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UuidV7Generator {

  private final JdbcTemplate jdbcTemplate;

  public UuidV7Generator(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public UUID generate() {
    return jdbcTemplate.queryForObject("SELECT uuidv7()", UUID.class);
  }
}
