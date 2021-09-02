package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;


public class JdbcTransferDaoTest extends TenmoDaoTests {

    private static final Transfer TRANSFER_1= new Transfer(1,2,2,2900,2901,new BigDecimal(100),"Approved","Send");
    private static final Transfer TRANSFER_2= new Transfer(2,2,2,2901,2902,new BigDecimal(50),"Approved","Send");
    private static final Transfer TRANSFER_3= new Transfer(3,2,3,2903,2900,new BigDecimal(9999),"Rejected","Send");

    private Transfer testTransfer;

    private JdbcTransferDao sut;

    @Before
    public void setup(){
        sut= new JdbcTransferDao(dataSource);
        testTransfer= new Transfer(0, 2, 2, 2800, 2801, new BigDecimal(200),"Approved", "Send");
    }

    @Test
    public void getTransfer_returns_correct_Transfer(){

    }


}