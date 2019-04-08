package gui;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.stellar.sdk.KeyPair;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import main.BuyOrder;
import main.EtsParticipant;
import main.Order;
import main.OrderBook;
import main.SellOrder;

public class SceneBuilderOrderBook extends Application {

	private static KeyPair buyerKeyPair;
	private static String sellerPublickey;

	private static EtsParticipant etsbuyer;
	private static EtsParticipant etsseller;

	private static List<BuyOrder> buyOrders;
	private static List<SellOrder> sellOrders;
	private List<ShortenedOrder> shortenedBuyOrders;
	private List<ShortenedOrder> shortenedSellOrders;

	private TableView<ShortenedOrder> buyTable = new TableView<ShortenedOrder>();
	private TableView<ShortenedOrder> sellTable = new TableView<ShortenedOrder>();

	private final ObservableList<ShortenedOrder> buyData = FXCollections.observableArrayList();
	private final ObservableList<ShortenedOrder> sellData = FXCollections.observableArrayList();

	private Button btnAddBuyOrder = new Button();
	private Button btnAddSellOrder = new Button();
	private TextField txtFieldBuyPrice = new TextField("");
	private TextField txtFieldBuyAmount = new TextField("");
	private TextField txtFieldSellPrice = new TextField("");
	private TextField txtFieldSellAmount = new TextField("");

	private Button btnEUAbuyer = new Button();
	private Button btnEUAseller = new Button();

	private long newOrderBuyAmount;
	private BigDecimal newOrderBuyPrice = new BigDecimal(0);
	private long newOrderSellAmount;
	private BigDecimal newOrderSellPrice = new BigDecimal(0);

	private OrderBook orderBook = new OrderBook();

	TableColumn<ShortenedOrder, BigDecimal> colPriceBuy = new TableColumn<ShortenedOrder, BigDecimal>("Price");
	TableColumn<ShortenedOrder, Long> colAmountBuy = new TableColumn<ShortenedOrder, Long>("Amount");
	TableColumn<ShortenedOrder, BigDecimal> colTotalBuy = new TableColumn<ShortenedOrder, BigDecimal>("Total");

	TableColumn<ShortenedOrder, BigDecimal> colPriceSell = new TableColumn<ShortenedOrder, BigDecimal>("Price");
	TableColumn<ShortenedOrder, Long> colAmountSell = new TableColumn<ShortenedOrder, Long>("Amount");
	TableColumn<ShortenedOrder, BigDecimal> colTotalSell = new TableColumn<ShortenedOrder, BigDecimal>("Total");

	@Override
	public void start(Stage primaryStage) {

		try {

			GridPane root = FXMLLoader.load(getClass().getResource("OrderBook.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			buyTable = getChildById(root, "tableBuyOrders");
			sellTable = getChildById(root, "tableSellOrders");

			// add styling to tables
			buyTable.getStyleClass().add("buy-table-view");
			sellTable.getStyleClass().add("sell-table-view");

			// tables columns are of the same size
			colPriceBuy.prefWidthProperty().bind(buyTable.widthProperty().divide(3));
			colAmountBuy.prefWidthProperty().bind(buyTable.widthProperty().divide(3));
			colTotalBuy.prefWidthProperty().bind(buyTable.widthProperty().divide(3));

			colPriceSell.prefWidthProperty().bind(sellTable.widthProperty().divide(3));
			colAmountSell.prefWidthProperty().bind(sellTable.widthProperty().divide(3));
			colTotalSell.prefWidthProperty().bind(sellTable.widthProperty().divide(3));

			btnAddBuyOrder = getChildById(root, "btnAddBuyOrder");
			btnAddSellOrder = getChildById(root, "btnAddSellOrder");
			txtFieldBuyPrice = getChildById(root, "entryFieldPriceBuy");
			txtFieldBuyAmount = getChildById(root, "entryFieldAmountBuy");
			txtFieldSellPrice = getChildById(root, "entryFieldPriceSell");
			txtFieldSellAmount = getChildById(root, "entryFieldAmountSell");
			btnEUAbuyer = getChildById(root, "btnEUABuyer");
			btnEUAseller = getChildById(root, "btnEUASeller");

			giveBuyerEuas();
			giveSellerEuas();

			checkNewBuyOrder();
			checkNewSellOrder();

			shortenedBuyOrders = shortenBuyOrders(buyOrders);
			shortenedSellOrders = shortenSellOrders(sellOrders);

			addBuyDataToTable(shortenedBuyOrders);
			addSellDataToTable(shortenedSellOrders);

			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args, KeyPair buyerKeypair, String sellerPublicKey, EtsParticipant etsBuyer,
			EtsParticipant etsSeller) {

		buyerKeyPair = buyerKeypair;
		sellerPublickey = sellerPublicKey;
		etsbuyer = etsBuyer;
		etsseller = etsSeller;

		if (buyOrders == null) {
			buyOrders = new ArrayList<BuyOrder>();
		}

		if (sellOrders == null) {
			sellOrders = new ArrayList<SellOrder>();
		}

		launch(args);
	}

	private void addBuyDataToTable(List<ShortenedOrder> orders) {

		buyTable.getColumns().clear();
		buyData.clear();

		for (ShortenedOrder order : orders) {

			buyData.add(order);

		}

		colPriceBuy.setMinWidth(100.0);
		colAmountBuy.setMinWidth(100.0);
		colTotalBuy.setMinWidth(100.0);

		colPriceBuy.setCellValueFactory(new PropertyValueFactory<ShortenedOrder, BigDecimal>("Price"));
		colAmountBuy.setCellValueFactory(new PropertyValueFactory<ShortenedOrder, Long>("Amount"));
		colTotalBuy.setCellValueFactory(new PropertyValueFactory<ShortenedOrder, BigDecimal>("Total"));

		buyTable.setItems(buyData);
		buyTable.getColumns().addAll(colPriceBuy, colAmountBuy, colTotalBuy);

	}

	private void addSellDataToTable(List<ShortenedOrder> orders) {

		sellTable.getColumns().clear();
		sellData.clear();

		for (ShortenedOrder order : orders) {

			sellData.add(order);

		}

		colPriceSell.setMinWidth(100.0);
		colAmountSell.setMinWidth(100.0);
		colTotalSell.setMinWidth(100.0);

		colPriceSell.setCellValueFactory(new PropertyValueFactory<ShortenedOrder, BigDecimal>("Price"));
		colAmountSell.setCellValueFactory(new PropertyValueFactory<ShortenedOrder, Long>("Amount"));
		colTotalSell.setCellValueFactory(new PropertyValueFactory<ShortenedOrder, BigDecimal>("Total"));

		sellTable.setItems(sellData);
		sellTable.getColumns().addAll(colPriceSell, colAmountSell, colTotalSell);

	}

	/**
	 * Creates a list of shortened buy orders, omitting every value but amount and
	 * price.
	 * 
	 * @param orders the list of buy or sell orders
	 */
	private List<ShortenedOrder> shortenBuyOrders(List<BuyOrder> orders) {

		List<ShortenedOrder> shortenedOrders = new ArrayList<ShortenedOrder>();

		for (Order order : orders) {
			shortenedOrders.add(new ShortenedOrder(order.getAmount(), order.getPricePerCo2E()));
		}

		return shortenedOrders;

	}

	/**
	 * Creates a list of shortened sell orders, omitting every value but amount and
	 * price.
	 * 
	 * @param orders the list of buy or sell orders
	 */
	private List<ShortenedOrder> shortenSellOrders(List<SellOrder> orders) {

		List<ShortenedOrder> shortenedOrders = new ArrayList<ShortenedOrder>();

		for (Order order : orders) {
			shortenedOrders.add(new ShortenedOrder(order.getAmount(), order.getPricePerCo2E()));
		}

		return shortenedOrders;

	}

	private static <T> T getChildById(Parent parent, String id) {
		String nodeId = null;

		if (parent instanceof TitledPane) {
			TitledPane titledPane = (TitledPane) parent;
			Node content = titledPane.getContent();
			nodeId = content.idProperty().get();

			if (nodeId != null && nodeId.equals(id)) {
				return (T) content;
			}

			if (content instanceof Parent) {
				T child = getChildById((Parent) content, id);

				if (child != null) {
					return child;
				}
			}
		}

		for (Node node : parent.getChildrenUnmodifiable()) {
			nodeId = node.idProperty().get();
			if (nodeId != null && nodeId.equals(id)) {
				return (T) node;
			}

			else if (node instanceof Parent) {
				T child = getChildById((Parent) node, id);

				if (child != null) {
					return child;
				}
			}
		}
		return null;
	}

	private void checkNewBuyOrder() {

		// add buy order action
		btnAddBuyOrder.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				// check text field buy price entry
				if (!txtFieldBuyPrice.getText().equals("")) {
					if (txtFieldBuyPrice.getText().matches("^\\d+(\\.\\d+)*$")
							|| txtFieldBuyPrice.getText().matches("^\\d+(\\,\\d+)*$")) {

						newOrderBuyPrice = new BigDecimal(txtFieldBuyPrice.getText());

					} else {
						System.out.println("Please enter a nummeric value, seperated either by a dot.");
						return;
					}
				} else {
					System.out.println("Please insert a price");
					return;
				}
				// check text field buy price entry
				if (!txtFieldBuyAmount.getText().equals("")) {
					if (txtFieldBuyAmount.getText().matches("[0-9]+")) {
						newOrderBuyAmount = Long.parseLong(txtFieldBuyAmount.getText());
					} else {
						System.out.println("Please enter a nummeric value");
						return;
					}
				} else {
					System.out.println("Please insert an amount");
					return;
				}

				try {
					// add new buy orders to table
					BuyOrder newBuyOrder = new BuyOrder(buyerKeyPair, newOrderBuyAmount, newOrderBuyPrice,
							newOrderBuyAmount + " of EUAs for " + newOrderBuyPrice + " each");
					buyOrders.add(newBuyOrder);
					// create shortened order
					List<BuyOrder> orderToShorten = new ArrayList<BuyOrder>();
					orderToShorten.add(newBuyOrder);
					buyData.addAll(shortenBuyOrders(orderToShorten));
					buyTable.setItems(buyData);
					orderBook.addBuyOrder(newBuyOrder);
					orderBook.checkSellOrders(newBuyOrder, etsbuyer, etsseller);
					updateTables();
					System.out.println("Buyer");
					etsbuyer.printEtsParticipant();
					System.out.println("Seller");
					etsseller.printEtsParticipant();
					txtFieldBuyPrice.setText("");
					txtFieldBuyAmount.setText("");

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});
	}

	private void checkNewSellOrder() {

		// add buy order action
		btnAddSellOrder.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				// check text field buy price entry
				if (!txtFieldSellPrice.getText().equals("")) {
					if (txtFieldSellPrice.getText().matches("^\\d+(\\.\\d+)*$")
							|| txtFieldSellPrice.getText().matches("^\\d+(\\,\\d+)*$")) {

						newOrderSellPrice = new BigDecimal(txtFieldSellPrice.getText());

					} else {
						System.out.println("Please enter a nummeric value, seperated a dot.");
					}
				} else {
					System.out.println("Please insert a price");
				}
				// check text field buy price entry
				if (!txtFieldSellAmount.getText().equals("")) {
					if (txtFieldSellAmount.getText().matches("[0-9]+")) {
						newOrderSellAmount = Long.parseLong(txtFieldSellAmount.getText());
					} else {
						System.out.println("Please enter a nummeric value");
					}
				} else {
					System.out.println("Please insert an amount");
				}

				try {
					// add new buy orders to table
					SellOrder newSellOrder = new SellOrder(sellerPublickey, newOrderSellAmount, newOrderSellPrice);
					sellOrders.add(newSellOrder);
					List<SellOrder> sellOrderToShorten = new ArrayList<SellOrder>();
					sellOrderToShorten.add(newSellOrder);
					sellData.addAll(shortenSellOrders(sellOrderToShorten));
					sellTable.setItems(sellData);
					orderBook.addSellOrder(newSellOrder);
					txtFieldSellPrice.setText("");
					txtFieldSellAmount.setText("");

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void giveBuyerEuas() {

		btnEUAbuyer.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				etsbuyer.setCertificates(etsbuyer.getCertificates() + 100);

			}
		});

	}

	private void giveSellerEuas() {

		btnEUAseller.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				etsseller.setCertificates(etsseller.getCertificates() + 100);

			}

		});
	}

	private void updateTables() {

		buyTable.refresh();
		sellTable.refresh();

	}

	public class ShortenedOrder {

		private long amount;
		private BigDecimal price;
		private BigDecimal total;
		private boolean completed;

		ShortenedOrder(long amount, BigDecimal price) {

			this.setAmount(amount);
			this.setPrice(price);
			this.setTotal(price.multiply(new BigDecimal(amount)));
		}

		public long getAmount() {
			return amount;
		}

		public void setAmount(long amount) {
			this.amount = amount;
		}

		public BigDecimal getPrice() {
			return price;
		}

		public void setPrice(BigDecimal price) {
			this.price = price;
		}

		public BigDecimal getTotal() {
			return total;
		}

		public void setTotal(BigDecimal total) {
			this.total = total;
		}

		public boolean isCompleted() {
			return completed;
		}

		public void setCompleted(boolean completed) {
			this.completed = completed;
		}

		@Override
		public String toString() {

			return "Amount:" + this.getAmount() + ", Price: " + this.getPrice() + ", Total: " + this.getTotal()
					+ ", Completed: " + this.isCompleted();
		}
	}

}
