package com.lzg.dynamicsource.config;

import java.util.regex.Pattern;

/**
 * @author 刘志钢
 */
public class Constants {

    public static final String URL_SUFFIX = "Url";
    public static final String USER_SUFFIX = "User";
    public static final String PASS_SUFFIX = "Password";
    public static final String DRIVER_SUFFIX = "Driver";

    public static final String MASTER_PREFIX = "master";
    public static final String SLAVE_PREFIX = "slave";

    public static final String WRITE_DATASOURCE = "Write";
    public static final String Read_DATASOURCE = "Read";
    public static final Pattern PATTERN = Pattern.compile("([a-zA-Z]+)(Write|Read)(Url|Driver|Password|User)$");
    static final String[] READ_DATASOURCE_PREFIX = {"select", "get", "query", "find"};

}
