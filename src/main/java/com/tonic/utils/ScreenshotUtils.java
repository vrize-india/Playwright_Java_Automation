package com.tonic.utils;

import com.tonic.driver.DriverManager;
import com.tonic.enums.ConfigProperties;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Base64;

/**
 * Screenshot Utils web mobile desktop
 * Author: Gaurav Purwar
 */
public class ScreenshotUtils {

	private ScreenshotUtils() {}

	public static String screenshotCapture() {
		WebDriver webdriver = null;
		webdriver = DriverManager.getDriver();
		webdriver = new Augmenter().augment(webdriver);
		try {
			if (PropertyBuilder.getPropValue(ConfigProperties.SNAPSHOT).equalsIgnoreCase("normal")) {
                return ((TakesScreenshot) webdriver).getScreenshotAs(OutputType.BASE64);

			} else {
				Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100))
						.takeScreenshot(webdriver);

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ImageIO.write(screenshot.getImage(), "PNG", out);

				byte[] bytes = out.toByteArray();
                return Base64.getEncoder().encodeToString(bytes);

			}
		} catch (Exception e) {
			Robot robot = null;
			try {
				robot = new Robot();
			} catch (AWTException e1) {
				e1.printStackTrace();
			}

			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

			Rectangle rectangle = new Rectangle(screenSize);

			BufferedImage source = robot.createScreenCapture(rectangle);
			String base64String = imgToBase64String(source, "png");
			try {
				webdriver.switchTo().alert().dismiss();
			} catch (Exception e1) {
				System.out.println("alert encountered");
			}
			return base64String;

		}

	}

	public static String imgToBase64String(final RenderedImage img, final String formatName) {
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (final OutputStream b64os = Base64.getEncoder().wrap(os)) {
			ImageIO.write(img, formatName, b64os);
		} catch (final IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
		return os.toString();
	}
}