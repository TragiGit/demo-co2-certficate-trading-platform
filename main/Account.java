package main;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Scanner;

import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.stellar.sdk.responses.SubmitTransactionTimeoutResponseException;
import org.stellar.sdk.responses.SubmitTransactionUnknownResponseException;

/**
 * Represents the account on the Stellar blockchain of a user. The account
 * consists of a keypair that holds the public and private key of the user.
 * 
 * @author Stefan Trageiser, Hochschule München, Matrikelnummer: 54260514
 *
 */
public class Account {

	/**
	 * The keypair of the user. Consists of a public key (account id) and a private
	 * key (secret).
	 */
	private KeyPair keyPair;

	/**
	 * Constructor for creating an account with no existing keypair. Calls the
	 * <b>createAccount()</b> method to register the newly created account on the
	 * blockchain.
	 */
	public Account() throws MalformedURLException, IOException {

		this.createAccount();

	}

	/**
	 * Constructor for creating an account with an existing keypair.
	 */
	public Account(KeyPair keyPair) {

		this.keyPair = keyPair;

	}

	public KeyPair getKeyPair() {
		return keyPair;
	}

	public void setKeyPair(KeyPair keyPair) {
		this.keyPair = keyPair;
	}

	/**
	 * Creates a new account and registers it on the stellar testnet.
	 */
	private void createAccount() throws MalformedURLException, IOException {

		// generate keys
		this.setKeyPair(KeyPair.random());

		// connect to testnet
		String body = "";
		String friendbotUrl = String.format("https://friendbot.stellar.org/?addr=%s", this.getKeyPair().getAccountId());
		InputStream response = new URL(friendbotUrl).openStream();
		try (Scanner scanner = new Scanner(response, "UTF-8")) {
			body = scanner.useDelimiter("\\A").next();
		}
		response.close();
		System.out.println("Account on Testnet successfully created. Details: " + body);

		// print out funds
		Server server = new Server("https://horizon-testnet.stellar.org");
		AccountResponse account = server.accounts().account(keyPair);
		System.out.println("Balances for account " + this.getKeyPair().getAccountId());
		for (AccountResponse.Balance balance : account.getBalances()) {
			System.out.println(String.format("Type: %s, Code: %s, Balance: %s", balance.getAssetType(),
					balance.getAssetCode(), balance.getBalance()));
		}

	}

	/**
	 * Sends the transaction to the Stellar testnet.
	 * 
	 * @param destination the destination account
	 * @param source      the source account
	 * @param message     an optional message, that can get appended to the
	 *                    transaction
	 * @param amount      the amount of
	 */
	public void sendTransaction(KeyPair destination, KeyPair source, String message) {

		if (message == null) {
			message = "";
		}

		Network.useTestNetwork();
		Server server = new Server("https://horizon-testnet.stellar.org");

		// check if destination exists
		try {
			server.accounts().account(destination);
		} catch (IOException e) {
			System.err.println("Account " + destination.getAccountId() + " does not exist");
			e.printStackTrace();
		}

		// update source account
		AccountResponse sourceAccount = updateAccount(source, server);
		// update destination account
		AccountResponse destinationAccount = updateAccount(destination, server);

		try {
			sourceAccount = server.accounts().account(source);
		} catch (IOException e) {
			System.err.println("Account " + source.getAccountId() + " does not exist");
			e.printStackTrace();
			return;
		}

		try {
			sourceAccount = server.accounts().account(source);
		} catch (IOException e) {
			System.err.println("Account " + source.getAccountId() + " does not exist");
			e.printStackTrace();
			return;
		}

		if (sourceAccount != null) {
			// build transaction
			Transaction transaction = new Transaction.Builder(sourceAccount)
					.addOperation(new PaymentOperation.Builder(destination, new AssetTypeNative(), "1").build())
					.addMemo(Memo.text(message)).build();
			System.out.println("Successfully built transaction");

			// sign transaction
			transaction.sign(source);
			System.out.println("Successfully signed transaction");

			// submit transaction
			try {
				SubmitTransactionResponse response = server.submitTransaction(transaction);
				System.out.println("Successfully sent transaction at " + Calendar.getInstance().getTime().toString());
				System.out.println("Transaction ID: " + response.getHash());
			} catch (IOException eIO) {
				System.err.println(eIO.getMessage());
				eIO.printStackTrace();
			} catch (SubmitTransactionTimeoutResponseException eTimeout) {
				System.err.println(
						"Connection timeout or timeout from horizon network when sending the transaction. Trying again");
				eTimeout.printStackTrace();
			} catch (SubmitTransactionUnknownResponseException eUnknownResponse) {
				System.err.println("Unknown response from Horizon network");
				eUnknownResponse.printStackTrace();
			}
		}

		System.out.println("Buyer funds: " + sourceAccount.getBalances()[0].getBalance());
		System.out.println("Seller funds: " + destinationAccount.getBalances()[0].getBalance());
	}

	private AccountResponse updateAccount(KeyPair keyPair, Server server) {

		AccountResponse accountResponse = null;

		try {
			accountResponse = server.accounts().account(keyPair);
		} catch (IOException e) {
			System.err.println("Account " + keyPair.getAccountId() + " does not exist");
			e.printStackTrace();
		}

		return accountResponse;

	}

	public void printAccount() {

		StringBuilder builder = new StringBuilder();

		builder.append("\n{\n");
		builder.append("Public Key (account id) : ").append(this.getKeyPair().getAccountId()).append(",\n");
		builder.append("Private Key (secret) : ").append(new String(this.getKeyPair().getSecretSeed())).append("\n");
		builder.append("}\n");

		System.out.println(builder.toString());
	}

}
