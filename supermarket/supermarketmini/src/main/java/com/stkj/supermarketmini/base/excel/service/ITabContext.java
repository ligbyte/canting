package com.stkj.supermarketmini.base.excel.service;

import java.util.Map;

public interface ITabContext {

        String getSheetName();

        int getSheetIndex();

        Map<Integer,String> getExcelProperty();

}
