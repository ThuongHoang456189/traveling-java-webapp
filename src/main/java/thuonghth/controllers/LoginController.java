package thuonghth.controllers;

import java.io.IOException;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import thuonghth.users.UserDAO;
import thuonghth.users.UserDTO;
import thuonghth.utils.input_validators.AccountValidator;

/**
 * Servlet implementation class LoginController
 */
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(LoginController.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginController() {
		super();
		// TODO Auto-generated constructor stub
	}

	private boolean isActiveUser(HttpServletRequest request) {
		return request.getSession().getAttribute("activeUser") != null;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!isActiveUser(request)) {
			request.getRequestDispatcher("login.html").forward(request, response);
			return;
		} else {
			response.sendRedirect("MainController?btnAction=searchtour");
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!isActiveUser(request)) {
			String userID = request.getParameter("userID");
			String password = request.getParameter("password");
			if (userID != null && password != null) {
				AccountValidator validator = new AccountValidator(userID, password);
				TreeMap<String, String> fieldsMsg = validator.validateAccount();
				if (validator.isAccountValid()) {
					UserDAO userDAO = new UserDAO();
					try {
						UserDTO user = userDAO.login(validator.getAccountDTO());
						if (user == null) {
							request.setAttribute("actionFeedback", "Login Failed!");
							request.setAttribute("userID", userID);
							request.getRequestDispatcher("login.jsp").forward(request, response);
							return;
						} else {
							request.getSession().setAttribute("activeUser", user);
							response.sendRedirect("MainController?btnAction=searchtour");
							return;
						}
					} catch (Exception e) {
						LOGGER.error("Login failed. " + e);
						response.sendRedirect("default-error-page.html");
						return;
					}
				} else {
					request.setAttribute("actionFeedback", "Login Failed!");
					request.setAttribute("userID", userID);
//					request.setAttribute("fieldsMsg", fieldsMsg);
					request.getRequestDispatcher("login.jsp").forward(request, response);
					return;
				}
			} else {
				LOGGER.error("userID input field or password input field is null");
				response.sendRedirect("default-error-page.html");
				return;
			}
		} else {
			response.sendRedirect("MainController?btnAction=searchtour");
			return;
		}
	}

}
