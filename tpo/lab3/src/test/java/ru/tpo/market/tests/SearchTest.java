package ru.tpo.market.tests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.tpo.market.pages.MainPage;
import ru.tpo.market.pages.SearchResultsPage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-1: Поиск товаров на Яндекс.Маркет.
 *
 * Тест-кейсы:
 *   TC-01 — поиск существующего товара возвращает карточки товаров
 *   TC-02 — поисковый запрос отражается в URL страницы результатов
 *   TC-03 — поиск несуществующего товара показывает сообщение «ничего не найдено»
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
     * TC-02: Поисковый запрос отражается в URL.
     * Предусловие: market.yandex.ru доступен.
     * Шаги: открыть главную страницу → ввести «смартфон» → нажать «Найти».
     * Ожидаемый результат: URL содержит поисковый запрос (в UTF-8 или URL-encoded).
     */
    @ParameterizedTest(name = "TC-02 Запрос отражён в URL [{0}]")
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
     * TC-03: Поиск несуществующего товара не возвращает результатов.
     * Предусловие: market.yandex.ru доступен.
     * Шаги: открыть главную страницу → ввести случайную строку → нажать «Найти».
     * Ожидаемый результат: карточки товаров отсутствуют ИЛИ отображается сообщение «ничего не найдено».
     */
    @ParameterizedTest(name = "TC-03 Поиск без результатов [{0}]")
    @MethodSource("browsers")
    void searchNoResults(String browser) {
        setup(browser);
        SearchResultsPage results = new MainPage(driver).open()
                .search("zzxqwerty12345нетакоготовара9999");
        results.waitForResults();
        boolean noCards = !results.hasResults();
        boolean noResultsMsg = results.hasNoResultsMessage();
        assertTrue(noCards || noResultsMsg,
                "Поиск несуществующего товара должен дать пустые результаты или сообщение 'ничего не найдено'");
    }
}
