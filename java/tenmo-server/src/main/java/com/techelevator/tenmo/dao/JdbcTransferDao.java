package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AccountDao accountDao;

    public JdbcTransferDao(DataSource dataSource){
        this.jdbcTemplate= new JdbcTemplate(dataSource);
    }

    public void createTransfer(int accountFrom,int accountTo, BigDecimal amount){
        int userFromId= accountDao.getUserId(accountFrom);
        int userToId= accountDao.getUserId(accountTo);
        BigDecimal balance= accountDao.getBalance(userFromId);
        if (balance.compareTo(amount)<0){
            String sql= "INSERT INTO transfers (transfer_type_id,transfer_status_id,account_from,account_to,amount) "+
                    "VALUES (?,?,?,?,?);";
           jdbcTemplate.update(sql,2,3,accountFrom,accountTo,amount);
            System.out.println("You do not have enough funds to send the desired amount.");
        } else {
            accountDao.subtract(userFromId, amount);
            accountDao.addToBalance(userToId, amount);
            String sql= "INSERT INTO transfers (transfer_type_id,transfer_status_id,account_from,account_to,amount) "+
                    "VALUES (?,?,?,?,?) ;";
            jdbcTemplate.update(sql,2,2,accountFrom,accountTo,amount);

        }

    }

    public void createRequestTransfer(int accountFrom,int accountTo, BigDecimal amount){
        String sql= "INSERT INTO transfers (transfer_type_id,transfer_status_id,account_from,account_to,amount) "+
                "VALUES (?,?,?,?,?);";
        jdbcTemplate.update(sql,1,1,accountFrom,accountTo,amount);

    }

    public void updateRequestTransfer(int transferId, int transferStatusId){
        String af= "SELECT account_from FROM transfers WHERE transfer_id=?;";
        Integer accountFrom= jdbcTemplate.queryForObject(af,Integer.class,transferId);
        String at= "SELECT account_to FROM transfers WHERE transfer_id=?;";
        Integer accountTo= jdbcTemplate.queryForObject(at,Integer.class,transferId);
        String am= "SELECT amount FROM transfers WHERE transfer_id=?;";
        BigDecimal amount= jdbcTemplate.queryForObject(am,BigDecimal.class,transferId);

        int userFromId= accountDao.getUserId(accountFrom);
        int userToId= accountDao.getUserId(accountTo);


        if (transferStatusId==3){
        String sql= "UPDATE transfers " +
                "SET transfer_status_id= ? WHERE transfer_id= ?";
        jdbcTemplate.update(sql,3,transferId);
        } else if(transferStatusId==2){
            BigDecimal balance= accountDao.getBalance(userFromId);
            if (balance.compareTo(amount)<0){
                String sql= "UPDATE transfers " +
                "SET transfer_status_id= ? WHERE transfer_id= ?";
                jdbcTemplate.update(sql,3,transferId);
                System.out.println("You do not have enough funds to send the desired amount.");
            } else {
                accountDao.subtract(userFromId, amount);
                accountDao.addToBalance(userToId, amount);
                String sql= "UPDATE transfers "+
                        "SET transfer_status_id= ? WHERE transfer_id=? ;";
                jdbcTemplate.update(sql,2,transferId);

            }

        }

    }


    public List<Transfer> getAllTransfersSentAndReceived(int userId){
        String accountIdSql= "SELECT account_id FROM accounts WHERE user_id=?;";
        Integer accountId= jdbcTemplate.queryForObject(accountIdSql,Integer.class,userId);

        List<Transfer> myTransfers= new ArrayList<>();
        String sql= "SELECT T.transfer_id,T.transfer_type_id,T.transfer_status_id,T.account_from,T.account_to,T.amount, D.transfer_type_desc, S.transfer_status_desc " +
                "FROM transfers T " +
                "JOIN transfer_types D ON T.transfer_type_id=D.transfer_type_id " +
                "JOIN transfer_statuses S ON T.transfer_status_id= S.transfer_status_id WHERE account_from = ? OR account_to = ? ORDER BY T.transfer_id; ";
        SqlRowSet results= jdbcTemplate.queryForRowSet(sql,accountId,accountId);
        while (results.next()){
            myTransfers.add(mapRowtoTransfer(results));
        }
        return myTransfers;
    }

    public Transfer getTransferByTransferId(int transferId){
        Transfer transfer= null;
        String sql= "SELECT T.transfer_id,T.transfer_type_id,T.transfer_status_id,T.account_from,T.account_to,T.amount, D.transfer_type_desc, S.transfer_status_desc " +
                "FROM transfers T " +
                "JOIN transfer_types D ON T.transfer_type_id=D.transfer_type_id " +
                "JOIN transfer_statuses S ON T.transfer_status_id= S.transfer_status_id WHERE transfer_id= ?; ";
        SqlRowSet result= jdbcTemplate.queryForRowSet(sql,transferId);
        if (result.next()){
            transfer= mapRowtoTransfer(result);
        }
        return transfer;
    }


    private Transfer mapRowtoTransfer(SqlRowSet results){
        int transferId = results.getInt("transfer_id");
        int transferTypeId = results.getInt("transfer_type_id");
        int transferStatusId = results.getInt("transfer_status_id");
        int accountFrom = results.getInt("account_from");
        int accountTo = results.getInt("account_to");
        BigDecimal amount= results.getBigDecimal("amount");
        String transferStatus= results.getString("transfer_status_desc");
        String transferType= results.getString("transfer_type_desc");
        Transfer theTransfer= new Transfer(transferId,transferTypeId,transferStatusId,accountFrom,accountTo,amount,transferStatus,transferType);
        return theTransfer;
    }



}
