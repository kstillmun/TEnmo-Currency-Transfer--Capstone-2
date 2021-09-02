package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    public void createTransfer(int userFromId,int userToId, BigDecimal amount);
    // transfer includes the User IDs of the from and to users and the amount
    // Transfer obj only has the account ids, so a join will be necessary

    //As an authenticated user of the system, I need to be able to see transfers I have sent or received.
    public List<Transfer> getAllTransfersSentAndReceived(int userId);

    //As an authenticated user of the system, I need to be able to retrieve the details of any transfer based upon the transfer ID.
    public Transfer getTransferByTransferId(int transferId);

    //Request and Approve

    public void createRequestTransfer(int accountFrom,int accountTo, BigDecimal amount);

    public void updateRequestTransfer(int transferId, int transferStatusId);

    //no delete



}
