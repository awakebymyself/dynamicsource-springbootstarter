package com.github.lzg.dynamicsource.filter;


import com.github.lzg.dynamicsource.config.Constants;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author 刘志钢
 */
public class CustomFieldFilterTest {

    @Test
    void regexTest() {
        String tested = "masterWriteDriver";
        Matcher matcher = Constants.PATTERN.matcher(tested);

        System.out.println(matcher.matches());
        assertEquals("master", matcher.group(1));
        assertEquals("Write", matcher.group(2));
    }

}