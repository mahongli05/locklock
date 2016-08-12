package com.common.util;

/**
 * Created by Administrator on 2016/7/12 0012.
 */
public class FormatHelper {
    public static String intToUSPrice(int price) {
        String priceString = String.valueOf(price);
        int length = priceString.length();
        String billion, million, thousand, last;
        if(length > 9) {
            billion = priceString.substring(0, length - 9);
            million = priceString.substring(length - 9, length - 6);
            thousand = priceString.substring(length - 6, length - 3);
            last = priceString.substring(length - 3);
            return billion + ',' + million + ',' + thousand + ',' + last;
        }else if(length > 6) {
            million = priceString.substring(0, length - 6);
            thousand = priceString.substring(length - 6, length - 3);
            last = priceString.substring(length - 3);
            return million + ',' + thousand + ',' + last;
        }else if(length > 3) {
            thousand = priceString.substring(0, length - 3);
            last = priceString.substring(length - 3);
            return thousand + ',' + last;
        }else{
            return priceString;
        }
    }
}
