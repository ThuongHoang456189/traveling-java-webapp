package thuonghth.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.ejb.ConcurrencyManagementType;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Currency;
import com.paypal.api.payments.Details;
import com.paypal.api.payments.Item;
import com.paypal.api.payments.ItemList;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.PayerInfo;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import thuonghth.orders.OrderDTO;
import thuonghth.orders.OrderDetailDTO;

public class PaymentService {
	public String authorizePayment(OrderDTO order) throws PayPalRESTException {
		Payer payer = getPayerInformation();
		RedirectUrls redirectUrls = getRedirectUrls();
		List<Transaction> listTransactions = getTransactionInformation(order);

		Payment requestPayment = new Payment();
		requestPayment.setTransactions(listTransactions);
		requestPayment.setRedirectUrls(redirectUrls);
		requestPayment.setPayer(payer);
		requestPayment.setIntent("authorize");

		APIContext apiContext = new APIContext(MyConstants.PAYPAL_CLIENT_ID, MyConstants.PAYPAL_CLIENT_SECRET,
				MyConstants.MODE);

		Payment approvedPayment = requestPayment.create(apiContext);

		return getApprovalLink(approvedPayment);
	}

	private Payer getPayerInformation() {
		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");

		PayerInfo payerInfo = new PayerInfo();
		payerInfo.setFirstName("Thuong").setLastName("Hoang").setEmail("ngocanh654189@gmail.com");

		payer.setPayerInfo(payerInfo);

		return payer;
	}

	private RedirectUrls getRedirectUrls() {
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl("http://localhost:8443/dream-traveling/");
		redirectUrls.setReturnUrl("http://localhost:8443/dream-traveling/MainController?btnAction=reviewpayment");
		return redirectUrls;
	}

	private List<Transaction> getTransactionInformation(OrderDTO order) {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
		symbols.setDecimalSeparator('.');
		DecimalFormat formatter = new DecimalFormat();
		formatter.setDecimalFormatSymbols(symbols);

		ItemList itemList = new ItemList();
		List<Item> items = new LinkedList<>();
		List<Transaction> listTransaction = new LinkedList<>();

		BigDecimal orderSubtotal = BigDecimal.ZERO;
		for (OrderDetailDTO orderDetail : order.getOrderDetails()) {

			Item item = new Item();
			item.setCurrency("USD");
			item.setName(orderDetail.getTour().getTourName());
			if (order.getDiscount() == null || order.getDiscount().getDiscountID() == -1
					|| order.getDiscount().getDiscountPercent().compareTo(BigDecimal.ZERO) == 0) {
				item.setPrice(formatter.format(orderDetail.getTour().getPrice().setScale(2, RoundingMode.CEILING)));
			} else {
				item.setPrice(formatter.format(orderDetail.getTour().getPrice()
						.multiply(BigDecimal.ONE
								.subtract(order.getDiscount().getDiscountPercent().divide(new BigDecimal(100))))
						.setScale(2, RoundingMode.CEILING)));
			}

			item.setQuantity("" + orderDetail.getAmount());

			orderSubtotal = orderSubtotal
					.add((new BigDecimal(item.getPrice()).multiply(new BigDecimal(item.getQuantity()))));

			items.add(item);
		}

		Details details = new Details();
		details.setSubtotal(formatter.format(orderSubtotal.setScale(2, RoundingMode.CEILING)));

		Amount amount = new Amount();
		amount.setCurrency("USD");
		amount.setTotal(formatter.format(orderSubtotal.setScale(2, RoundingMode.CEILING)));
		amount.setDetails(details);

		Transaction transaction = new Transaction();
		transaction.setAmount(amount);
		transaction.setDescription("Your order");

		itemList.setItems(items);
		transaction.setItemList(itemList);

		listTransaction.add(transaction);
		return listTransaction;
	}

	private String getApprovalLink(Payment approvedPayment) {
		List<Links> links = approvedPayment.getLinks();
		String approvalLink = null;

		for (Links link : links) {
			if (link.getRel().equalsIgnoreCase("approval_url")) {
				approvalLink = link.getHref();
				break;
			}
		}

		return approvalLink;
	}

	public Payment getPaymentDetails(String paymentId) throws PayPalRESTException {
		APIContext apiContext = new APIContext(MyConstants.PAYPAL_CLIENT_ID, MyConstants.PAYPAL_CLIENT_SECRET,
				MyConstants.MODE);
		return Payment.get(apiContext, paymentId);
	}

	public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
		PaymentExecution paymentExecution = new PaymentExecution();
		paymentExecution.setPayerId(payerId);

		Payment payment = new Payment().setId(paymentId);

		APIContext apiContext = new APIContext(MyConstants.PAYPAL_CLIENT_ID, MyConstants.PAYPAL_CLIENT_SECRET,
				MyConstants.MODE);

		return payment.execute(apiContext, paymentExecution);
	}
}
