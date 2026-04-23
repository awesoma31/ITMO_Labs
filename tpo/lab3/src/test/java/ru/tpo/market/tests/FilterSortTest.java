package ru.tpo.market.tests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.tpo.market.pages.MainPage;
import ru.tpo.market.pages.SearchResultsPage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-2: Просмотр и фильтрация результатов поиска.
 *
 * Тест-кейсы:
 *   TC-04 — на странице результатов отображаются названия товаров
 *   TC-05 — на странице результатов отображаются цены товаров
 *   TC-06 — на странице результатов присутствует кнопка сортировки по цене
 */
class FilterSortTest extends BaseTest {

    /**
     * TC-04: Названия товаров отображаются в результатах поиска.
     * Предусловие: market.yandex.ru доступен.
     * Шаги: открыть главную страницу → ввести «телевизор» → нажать «Найти».
     * Ожидаемый результат: найден хотя бы один непустой заголовок товара.
     */
    @ParameterizedTest(name = "TC-04 Названия товаров присутствуют [{0}]")
    @MethodSource("browsers")
    void productTitlesPresent(String browser) {
        setup(browser);
        SearchResultsPage results = new MainPage(driver).open().search("телевизор");
        results.waitForResults();
        List<String> titles = results.getProductTitles();
        assertFalse(titles.isEmpty(),
                "На странице результатов должны отображаться названия товаров");
        assertTrue(titles.stream().anyMatch(t -> !t.isBlank()),
                "Хотя бы одно название товара не должно быть пустым");
    }

    /**
     * TC-05: Цены товаров отображаются в результатах поиска.
     * Предусловие: market.yandex.ru доступен.
     * Шаги: открыть главную страницу → ввести «наушники» → нажать «Найти».
     * Ожидаемый результат: найдена хотя бы одна цена с символом ₽.
     */
    @ParameterizedTest(name = "TC-05 Цены товаров отображаются [{0}]")
    @MethodSource("browsers")
    void pricesDisplayed(String browser) {
        setup(browser);
        SearchResultsPage results = new MainPage(driver).open().search("наушники");
        results.waitForResults();
        List<String> prices = results.getPrices();
        assertFalse(prices.isEmpty(),
                "На странице результатов должны отображаться цены товаров");
    }

    /**
     * TC-06: На странице результатов присутствует кнопка сортировки по цене.
     * Предусловие: market.yandex.ru доступен.
     * Шаги: открыть главную страницу → ввести «ноутбук» → нажать «Найти».
     * Ожидаемый результат: кнопка «по цене» видна на странице результатов.
     */
    @ParameterizedTest(name = "TC-06 Кнопка сортировки по цене присутствует [{0}]")
    @MethodSource("browsers")
    void sortByPriceButtonPresent(String browser) {
        setup(browser);
        SearchResultsPage results = new MainPage(driver).open().search("ноутбук");
        results.waitForResults();
        assertTrue(results.hasSortByPriceButton(),
                "На странице результатов должна быть кнопка сортировки по цене");
    }
}
