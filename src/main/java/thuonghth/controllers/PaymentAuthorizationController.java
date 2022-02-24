package thuonghth.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.paypal.base.rest.PayPalRESTException;

import thuonghth.carts.CartDAO;
import thuonghth.carts.CartDTO;
import thuonghth.orders.OrderDAO;
import thuonghth.orders.OrderDTO;
import thuonghth.users.UserDTO;
import thuonghth.utils.MyConstants;
import thuonghth.utils.PaymentService;

/**
 * Servlet implementation class AuthorizePaymentServlet
 */
public class PaymentAuthorizationController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(PaymentAuthorizationController.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PaymentAuthorizationController() {
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

	private CartDTO getCart(HttpServletRequest request) {
		return (CartDTO) request.getSession().getAttribute("cart");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (isActiveCustomer(request)) {
			OrderDAO orderDAO = new OrderDAO();
			CartDTO cart = getCart(request);
			OrderDTO order = orderDAO.convertCartToOrder(cart);
			try {
				PaymentService paymentService = new PaymentService();
				String approvalLink = paymentService.authorizePayment(order);

				response.sendRedirect(approvalLink);
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

}
