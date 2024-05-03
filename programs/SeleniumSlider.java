import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

public class SeleniumSlider {
  public static void getListOfNumbers(){
    WebDriver driver = new ChromeDriver();
    driver.get("https://seleniumbase.io/demo_page");
    WebElement ele = driver.findElement(By.cssSelector("#mySlider"));
    Actions action = new Actions(driver);
    action.dragAndDropBy(ele, -10, 0).build().perform();

    }

  public static void main(String[] a){
    SeleniumSlider.getListOfNumbers();
  }

}
