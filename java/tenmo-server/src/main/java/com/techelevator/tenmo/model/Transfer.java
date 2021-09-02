package com.techelevator.tenmo.model;

import javax.validation.constraints.Min;
import java.math.BigDecimal;

public class Transfer {

    private int transferId;
    private int transferTypeId;
    private int transferStatusId;
    private int accountFromId;
    private int accountToId;
    @Min( value = 1, message = "The minimum transfer amount must be $1 or greater.")
    private BigDecimal amount;
    private String transferStatus;
    private String transferType;

    public Transfer(int transferId, int transferTypeId, int transferStatusId, int accountFromId, int accountToId, BigDecimal amount, String transferStatus, String transferType) {
        this.transferId = transferId;
        this.transferTypeId = transferTypeId;
        this.transferStatusId = transferStatusId;
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.amount = amount;
        this.transferStatus = transferStatus;
        this.transferType = transferType;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(int transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public int getAccountFromId() {
        return accountFromId;
    }

    public void setAccountFromId(int accountFromId) {
        this.accountFromId = accountFromId;
    }

    public int getAccountToId() {
        return accountToId;
    }

    public void setAccountToId(int accountToId) {
        this.accountToId = accountToId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    //do we need to do a join the jdbc transfer to get the name of the person or the other fields corresponding to other tables?

    @Override
    public String toString() {
        return "--------------------------------------------\n" +
                "Transfer Details\n" +
                "--------------------------------------------\n" +
                "Id: " + transferId +"\n"+
                "From: " + accountFromId +"\n"+
                "To: " + accountToId +"\n"+
                "Amount: " + amount +"\n"+
                "Type:" + transferTypeId +"\n"+
                "Status:" + transferStatusId +"\n";
    }
}
