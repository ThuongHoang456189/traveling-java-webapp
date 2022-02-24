package thuonghth.controllers;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.TreeMap;

import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import thuonghth.tours.TourDAO;
import thuonghth.tours.TourDTO;
import thuonghth.utils.input_validators.TourValidator;

/**
 * Servlet implementation class TourSearchingController
 */
public class TourSearchingController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(TourSearchingController.class);

	private String IMAGE_DIR;
	private TourValidator defaultTourSearchingInfo;

	private int page = 0;
	private LinkedList<TourDTO> toursList;
	private int numberOfAvailableTours = 0;
	private int maxPage = 0;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TourSearchingController() {
		super();
	}

	@Override
	public void init() throws ServletException {
		super.init();
		IMAGE_DIR = this.getServletContext().getRealPath("/") + File.separator + "images";
		defaultTourSearchingInfo = TourValidator.getDefaultTourSearchingInfo();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		IMAGE_DIR = this.getServletContext().getRealPath("/") + File.separator + "images";
		defaultTourSearchingInfo = TourValidator.getDefaultTourSearchingInfo();
	}

	private void searchTour(String contextPath, TourValidator tourSearchingInfo, int page)
			throws NamingException, SQLException {
		TourDAO tourDAO = new TourDAO();
		toursList = tourDAO.getToursList(contextPath, IMAGE_DIR, tourSearchingInfo.getTourSearchingInfo(), page);
		numberOfAvailableTours = tourDAO.getNumberOfAvailableTours();
		maxPage = tourDAO.getNumOfPages();
	}

	private void updateTourSearchingInfo(HttpServletRequest request) {
		TourValidator searchingInfo = (TourValidator) request.getSession().getAttribute("tourSearchingInfo");
		if (searchingInfo == null)
			request.getSession().setAttribute("tourSearchingInfo", defaultTourSearchingInfo);
	}

	private void updatePage(HttpServletRequest request) {
		String page = request.getParameter("page");
		try {
			this.page = Integer.parseInt(page);
		} catch (NumberFormatException e) {
			this.page = 1;
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		updateTourSearchingInfo(request);

		updatePage(request);

		try {
			TourValidator fields = (TourValidator) request.getSession().getAttribute("tourSearchingInfo");
			searchTour(request.getContextPath(), fields, page);

			request.setAttribute("fields", fields);
			request.setAttribute("toursList", toursList);
			request.setAttribute("numberOfAvailableTours", numberOfAvailableTours);
			request.setAttribute("maxPage", maxPage);

			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		} catch (Exception e) {
			LOGGER.error("Failed to search tour(s). " + e);
			response.sendRedirect("default-error-page.html");
			return;
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Get string field
		String place = request.getParameter("place");
		String fromDate = request.getParameter("fromDate");
		String toDate = request.getParameter("toDate");
		String upToPrice = request.getParameter("upToPrice");

		TourValidator fields = new TourValidator(place, fromDate, toDate, upToPrice);

		TreeMap<String, String> fieldsMsg = fields.validateTourSearchingInfo();

		if (fields.isTourSearchingInfoValid()) {
			request.getSession().setAttribute("tourSearchingInfo", fields);
			doGet(request, response);
		} else {
			request.setAttribute("fields", fields);
//			request.setAttribute("fieldsMsg", fieldsMsg);

			request.getRequestDispatcher("index.jsp").forward(request, response);
			return;
		}
	}

}
