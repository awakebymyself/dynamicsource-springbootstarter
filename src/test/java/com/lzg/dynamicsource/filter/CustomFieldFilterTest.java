package com.lzg.dynamicsource.filter;

import org.junit.Test;

import java.util.regex.Matcher;

/**
 * @author 刘志钢
 */
public class CustomFieldFilterTest {

    @Test
    public void regexTest() {
        String tested = "masterWriteDriver";
        Matcher matcher = CustomFieldFilter.PATTERN.matcher(tested);

        System.out.println(matcher.matches());
        if (matcher.matches()) {
            System.out.println(matcher.group(1));
        }
    }

}