package thuonghth.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.paypal.api.payments.PayerInfo;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import thuonghth.carts.CartDAO;
import thuonghth.carts.CartDTO;
import thuonghth.orders.OrderDAO;
import thuonghth.users.UserDTO;
import thuonghth.utils.DBHelper;
import thuonghth.utils.MyConstants;
import thuonghth.utils.PaymentService;

/**
 * Servlet implementation class PaymentExecutionController
 */
public class PaymentExecutionController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(PaymentExecutionController.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PaymentExecutionController() {
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

	private UserDTO getCustomer(HttpServletRequest request) {
		return (UserDTO) request.getSession().getAttribute("activeUser");
	}

	private CartDTO getCart(HttpServletRequest request) {
		return (CartDTO) request.getSession().getAttribute("cart");
	}

	private String getDiscountCode(CartDTO cart) {
		return cart.getDiscount().getInputObject().getDiscountCode();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (isActiveCustomer(request)) {
			boolean isSuccess = false;

			String paymentId = request.getParameter("paymentId");
			String payerId = request.getParameter("PayerID");

			OrderDAO orderDAO = null;
			try {
				PaymentService paymentService = new PaymentService();
				Payment payment = paymentService.executePayment(paymentId, payerId);

				PayerInfo payerInfo = payment.getPayer().getPayerInfo();

				// Doan nay them code cho order

				CartDTO cart = getCart(request);
				if (cart != null) {
					CartDAO cartDAO = new CartDAO(cart);
					String discountCode = getDiscountCode(cart);
					Connection con = null;
					try {
						boolean isDiscountValid = false;

						con = DBHelper.getConnect();
						con.setAutoCommit(false);
						isDiscountValid = cartDAO.validateCart(discountCode, con);
						if (isDiscountValid) {
							// Sua doan nay
//							cart.setStatus(CartDTO.CONFIRMED_STATUS);
							cartDAO.updateCartTotal();
							// Sua logic doan nay
							orderDAO = new OrderDAO();
							isSuccess = orderDAO.insertOrder(cart, con, getCustomer(request));
							if (!isSuccess)
								con.rollback();
						}
//							else
//								cart.setStatus(CartDTO.INITED_STATUS);
						con.commit();

					} catch (Exception e) {
						try {
							if (con != null)
								con.rollback();
						} catch (SQLException e1) {
							LOGGER.error(e);
						}
						LOGGER.error(e);
						response.sendRedirect("default-error-page.html");
						return;
					} finally {
						if (con != null)
							try {
								con.setAutoCommit(true);
								con.close();
							} catch (SQLException e) {
								LOGGER.error(e);
							}
					}
				}

			} catch (PayPalRESTException e) {
				LOGGER.error(e);
				response.sendRedirect("default-error-page.html");
			}
			// Doan nay dieu phoi ve show order
			if (isSuccess) {
				request.setAttribute("order", orderDAO.getOrder());
				request.getSession().removeAttribute("cart");
				request.getRequestDispatcher("receipt.jsp").forward(request, response);
				return;
			} else {
				request.setAttribute("actionFeedback", "Ordered failed !!!");
				request.getRequestDispatcher("default-error-page.html").forward(request, response);
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
		doGet(request, response);
	}

}
