/**
 * 
 */
package edu.washington.shan.stock;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

/**
 * @author shan@uw.edu
 *
 */
public class Stock {
    
    private static final String TAG = "Stock";
    
    // TODO make them private
    public String symbol;
    public String pretty_symbol;
    public String symbol_lookup_url;
    public String company;
    public String exchange;
    public String exchange_timezone;
    public String exchange_utc_offset;
    public String exchange_closing;
    public String divisor;
    public String currency;
    public String last;
    public String high;
    public String low;
    public String volume;
    public String avg_volume;
    public String market_cap;
    public String open;
    public String y_close;
    public String change;
    public String perc_change;
    public String delay;
    public String trade_timestamp;
    public String trade_date_utc;
    public String trade_time_utc;
    public String current_date_utc;
    public String current_time_utc;
    public String symbol_url;
    public String chart_url;
    public String disclaimer_url;
    public String ecn_url;
    public String isld_last;
    public String isld_trade_date_utc;
    public String isld_trade_time_utc;
    public String brut_last;
    public String brut_trade_date_utc;
    public String brut_trade_time_utc;
    public String daylight_savings;
    
    private Stock(){
        // default ctor used in the parser
    }
    
    public Stock(
            String symbol,
            String pretty_symbol,
            String symbol_lookup_url,
            String company,
            String exchange,
            String exchange_timezone,
            String exchange_utc_offset,
            String exchange_closing,
            String divisor,
            String currency,
            String last,
            String high,
            String low,
            String volume,
            String avg_volume,
            String market_cap,
            String open,
            String y_close,
            String change,
            String perc_change,
            String delay,
            String trade_timestamp,
            String trade_date_utc,
            String trade_time_utc,
            String current_date_utc,
            String current_time_utc,
            String symbol_url,
            String chart_url,
            String disclaimer_url,
            String ecn_url,
            String isld_last,
            String isld_trade_date_utc,
            String isld_trade_time_utc,
            String brut_last,
            String brut_trade_date_utc,
            String brut_trade_time_utc,
            String daylight_savings
            ){
        this.symbol = symbol;
        this.pretty_symbol = pretty_symbol;
        this.symbol_lookup_url = symbol_lookup_url;
        this.company = company;
        this.exchange = exchange;
        this.exchange_timezone = exchange_timezone;
        this.exchange_utc_offset = exchange_utc_offset;
        this.exchange_closing = exchange_closing;
        this.divisor = divisor;
        this.currency = currency;
        this.last = last;
        this.high = high;
        this.low = low;
        this.volume = volume;
        this.avg_volume = avg_volume;
        this.market_cap = market_cap;
        this.open = open;
        this.y_close = y_close;
        this.change = change;
        this.perc_change = perc_change;
        this.delay = delay;
        this.trade_timestamp = trade_timestamp;
        this.trade_date_utc = trade_date_utc;
        this.trade_time_utc = trade_time_utc;
        this.current_date_utc = current_date_utc;
        this.current_time_utc = current_time_utc;
        this.symbol_url = symbol_url;
        this.chart_url = chart_url;
        this.disclaimer_url = disclaimer_url;
        this.ecn_url = ecn_url;
        this.isld_last = isld_last;
        this.isld_trade_date_utc = isld_trade_date_utc;
        this.isld_trade_time_utc = isld_trade_time_utc;
        this.brut_last = brut_last;
        this.brut_trade_date_utc = brut_trade_date_utc;
        this.brut_trade_time_utc = brut_trade_time_utc;
        this.daylight_savings = daylight_savings;
    }

    public static Stock[] parse(String xmlStr) throws XmlPullParserException, IOException {
        if (xmlStr == null || xmlStr.length() == 0)
            return null;
        
        List<Stock> stocks = new ArrayList<Stock>();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        // factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput(new StringReader(xmlStr));
        int eventType = xpp.getEventType();
        Stock stock = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
                // nothing to do
            } else if (eventType == XmlPullParser.START_TAG) {
                String name = xpp.getName();
                
                if(name.equals("finance")) stock = new Stock();
                else if (name.equals("symbol")) stock.symbol = xpp.getAttributeValue("", "data");
                else if (name.equals("pretty_symbol")) stock.pretty_symbol = xpp.getAttributeValue("", "data");
                else if (name.equals("symbol_lookup_url")) stock.symbol_lookup_url = xpp.getAttributeValue("", "data");
                else if (name.equals("company")) stock.company = xpp.getAttributeValue("", "data");
                else if (name.equals("exchange")) stock.exchange = xpp.getAttributeValue("", "data");
                else if (name.equals("exchange_timezone")) stock.exchange_timezone = xpp.getAttributeValue("", "data");
                else if (name.equals("exchange_utc_offset")) stock.exchange_utc_offset = xpp.getAttributeValue("", "data");
                else if (name.equals("exchange_closing")) stock.exchange_closing = xpp.getAttributeValue("", "data");
                else if (name.equals("divisor")) stock.divisor = xpp.getAttributeValue("", "data");
                else if (name.equals("currency")) stock.currency = xpp.getAttributeValue("", "data");
                else if (name.equals("last")) stock.last = xpp.getAttributeValue("", "data");
                else if (name.equals("high")) stock.high = xpp.getAttributeValue("", "data");
                else if (name.equals("low")) stock.low = xpp.getAttributeValue("", "data");
                else if (name.equals("volume")) stock.volume = xpp.getAttributeValue("", "data");
                else if (name.equals("avg_volume")) stock.avg_volume = xpp.getAttributeValue("", "data");
                else if (name.equals("market_cap")) stock.market_cap = xpp.getAttributeValue("", "data");
                else if (name.equals("open")) stock.open = xpp.getAttributeValue("", "data");
                else if (name.equals("y_close")) stock.y_close = xpp.getAttributeValue("", "data");
                else if (name.equals("change")) stock.change = xpp.getAttributeValue("", "data");
                else if (name.equals("perc_change")) stock.perc_change = xpp.getAttributeValue("", "data");
                else if (name.equals("delay")) stock.delay = xpp.getAttributeValue("", "data");
                else if (name.equals("trade_timestamp")) stock.trade_timestamp = xpp.getAttributeValue("", "data");
                else if (name.equals("trade_date_utc")) stock.trade_date_utc = xpp.getAttributeValue("", "data");
                else if (name.equals("trade_time_utc")) stock.trade_time_utc = xpp.getAttributeValue("", "data");
                else if (name.equals("current_date_utc")) stock.current_date_utc = xpp.getAttributeValue("", "data");
                else if (name.equals("current_time_utc")) stock.current_time_utc = xpp.getAttributeValue("", "data");
                else if (name.equals("symbol_url")) stock.symbol_url = xpp.getAttributeValue("", "data");
                else if (name.equals("chart_url")) stock.chart_url = xpp.getAttributeValue("", "data");
                else if (name.equals("disclaimer_url")) stock.disclaimer_url = xpp.getAttributeValue("", "data");
                else if (name.equals("ecn_url")) stock.ecn_url = xpp.getAttributeValue("", "data");
                else if (name.equals("isld_last")) stock.isld_last = xpp.getAttributeValue("", "data");
                else if (name.equals("isld_trade_date_utc")) stock.isld_trade_date_utc = xpp.getAttributeValue("", "data");
                else if (name.equals("isld_trade_time_utc")) stock.isld_trade_time_utc = xpp.getAttributeValue("", "data");
                else if (name.equals("brut_last")) stock.brut_last = xpp.getAttributeValue("", "data");
                else if (name.equals("brut_trade_date_utc")) stock.brut_trade_date_utc = xpp.getAttributeValue("", "data");
                else if (name.equals("brut_trade_time_utc")) stock.brut_trade_time_utc = xpp.getAttributeValue("", "data");
                else if (name.equals("daylight_savings")) stock.daylight_savings = xpp.getAttributeValue("", "data");
                
            } else if (eventType == XmlPullParser.END_TAG
                    && xpp.getName().equalsIgnoreCase("finance")) {
                stocks.add(stock);
            }
            eventType = xpp.next();
        }
        return stocks.toArray(new Stock[]{});
    }
    
    public static void storeToDatabase(Context context, Stock[] stocks){
        DBAdapter dbAdapter = new DBAdapter(context);
        try{
            dbAdapter.open();
            for(Stock stock : stocks){
                String symbol = stock.symbol;
                if(!dbAdapter.doesItemExist(symbol)){
                    if(dbAdapter.createItem(stock) != -1){
                        Log.v(TAG, "successfully added to db " + symbol);
                    }else{
                        Log.e(TAG, "failed to add to db " + symbol);
                    }
                }else{
                    Log.v(TAG, "item already exists in db " + symbol);
                    dbAdapter.updateItem(stock);
                }
            }
        }finally{
            dbAdapter.close();
        }
    }

}
