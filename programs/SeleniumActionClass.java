import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import java.io.File;
import java.io.IOException;

public class SeleniumActionClass {
  public static void main(String[] a) throws IOException {
    WebDriver driver = new ChromeDriver();
    WebElement elementSource = driver.findElement(By.xpath("test"));
    WebElement elementTarget = driver.findElement(By.xpath("test"));
    Actions actions = new Actions(driver);
    actions.dragAndDrop(elementSource, elementTarget).build().perform();

    actions.moveToElement(elementTarget).contextClick().build().perform();

    actions.moveToElement(elementTarget).doubleClick().build().perform();

    //screenshot
    File ss = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    FileUtils.copyFile(ss, new File("path/file.png"));

    actions.keyDown(Keys.CONTROL).sendKeys(elementTarget, "test").keyUp(Keys.CONTROL).build().perform();

    //file download
//    ChromeOptions options = new ChromeOptions();
//    HashMap<String, Object> chromePrefs = new HashMap<>();
//    chromePrefs.put("download.default_directory", "path/to/download/directory");
//    options.setExperimentalOption("prefs", chromePrefs);
//    WebDriver driver = new ChromeDriver(options);

  }

}
