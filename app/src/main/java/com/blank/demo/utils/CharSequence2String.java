package com.blank.demo.utils;

/**
 * <pre>
 *     author : fupp-
 *     time   : 2024/04/20
 *     desc   :
 * </pre>
 */
public class CharSequence2String {
    public static String[] convertToStringArray(CharSequence[] charSequences) {
        if (charSequences == null) {
            return null;
        }
        String[] strings = new String[charSequences.length];
        for (int i = 0; i < charSequences.length; i++) {
            strings[i] = charSequences[i].toString();
        }
        return strings;
    }
}
