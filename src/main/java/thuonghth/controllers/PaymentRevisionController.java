package thuonghth.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.paypal.api.payments.PayerInfo;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.PayPalRESTException;

import thuonghth.carts.CartDAO;
import thuonghth.carts.CartDTO;
import thuonghth.users.UserDTO;
import thuonghth.utils.MyConstants;
import thuonghth.utils.PaymentService;

/**
 * Servlet implementation class PaymentRevisionController
 */
public class PaymentRevisionController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(PaymentRevisionController.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PaymentRevisionController() {
		super();
		// TODO Auto-generated constructor stub
	}

	private boolean isActiveCustomer(HttpServletRequest request) {
		return request.getSession().getAttribute("activeUser") != null
				&& ((UserDTO) request.getSession().getAttribute("activeUser"))
						.getRoleID() == MyConstants.CUSTOMER_USER_ROLE_ID;
	}

	private boolean isActiveAdmin(HttpServletRequest request) {
		return request.getSession().getAttribute("activeUser") != null
				&& ((UserDTO) request.getSession().getAttribute("activeUser")).getRoleID() == MyConstants.ADMIN_ROLE_ID;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (isActiveCustomer(request)) {
			String paymentId = request.getParameter("paymentId");
			String payerId = request.getParameter("PayerID");

			try {
				PaymentService paymentService = new PaymentService();
				Payment payment = paymentService.getPaymentDetails(paymentId);

				PayerInfo payerInfo = payment.getPayer().getPayerInfo();

				// Khi review ve payment
				request.setAttribute("payer", payerInfo);
				request.setAttribute("btnAction", "pay");
				String url = "cart.jsp?paymentId=" + paymentId + "&PayerID=" + payerId;
				request.getRequestDispatcher(url).forward(request, response);
				return;

			} catch (PayPalRESTException e) {
				LOGGER.error(e);
				response.sendRedirect("default-error-page.html");
				return;
			}
		} else {
			if (isActiveAdmin(request)) {
				LOGGER.error("Only customer can book tour(s). ");
				response.sendRedirect("default-error-page.html");
				return;
			} else {
				response.sendRedirect("MainController?btnAction=login");
				return;
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
