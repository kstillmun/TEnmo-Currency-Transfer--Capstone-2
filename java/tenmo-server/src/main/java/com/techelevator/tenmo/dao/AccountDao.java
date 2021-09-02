package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {

    //don't need to create account this is done in the jdbcuserdao

//    List<Account> list(); //not sure we need this.

    BigDecimal getBalance(int userId); //may need to throw an exception

    void addToBalance( int accountTo, BigDecimal amount);

    void subtract( int accountFrom, BigDecimal amount);

    int getAccountId(int userId);

    int getUserId(int accountId);

    //delete not necessary?

}
