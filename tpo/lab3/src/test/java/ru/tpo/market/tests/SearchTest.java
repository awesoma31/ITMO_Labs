package ru.tpo.market.tests;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.tpo.market.pages.MainPage;
import ru.tpo.market.pages.ProductPage;
import ru.tpo.market.pages.SearchResultsPage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-1: Поиск товаров на Яндекс.Маркет.
 *
 * Тест-кейсы:
 *   TC-01 — поиск существующего товара возвращает карточки товаров
 *   TC-02 — результаты сортируются по цене по убыванию
 *   TC-03 — поисковый запрос отражается в URL страницы результатов
 *   TC-04 — клик на первый товар открывает страницу с заголовком и ценой
 */
class SearchTest extends BaseTest {

    /**
     * TC-01: Поиск по запросу «ноутбук» возвращает список товаров.
     * Предусловие: market.yandex.ru доступен.
     * Шаги: открыть главную страницу → ввести «ноутбук» → нажать «Найти».
     * Ожидаемый результат: страница результатов содержит карточки товаров.
     */
    @ParameterizedTest(name = "TC-01 Поиск возвращает результаты [{0}]")
    @MethodSource("browsers")
    void searchReturnsResults(String browser) {
        setup(browser);
        SearchResultsPage results = new MainPage(driver).open().search("ноутбук");
        results.waitForResults();
        assertTrue(results.hasResults(),
                "Поиск 'ноутбук' должен вернуть карточки товаров");
    }

    /**
     * TC-02: Результаты поиска сортируются по цене по убыванию («Подороже»).
     * Предусловие: market.yandex.ru доступен.
     * Шаги: открыть главную страницу → ввести «ноутбук» → нажать «Найти» → открыть дропдаун сортировки → выбрать «Подороже».
     * Ожидаемый результат: цены карточек идут в порядке убывания.
     */
    @Disabled("Сортировка по цене не реализована — кнопка не кликается. разрабы яндекса долбоебы. чтобы кликнуть по ебучей Подороже - надо мать продать")
    @ParameterizedTest(name = "TC-02 Результаты сортируются по цене [{0}]")
    @MethodSource("browsers")
    void sortingByPriceDescending(String browser) {
        setup(browser);
        SearchResultsPage results = new MainPage(driver).open().search("ноутбук");
        results.waitForResults();
        
        results.sortByPrice();
        List<Integer> prices = results.getParsedPrices();
        assertFalse(prices.isEmpty(), "После сортировки по цене должны быть карточки с ценами");
        for (int i = 0; i < prices.size() - 1; i++) {
            assertTrue(prices.get(i) >= prices.get(i + 1),
                    "Цена [" + i + "]=" + prices.get(i) + " меньше цены [" + (i + 1) + "]=" + prices.get(i + 1) + " — сортировка «Подороже» нарушена");
        }
    }

    /**
     * TC-03: Поисковый запрос отражается в URL страницы результатов.
     * Предусловие: market.yandex.ru доступен.
     * Шаги: открыть главную страницу → ввести «смартфон» → нажать «Найти».
     * Ожидаемый результат: URL содержит поисковый запрос (в UTF-8 или URL-encoded).
     */
    @ParameterizedTest(name = "TC-03 Запрос отражён в URL [{0}]")
    @MethodSource("browsers")
    void searchQueryAppearsInUrl(String browser) {
        setup(browser);
        SearchResultsPage results = new MainPage(driver).open().search("смартфон");
        results.waitForResults();
        String url = results.getUrl();
        assertTrue(
                url.contains("смартфон") || url.contains("%D1%81%D0%BC%D0%B0%D1%80%D1%82%D1%84%D0%BE%D0%BD"),
                "URL должен содержать поисковый запрос. Текущий URL: " + url
        );
    }

    /**
     * TC-04: Клик на первый товар открывает страницу с заголовком и ценой.
     * Предусловие: market.yandex.ru доступен.
     * Шаги: открыть главную страницу → ввести «телевизор» → нажать «Найти» → кликнуть на первую карточку.
     * Ожидаемый результат: страница товара содержит заголовок и цену.
     */
    @ParameterizedTest(name = "TC-04 Страница товара содержит заголовок и цену [{0}]")
    @MethodSource("browsers")
    void productPageHasTitleAndPrice(String browser) {
        setup(browser);
        SearchResultsPage results = new MainPage(driver).open().search("телевизор");
        results.waitForResults();
        ProductPage product = results.openFirstProduct();
        assertTrue(product.hasTitle(), "Страница товара должна содержать заголовок");
        assertTrue(product.hasPrice(), "Страница товара должна содержать цену");
    }
}
