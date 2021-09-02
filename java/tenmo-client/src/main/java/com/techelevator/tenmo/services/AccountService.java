package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;

public class AccountService {

    public static String AUTH_TOKEN="";
    private final String API_BASE_URL;
    public RestTemplate restTemplate= new RestTemplate();

    public AccountService(String url) {
        API_BASE_URL =url;}

    // we used big decimal but should it be account?
    public BigDecimal getBalance(String token) {
        BigDecimal balance= null;
        try {
            balance = restTemplate.exchange(API_BASE_URL + "/account/balance", HttpMethod.GET, makeEntity(token), BigDecimal.class).getBody();
        }catch (RestClientResponseException e){
            System.out.println("ERROR:"+ e.getRawStatusCode());
        }
        return balance;
    }

    public int getAccountId(int userId, String token){
        int accountId=0;
        try {
            accountId = restTemplate.exchange(API_BASE_URL + "account/" + userId, HttpMethod.GET, makeEntity(token), Integer.class).getBody();
        }catch(RestClientResponseException e){
            System.out.println("ERROR:"+ e.getRawStatusCode());
        }
        return accountId;
    }


    public User[] listUsers(String token){
        User[] users= null;
        try {
            users = restTemplate.exchange(API_BASE_URL + "users", HttpMethod.GET, makeEntity(token), User[].class).getBody();
        }catch (RestClientResponseException e){
            System.out.println("ERROR:"+ e.getRawStatusCode());
        }
        return users;
    }

    public String getUserName(int accountId,String token){
        String username= "";
        try {
            username = restTemplate.exchange(API_BASE_URL + "username/" + accountId, HttpMethod.GET, makeEntity(token), String.class).getBody();
        }catch (RestClientResponseException e){
            System.out.println("ERROR:"+ e.getRawStatusCode());
        }
        return username;
    }

    public Transfer sendTransfer(Transfer transfer,String token)  {
        try {
            restTemplate.exchange(API_BASE_URL + "transfers", HttpMethod.POST, makeTransferEntity(transfer, token), Transfer.class);
        } catch (RestClientResponseException e){
            System.out.println("ERROR:"+ e.getRawStatusCode()+": You have likely provided an invalid amount. Please try again.");
        }
        return transfer;
    }

    public Transfer sendRequest(Transfer transfer,String token){
        try {
            restTemplate.exchange(API_BASE_URL + "/requests", HttpMethod.POST, makeTransferEntity(transfer, token), Transfer.class);
        } catch (RestClientResponseException e){
            System.out.println("ERROR:"+ e.getRawStatusCode()+": You have likely provided an invalid amount. Please try again.");
        }
        return transfer;
    }

    public Transfer updateRequest(Transfer transfer,String token){
        try {
            restTemplate.exchange(API_BASE_URL + "/mypendingtransfers", HttpMethod.PUT, makeTransferEntity(transfer, token), Transfer.class);
        } catch(RestClientResponseException e){
            System.out.println("ERROR:"+ e.getRawStatusCode()+": You have likely provided an invalid input. Please try again.");
        }
        return transfer;
    }

    public Transfer [] listTransfers (String token) {
        Transfer[] transferList = null;
        try {
            transferList = restTemplate.exchange(API_BASE_URL + "mytransfers", HttpMethod.GET, makeEntity(token), Transfer[].class).getBody();
        } catch (RestClientResponseException e){
            System.out.println("ERROR:"+ e.getRawStatusCode());
        }
        return  transferList;
    }

    public Transfer getTransferInfo (int transferId,String token) {
        Transfer transfer =null;
        try {
            transfer = restTemplate.exchange(API_BASE_URL + "transfers/" + transferId, HttpMethod.GET, makeEntity(token), Transfer.class).getBody();
        } catch(RestClientResponseException e){
            System.out.println("ERROR:"+ e.getRawStatusCode());
        }
        return transfer;
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer, String token) { //create is for this
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer,headers);
        return entity;
    }

    private HttpEntity makeEntity(String token) { //for get requests
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }

}
