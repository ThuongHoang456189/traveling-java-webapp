package thuonghth.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.imgscalr.Scalr;

public class ImageUtil {
	class ImageDimension {
		private int width;
		private int height;

		ImageDimension(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

	}

	private final ImageDimension BOUNDARY_IMAGE_DIMENSION = new ImageDimension(200, 200);

	private final String IMAGES_RELATIVE_PATH = "/images";

	private ImageDimension getFittedDimension(ImageDimension originalDimension, ImageDimension boundaryDimension) {
		int originalWidth = originalDimension.getWidth();
		int originalHeight = originalDimension.getHeight();
		int boundaryWidth = boundaryDimension.getWidth();
		int boundaryHeight = boundaryDimension.getHeight();
		double resultWidth = (double) boundaryWidth;
		double resultHeight = (double) resultWidth * originalHeight / originalWidth;
		if (resultHeight > boundaryHeight) {
			resultHeight = boundaryHeight;
			resultWidth = resultHeight * originalWidth / originalHeight;
		}
		resultWidth = Math.floor(resultWidth);
		resultHeight = Math.floor(resultHeight);
		resultWidth = resultWidth > 0 ? resultWidth : 1;
		resultHeight = resultHeight > 0 ? resultHeight : 1;
		return new ImageDimension((int) resultWidth, (int) resultHeight);
	}

	private BufferedImage resizeImage(BufferedImage originalImage, ImageDimension targetDimension) {
		return Scalr.resize(originalImage, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC, targetDimension.getWidth(),
				targetDimension.getHeight(), Scalr.OP_ANTIALIAS);
	}

	private ImageDimension getImageDimension(File imageFile) throws IOException {
		ImageInputStream imageInputStream = ImageIO.createImageInputStream(imageFile);
		Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(imageInputStream);
		if (!imageReaders.hasNext())
			return null;
		else {
			ImageReader imageReader = imageReaders.next();
			imageReader.setInput(imageInputStream);
			return new ImageDimension(imageReader.getWidth(0), imageReader.getHeight(0));
		}
	}

	public void writeResizeImage(File sourceFile, File targetFile) throws IOException {
		ImageDimension originalDimension = getImageDimension(sourceFile);
		BufferedImage originalImage = ImageIO.read(sourceFile);
		BufferedImage outputImage = resizeImage(originalImage,
				getFittedDimension(originalDimension, BOUNDARY_IMAGE_DIMENSION));
		ImageIO.write(outputImage, FileUtil.getImageFileType(sourceFile), targetFile);
	}

	public String getDisplayableImage(String contextPath, String imagesDir, String relativeImageFilePath) {
		String displayableImageFilePath = "";

		String fullPath = imagesDir + relativeImageFilePath;

		File file = new File(fullPath);

		if (file.exists() && file.isFile() && !FileUtil.getImageFileType(file).equals("")) {
			displayableImageFilePath = contextPath + IMAGES_RELATIVE_PATH + relativeImageFilePath;
		} else {
			displayableImageFilePath = contextPath + IMAGES_RELATIVE_PATH + "/" + MyConstants.DEFAULT_IMAGE_FILE_NAME;
		}

		return displayableImageFilePath;
	}

//	public String getDimensionInfo(Dimension dimension) {
//		return "Width: " + dimension.getWidth() + "; Height: " + dimension.getHeight();
//	}
//
//	public static void main(String[] args) {
//		ImageUtil imageUtil = new ImageUtil();
//		System.out.println(imageUtil
//				.getDimensionInfo(imageUtil.getFittedDimension(new Dimension(1, 10000), new Dimension(200, 200))));
//		System.out.println(imageUtil
//				.getDimensionInfo(imageUtil.getFittedDimension(new Dimension(20000, 10000), new Dimension(200, 200))));
//		System.out.println(imageUtil
//				.getDimensionInfo(imageUtil.getFittedDimension(new Dimension(50, 100), new Dimension(200, 200))));
//		System.out.println(imageUtil
//				.getDimensionInfo(imageUtil.getFittedDimension(new Dimension(100000, 1), new Dimension(200, 200))));
//	}

//	public static void main(String[] args) {
//		File sourceFile = new File("D:\\picture_backup_1\\default.png");
//		File targetFile = new File("D:\\FE_SEM5\\LAB_WEB\\TrialLabs\\lab.lab2.dream-traveling-0.0.1-SNAPSHOT\\src\\main\\webapp\\images\\default.png");
//		try {
//			new ImageUtil().writeResizeImage(sourceFile, targetFile);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("Hello");
//	}

//	public static void main(String[] args) {
//		File dir = new File(
//				"D:\\FE_SEM5\\LAB_WEB\\TrialLabs\\lab.lab2.dream-traveling-0.0.1-SNAPSHOT\\src\\main\\webapp\\images/default.png/");
//		System.out.println("La dir: " + dir.isFile());
//		String imagesDir = "D:\\FE_SEM5\\LAB_WEB\\TrialLabs\\lab.lab2.dream-traveling-0.0.1-SNAPSHOT\\src\\main\\webapp\\images";
//		System.out.println("----------------------------------");
//		String relativeImageFilePath = "/10024/";
//		System.out.println(new ImageUtil().getDisplayableImage(imagesDir, relativeImageFilePath));
//		System.out.println("----------------------------------");
//		relativeImageFilePath = "/10027/$31$16$fhyjQxPrJnQxMtoytw_iaXtvvHz73OMmhTQUeHPZqgM.png";
//		System.out.println(new ImageUtil().getDisplayableImage(imagesDir, relativeImageFilePath));
//		System.out.println("----------------------------------");
//		relativeImageFilePath = "/10022/$31$16$tjBIvGiX1tjUxSXUtbb_ydHxaY7jDqvN6Tkaw2TNndc";
//		System.out.println(new ImageUtil().getDisplayableImage(imagesDir, relativeImageFilePath));
//		System.out.println("----------------------------------");
//		relativeImageFilePath = "/1/nhatrang.png";
//		System.out.println(new ImageUtil().getDisplayableImage(imagesDir, relativeImageFilePath));
//		System.out.println("----------------------------------");
//		relativeImageFilePath = "/1/nhatrang.png";
//		System.out.println(new ImageUtil().getDisplayableImage(imagesDir, relativeImageFilePath));
//		System.out.println("----------------------------------");
//		relativeImageFilePath = "nhatrang.png";
//		System.out.println(new ImageUtil().getDisplayableImage(imagesDir, relativeImageFilePath));
//	}

}
