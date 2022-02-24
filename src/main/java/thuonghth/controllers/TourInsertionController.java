package thuonghth.controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import thuonghth.tours.TourDAO;
import thuonghth.users.UserDTO;
import thuonghth.utils.FileUtil;
import thuonghth.utils.ImageUtil;
import thuonghth.utils.MyConstants;
import thuonghth.utils.input_validators.TourValidator;

/**
 * Servlet implementation class TourInsertionController
 */
public class TourInsertionController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(TourInsertionController.class);

	private String IMAGE_DIR;

	public TourInsertionController() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() throws ServletException {
		super.init();
		IMAGE_DIR = this.getServletContext().getRealPath("/") + File.separator + "images";
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		IMAGE_DIR = this.getServletContext().getRealPath("/") + File.separator + "images";
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	private boolean isActiveCustomer(HttpServletRequest request) {
		return request.getSession().getAttribute("activeUser") != null
				&& ((UserDTO) request.getSession().getAttribute("activeUser"))
						.getRoleID() == MyConstants.CUSTOMER_USER_ROLE_ID;
	}

	private boolean isActiveAdmin(HttpServletRequest request) {
		return request.getSession().getAttribute("activeUser") != null
				&& ((UserDTO) request.getSession().getAttribute("activeUser")).getRoleID() == MyConstants.ADMIN_ROLE_ID;
	}

	private String getUserID(HttpServletRequest request) {
		return "" + ((UserDTO) request.getSession().getAttribute("activeUser")).getAccountID();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Lock the function to admin
		if (isActiveAdmin(request)) {
			request.getRequestDispatcher("tour-info-form.jsp").forward(request, response);
			return;
		} else {
			request.getRequestDispatcher("login.jsp").forward(request, response);
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Lock the function to admin
		if (!isActiveAdmin(request)) {
			if (isActiveCustomer(request)) {
				response.sendRedirect("MainController?btnAction=login");
				return;
			} else {
				LOGGER.error("Customer must not insert a new tour. ");
				response.sendRedirect("default-error-page.html");
				return;
			}

		} else {
			// Get active User
			String userID = getUserID(request);

			// actionFeedback;
			String actionFeedback = "";

			// Define some validator's objects
			TreeMap<String, String> fieldsMsg = null;
			TourValidator fields = null;
			String imageFileName = "";
//			ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
			File temp = new File(IMAGE_DIR + File.separator + "temp");
			try {
				List<FileItem> items = (List<FileItem>) request.getAttribute("items");

				// First get all string fields from request
				String tourName = items.get(1).getString();
				String fromPlace = items.get(2).getString();
				String toPlace = items.get(3).getString();
				String fromDate = items.get(4).getString();
				String toDate = items.get(5).getString();
				String price = items.get(6).getString();

				// Validate the tour
				fields = new TourValidator(tourName, fromPlace, toPlace, fromDate, toDate, price);
				fieldsMsg = fields.validateTour();

				if (!fields.isTourValid()) {
					actionFeedback += "Your tour has some invalid information. ";
				} else {
					String imageFileExtension = "";

					if (items.size() > 7) {
						items.get(7).write(temp);
						imageFileExtension = FileUtil.getImageFileType(temp);
						imageFileExtension = imageFileExtension.isBlank() ? "" : "." + imageFileExtension;
					}

					imageFileName = FileUtil.generateFilename(userID,
							items.size() > 7 && !imageFileExtension.equals("") ? items.get(7).getName() : "")
							+ imageFileExtension;

					TourDAO tourDAO = new TourDAO();

					long tourID = tourDAO.insertNewTour(fields.getTourDTO(imageFileName));

					String imageDirPath = IMAGE_DIR + File.separator + tourID;
					File imageDir = new File(imageDirPath);

					if (!imageDir.exists()) {
						imageDir.mkdir();
					}

					String imageFilePath = imageDirPath + File.separator + imageFileName;

					if (temp.exists()) {
						if (imageFileName.isBlank())
							actionFeedback += "Invalid image file. ";
						else
							new ImageUtil().writeResizeImage(temp, new File(imageFilePath));

						temp.delete();
					}

					// Redirect to successpage
					request.getSession().setAttribute("tourSearchingInfo", TourValidator.getDefaultTourSearchingInfo());
					response.sendRedirect("MainController?btnAction=searchTour");
					return;
				}
			} catch (FileUploadException e) {
				LOGGER.error("Servlet File Upload cannot parse the Request. ", e);
				actionFeedback += "Failed to upload your image. ";
			} catch (Exception e) {
				LOGGER.error("Failed to insert new tour. ", e);
				actionFeedback += "Failed to insert new tour. ";
			} finally {
				if (temp.exists()) {
					temp.delete();
				}
			}

			// Default dispatcher information
			request.setAttribute("actionFeedback", actionFeedback);
			request.setAttribute("fields", fields);
			request.setAttribute("fieldsMsg", fieldsMsg);

			request.getRequestDispatcher("tour-info-form.jsp").forward(request, response);
			return;
		}

	}

}
