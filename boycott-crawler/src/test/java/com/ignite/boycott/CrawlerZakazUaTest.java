package com.ignite.boycott;

import org.junit.Before;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by mdelegan on 25.12.13.
 */
public class CrawlerZakazUaTest {
    private CrawlerZakazUa crawlerZakazUa;

    @Before
    public void setUp() throws IOException {
        this.crawlerZakazUa = new CrawlerZakazUa();
    }

    @org.junit.Test
    public void parseUrl_WithCorrectBarcode() throws Exception {
        UrlData data = crawlerZakazUa.parseUrl("http://zakaz.ua/uk/products/pickled-cheese/4820006259280/%D1%81%D0%B8%D1%80-%D0%B4%D0%BE%D0%B1%D1%80%D1%8F%D0%BD%D0%B0-125%D0%B3-%D1%83%D0%BA%D1%80%D0%B0%D1%97%D0%BD%D0%B0");
        assertTrue(data.isProduct());
        assertEquals("4820006259280", data.barcode);
        assertEquals(482, data.countryCode);
        assertEquals(62, data.makerCode);
    }

    @org.junit.Test
    public void parseUrl_With0Barcode() throws Exception {
        UrlData data = crawlerZakazUa.parseUrl("http://zakaz.ua/uk/products/pickled-cheese/05760466815338/%D1%81%D0%B8%D1%80-%D0%B0%D1%80%D0%BB%D0%B0-500%D0%B3-%D0%B4%D0%B0%D0%BD%D1%96%D1%8F");
        assertTrue(data.isProduct());
        assertEquals("5760466815338", data.barcode);
        assertEquals(576, data.countryCode);
        assertEquals(4668, data.makerCode);
    }

    @org.junit.Test
    public void parseUrl_WithStoreBarcode() throws Exception {
        UrlData data = crawlerZakazUa.parseUrl("http://zakaz.ua/uk/products/krekeliny-and-muffins/amstor02381837000150/%D0%BA%D0%B5%D0%BA%D1%81-%D0%B0%D0%BC%D1%81%D1%82%D0%BE%D1%80");
        assertFalse(data.isProduct());
    }

    @org.junit.Test
    public void parseUrl_WithoutBarcode() throws Exception {
        UrlData data = crawlerZakazUa.parseUrl("http://zakaz.ua/uk/products/krekeliny-and-muffins/");
        assertFalse(data.isProduct());
    }
}
