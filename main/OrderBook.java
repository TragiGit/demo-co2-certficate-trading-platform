package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.stellar.sdk.KeyPair;

/**
 * Contains functionality of an order book including adding and removing buy and
 * sale orders and going through orders to find matches.
 * 
 * @author Stefan Trageiser, Hochschule München, Matrikelnummer: 54260514
 *
 */
public class OrderBook {

	/**
	 * The list of sell orders. Each time a new buy order is added or removed, the
	 * list gets updated.
	 */
	private List<SellOrder> sellOrders;
	/**
	 * The list of buy orders. Each time a new buy order is added or removed, the
	 * list gets updated.
	 */
	private List<BuyOrder> buyOrders;

	public OrderBook() {

		sellOrders = new ArrayList<SellOrder>();
		buyOrders = new ArrayList<BuyOrder>();
	}

	public List<SellOrder> getSellOrders() {
		return sellOrders;
	}

	public void setSellOrders(List<SellOrder> sellOrders) {
		this.sellOrders = sellOrders;
	}

	public List<BuyOrder> getBuyOrders() {
		return buyOrders;
	}

	public void setBuyOrders(List<BuyOrder> buyOrders) {
		this.buyOrders = buyOrders;
	}

	public void addBuyOrder(BuyOrder buyOrder) {

		buyOrders.add(buyOrder);
	}

	public void addSellOrder(SellOrder sellOrder) {

		sellOrders.add(sellOrder);
	}

	public void removeBuyOrder(BuyOrder buyOrder) throws IOException {

		if (!buyOrders.isEmpty() && buyOrders.contains(buyOrder)) {
			buyOrders.remove(buyOrder);
		} else {
			throw new IOException(
					"Could not remove buy order from list. Either the list is empty or the buy order is not in the list.");
		}
	}

	public void removeSellOrder(SellOrder sellOrder) throws IOException {

		if (!sellOrders.isEmpty() && sellOrders.contains(sellOrder)) {
			sellOrders.remove(sellOrder);
		} else {
			throw new IOException(
					"Could not remove sell order from list. Either the list is empty or the sell order is not in the list.");
		}
	}

	/**
	 * * Checks all sell orders if the offered price is smaller or equal to the
	 * buying price. If a match occurs, the order sell order will be purchased and
	 * either the amount of EUAs offered or bought is adjusted accordingly. After
	 * that the sell order is bought and a transaction is send to the stellar
	 * network.
	 * 
	 * @param buyOrder  the buy order, used for price and amount
	 * @param etsBuyer  the ets participant, used for number of owned EUAs
	 * @param etsSeller same as etsBuyer
	 * @throws IOException when the sending of the transaction fails
	 */
	public void checkSellOrders(BuyOrder buyOrder, EtsParticipant etsBuyer, EtsParticipant etsSeller)
			throws IOException {

		long buyAmount = buyOrder.getAmount();
		long amountRemaining = buyAmount;
		Account buyerAccount = new Account(buyOrder.getBuyerKeyPair());

		if (!buyOrder.getIsCompleted()) {

			for (SellOrder sellOrder : sellOrders) {

				System.out.println("Checking all sell orders");
				if (!sellOrder.getIsCompleted()) {
					if (sellOrder.getAmount() <= etsSeller.getCertificates()) {

						System.out.println("Found matching sell order");
						long sellAmount = sellOrder.getAmount();

						// check for price
						if (buyOrder.getPricePerCo2E().compareTo(sellOrder.getPricePerCo2E()) < 1) { // price is less
																										// or equal
							if (amountRemaining < sellAmount) {
								sellAmount -= amountRemaining;
								sellOrder.setAmount(sellAmount);
								sellOrder.setCompleted(true);

							} else if (amountRemaining > sellAmount) {
								amountRemaining -= sellAmount;
								buyAmount = sellAmount;
								sellOrder.setCompleted(true);

							} else {
								amountRemaining = 0;
								buyOrder.setCompleted(true);
								sellOrder.setCompleted(true);
							}

							final long startTime = System.nanoTime();
							// send transaction
							buyerAccount.sendTransaction(KeyPair.fromAccountId(sellOrder.getSellerAccountId()),
									buyerAccount.getKeyPair(), buyOrder.getMessage());
							final long endTime = System.nanoTime();

							System.out.println("Elapsed time: " + (endTime - startTime) / 1000000 + " ms");

							// add EUAs to buyer
							etsBuyer.setCertificates(etsBuyer.getCertificates() + buyAmount);
							// deduct EUAs from seller
							etsSeller.setCertificates(etsSeller.getCertificates() - buyAmount);

						} else {
							System.out.println("Buy order wasnt completed because the selling price of "
									+ sellOrder.getPricePerCo2E() + " is higher than the buying price of "
									+ buyOrder.getPricePerCo2E());
						}

					} else {
						System.err.println("Seller does not own the required amount of certificates.");
					}

				} else {
					System.err.println("Sell order already completed, checking next");
				}
			}

		} else {
			System.err.println("Buy order already completed, checking next");
		}
	}

	/***
	 * Searches for buy or sell orders by ID,
	 * 
	 * @param id the ID of the buy or sell order.
	 * @return the buy or sell order, if the ID matches. If no order was found,
	 *         <b>null</b> is returned.
	 */
	public Order getOrderById(UUID id) {

		// check buy orders
		for (Order order : buyOrders) {
			if (order.getId().equals(id)) {
				return order;
			}
		}

		// check sell orders
		for (Order order : sellOrders) {
			if (order.getId().equals(id)) {
				return order;
			}
		}

		return null;
	}

	/**
	 * Prints all data from the order book on the console in JSON format.
	 */
	public void printOrderBook() {

		System.out.println("Buy Orders:");

		for (BuyOrder buyOrder : buyOrders) {
			buyOrder.printOrder();
		}

		System.out.println("Sell Orders");

		for (SellOrder sellOrder : sellOrders) {
			sellOrder.printOrder();
		}
	}
}
