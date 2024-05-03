import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitForClickableElement {

  public static void main(String[] args) {
    // Set the path to the ChromeDriver executable
    System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");

    // Create a new instance of the ChromeDriver
    WebDriver driver = new ChromeDriver();

    // Navigate to the webpage
    driver.get("https://example.com");

    // Example: Wait for a button with id "myButton" to be clickable
    WebElement myButton = driver.findElement(By.id("myButton"));

    // Create WebDriverWait with a timeout of 10 seconds
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    // Wait until the element is clickable
    wait.until(ExpectedConditions.elementToBeClickable(myButton));

    // Perform actions on the clickable element
    myButton.click();

    // Close the browser
    driver.quit();
  }
}
