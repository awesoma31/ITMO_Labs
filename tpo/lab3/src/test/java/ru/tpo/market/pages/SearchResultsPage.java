package ru.tpo.market.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class SearchResultsPage extends BasePage {

    private static final By PRODUCT_CARDS = By.xpath(
            "//article[@data-auto='productCard']" +
            " | //div[@data-auto='snippet-cell']" +
            " | //li[contains(@data-auto,'productCard')]"
    );
    private static final By PRODUCT_TITLES = By.xpath(
            "//h3[@data-auto='snippet-title']//span" +
            " | //a[@data-auto='snippet-title-link']" +
            " | //div[@data-auto='snippet-title']//span"
    );
    // [1] on union returns first node in document order
    private static final By FIRST_PRODUCT_LINK = By.xpath(
            "(//h3[@data-auto='snippet-title']//a" +
            " | //a[@data-auto='snippet-title-link'])[1]"
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
    private static final By SORT_PRICE_BUTTON = By.xpath(
            "//div[@data-auto='sort']//button[contains(.,'цене')]" +
            " | //button[contains(@data-auto,'sort') and contains(.,'цене')]" +
            " | //span[contains(.,'по цене')]/.."
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
        return findAll(PRODUCT_TITLES).stream()
                .map(WebElement::getText)
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
        WebElement link = wait.until(
                ExpectedConditions.presenceOfElementLocated(FIRST_PRODUCT_LINK)
        );
        String href = link.getAttribute("href");
        // Navigate directly to avoid new-tab issues on dynamic SPAs
        if (href != null && href.startsWith("http")) {
            driver.get(href);
        } else {
            waitClickable(FIRST_PRODUCT_LINK).click();
        }
        return new ProductPage(driver);
    }

    public boolean hasSortByPriceButton() {
        return isPresent(SORT_PRICE_BUTTON);
    }
}
