package main;

import java.math.BigDecimal;

import javax.swing.JOptionPane;

/**
 * Contains some methods to show information and ask for input from the user.
 * 
 * @author Stefan Trageiser, Hochschule München, Matrikelnummer: 54260514
 *
 */
public class GuiDialogues {

	/**
	 * Shows a message for the user with some info about the software.
	 */
	public static void showEntryDialogue() {

		JOptionPane.showMessageDialog(null,
				"Mit dieser Software-Applikation kann ein blockchain-basiertes Zertifikatshandelssystem durchgespielt werden. "
						+ "Zeritfikate können ge- und verkauft werden. Dafür muss ein jeweils ein Kauf- und Verkaufsangebot mit Preis und Menge erstellt werden.",
				"Informationen", JOptionPane.OK_OPTION);

	}

	/**
	 * Asks the user for the amount of certificates he wants to buy.
	 * 
	 * @return amount of certificates to be bought
	 */
	public static long showBuyAmountDialogue() {

		String buyAmount = JOptionPane.showInputDialog(null,
				"Bitte geben Sie die Anzahl an zu erwebenden Zertifikaten an.", "Menge angeben", JOptionPane.OK_OPTION);

		if (buyAmount.matches("[0-9]+")) {
			return Long.parseLong(buyAmount);
		} else {
			JOptionPane.showMessageDialog(null, "Fehler. Eingabe darf nur aus ganzen Zahlen bestehen.", "Fehler",
					JOptionPane.OK_OPTION);
			showBuyAmountDialogue();
		}

		return 0;
	}

	/**
	 * Asks the user for the price he will pay for a certificate.
	 * 
	 * @return the price for a certificate
	 */
	public static BigDecimal showBuyPriceDialogue() {

		String buyPrice = JOptionPane.showInputDialog(null, "Bitte geben Sie den Preis pro EUA an.", "Preis angeben",
				JOptionPane.OK_OPTION);

		if (buyPrice.matches("^\\d+(\\.\\d+)*$")) {
			return new BigDecimal(buyPrice);

		} else {
			JOptionPane.showMessageDialog(null,
					"Fehler. Eingabe darf nur aus Ziffern und Trennsymbolen (Punkt, Komma) bestehen.", "Fehler",
					JOptionPane.OK_OPTION);
			showBuyPriceDialogue();
		}

		return new BigDecimal(0.0);
	}

	/**
	 * Asks the user for an optional message that can get appended to the
	 * transaction.
	 * 
	 * @return the message
	 */
	public static String showBuyMessageDialogue() {

		String buyMessage = JOptionPane.showInputDialog(null,
				"Bitte geben Sie eine optionale Nachricht für Überweisung an. Wenn sie keine Nachricht angeben wollen, lassen Sie das Feld leer.",
				"Nachricht angeben", JOptionPane.OK_OPTION);

		if (buyMessage == null) {
			buyMessage = "";
		}
		return buyMessage;
	}

}
