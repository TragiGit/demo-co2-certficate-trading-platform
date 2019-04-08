package main;

/**
 * Represents an ETS participant that wants to buy EUAs. He has an account on
 * the blockchain and an amount of certificates he currently ownes.
 * 
 * @author Stefan Trageiser, Hochschule München, Matrikelnummer: 54260514
 *
 */
public class EtsParticipant {

	/**
	 * The participant´s account on the stellar testnet consiting of a keypair.
	 */
	private Account account;
	/**
	 * The number of certificates the participant currently posseses.
	 */
	private long certificates;

	public EtsParticipant(Account account, long certificates) {

		this.account = account;
		this.certificates = certificates;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public long getCertificates() {
		return certificates;
	}

	public void setCertificates(long certificates) {
		this.certificates = certificates;
	}

	/**
	 * Prints all the data from the ets participant on the console in JSON format.
	 */
	public void printEtsParticipant() {

		StringBuilder builder = new StringBuilder();

		builder.append("\n{\n");
		builder.append("Public key: " + account.getKeyPair().getAccountId()).append(",\n");
		builder.append("Certificates: " + this.getCertificates()).append("\n");
		builder.append("}\n");

		System.out.println(builder.toString());
	}

}
