package com.blank.demo.utils;

import java.text.DecimalFormat;

/**
 * <pre>
 *     author : fupp-
 *     time   : 2024/04/20
 *     desc   :
 * </pre>
 */
public class Number2String {

    public static String float2String(float floatValue) {
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        String formattedString = decimalFormat.format(floatValue);
        return formattedString;
    }
}
