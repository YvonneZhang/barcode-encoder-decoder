/**
 * 版本一：只能识别EAN13图像
 */

package barcode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class EAN13ReaderV1 {
	enum color {
		white, black, gray
	};

	static String[] LEFT_TABLE = new String[] { "3211", "2221", "2122", "1411",
			"1132", "1231", "1114", "1312", "1213", "3112", "1123", "1222",
			"2212", "1141", "2311", "1321", "4111", "2131", "3121", "2113" };
	static String[] RIGHT_TABLE = new String[] { "3211", "2221", "2122",
			"1411", "1132", "1231", "1114", "1312", "1213", "3112" };
	static String[] TABLE = new String[] { "AAAAAA", "AABABB", "AABBAB",
			"AABBBA", "ABAABB", "ABBAAB", "ABBBAA", "ABABAB", "ABABBA",
			"ABBABA" };

	public static void main(String[] args) {
		String imgPath = "C:/Users/HP/Desktop/2.jpg";
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(imgPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int x = 0;
		int y = img.getHeight() / 2;
		int in;
		int width = img.getWidth();
		int pixelCount = 0;
		int base = 2;
		boolean change = false;
		color currentColor, preColor = getColor(img.getRGB(x, y));
		String value = "";
		String EAN13 = "";

		// 寻找起始的像素点位置
		for (in = 0; in < width; in++) { // 初始值
			if (getColor(img.getRGB(in, y)) == color.black) {
				break;
			}
		}

		// 生成value
		for (x = in; x < width; x++) {
			currentColor = getColor(img.getRGB(x, y));
			
			if (currentColor != color.gray) {
				change = (currentColor == preColor) ? false : true;
				if (!change) {
					pixelCount++;
				} else {
					value += (pixelCount / base);
					pixelCount = 1;
				}
				preColor = currentColor;
			} else
				continue;
		}

		// 存储左侧数据与右侧数据
		String[] left = new String[6];
		String[] right = new String[6];
		for (int i = 4; i < 28; i += 4) {
			left[(i / 4) - 1] = value.substring(i, i + 4);
		}
		for (int i = 33; i < 57; i += 4) {
			right[(i - 33) / 4] = value.substring(i, i + 4);
		}

		// 转换成EAN13
		String table = "";
		for (int i = 0; i < 6; i++) {// 转换左侧数据
			for (int j = 0; j < 20; j++) {
				if (left[i].equals(LEFT_TABLE[j])) {
					EAN13 += (j % 10);
					if (j < 10)
						table += "A"; // 位于A集合
					else
						table += "B";// 位于B集合
				}
			}
		}

		for (int i = 0; i < 6; i++) { // 转换右侧数据
			for (int j = 0; j < 10; j++) {
				if (right[i].equals(RIGHT_TABLE[j]))
					EAN13 += (j % 10);
			}
		}

		// 加上前置码
		for (int i = 0; i < 10; i++) {
			if (table.equals(TABLE[i])) {
				EAN13 = i + EAN13;
				break;
			}
		}

		// 校验
		if (computeCheckcode(EAN13) == Character.getNumericValue(EAN13
				.charAt(12)))
			System.out.print("解码结果：" + EAN13);
		else
			System.out.print("解码失败");
	}

	/**
	 * 根据灰度值判断该像素为黑色还是白色
	 */
	private static color getColor(int rgb) {
		int r = (rgb >> 16) & 255;
		int g = (rgb >> 8) & 255;
		int b = rgb & 255;
		double avg = 0.299 * r + 0.587 * g + 0.114 * b; // 计算灰度值
		if ((int) avg > 200)
			return color.white;// white
		else if ((int) avg < 50)
			return color.black;// black
		else
			return color.gray;// gray
	}

	/**
	 * 生成校验位
	 */
	private static int computeCheckcode(String str) {
		int sum_even = 0;// 偶数位之和
		int sum_odd = 0;// 奇数位之和

		for (int i = 0; i < 12; i++) {
			if (i % 2 == 0) {
				sum_odd += Character.getNumericValue(str.charAt(i));
			} else {
				sum_even += Character.getNumericValue(str.charAt(i));
			}
		}
		int checkcode = (10 - (sum_even * 3 + sum_odd) % 10) % 10;
		return checkcode;
	}
}
