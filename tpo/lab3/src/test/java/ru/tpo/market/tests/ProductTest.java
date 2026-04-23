package ru.tpo.market.tests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.tpo.market.pages.MainPage;
import ru.tpo.market.pages.ProductPage;
import ru.tpo.market.pages.SearchResultsPage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-3: Просмотр карточки товара.
 *
 * Тест-кейсы:
 *   TC-07 — страница товара загружается и содержит заголовок (h1)
 *   TC-08 — страница товара отображает цену
 */
class ProductTest extends BaseTest {

    /**
     * TC-07: Страница товара содержит непустой заголовок.
     * Предусловие: market.yandex.ru доступен; в результатах поиска «планшет» есть хотя бы один товар.
     * Шаги: открыть главную страницу → ввести «планшет» → открыть первый результат.
     * Ожидаемый результат: страница товара загружена, заголовок h1 непустой.
     */
    @ParameterizedTest(name = "TC-07 Страница товара содержит заголовок [{0}]")
    @MethodSource("browsers")
    void productPageHasTitle(String browser) {
        setup(browser);
        SearchResultsPage results = new MainPage(driver).open().search("планшет");
        results.waitForResults();
        assertTrue(results.hasResults(),
                "Нужен хотя бы один товар в результатах поиска");
        ProductPage product = results.openFirstProduct();
        assertTrue(product.hasTitle(),
                "Страница товара должна содержать видимый заголовок h1");
        assertFalse(product.getTitle().isBlank(),
                "Заголовок товара не должен быть пустым");
    }

    /**
     * TC-08: Страница товара отображает цену.
     * Предусловие: market.yandex.ru доступен; в результатах поиска «монитор» есть хотя бы один товар.
     * Шаги: открыть главную страницу → ввести «монитор» → открыть первый результат.
     * Ожидаемый результат: на странице товара отображается цена.
     */
    @ParameterizedTest(name = "TC-08 Страница товара отображает цену [{0}]")
    @MethodSource("browsers")
    void productPageHasPrice(String browser) {
        setup(browser);
        SearchResultsPage results = new MainPage(driver).open().search("монитор");
        results.waitForResults();
        assertTrue(results.hasResults(),
                "Нужен хотя бы один товар в результатах поиска");
        ProductPage product = results.openFirstProduct();
        assertTrue(product.hasPrice(),
                "Страница товара должна отображать цену");
    }
}
