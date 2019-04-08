package main;

import java.io.IOException;
import java.math.BigDecimal;

import org.stellar.sdk.KeyPair;

/**
 * Represents a buy order on the order book. It contains an ID, the buyer´s key
 * pair, the amount asked and the price they´re willing to pay.
 * 
 * @author Stefan Trageiser, Hochschule München, Matrikelnummer: 54260514
 *
 */
public class BuyOrder extends Order {

	/**
	 * The buyer´s account´s keypair containing his public (account id) and private
	 * (secret) key.
	 */
	private KeyPair buyerKeyPair;

	/**
	 * An optional message for a transaction.
	 */

	private String message = "";

	/**
	 * Constructor for empty buyOrder.
	 */
	public BuyOrder() {

		super();
		this.buyerKeyPair = KeyPair.random(); // creates dummy keys
		this.message = "";
	}

	/**
	 * Creates a new buy order. The id is a randomised UUID.
	 * 
	 * @param buyerKeyPair The buyer´s key pair.
	 * @param amount       The amount of certificates the user wants to buy.
	 * @param pricePerCo2E The price per tCO2e the user is willing to pay.
	 * @param message      An optional message for the transaction.
	 * @throws IOException thrown when any argument has an illegal value.
	 */
	public BuyOrder(KeyPair buyerKeyPair, long amount, BigDecimal pricePerCo2E, String message) throws IOException {

		super(amount, pricePerCo2E);

		if (buyerKeyPair != null && buyerKeyPair.getAccountId() != null && buyerKeyPair.getSecretSeed() != null) {
			this.buyerKeyPair = buyerKeyPair;
		} else {
			throw new IOException("BuyOrder: Invalid keypair");
		}

		if (message != null) {
			this.message = message;
		} else {
			throw new IOException("Message field must not be null");
		}

	}

	public KeyPair getBuyerKeyPair() {
		return buyerKeyPair;
	}

	public void setBuyerKeyPair(KeyPair buyerKeyPair) {
		this.buyerKeyPair = buyerKeyPair;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Prints all data from the buy order on the console in JSON format.
	 */
	public void printOrder() {

		final StringBuilder builder = new StringBuilder();

		builder.append("\n{\n");
		builder.append("BuyOrderID : ").append(this.getId()).append(",\n");
		builder.append("BuyerKeyPair : ").append(this.getBuyerKeyPair()).append(",\n");
		builder.append("AmountWanted : ").append(this.getAmount()).append(",\n");
		builder.append("PricePertCO2e : ").append(this.getPricePerCo2E()).append(",\n");
		builder.append("Total : ").append(this.getTotal()).append(",\n");
		builder.append("Completed : ").append(this.getIsCompleted()).append("\n");
		builder.append("}\n");

		System.out.println(builder.toString());
	}

}
