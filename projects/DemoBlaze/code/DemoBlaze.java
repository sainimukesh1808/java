/*
DemoBlaze - Web FE Automation.
Introduction:
You have to implement the following Web automated checks over our DEMO ONLINE. SHOP: https://www.demoblaze.com/index.html.

	1. Customer navigation through product categories: Phones, Laptops and Monitors.
	2. Navigate to "Laptop" → "Sony vaio i5" and click on "Add to cart". Accept pop up confirmation.
	3. Navigate to "Laptop" → "Dell i7 8gb" and click on "Add to cart". Accept pop up confirmation.
	4. Navigate to "Cart" → Delete "Dell i7 8gb" from cart.
	5. Click on "Place order".
	6. Fill in all web form fields.
	7. Click on "Purchase".
	8. Capture and log purchase Id and Amount.
	9. Assert purchase amount equals expected.
	10. Click on "Ok".
*/
package TestNG;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DemoBlaze {
	public static WebDriver driver;
	public static Properties prop;
	public static long PAGE_LOAD_TIMEOUT = 20;
	public static long IMPLICIT_WAIT = 10;
	public static WebDriverWait wait;
	public DemoBlaze(){
		try {
			prop = new Properties();
			FileInputStream ip = new FileInputStream("D:\\CIT\\eclipse\\workspace\\SeleniumSession\\src\\TestNG\\data.properties");
			prop.load(ip);
		}catch(FileNotFoundException e) {
			e.printStackTrace();
			
		}catch(IOException e) {
			e.printStackTrace();
			
		}
		
	}
	
	@BeforeClass
	public static void beforeClass(){
		System.setProperty("webdriver.chrome.driver","C:\\Users\\msi26.DSONE\\Downloads\\chromedriver_win32\\chromedriver.exe");
		driver = new ChromeDriver();
		wait = new WebDriverWait(driver,30);
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().pageLoadTimeout(PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT, TimeUnit.SECONDS);
		
		}
	
	public String readProperty(String object_key){
		String object_value = prop.getProperty(object_key);
		return object_value;
	}
	
	public void addLaptop(String model){
		  driver.get(readProperty("url"));
		  driver.findElement(By.xpath(readProperty("laptop"))).click();
		  wait.until(ExpectedConditions.elementToBeClickable(By.xpath(readProperty(model))));
		  driver.findElement(By.xpath(readProperty(model))).click();
		  wait.until(ExpectedConditions.elementToBeClickable(By.xpath(readProperty("add_to_cart")))); 
		  driver.findElement(By.xpath(readProperty("add_to_cart"))).click();
		  wait.until(ExpectedConditions.alertIsPresent());
		  driver.switchTo().alert().accept();
	  }
	
	  @Test(priority = 1)
	  public void addSonyVaio() throws InterruptedException {
		  addLaptop("sony_vaio_i5");
	  }
	  @Test(priority = 2)
	  public void addDell() throws InterruptedException {
		  addLaptop("dell_i7_8gb");
	  }
	  @Test(priority = 3)
	  public void deleteDell() throws InterruptedException {
		  driver.navigate().to(readProperty("url"));
		  driver.findElement(By.xpath(readProperty("cart"))).click();
		  String dell_model = readProperty("dell_model");
		  String table = readProperty("table");
		  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(table))); 
		  String all_rows = table + "/tbody/tr";
		  List<WebElement> rows = driver.findElements(By.xpath(all_rows));
		  for(int row=0; row<rows.size(); row++){
			  String all_cols = all_rows + "[" + (row+1) + "]"+ "/td";
			  List<WebElement> cols = driver.findElements(By.xpath(all_cols));
			  for(int col=0; col<cols.size(); col++){
				  if(dell_model.equals(cols.get(col).getText())){
					  driver.findElement(By.xpath(all_cols + "/a" + "[1]")).click();  
					  break;
				  }
			  }
			  
		  }
	  }
	  
	  @Test(priority = 4)
	  public void placeOrder() throws InterruptedException {
		  Thread.sleep(5000);
		  wait.until(ExpectedConditions.elementToBeClickable(By.xpath(readProperty("place_order")))); 
		  driver.findElement(By.xpath(readProperty("place_order"))).click();
		  
	  }
	  
	  @Test(priority = 5)
	  public void fillForm() throws InterruptedException {
		  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(readProperty("name")))); 
		  driver.findElement(By.xpath(readProperty("name"))).sendKeys("Mukesh");
		  driver.findElement(By.xpath(readProperty("country"))).sendKeys("India");
		  driver.findElement(By.xpath(readProperty("city"))).sendKeys("Jaipur");
		  driver.findElement(By.xpath(readProperty("card"))).sendKeys("ICICI");
		  driver.findElement(By.xpath(readProperty("month"))).sendKeys("October");
		  driver.findElement(By.xpath(readProperty("year"))).sendKeys("2020");
		  driver.findElement(By.xpath(readProperty("purchase"))).click();
		  
	  }
	  
	  @Test(priority = 6)
	  public void purchaseDetails() throws InterruptedException {
		  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(readProperty("purchase_details")))); 
		  String purchase_detail = driver.findElement(By.xpath(readProperty("purchase_details"))).getText();
		  String details[] = purchase_detail.split("\\n");
		  for(String detail:details){
			  if(detail.contains("Id")){
				  String id[] = detail.split(" ");
				  System.out.println("id of order is " + id[id.length-1]);
			  }
			  if(detail.contains("Amount")){
				  String amount[] = detail.split(" ");
				  System.out.println("Amount of order is " + amount[amount.length-2] + " USD");
				  Assert.assertEquals(Integer.parseInt(amount[amount.length-2]), 790, "Purchase amount is not as per expected.");
			  }
			  
		  }
		  driver.findElement(By.xpath(readProperty("ok"))).click();
	  }
	  
	  @AfterClass
	  public void afterClass() {
		  driver.quit();
	  }

}







