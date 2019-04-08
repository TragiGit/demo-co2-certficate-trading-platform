package main;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Abstract class for buy and sell orders.
 * 
 * @author Stefan Trageiser, Hochschule München, Matrikelnummer: 54260514
 *
 */
public abstract class Order {

	/**
	 * The uniquie ID of the buy order.
	 */
	private final UUID id;

	/**
	 * The amount of tCO2e desired by the buyer.
	 */
	private long amount;

	/**
	 * The price the buyer is willing to pay per tCO2e.
	 */
	private BigDecimal pricePerCo2E;

	/**
	 * The price multiplied by the amount.
	 */
	private BigDecimal total;

	/**
	 * Checks whether the transaction has been completed so it can be removed from
	 * the order book.
	 */
	private boolean isCompleted;

	/**
	 * Constructor for creating an empty order.
	 */
	Order() {

		id = UUID.randomUUID();
		pricePerCo2E = new BigDecimal(0);
		total = pricePerCo2E;
	}

	public Order(long amount, BigDecimal pricePerCo2E) throws IOException {

		id = UUID.randomUUID();

		if (amount > 0) {
			this.amount = amount;
		} else {
			throw new IOException("Amount must be positive");
		}

		if (pricePerCo2E != null && pricePerCo2E.compareTo(new BigDecimal(0)) == 1) { // price is a positive value
			this.pricePerCo2E = pricePerCo2E;
		} else {
			throw new IOException("Price must be a positive numeric value");
		}

		total = pricePerCo2E.multiply(new BigDecimal(amount));
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public BigDecimal getPricePerCo2E() {
		return pricePerCo2E;
	}

	public void setPricePerCo2E(BigDecimal pricePerCo2E) {
		this.pricePerCo2E = pricePerCo2E;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public boolean getIsCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public UUID getId() {
		return id;
	}

}
