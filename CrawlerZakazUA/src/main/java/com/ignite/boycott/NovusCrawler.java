package com.ignite.boycott;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
public class NovusCrawler extends WebCrawler {

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
            + "|png|tiff?|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf"
            + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

//    private final static Pattern PRODUCT_PAGE = Pattern.compile("(/\\p{Alpha}*\\d{9,}/)");
    private final static Pattern PRODUCT_PAGE = Pattern.compile("/0*(\\d{9,})/");
    private final static Pattern BRAND_NAME = Pattern.compile("<span itemprop=\"brand\">(.*)</span>");

    private static CSVWriter writer;

    static {
        try {
            writer = new CSVWriter(new FileWriter("data.csv"), '\t');
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
//            System.out.println("URL: " + urlData.url);
//            System.out.println("Barcode: " + urlData.barcode);

            if (page.getParseData() instanceof HtmlParseData) {
                HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
//                String text = htmlParseData.getText();
                String html = htmlParseData.getHtml();
//                List<WebURL> links = htmlParseData.getOutgoingUrls();

                //Brand: //*[@itemprop="brand"]/text()
                //Product name: //*[@itemprop="name"]/text()
                //

                String brand = null;
                Matcher matcher = BRAND_NAME.matcher(html);
                if (matcher.find()) {
                    brand = matcher.group(1);
                }

                String title = htmlParseData.getTitle();
//                System.out.println("Title: " + title);
//                System.out.println("Country code: " + urlData.countryCode);
//                System.out.println("Maker code: " + urlData.makerCode);
//                System.out.println("Brand name: " + brand==null?"N/A":brand);

                write(url, urlData, brand, title);

            }
        }
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
