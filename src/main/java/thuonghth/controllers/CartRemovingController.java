package thuonghth.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import thuonghth.carts.CartDAO;
import thuonghth.carts.CartDTO;
import thuonghth.users.UserDTO;
import thuonghth.utils.MyConstants;

/**
 * Servlet implementation class CartRemovingController
 */
public class CartRemovingController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(CartRemovingController.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CartRemovingController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	private CartDTO getCart(HttpServletRequest request) {
		return (CartDTO) request.getSession().getAttribute("cart");
	}

	private String getTourIDString(HttpServletRequest request) {
		String tourID = request.getParameter("tourID");
		return tourID == null ? "" : tourID.trim();
	}

	private void removeTourFromCart(HttpServletRequest request) {
		try {
			int tourID = Integer.parseInt(getTourIDString(request));
			CartDTO cart = getCart(request);
			if (cart != null) {
				cart.getCart().remove(tourID);
				CartDAO cartDAO = new CartDAO(cart);
				cartDAO.updateCartTotal();
			}
		} catch (NumberFormatException e) {
			LOGGER.error("Removing TourID is not valid. " + e);
		}
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (isActiveCustomer(request)) {
			removeTourFromCart(request);
			response.sendRedirect("MainController?btnAction=viewCart");

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
