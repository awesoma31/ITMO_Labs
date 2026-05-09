package ru.tpo.market.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class SearchResultsPage extends BasePage {

    private static final By PRODUCT_CARDS = By.xpath(
            "//article[@data-auto='productCard']" +
            " | //div[@data-auto='snippet-cell']" +
            " | //li[contains(@data-auto,'productCard')]" +
            " | //*[@data-auto='snippet-title']"
    );
    private static final By PRODUCT_TITLES = By.xpath(
            "//h3[@data-auto='snippet-title']//span" +
            " | //a[@data-auto='snippet-title-link']" +
            " | //div[@data-auto='snippet-title']//span" +
            " | //*[@data-auto='snippet-title']" +
            " | //article[@data-auto='productCard']//h3" +
            " | //div[@data-auto='snippet-cell']//h3" +
            " | //li[contains(@data-auto,'productCard')]//h3"
    );
    private static final By FIRST_PRODUCT_LINK = By.xpath(
            "(//*[@data-auto='snippet-title'])[1]"
    );
    private static final By NO_RESULTS = By.xpath(
            "//*[contains(text(),'Ничего не нашлось')" +
            " or contains(text(),'Ничего не найдено')" +
            " or contains(text(),'не найден')]"
    );
    private static final By PRICES = By.xpath(
            "//span[@data-auto='mainPrice']" +
            " | //div[@data-auto='price']//span[contains(.,'₽')]" +
            " | //span[contains(@class,'price') and contains(.,'₽')]"
    );

    private static final By SORT_SECTION = By.xpath(
            "//div[@data-auto='SearchSort']"
    );
    private static final By SORT_CHIP_TRIGGER = By.xpath(
            "//button[contains(@aria-label,'Показать сначала')]"
    );
    private static final By SORT_BY_PRICE_DESC = By.xpath(
            "//div[@data-auto='tooltip__content']//button[normalize-space()='Подороже']"
    );

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    public boolean waitForResults() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(PRODUCT_CARDS),
                    ExpectedConditions.presenceOfElementLocated(NO_RESULTS)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasResults() {
        return !findAll(PRODUCT_CARDS).isEmpty();
    }

    public boolean hasNoResultsMessage() {
        return isPresent(NO_RESULTS);
    }

    public int getResultCount() {
        return findAll(PRODUCT_CARDS).size();
    }

    public List<String> getProductTitles() {
        List<String> titles = findAll(PRODUCT_TITLES).stream()
                .map(WebElement::getText)
                .filter(t -> !t.isBlank())
                .toList();
        if (!titles.isEmpty()) return titles;
        // fallback: take non-blank text from card elements themselves
        return findAll(PRODUCT_CARDS).stream()
                .map(WebElement::getText)
                .map(t -> t.lines().findFirst().orElse(""))
                .filter(t -> !t.isBlank())
                .toList();
    }

    public List<String> getPrices() {
        return findAll(PRICES).stream()
                .map(WebElement::getText)
                .filter(t -> !t.isBlank())
                .toList();
    }

    public ProductPage openFirstProduct() {
        waitClickable(FIRST_PRODUCT_LINK).click();
        return new ProductPage(driver);
    }

    public boolean hasSortByPriceButton() {
        return isPresent(SORT_CHIP_TRIGGER);
    }

    public SearchResultsPage sortByPrice() {
        wait.until(ExpectedConditions.presenceOfElementLocated(SORT_SECTION));
        waitClickable(SORT_CHIP_TRIGGER).click();
        WebElement option = wait.until(
                ExpectedConditions.presenceOfElementLocated(SORT_BY_PRICE_DESC));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
        waitForResults();
        return this;
    }

    public List<Integer> getParsedPrices() {
        return getPrices().stream()
                .map(p -> p.replaceAll("[^0-9]", ""))
                .filter(p -> !p.isEmpty())
                .map(Integer::parseInt)
                .toList();
    }
}
