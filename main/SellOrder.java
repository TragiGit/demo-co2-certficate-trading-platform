package main;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Represents a sell order on the order book. It contains an ID, the seller´s
 * public key, the amount of EUAs sold and the price they´re asking.
 * 
 * @author Stefan Trageiser, Hochschule München, Matrikelnummer: 54260514
 *
 */
public class SellOrder extends Order {

	/**
	 * The seller´s public key, or account ID.
	 */
	private String sellerAccountId;

	public SellOrder() {

		super();
		this.sellerAccountId = "";
	}

	public SellOrder(String sellerAccountId, long amount, BigDecimal pricePerCo2E) throws IOException {

		super(amount, pricePerCo2E);

		if (sellerAccountId != null && !sellerAccountId.equals("")) {
			this.sellerAccountId = sellerAccountId;
		} else {
			throw new IOException("SellOrder: Invalid issuerId");
		}
	}

	public String getSellerAccountId() {
		return sellerAccountId;
	}

	public void setSellerAccountId(String sellerAccountId) {
		this.sellerAccountId = sellerAccountId;
	}

	public void printOrder() {

		final StringBuilder builder = new StringBuilder();

		builder.append("\n{\n");
		builder.append("SellOrderID : ").append(this.getId()).append(",\n");
		builder.append("SelIssuerAccountID : ").append(this.getSellerAccountId()).append(",\n");
		builder.append("AmountOffered : ").append(this.getAmount()).append(",\n");
		builder.append("PricePertCO2e : ").append(this.getPricePerCo2E()).append(",\n");
		builder.append("Total : ").append(this.getTotal()).append(",\n");
		builder.append("Completed : ").append(this.getIsCompleted()).append("\n");
		builder.append("}\n");

		System.out.println(builder.toString());
	}
}
