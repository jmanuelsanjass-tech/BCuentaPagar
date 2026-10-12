package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DemoApplicationTests {

  @Test
  void contextLoads() {
  }

  @Test
  void mainStartsApplicationWithoutWebServer() {
    assertDoesNotThrow(() -> DemoApplication.main(
        new String[] { "--spring.main.web-application-type=none" }));
  }

}
