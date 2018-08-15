package com.lzg.dynamicsource.filter;


import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;

import static com.lzg.dynamicsource.config.Constants.PATTERN;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 刘志钢
 */
public class CustomFieldFilterTest {

    @Test
     void regexTest() {
        String tested = "masterWriteDriver";
        Matcher matcher = PATTERN.matcher(tested);

        System.out.println(matcher.matches());
        assertEquals("master", matcher.group(1));
        assertEquals("Write", matcher.group(2));
    }

}