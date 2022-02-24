package thuonghth.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import thuonghth.carts.CartDAO;
import thuonghth.carts.CartDTO;
import thuonghth.tours.TourDTO;
import thuonghth.users.UserDTO;
import thuonghth.utils.MyConstants;
import thuonghth.utils.input_validators.TourValidator;

/**
 * Servlet implementation class CartInsertionController
 */
public class CartInsertionController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(CartInsertionController.class);

	private CartDTO cart;

	private long page = 0;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CartInsertionController() {
		super();
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

	private void initialCart(HttpServletRequest request) {
		CartDTO cartInSession = (CartDTO) request.getSession().getAttribute("cart");
		if (cartInSession == null) {
			cart = new CartDTO();
			request.getSession().setAttribute("cart", cart);
		}
	}

	private TourDTO getTourDTOFromRequest(HttpServletRequest request) {
		TourDTO tour = null;
		String tourID = request.getParameter("tourID");
		String tourName = request.getParameter("tourName");
		String fromDate = request.getParameter("fromDate");
		String toDate = request.getParameter("toDate");
		String price = request.getParameter("price");
		TourValidator fields = new TourValidator(tourID, tourName, fromDate, toDate, price);
		fields.validateTourInCart();
		if (fields.isTourInCartValid())
			tour = fields.getTourDTOinCart();
		return tour;
	}

	private CartDTO getCart(HttpServletRequest request) {
		return (CartDTO) request.getSession().getAttribute("cart");
	}

	private void updatePage(HttpServletRequest request) {
		String page = request.getParameter("page");
		try {
			this.page = Long.parseLong(page);
		} catch (NumberFormatException e) {
			this.page = 1;
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (isActiveCustomer(request)) {
			updatePage(request);

			TourDTO tour = getTourDTOFromRequest(request);
			if (tour != null) {
				initialCart(request);
				CartDTO cart = getCart(request);
				CartDAO cartDAO = new CartDAO(cart);
				cartDAO.updateCart(tour);
			}

			response.sendRedirect("MainController?btnAction=searchTour&page=" + this.page);
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
