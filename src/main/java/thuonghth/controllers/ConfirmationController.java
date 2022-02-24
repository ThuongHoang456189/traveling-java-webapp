package thuonghth.controllers;

import java.io.IOException;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import thuonghth.carts.CartDAO;
import thuonghth.carts.CartDTO;
import thuonghth.carts.CartRecord;
import thuonghth.users.UserDTO;
import thuonghth.utils.MyConstants;

/**
 * Servlet implementation class OrderInsertionController
 */
public class ConfirmationController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(ConfirmationController.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ConfirmationController() {
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

	private boolean setUpAmount(HttpServletRequest request) {
		boolean isInputValid = true;
		CartDTO cart = getCart(request);
		if (cart == null)
			isInputValid = false;
		else {
			for (CartRecord record : cart.getCart().values()) {
				TreeMap<String, String> fieldsMsg = record.getTour().getFieldsMsg();
				if (fieldsMsg == null) {
					fieldsMsg = new TreeMap<>();
					record.getTour().setFieldsMsg(fieldsMsg);
				}
				try {
					String amountString = request
							.getParameter("amount" + record.getTour().getInputObject().getTourID());
					int amount = Integer.parseInt(amountString);
					if (amount <= 0) {
						fieldsMsg.put("amount", "The amount field must be at least 1. ");
						isInputValid = isInputValid && false;
					}
					record.setAmount(amount);
				} catch (NumberFormatException e) {
					fieldsMsg.put("amount", "The amount field must be a long number. ");
					isInputValid = isInputValid && false;
				}
			}
		}

		return isInputValid;
	}

	private String getDiscountCode(HttpServletRequest request) {
		return request.getParameter("discountCode");
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
			boolean isCartValid = true;
			isCartValid = isCartValid && setUpAmount(request);
			CartDTO cart = getCart(request);
			if (cart != null) {
				CartDAO cartDAO = new CartDAO(cart);
				String discountCode = getDiscountCode(request);
				try {
					boolean isDiscountValid = false;
					if (discountCode != null && !discountCode.isBlank()) {
						isDiscountValid = cartDAO.validateDiscount(discountCode);
						isCartValid = isCartValid && isDiscountValid && cartDAO.validateCart(discountCode, null);
						if (isCartValid) {
							cartDAO.updateCartTotal();
							cart.setStatus(CartDTO.ORDERING_STATUS);
							request.setAttribute("btnAction", "confirm");
							request.getRequestDispatcher("/viewCart").forward(request, response);
							return;
						}
					} else {

						// Logic cho authenticate
						isDiscountValid = cartDAO.validateCart(discountCode, null);
						isCartValid = isCartValid && isDiscountValid;
						if (isCartValid) {
							request.getRequestDispatcher("/authorizePayment").forward(request, response);
							return;
						}
					}
				} catch (Exception e) {
					LOGGER.error(e);
					response.sendRedirect("default-error-page.html");
					return;
				}
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
