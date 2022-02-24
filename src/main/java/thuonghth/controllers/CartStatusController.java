package thuonghth.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thuonghth.carts.CartDTO;

/**
 * Servlet implementation class CartStatusController
 */
public class CartStatusController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CartStatusController() {
		super();
		// TODO Auto-generated constructor stub
	}

	private String getDirection(HttpServletRequest request) {
		String direction = request.getParameter("direction");
		if (direction == null) {
			direction = "notKnown";
		} else {
			direction = direction.trim().toLowerCase();
			if (!direction.equals("backward")) {
				direction = "notKnown";
			}
		}
		return direction;
	}

	private String getBackwardStatusOfCart(HttpServletRequest request) {
		String backwardStatus = "";
		CartDTO cart = getCart(request);
		if (cart != null) {
			String status = cart.getStatus();
			switch (status) {
			case CartDTO.INITED_STATUS:
				backwardStatus = CartDTO.INITED_STATUS;
				break;
			case CartDTO.ORDERING_STATUS:
				backwardStatus = CartDTO.INITED_STATUS;
				break;
			case CartDTO.CONFIRMED_STATUS:
				backwardStatus = CartDTO.ORDERING_STATUS;
				break;
			default:
				backwardStatus = "";
				break;
			}
		}
		return backwardStatus;
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
		String direction = getDirection(request);
		CartDTO cart = getCart(request);
		if (cart != null) {
			if (direction.equals("backward")) {
				String backwardStatus = getBackwardStatusOfCart(request);
				if (!backwardStatus.isBlank()) {
					cart.setStatus(backwardStatus);
				}
			}
		}
		response.sendRedirect("MainController?btnAction=viewCart");
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
