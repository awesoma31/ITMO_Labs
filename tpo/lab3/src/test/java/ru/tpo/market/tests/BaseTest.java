package ru.tpo.market.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.provider.Arguments;
import org.openqa.selenium.WebDriver;
import ru.tpo.market.driver.DriverFactory;

import java.util.stream.Stream;

public abstract class BaseTest {

    protected WebDriver driver;

    protected static Stream<Arguments> browsers() {
        return Stream.of(
                Arguments.of("chrome"),
                Arguments.of("firefox")
        );
    }

    protected void setup(String browser) {
        driver = DriverFactory.create(browser);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
