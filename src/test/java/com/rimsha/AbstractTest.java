package com.rimsha;

import com.rimsha.model.db.entity.Booking;
import com.rimsha.model.db.entity.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.time.LocalDate;

@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class AbstractTest {

    // Создание контейнера с PostgreSQL
    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:16")  // Укажите нужную версию PostgreSQL
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test")
                    .withReuse(true);

    // Настройка динамических свойств для подключения к базе данных в контейнере
    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private DataSource dataSource;

    @Test
    void testDatabaseConnection() throws Exception {
        // Ваши тесты
        System.out.println("DataSource: " + dataSource.getConnection().getMetaData().getURL());
    }

    protected Booking createBooking(LocalDate checkInDate, LocalDate checkOutDate, Room room) {
        Booking b = new Booking();
        b.setCheckInDate(checkInDate);
        b.setCheckOutDate(checkOutDate);
        b.setRoom(room);
        return b;
    }
}

