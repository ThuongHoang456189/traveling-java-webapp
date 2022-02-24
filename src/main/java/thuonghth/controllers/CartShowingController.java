package thuonghth.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import thuonghth.carts.CartDTO;
import thuonghth.users.UserDTO;
import thuonghth.utils.MyConstants;

/**
 * Servlet implementation class CartShowingController
 */
public class CartShowingController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(CartShowingController.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CartShowingController() {
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

	private String getCartStatus(CartDTO cart) {
		return cart.getStatus();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (isActiveCustomer(request)) {
			CartDTO cart = getCart(request);
			if (cart != null) {
				String btnAction = getCartStatus(cart).equals(CartDTO.ORDERING_STATUS) ? "confirm" : "order";
				request.setAttribute("btnAction", btnAction);
			}
			request.getRequestDispatcher("cart.jsp").forward(request, response);
		} else {
			if (isActiveAdmin(request)) {
				LOGGER.error("Only customer can view cart. ");
				response.sendRedirect("default-error-page.html");
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
