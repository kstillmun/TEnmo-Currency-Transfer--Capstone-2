package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.view.ConsoleService;
import org.springframework.web.client.*;

import java.math.BigDecimal;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private AccountService account= new AccountService(API_BASE_URL);

    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService) {
		this.console = console;
		this.authenticationService = authenticationService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
			while (true) {
				String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
				if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
					viewCurrentBalance();
				} else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
					viewTransferHistory();
				} else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
					viewPendingRequests();
				} else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
					sendBucks();
				} else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
					requestBucks();
				} else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
					login();
				} else {
					// the only other option on the main menu is to exit
					exitProgram();
				}
			}
	}


	private void viewCurrentBalance() {
		System.out.println("Your current account balance is: "+ account.getBalance(currentUser.getToken()));
	}

	private void viewTransferHistory() {
		printTransfers(account.listTransfers(currentUser.getToken()));
		int transferId = console.getUserInputInteger("Please enter transfer ID to view details (0 to cancel)");

		if (transferId == 0) {
			mainMenu();
		} else {
			try{
			Transfer unique = account.getTransferInfo(transferId, currentUser.getToken());

			System.out.println("--------------------------------------------------");
			System.out.println("Transfer Details");
			System.out.println("--------------------------------------------------");
			System.out.println("Id:" + unique.getTransferId());
			System.out.println("From:" + account.getUserName(unique.getAccountFromId(),currentUser.getToken()));
			System.out.println("To:" + account.getUserName(unique.getAccountToId(),currentUser.getToken()));
			System.out.println("Type:" + unique.getTransferType());
			System.out.println("Status:" + unique.getTransferStatus());
			System.out.println("Amount: $" + unique.getAmount());
			System.out.println("--------------------------------------------------");
			} catch (NullPointerException e){
				System.out.println("--------------------------------------------------");
				System.out.println("You've entered an invalid transferId. Please try again");
				System.out.println("--------------------------------------------------");
			}
		}
	}

	private void viewPendingRequests() {
		printPendingTransfersOnly(account.listTransfers(currentUser.getToken()));

		int transferId=console.getUserInputInteger("Please enter transfer ID to approve/reject (0 to cancel)");

		if (transferId==0){
			mainMenu();
		} else if (transferId>3000 && transferId<4000) {

			System.out.println("-------------------------------------------\n");
			System.out.println("1: Approve");
			System.out.println("2: Reject");
			System.out.println("0: Don't Approve or Reject");
			System.out.println("-------------------------------------------\n");
			int transferStatusId = console.getUserInputInteger("Please choose an option:\n");
			System.out.println("-------------------------------------------");

			if (transferStatusId == 0) {
				mainMenu();
			} else if (transferStatusId != 1 && transferStatusId != 2) {
				System.out.println("--------------------------------------------------");
				System.out.println("You have selected an invalid option. Please try again.\n");
				System.out.println("--------------------------------------------------\n");
			} else if (transferStatusId == 1) {
				Transfer approveTransfer = new Transfer();
				approveTransfer.setTransferStatusId(2);
				approveTransfer.setTransferId(transferId);
				account.updateRequest(approveTransfer,currentUser.getToken());
			} else if (transferStatusId == 2) {
				Transfer rejectTransfer = new Transfer();
				rejectTransfer.setTransferStatusId(3);
				rejectTransfer.setTransferId(transferId);
				account.updateRequest(rejectTransfer,currentUser.getToken());
			}
		} else {
			System.out.println("--------------------------------------------------");
			System.out.println("You have selected an invalid transfer Id. Please try again.\n");
			System.out.println("--------------------------------------------------\n");
		}
	}

	private void sendBucks(){
		printUsers(account.listUsers(currentUser.getToken()));
		int userToId=console.getUserInputInteger("Enter ID of user you are sending to (0 to cancel)");
		if (userToId==0){
			mainMenu();}

		int currentUserId= currentUser.getUser().getId();
		int accountTo= account.getAccountId(userToId,currentUser.getToken());
		int accountFrom= account.getAccountId(currentUserId,currentUser.getToken());

		 if (userIdExists(account.listUsers(currentUser.getToken()), userToId,currentUserId)) {

			int inputAmount = console.getUserInputInteger("Enter amount");
			BigDecimal amount = BigDecimal.valueOf(inputAmount);
			BigDecimal balance=account.getBalance(currentUser.getToken());

			 if (balance.compareTo(amount) < 0 || amount.compareTo(BigDecimal.ZERO)<0) {
				 System.out.println("--------------------------------------------------");
				 System.out.print("Declined. The amount you entered is not valid.\n");
				 System.out.println("--------------------------------------------------\n");
			 } else {

			 	System.out.println("--------------------------------------------------");
				 System.out.print("Approved. Feel free to check your balance or transfers history if you desire.\n");
				 System.out.println("--------------------------------------------------\n");

				 Transfer transfer = new Transfer();
				 transfer.setAccountFromId(accountFrom);
				 transfer.setAccountToId(accountTo);
				 transfer.setAmount(amount);
				 account.sendTransfer(transfer,currentUser.getToken());
			 }

		} else {
			 System.out.println("--------------------------------------------------");
			 System.out.println("We cannot execute your request as you have entered an invalid id. Please try again.");
			 System.out.println("--------------------------------------------------\n");
		}


	}

	private void requestBucks() {
		printUsers(account.listUsers(currentUser.getToken()));

		//careful we are swapping!
		int userFromId=console.getUserInputInteger("Enter ID of user you are requesting from (0 to cancel)");
		if (userFromId==0){
			mainMenu();}

		int userToId= currentUser.getUser().getId();
		int accountFrom= account.getAccountId(userFromId,currentUser.getToken());
		int accountTo= account.getAccountId(userToId,currentUser.getToken());

		if (userIdExists(account.listUsers(currentUser.getToken()), userFromId,userToId)){ //careful here we are swapping stuff
			int inputAmount = console.getUserInputInteger("Enter amount");
			BigDecimal amount = BigDecimal.valueOf(inputAmount);

			if (amount.compareTo(BigDecimal.ZERO)<0) {
				System.out.println("--------------------------------------------------");
				System.out.print("Declined. The amount you entered is not valid.\n");
				System.out.println("--------------------------------------------------\n");
			} else {

				Transfer transfer = new Transfer();
				transfer.setAccountFromId(accountFrom);
				transfer.setAccountToId(accountTo);
				transfer.setAmount(amount);
				account.sendRequest(transfer, currentUser.getToken());

				System.out.println("--------------------------------------------------------------------------------");
				System.out.println("The request for funds was processed. If approved, the transaction will complete.\nPlease check your pending history for whether the request was approved or rejected.");
				System.out.println("--------------------------------------------------------------------------------\n");
			}
		} else {
			System.out.println("--------------------------------------------------");
			System.out.println("You have likely entered an invalid userId. Please try again. ");
			System.out.println("--------------------------------------------------\n");

		}
		
	}

	public void printUsers(User[] users) {
			System.out.println("--------------------------------------------");
			System.out.println("Users");
			System.out.println("ID          Name");
			System.out.println("--------------------------------------------");
			for (User user : users) {
				System.out.println(user.toString());
			}
			System.out.println("--------------------------------------------");
			System.out.println("\n");
	}

	public boolean userIdExists(User[] users,int userId, int currentUserId){

    	boolean userExists=false;
    	int count=0;
		for (User user : users) {
			if (user.getId()==userId && userId!=currentUserId){
				count+=1;
			}
		}

		if (count>0){
			userExists= true;
		}
    	return userExists;
	}

	public void printTransfers(Transfer[] transfers) {
		System.out.println("--------------------------------------------------");
		System.out.println("Transfers");
		System.out.printf("%-15s %-25s %-10s","ID","From/To:","Amount");
		System.out.println("\n--------------------------------------------------");
		for (Transfer transfer : transfers) {

			if (account.getAccountId(currentUser.getUser().getId(),currentUser.getToken()) == transfer.getAccountFromId()) {
				System.out.printf("%-15s %-25s %-10s \n",transfer.getTransferId(),"To:" + account.getUserName(transfer.getAccountToId(),currentUser.getToken()),"$" + transfer.getAmount());
			} else if (account.getAccountId(currentUser.getUser().getId(),currentUser.getToken()) == transfer.getAccountToId()) {
				System.out.printf("%-15s %-25s %-10s \n",transfer.getTransferId(),"From:" + account.getUserName(transfer.getAccountFromId(),currentUser.getToken()),"$" + transfer.getAmount());
			}
		}
		System.out.println("\n--------------------------------------------------");
		System.out.println("All attempts (including rejected) are shown.");
	}

	public void printPendingTransfersOnly(Transfer[] transfers) {
			System.out.println("--------------------------------------------------");
			System.out.println("Transfers");
			System.out.printf("%-15s %-25s %-10s", "ID", "From/To:", "Amount");
			System.out.println("\n--------------------------------------------------");

			for (Transfer transfer : transfers) {
				if (account.getAccountId(currentUser.getUser().getId(), currentUser.getToken()) == transfer.getAccountFromId() && transfer.getTransferStatusId() == 1) {
					System.out.printf("%-15s %-25s %-10s", transfer.getTransferId(), "To:" + account.getUserName(transfer.getAccountToId(), currentUser.getToken()), "$" + transfer.getAmount());
				}
			}
			System.out.println("\n--------------------------------------------------");

	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
		    } catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
}
