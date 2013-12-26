package com.ignite.boycott;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVWriter;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * Created by mdelegan on 25.12.13.
 */
public class CrawlerZakazUa extends WebCrawler {

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
            + "|png|tiff?|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf"
            + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

//    private final static Pattern PRODUCT_PAGE = Pattern.compile("(/\\p{Alpha}*\\d{9,}/)");
    private final static Pattern PRODUCT_PAGE = Pattern.compile("/0*(\\d{9,})/");
    private final static Pattern BRAND_NAME = Pattern.compile("<span itemprop=\"brand\">(.*)</span>");

    private static CSVWriter writer;
    private static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Set<String>>> countryMakerMap = new ConcurrentHashMap<>();

    static {
        try {
            writer = new CSVWriter(new FileWriter("c:\\Users\\mdelegan\\Диск Google\\projects\\Boycott\\data.csv"), '\t');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * You should implement this function to specify whether
     * the given url should be crawled or not (based on your
     * crawling logic).
     */
    @Override
    public boolean shouldVisit(WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches() && href.startsWith("http://zakaz.ua/uk/");
    }

    @Override
    public void onBeforeExit() {
        super.onBeforeExit();
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //dump mapping
        try(CSVWriter mappingWriter = new CSVWriter(new FileWriter("c:\\Users\\mdelegan\\Диск Google\\projects\\Boycott\\mapping.csv"), ',')) {
            for (ConcurrentHashMap.Entry<Integer, ConcurrentHashMap<Integer, Set<String>>> countryMaker: countryMakerMap.entrySet()) {
                for (ConcurrentHashMap.Entry<Integer, Set<String>> makerBrand : countryMaker.getValue().entrySet()) {
                    String[] row = new String[makerBrand.getValue().size() + 2];
                    row[0] = countryMaker.getKey().toString();
                    row[1] = makerBrand.getKey().toString();
                    int position = 2;
                    for (String brand : makerBrand.getValue()) row[position++] = brand;

                    mappingWriter.writeNext(row);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();

        UrlData urlData = parseUrl(url);
        if (urlData.isProduct()) {
            if (page.getParseData() instanceof HtmlParseData) {
                HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
                String html = htmlParseData.getHtml();

                String brand = null;
                Matcher matcher = BRAND_NAME.matcher(html);
                if (matcher.find()) {
                    brand = matcher.group(1);
                }

                String title = htmlParseData.getTitle();

                write(url, urlData, brand, title);
                addToMap(urlData, brand);
            }
        }
    }

    private void addToMap(UrlData urlData, String brand) {
        if (!countryMakerMap.containsKey(urlData.countryCode))
            countryMakerMap.putIfAbsent(urlData.countryCode, new ConcurrentHashMap<Integer, Set<String>>());

        ConcurrentHashMap<Integer, Set<String>> makerMap = countryMakerMap.get(urlData.countryCode);
        if (!makerMap.containsKey(urlData.makerCode))
            makerMap.putIfAbsent(urlData.makerCode, Collections.synchronizedSet(new HashSet<String>()));

        Set<String> brands = makerMap.get(urlData.makerCode);
        brands.add(brand);
    }

    private synchronized void write(String url, UrlData urlData, String brand, String title) {
        try {
            writer.writeNext(new String[] {Integer.toString(urlData.countryCode), Integer.toString(urlData.makerCode),
                    urlData.barcode, brand, URLDecoder.decode(url, "UTF-8"), title.replaceAll("\n", " ")});
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    UrlData parseUrl(String url) {
        Matcher matcher = PRODUCT_PAGE.matcher(url);
        if (matcher.find()) {
            return new UrlData(url, matcher.group(1));
        } else {
            return new UrlData(url, null);
        }
    }
}
