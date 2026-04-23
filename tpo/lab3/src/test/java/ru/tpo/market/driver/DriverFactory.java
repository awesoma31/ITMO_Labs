package ru.tpo.market.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.List;

public class DriverFactory {

    private static final boolean HEADLESS =
            Boolean.parseBoolean(System.getProperty("headless", "false"));

    public static WebDriver create(String browser) {
        return switch (browser.toLowerCase()) {
            case "chrome" -> createChrome();
            case "firefox" -> createFirefox();
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
    }

    private static WebDriver createChrome() {
        ChromeOptions opts = new ChromeOptions();
        if (HEADLESS) {
            opts.addArguments("--headless=new");
        }
        opts.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--window-size=1920,1080",
                "--disable-blink-features=AutomationControlled",
                "--user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
        );
        opts.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        opts.setExperimentalOption("useAutomationExtension", false);
        return new ChromeDriver(opts);
    }

    private static WebDriver createFirefox() {
        FirefoxOptions opts = new FirefoxOptions();
        if (HEADLESS) {
            opts.addArguments("-headless");
        }
        opts.addArguments("-width=1920", "-height=1080");
        return new FirefoxDriver(opts);
    }
}
