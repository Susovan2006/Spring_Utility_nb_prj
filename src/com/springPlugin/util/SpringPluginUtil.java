/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springPlugin.util;

import java.util.List;
import java.util.regex.Pattern;
import java.util.Arrays;

/**
 *
 * @author Sandipan
 */
public class SpringPluginUtil {

    /**
     * @param args the command line arguments
     */
   /* public static void main(String[] args) {

        System.out.println(changeFieldName("First_Name"));
    }*/

    public static String changeFieldName(String fieldName) {
        boolean isAllCapital = true;
        for (int i = 0; i < fieldName.length(); i++) {
            int ascii = fieldName.charAt(i);
            if (ascii >= 97 && ascii <= 122) {
                isAllCapital = false;
            }
        }
        if (isAllCapital) {
            fieldName = fieldName.toLowerCase();
        }

        if (fieldName.contains("_")) {
            fieldName = fieldName.toLowerCase();
            while (fieldName.contains("_")) {
                int position = fieldName.indexOf("_");
                String replaceString = fieldName.substring(position + 1, position + 2).toUpperCase();
                fieldName = fieldName.replaceFirst("_", "");
                fieldName = fieldName.substring(0, position) + replaceString + fieldName.substring(position + 1);
            }
        }
        
        fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);

        return fieldName;
    }

    public static String formatJavaFile(String code) {
        final String CH_224 = "" + (char) 224;
        final String CH_225 = "" + (char) 225;
        final String CH_226 = "" + (char) 226;
        code = code.replaceAll("[;]", ";" + CH_224).replaceAll("[\\{]", "{" + CH_225).replaceAll("[\\}]", "}" + CH_226);
        String regex = "[" + CH_224 + "]|[" + CH_225 + "]|[" + CH_226 + "]";

        Pattern pattern = Pattern.compile(regex);
        String[] data = pattern.split(code);
        List<String> codeLines = Arrays.asList(data);

        StringBuilder sb = new StringBuilder();
        int tabCount = 0;
        for (String st : codeLines) {
            if (st.trim().endsWith("{")) {
                tabCount = tabCount + 1;
            } else if (st.trim().endsWith("}")) {
                tabCount = tabCount - 1;
            }
            st = st + "\n" + tabAppender(tabCount);
            sb.append(st);
        }
        return sb.toString();
    }

    private static String tabAppender(int count) {
        String tabber = "";
        for (int i = 0; i < count; i++) {
            tabber = tabber + "\t";
        }
        return tabber;
    }
}
