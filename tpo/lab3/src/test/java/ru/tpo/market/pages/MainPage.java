package ru.tpo.market.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MainPage extends BasePage {

    public static final String URL = "https://market.yandex.ru";

    // XPath using data-auto (Yandex's own test automation attributes) where possible
    private static final By SEARCH_INPUT = By.xpath(
            "//input[@data-auto='search-input-field']" +
            " | //input[@id='header-search']" +
            " | //form[contains(@action,'search')]//input[@type='text' or @type='search']"
    );
    private static final By SEARCH_SUBMIT = By.xpath(
            "//button[@data-auto='search-submit']" +
            " | //form[contains(@action,'search')]//button[@type='submit']"
    );

    // Dialogs that may appear on first visit
    private static final By REGION_CONFIRM = By.xpath(
            "//button[contains(.,'Всё верно') or contains(.,'Хорошо') or contains(.,'ОК')]"
    );
    private static final By COOKIE_ACCEPT = By.xpath(
            "//button[contains(@data-auto,'cookie') and contains(.,'Принять')]" +
            " | //button[contains(@class,'cookie') and contains(.,'Принять')]"
    );

    public MainPage(WebDriver driver) {
        super(driver);
    }

    public MainPage open() {
        driver.get(URL);
        dismissDialogs();
        return this;
    }

    public SearchResultsPage search(String query) {
        WebElement input = waitClickable(SEARCH_INPUT);
        input.clear();
        input.sendKeys(query);

        if (isPresent(SEARCH_SUBMIT)) {
            waitClickable(SEARCH_SUBMIT).click();
        } else {
            input.sendKeys(Keys.ENTER);
        }
        return new SearchResultsPage(driver);
    }

    private void dismissDialogs() {
        if (isPresent(REGION_CONFIRM)) {
            driver.findElement(REGION_CONFIRM).click();
        }
        if (isPresent(COOKIE_ACCEPT)) {
            driver.findElement(COOKIE_ACCEPT).click();
        }
    }
}
