package main;

import java.io.IOException;
import java.net.MalformedURLException;

import org.stellar.sdk.KeyPair;

import gui.SceneBuilderOrderBook;

public class Main {

	public static void main(String[] args) throws MalformedURLException, IOException {

		// get account data
		Account buyer = new Account(KeyPair.random());
		// the keypair are hardcoded so a new account isnt created every time the
		// software is run and the network doesnt get filled with dead accounts
		System.out.println("Buyer Account");
		buyer.printAccount();

		// create participant
		EtsParticipant trageiserStahlWerke = new EtsParticipant(buyer, 400);
		System.out.println("ETS participant Trageiser Stahlwerke");
		trageiserStahlWerke.printEtsParticipant();

		// get account data
		Account seller = new Account(
				KeyPair.random());
		System.out.println("Seller Account");
		seller.printAccount();

		// create participant
		EtsParticipant aubingerBrauerei = new EtsParticipant(seller, 600);
		System.out.println("ETS participant Aubiner Brauerei");
		aubingerBrauerei.printEtsParticipant();

		runSceneBuilderMain(buyer.getKeyPair(), seller.getKeyPair().getAccountId(), trageiserStahlWerke,
				aubingerBrauerei);

	}

	private static void runSceneBuilderMain(KeyPair buyerKeyPair, String sellerPublicKey, EtsParticipant etsBuyer,
			EtsParticipant etsSeller) {

		String[] args = {};
		SceneBuilderOrderBook.main(args, buyerKeyPair, sellerPublicKey, etsBuyer, etsSeller);

	}

}
