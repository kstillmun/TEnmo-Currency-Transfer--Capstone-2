package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {

    @Autowired //find me a candidate that I can use that can be assigned to userDao
    private JdbcUserDao userDao;
    @Autowired
    private JdbcAccountDao accountDao;
    @Autowired
    private JdbcTransferDao transferDao;


    //#3 see my account balance
    @RequestMapping (path="/account/balance", method= RequestMethod.GET)
    public BigDecimal get(Principal principal) {return accountDao.getBalance(
            userDao.findIdByUsername(principal.getName()));}

    @RequestMapping (path="/account/{id}",method= RequestMethod.GET)
    public int getAccountId(@PathVariable("id") int userId){
        return accountDao.getAccountId(userId);
    }

    //4.1 choose from list of users
    @RequestMapping(path="/users", method= RequestMethod.GET)
    public List<User> listofUsers(Principal principal){

        int userId= userDao.findIdByUsername(principal.getName());

        return userDao.findAll(userId);}

    //#4 Send Transfer
    @RequestMapping (path="/transfers", method=RequestMethod.POST)
    public void sendNewTransfer(@RequestBody Transfer transfer){
        //tech we are passing in user ids not account ids so convert (ignore naming)
        int accountFrom= transfer.getAccountFromId();
        int accountTo= transfer.getAccountToId();
        transferDao.createTransfer(accountFrom,accountTo,transfer.getAmount());
    }

    //#5 see all of my transfer transactions from and to
    @RequestMapping (path="/mytransfers", method=RequestMethod.GET)
    public List<Transfer> listTransfersByAccountId(Principal principal){
        return transferDao.getAllTransfersSentAndReceived(userDao.findIdByUsername(principal.getName()));
    }

    //#6 see all of my transfers by transfer id
    @RequestMapping (path="/transfers/{id}", method=RequestMethod.GET)
    public Transfer listTransfersByTransferId(@PathVariable("id") int transferId){
        return transferDao.getTransferByTransferId(transferId);
    }

    //find username by accountid
    @RequestMapping (path="/username/{id}",method=RequestMethod.GET)
    public String usernameByAccountId(@Valid @PathVariable("id") int accountId){
        return userDao.findUsernameById(accountId);
    }

    //create Request
    @RequestMapping (path="/requests", method=RequestMethod.POST)
    public void sendNewRequest(@RequestBody Transfer transfer){
        int accountFrom= transfer.getAccountFromId();
        int accountTo= transfer.getAccountToId();
        transferDao.createRequestTransfer(accountFrom,accountTo,transfer.getAmount());
    }

    //update Pending
    @RequestMapping (path="/mypendingtransfers", method=RequestMethod.PUT)
    public void updatePendingRequest(@RequestBody Transfer transfer){
        transferDao.updateRequestTransfer(transfer.getTransferId(),transfer.getTransferStatusId());
    }




}
