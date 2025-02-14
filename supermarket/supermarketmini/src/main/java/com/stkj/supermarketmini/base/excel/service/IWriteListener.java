package com.stkj.supermarketmini.base.excel.service;

public interface IWriteListener {

    void onStartWrite();

    /**
     * @param e 异常
     */
    void onWriteError(Exception e);


    void onEndWrite();

}
