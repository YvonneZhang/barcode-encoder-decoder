package barcode;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class EAN13Creater {
	static int width = 300;
	static int height = 240;

	// 创建BufferedImage对象
	static BufferedImage bi = new BufferedImage(width, height,
			BufferedImage.TYPE_INT_RGB);

	// 获取Graphics2D
	static Graphics2D g2 = (Graphics2D) bi.getGraphics();

	public static void main(String[] args) throws Exception {

		System.out.println("请输入12位代码：");

		// 读取用户输入
		InputStreamReader is_reader = new InputStreamReader(System.in);
		String str = new BufferedReader(is_reader).readLine();

		if (str.length() != 12) {
			System.out.print("输入的代码不是12位！");
		} else {

			// 将字符串转换为二进制代码
			String EAN13code = EAN13.Get(str, width, height);
			
			// 生成校验位
			str += computeCheckcode(str);
			
			// 图像路径
			File file = new File("C:/Users/HP/Desktop/" + str + ".jpg");

			// 画出条形码
			drawEAN13Barcode(EAN13code);
			drawString(str);

			// 保存图像
			ImageIO.write(bi, "jpg", file);

			System.out.println("条形码图片已生成");
		}
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

	/**
	 * 画出黑色线条，宽度为2像素
	 */
	private static void drawBlackLine(int x, int y, int h) {
		g2.setPaint(Color.BLACK);
		g2.drawLine(x, y, x, y + h);
		g2.drawLine(x + 1, y, x + 1, y + h);
	}

	/**
	 * 画出白色线条，宽度为2像素
	 */
	private static void drawWhiteLine(int x, int y, int h) {
		g2.setPaint(Color.WHITE);
		g2.drawLine(x, y, x, y + h);
		g2.drawLine(x + 1, y, x + 1, y + h);
	}

	/**
	 * 根据二进制代码生成图像，“1” 画黑色线条，“0” 画白色线条
	 */
	private static void drawEAN13Barcode(String s) {

		// 生成图像，设置背景色为白色
		g2.setBackground(Color.WHITE);
		g2.clearRect(0, 0, width, height);

		// 画出条形码
		int location = 36;
		int bin;
		for (int i = 0; i < 113; i++) {
			bin = s.charAt(i) - '0';
			switch (bin) {
			case 0:
				drawWhiteLine(location, 60, 100);
				break;
			case 1:
				drawBlackLine(location, 60, 100);
				break;
			}
			location += 2;
		}

		// 加长警戒条
		drawBlackLine(58, 160, 10);
		drawBlackLine(62, 160, 10);
		drawBlackLine(150, 160, 10);
		drawBlackLine(154, 160, 10);
		drawBlackLine(242, 160, 10);
		drawBlackLine(246, 160, 10);
	}

	/**
	 * 在条形码下方添加商品代码
	 */
	private static void drawString(String str) {

		// 处理字符串
		String s1 = str.substring(0, 1);
		String s2 = str.substring(1, 7);
		String s3 = str.substring(7, 13);
		String newStr = s1 + "  " + s2 + "  " + s3;

		// 画出商品代码
		Font font = new Font("Dialog", Font.BOLD, 24);
		g2.setFont(font);
		g2.setPaint(Color.BLACK);
		g2.drawString(newStr, 40, 182);
	}

}
