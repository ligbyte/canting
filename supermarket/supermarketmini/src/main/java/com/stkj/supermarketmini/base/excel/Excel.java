package com.stkj.supermarketmini.base.excel;

import com.stkj.supermarketmini.base.excel.impl.ExcelImpl;
import com.stkj.supermarketmini.base.excel.service.IExcelUtils;
import com.stkj.supermarketmini.base.excel.service.IParseListener;
import com.stkj.supermarketmini.base.excel.service.IWriteListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


public class Excel {

    private static final String TAG = "ExcelUtils";

    private static volatile Excel excel;

    private final IExcelUtils iExcelUtils;

    private InputStream inputStream;

    private OutputStream outputStream;


    private Excel() {
        iExcelUtils = new ExcelImpl();
    }

    public static Excel get() {
        if (excel == null) {
            excel = new Excel();
        }
        return excel;
    }


    public Excel readWith(File file) {
        if (excel == null) {
            throw new RuntimeException("Excel is null must call get() first");
        }
        try {
            this.inputStream = new FileInputStream(file);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return excel;
    }

    public Excel readWith(InputStream inputStream) {
        if (excel == null) {
            throw new RuntimeException("Excel is null must call get() first");
        }
        this.inputStream = inputStream;
        return excel;
    }

    public <T> void doWrite(IWriteListener listener, List<T> data) {
        listener.onStartWrite();
        iExcelUtils.writeExcelXSSF(outputStream, data, listener);
    }

    public <T> Excel writeWith(OutputStream outputStream) {
        if (excel == null) {
            throw new RuntimeException("Excel is null must call get() first");
        }
        this.outputStream = outputStream;
        return excel;
    }

    public <T> Excel writeWith(File file) {
        if (excel == null) {
            throw new RuntimeException("Excel is null must call get() first");
        }
        try {
            this.outputStream = new FileOutputStream(file);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return excel;
    }

    public <T> void doReadXLSX(IParseListener<T> listener, Class<T> clazz) throws IOException {
        listener.onStartParse();
        iExcelUtils.readExcelXSSF(inputStream, clazz, listener);
    }

    public <T> void doReadXLS(IParseListener<T> listener, Class<T> clazz) {
        listener.onStartParse();
        iExcelUtils.readExcelHSSF(inputStream, clazz, listener);

    }


}
