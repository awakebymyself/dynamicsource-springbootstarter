package com.github.lzg.dynamicsource;

import com.github.lzg.dynamicsource.config.DataSourceContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContext.getDataSource();
    }

}
