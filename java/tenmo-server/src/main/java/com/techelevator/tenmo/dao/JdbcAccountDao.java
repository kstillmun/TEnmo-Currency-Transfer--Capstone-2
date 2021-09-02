package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

@Component
public class JdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;
    @Autowired
    public UserDao userDao;

    public JdbcAccountDao(DataSource dataSource){
        this.jdbcTemplate= new JdbcTemplate(dataSource);
    }


    public BigDecimal getBalance(int userId){
        String sql= "SELECT balance FROM accounts WHERE user_id= ?; ";
        BigDecimal balance= jdbcTemplate.queryForObject(sql,BigDecimal.class,userId);
        return balance;
    }

    public void addToBalance( int userToId, BigDecimal amount){
        BigDecimal currentBalance= getBalance(userToId);

        String sql= "UPDATE accounts " +
                "SET balance= ? "+
                "WHERE user_id=?;";
        jdbcTemplate.update(sql,currentBalance.add(amount),userToId);
    }
    public void subtract( int userFromId, BigDecimal amount){
        BigDecimal currentBalance= getBalance(userFromId);

        String sql= "UPDATE accounts " +
                "SET balance= ? "+
                "WHERE user_id=?;";
        jdbcTemplate.update(sql,currentBalance.subtract(amount),userFromId);
    }

    public int getAccountId(int userId){
        String sql= "SELECT account_id FROM accounts WHERE user_id= ?;";
        int accountId=jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return accountId;
    };

    public int getUserId(int accountId){
        String sql= "SELECT user_id FROM accounts WHERE account_id= ?;";
        int userId=jdbcTemplate.queryForObject(sql, Integer.class, accountId);
        return userId;
    };


}
