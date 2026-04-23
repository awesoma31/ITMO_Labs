package ru.tpo.market.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductPage extends BasePage {

    private static final By TITLE = By.xpath(
            "//h1[@data-auto='productCardTitle']" +
            " | //h1[contains(@itemprop,'name')]" +
            " | //h1[contains(@class,'title')]" +
            " | //h1"
    );
    private static final By PRICE = By.xpath(
            "//span[@data-auto='mainPrice']" +
            " | //div[@data-auto='price-value']" +
            " | //h3[@data-auto='price']//span" +
            " | //*[contains(@class,'price') and contains(.,'₽')]"
    );
    private static final By ADD_TO_CART = By.xpath(
            "//button[@data-auto='cartButton']" +
            " | //button[contains(.,'В корзину')]"
    );
    private static final By RATING = By.xpath(
            "//div[@data-auto='rating-value']" +
            " | //span[contains(@class,'rating') and contains(@class,'value')]" +
            " | //*[@aria-label and contains(@aria-label,'рейтинг')]"
    );
    private static final By BREADCRUMBS = By.xpath(
            "//nav[@aria-label='breadcrumb']" +
            " | //ol[contains(@class,'breadcrumb')]" +
            " | //ul[@data-auto='breadcrumbs']" +
            " | //div[@data-auto='breadcrumbs']"
    );
    private static final By SPECS_TAB = By.xpath(
            "//a[contains(.,'Характеристики')]" +
            " | //button[contains(.,'Характеристики')]"
    );

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    public boolean hasTitle() {
        return isPresent(TITLE);
    }

    public String getTitle() {
        return waitVisible(TITLE).getText();
    }

    public boolean hasPrice() {
        return isPresent(PRICE);
    }

    public String getPrice() {
        return waitVisible(PRICE).getText();
    }

    public boolean hasAddToCartButton() {
        return isPresent(ADD_TO_CART);
    }

    public boolean hasRating() {
        return isPresent(RATING);
    }

    public boolean hasBreadcrumbs() {
        return isPresent(BREADCRUMBS);
    }

    public ProductPage clickSpecsTab() {
        if (isPresent(SPECS_TAB)) {
            waitClickable(SPECS_TAB).click();
        }
        return this;
    }
}
