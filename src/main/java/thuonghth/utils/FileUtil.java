package thuonghth.utils;

import java.io.File;
import java.time.Instant;
import java.util.EnumSet;

import org.apache.commons.io.filefilter.MagicNumberFileFilter;

public class FileUtil {
	public enum ImageSignatures {
		GIF("GIF format", "gif", new byte[] { (byte) 0x47, (byte) 0x49, (byte) 0x46, (byte) 0x38 }),
		JPEG("JPEG File Interchange Format", "jpg", new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF }),
		PNG("PNG format", "png", new byte[] { (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47 });

		private String description;
		private String extension;
		private byte[] magicNumber;

		private ImageSignatures(String description, String extension, byte[] magicNumber) {
			this.description = description;
			this.extension = extension;
			this.magicNumber = magicNumber;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getExtension() {
			return extension;
		}

		public void setExtension(String extension) {
			this.extension = extension;
		}

		public byte[] getMagicNumber() {
			return magicNumber;
		}

		public void setMagicNumber(byte[] magicNumber) {
			this.magicNumber = magicNumber;
		}

	}

//	public static boolean isImageFile(File file) {
//		MagicNumberFileFilter magicNumberFileFilter = null;
//		for (ImageSignatures signatures : EnumSet.allOf(ImageSignatures.class)) {
//			magicNumberFileFilter = new MagicNumberFileFilter(signatures.getMagicNumber());
//			if (magicNumberFileFilter.accept(file)) {
//				return true;
//			}
//		}
//		return false;
//	}

	public static String getImageFileType(File file) {
		MagicNumberFileFilter magicNumberFileFilter = null;
		for (ImageSignatures signatures : EnumSet.allOf(ImageSignatures.class)) {
			magicNumberFileFilter = new MagicNumberFileFilter(signatures.getMagicNumber());
			if (magicNumberFileFilter.accept(file)) {
				return signatures.getExtension();
			}
		}
		return "";
	}

	public static String generateFilename(String userID, String originalFilename) {
		return originalFilename.isBlank() ? ""
				: new PBKDF2PasswordHasher().hash(userID + Instant.now() + originalFilename);
	}

	public static void main(String[] args) {
		File file = new File(
				"D:\\FE_SEM5\\LAB_WEB\\TrialLabs\\lab.lab2.dream-traveling-0.0.1-SNAPSHOT\\src\\main\\webapp\\images/10022/$31$16$tjBIvGiX1tjUxSXUtbb_ydHxaY7jDqvN6Tkaw2TNndc");
		System.out.println("|" + FileUtil.getImageFileType(file) + "|");
		System.out.println(File.separator);
	}
}
