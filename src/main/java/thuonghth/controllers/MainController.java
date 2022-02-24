package thuonghth.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import thuonghth.carts.CartDTO;

/**
 * Servlet implementation class MainController
 */

public class MainController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(MainController.class);

	private final String LOGIN_CONTROLLER = "/login";
	private final String LOGOUT_CONTROLLER = "/logout";
	private final String TOUR_INSERTION_CONTROLLER = "/insertTour";
	private final String TOUR_SEARCHING_CONTROLLER = "/searchTour";
	private final String CART_INSERTION_CONTROLLER = "/addToCart";
	private final String CART_STATUS_CONTROLLER = "/changeCartStatus";
	private final String CART_SHOWING_CONTROLLER = "/viewCart";
	private final String CART_REMOVING_CONTROLLER = "/removeFromCart";
	private final String ORDERING_CONTROLLER = "/order";
	private final String CONFIRMATION_CONTROLLER = "/confirm";
	private final String PAYMENT_REVISION_CONTROLLER = "/reviewPayment";
	private final String PAYMENT_EXECUTION_CONTROLLER = "/executePayment";
	private final String ERROR_404_PAGE = "error-404-page.jsp";

	/**
	 * Default constructor.
	 */
	public MainController() {

	}

	@Override
	public void init() throws ServletException {
		super.init();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	private CartDTO getCart(HttpServletRequest request) {
		return (CartDTO) request.getSession().getAttribute("cart");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	private String getDispatcherPath(HttpServletRequest request) {
		if (getCart(request) != null) {
			getCart(request).setStatus(CartDTO.INITED_STATUS);
		}
		String path = "";
		String action = request.getParameter("btnAction");
		if (action == null || action.isBlank()) {
			if (request.getMethod().equalsIgnoreCase("POST")) {
				try {
					ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
					List<FileItem> items = servletFileUpload.parseRequest(request);
					action = items.get(0).getString();
					request.setAttribute("items", items);
					if (action.equalsIgnoreCase("inserttour"))
						path = TOUR_INSERTION_CONTROLLER;
				} catch (Exception e) {
					LOGGER.error(e);
				}
			} else
				path = TOUR_SEARCHING_CONTROLLER;
		} else {
			switch (action.trim().toLowerCase()) {
			case "login":
				path = LOGIN_CONTROLLER;
				break;
			case "logout":
				path = LOGOUT_CONTROLLER;
				break;
			case "inserttour":
				path = TOUR_INSERTION_CONTROLLER;
				break;
			case "searchtour":
				path = TOUR_SEARCHING_CONTROLLER;
				break;
			case "addtocart":
				path = CART_INSERTION_CONTROLLER;
				break;
			case "changecartstatus":
				path = CART_STATUS_CONTROLLER;
				break;
			case "viewcart":
				path = CART_SHOWING_CONTROLLER;
				break;
			case "removefromcart":
				path = CART_REMOVING_CONTROLLER;
				break;
			case "order":
				path = ORDERING_CONTROLLER;
				break;
			case "confirm":
				path = CONFIRMATION_CONTROLLER;
				break;
			case "reviewpayment":
				path = PAYMENT_REVISION_CONTROLLER;
				break;
			case "pay":
				path = PAYMENT_EXECUTION_CONTROLLER;
				break;
			default:
				path = ERROR_404_PAGE;
				break;
			}
		}
		return path;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher(getDispatcherPath(request)).forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher(getDispatcherPath(request)).forward(request, response);
	}

}
