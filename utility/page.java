package pages;

import static org.testng.Assert.assertNotNull;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import custom.strings.Locale_en_US;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.AssertJUnit;
import pages.account.AccountPage;
import pages.account.AutoDelegationSettingsPage;
import pages.account.MyAccessTokensPage;
import pages.account.MySignaturePage;
import pages.account.RegisterAccountPage;
import pages.account.SecuritySettingsPage;
import pages.account.SendProgressPage;
import pages.account.SendSettingsPage;
import pages.account.api.APIInformationPage;
import pages.account.api.ApplicationViewPage;
import pages.account.api.CreateApplicationPage;
import pages.account.api.CreateIntegrationKeyPage;
import pages.account.api.DisableAppPage;
import pages.account.api.EditOAuthConfigurationPage;
import pages.account.api.ViewAPIKeyPage;
import pages.account.api.ViewIntegrationKeyPage;
import pages.account.usersAndGroups.ViewUserPage;
import pages.accountSettingsPage.AccessTokenPage;
import pages.accountSettingsPage.AccountSettingsPage;
import pages.accountSettingsPage.api.ManageApplicationsPage;
import pages.admin.AdminPage;
import pages.authoringJS.AuthoringJSPage;
import pages.authoringJS.ReusableDocumentJSPage;
import pages.esign.ESignPage;
import pages.home.HomePage;
import pages.homeJS.HomeJSPage;
import pages.login.companyNameSignOnPage;
import pages.login.LoginPage;
import pages.login.LoginTermsOfUsePage;
import pages.manage.ManagePage;
import pages.oauth.OAuthConfirmAccessPage;
import pages.oauth.OAuthDemoPage;
import pages.oauth.OAuthDocPage;
import pages.profile.MyProfilePage;
import pages.reports.ReportsPage;
import pages.send.MegaSignPage;
import pages.send.OnlyISignPage;
import pages.send.PostSendPage;
import pages.send.ProcessSend;
import pages.sendJS.CreateLibraryTemplateJSPage;
import pages.sendJS.NewFormPage;
import pages.sendJS.SendJSPage;
import pages.swagger.REST.V1VersionPage;
import pages.swagger.REST.V2VersionPage;
import pages.userMessage.UserMessagePage;
import pages.widget.WidgetCreatePage;
import service.PropertiesService;
import service.ShardService;
import utils.BrowserUtils;
import utils.DateTimeUtils;
import utils.Env;
import utils.StringUtils;
import utils.Time;
import utils.TimeDuration;
import vo.RegisteredUser;
import vo.WebClientData;
import webdriver.DriverFactory;

/**
 * Page object
 */
public abstract class Page {
  @FindBy(className = "alert")
  protected WebElement alertLocator;

  @FindBy(className = "alert-messages")
  protected WebElement alertMessagesLocator;

  @FindBy(className = "alert-message")
  protected WebElement alertMessageLocator;

  @FindBy(className = "modal-body")
  protected WebElement modalbodyLocator;

  @FindBy(id = "alert-red-box")
  protected WebElement alertRedLocator;

  @FindBy(id = "global-error-text")
  protected WebElement globalErrorLocator;

  @FindBy(className = "alert-danger")
  protected WebElement alertDangerLocator;

  @FindBy(xpath = "//h1[@id='title']")
  private WebElement acrsTitle;

  @FindBy(xpath= "//button[@data-testid='cta-button']")
  private WebElement acrsCloseButton;

  protected final String toastMessageCssSelector = "[class*='spectrum-Toast-content']";
  @FindBy(css = toastMessageCssSelector)
  protected WebElement toastMessageLocator;

  protected final String errorMessageCssSelector = ".spectrum-Toast--error";
  @FindBy(css = errorMessageCssSelector)
  protected WebElement errorMessageLocator;

  @FindAll(@FindBy(css = errorMessageCssSelector))
  private List<WebElement> toastErrorLocators;

  @FindBy(id = "global-error-text")
  private List<WebElement> globalErrorLocators;

  private By spinnerByLocator = new By.ByClassName("spinner");
  private By actionProgressbarByLocator = new By.ById("progressBar-actionItem");

  private final String spectrumSpinnerCss = "[class*='spectrum-CircleLoader'][role='progressbar']";
  private By spectrumSpinnerByLocator = By.cssSelector(spectrumSpinnerCss);
  @FindBy (css = spectrumSpinnerCss)
  private WebElement spectrumSpinnerLocator;

  protected WebDriver driver = null;
  protected static final Log log = LogFactory.getLog("seleniumTestSuite");
  public static final String MODERN_ESIGN_TEST_CASE_IDENTIFIER = "modernESignExperience-";
  protected static String context = "";
  protected static URL baseURL;

  protected static Map<String, Class> pages;
  protected static final int PAGE_WAIT_TIMEOUT = Env.isGovCloud() ? 180: 120;
  protected static final Duration PAGE_WAIT_TIMEOUT_DURATION = Env.isGovCloud() ? TimeDuration.SUPERLONG : TimeDuration.MAXLONG;
  // Error locator id for the Send Page V2
  protected final String ERROR_ID = "EchoSignErrorMessage";
  // Error locator id for SendJS
  protected final String ERROR_CSS_SELECTOR_SENDJS = "div.alert.alert-danger.fade.in";

  private static ObjectMapper mapper = new ObjectMapper();

  protected Page(WebDriver driver) {
    if (driver == null)
      throw new AssertionError("driver instance is null.");
    this.driver = driver;
    PageFactory.initElements(driver, this);
    //account/audit page and document/cp are displaying pdf file, , not a page with Page Title
    if (!this.driver.getCurrentUrl().contains("about:blank") && !this.driver.getCurrentUrl().contains("data:,")
            && !this.driver.getCurrentUrl().contains("audit") && !this.driver.getCurrentUrl().contains("document/cp")
            && !this.driver.getCurrentUrl().contains("/uidSign") && !this.driver.getCurrentUrl().contains("acrobat")
    ) {
      validateEchosignIsAvailable();
    }
  }

  /*
   * Initialize class variables.
   */
  static {
    try {
      // Initial class context.
      setBaseURL(ShardService.getHomeShardURL());
    }
    catch (Exception e) {
      log.error("setBaseURL", e);
    }
    // Initial class pages.
    initPages();
  }


  /**
   * Builds up a path segment given an optional context defined in properties and the partialPath.
   *
   * @param partialPath
   * @return String A context prefixed path
   */
  static private String buildContextPath(String partialPath) {

    return getContext() + partialPath;
  }

  /**
   * Returns the URL based on the local.properties file
   */
  private static URL getBaseURL() {
    log.trace("[ENV] baseUrl=" + baseURL);
    return baseURL;
  }

  /**
   * Returns the nickname for the current environment being tested, such as: preview, stage, or prod
   *
   * @return {String}
   */
  public static String getEnv() {
    return DriverFactory.properties.getEnv();
  }

  public static String getContext() {
    return context;
  }

  /**
   * Returns the page URL to execute for the echosign product application.
   */
  public static String getEchoSignURL(String path) {
    return getURL(getBaseURL(), path);
  }

  /**
   * @param baseURL
   * @param path
   * @return String url value based on baseURL and path.
   */
  public static String getURL(URL baseURL, String path) {
    URL relativeURL = null;
    try {
      relativeURL = new URL(baseURL, getContext() + path);
    }
    catch (MalformedURLException e) {
      log.error("----> Exception in getURL()!", e);
    }
    log.trace("[ENV] setup URL: " + relativeURL.toString());
    return relativeURL.toString();
  }

  /**
   * Extract domain name part of the URL
   *
   * @param url as string
   * @return domain name as string
   */
  public static String getDomainName(String url) {
    String domain = null;
    try {
      URI uri = new URI(url);
      domain = uri.getAuthority();  // get the host + the port (if any)
    }
    catch (URISyntaxException e) {
      log.error("Exception during getDomainName(): " + e.getMessage());
    }
    finally {
      log.debug("getDomainName() returned [" + domain + "]");
    }
    return domain;
  }

  public static String getActualBaseURL(String url) {
    return "https://" + getDomainName(url);
  }

  /**
   * Initialize pages map.
   */
  static private void initPages() {
    pages = new HashMap<String, Class>();
    pages.put(buildContextPath(PageUrlPaths.MYACCESSTOKENS_PAGE), AccessTokenPage.class);
    pages.put(buildContextPath(PageUrlPaths.ACCOUNT), AccountPage.class);
    pages.put(buildContextPath(PageUrlPaths.ACCOUNT_SETTING_PAGE), AccountSettingsPage.class);
    pages.put(buildContextPath(PageUrlPaths.MYACCESSTOKENS_PAGE), AccessTokenPage.class);
    pages.put(buildContextPath(PageUrlPaths.ADMIN), AdminPage.class);
    pages.put(buildContextPath(PageUrlPaths.companyNameLOGIN), companyNameSignOnPage.class);
    pages.put(buildContextPath(PageUrlPaths.API_INFORMATION), APIInformationPage.class);
    pages.put(buildContextPath(PageUrlPaths.APPLICATION_VIEW), ApplicationViewPage.class);
    pages.put(buildContextPath(PageUrlPaths.AUTHOR), AuthoringJSPage.class);
    pages.put(buildContextPath(PageUrlPaths.AUTO_DELEGATION_SETTINGS), AutoDelegationSettingsPage.class);
    pages.put(buildContextPath(PageUrlPaths.CONFIGURE_OAUTH), EditOAuthConfigurationPage.class);
    pages.put(buildContextPath(PageUrlPaths.CREATE_APPLICATION), CreateApplicationPage.class);
    pages.put(buildContextPath(PageUrlPaths.CREATE_INTEGRATION_KEY), CreateIntegrationKeyPage.class);
    pages.put(buildContextPath(PageUrlPaths.DISABLE_APPLICATION), DisableAppPage.class);
    pages.put(buildContextPath(PageUrlPaths.ESIGN), ESignPage.class);
    pages.put(buildContextPath(PageUrlPaths.HOME), HomePage.class);
    pages.put(buildContextPath(PageUrlPaths.HOMEJS), HomeJSPage.class);
    pages.put(buildContextPath(PageUrlPaths.LOGIN), LoginPage.class);
    pages.put(buildContextPath(PageUrlPaths.LOGIN_NOPASSWORD), LoginPage.class);
    pages.put(buildContextPath(PageUrlPaths.LOGIN_companyName_ID), companyNameSignOnPage.class);
    pages.put(buildContextPath(PageUrlPaths.LOGIN_companyName_SSO), companyNameSignOnPage.class);
    pages.put(buildContextPath(PageUrlPaths.LOGIN_companyName_TOS), LoginTermsOfUsePage.class);
    pages.put(buildContextPath(PageUrlPaths.MANAGE), ManagePage.class);
    pages.put(buildContextPath(PageUrlPaths.MANAGE_APPLICATIONS), ManageApplicationsPage.class);
    pages.put(buildContextPath(PageUrlPaths.MEGASIGN), MegaSignPage.class);
    pages.put(buildContextPath(PageUrlPaths.MYACCESSTOKENS), MyAccessTokensPage.class);
    pages.put(buildContextPath(PageUrlPaths.MYPROFILE), MyProfilePage.class);
    pages.put(buildContextPath(PageUrlPaths.MY_SIGNATURE), MySignaturePage.class);
    pages.put(buildContextPath(PageUrlPaths.NEWFORM), NewFormPage.class);
    pages.put(buildContextPath(PageUrlPaths.OAUTHCONFIRMACCESS), OAuthConfirmAccessPage.class);
    pages.put(buildContextPath(PageUrlPaths.OAUTHDEMO), OAuthDemoPage.class);
    pages.put(buildContextPath(PageUrlPaths.OAUTHDOC), OAuthDocPage.class);
    pages.put(buildContextPath(PageUrlPaths.ONLYISIGN), OnlyISignPage.class);
    pages.put(buildContextPath(PageUrlPaths.POSTSEND), PostSendPage.class);
    pages.put(buildContextPath(PageUrlPaths.PROCESS_SEND), ProcessSend.class);
    pages.put(buildContextPath(PageUrlPaths.REGISTER_ACCOUNT), RegisterAccountPage.class);
    pages.put(buildContextPath(PageUrlPaths.REPORTS), ReportsPage.class);
    pages.put(buildContextPath(PageUrlPaths.REUSABLE_DOCUMENT), ReusableDocumentJSPage.class);
    pages.put(buildContextPath(PageUrlPaths.SECURITY_SETTINGS), SecuritySettingsPage.class);
    pages.put(buildContextPath(PageUrlPaths.SEND), SendJSPage.class);
    pages.put(buildContextPath(PageUrlPaths.SEND_PROGRESS), SendProgressPage.class);
    pages.put(buildContextPath(PageUrlPaths.SEND_SETTINGS), SendSettingsPage.class);
    pages.put(buildContextPath(PageUrlPaths.SWAGGER_REST_VERSION_LATEST), V2VersionPage.class);
    pages.put(buildContextPath(PageUrlPaths.SWAGGER_REST_VERSION_V1), V1VersionPage.class);
    pages.put(buildContextPath(PageUrlPaths.TEMPLATELIBRARY), CreateLibraryTemplateJSPage.class);
    pages.put(buildContextPath(PageUrlPaths.USER_MSG), UserMessagePage.class);
    pages.put(buildContextPath(PageUrlPaths.VIEW_AGREEMENT), ViewAPIKeyPage.class);
    pages.put(buildContextPath(PageUrlPaths.VIEW_API_KEY), ViewAPIKeyPage.class);
    pages.put(buildContextPath(PageUrlPaths.VIEW_INTEGRATION_KEY), ViewIntegrationKeyPage.class);
    pages.put(buildContextPath(PageUrlPaths.VIEW_USER), ViewUserPage.class);
    pages.put(buildContextPath(PageUrlPaths.WIDGET_CREATE), WidgetCreatePage.class);
    pages.put(buildContextPath(PageUrlPaths.SSO_VERIFICATION), SSOVerificationPage.class);

  }

  public static void setBaseURL(URL baseURL) {
    Page.baseURL = baseURL;
  }

  protected static void setContext(String value) {

    if (value == null || value.isEmpty()) {
      context = "";
    }
    else {
      if (value.startsWith("/") == false) {
        context = "/";
      }
      context += value;
    }
  }

  /**
   * Set a custom URL for example http://localhost:8443/echosign/public/login
   *
   * @param protocol ( ex: http )
   * @param host     ( ex: localhost )
   * @param port     ( ex: 8443 )
   * @param context  ( ex: echosign )
   * @param path     ( ex: /public/login )
   * @return the complete URL: http://localhost:8443/echosign/public/login
   */
  public static String setRelativeURL(String protocol, String host, int port, String context, String path) {
    URL relativeURL = null;
    try {

      if (context.length() > 0) {
        // (Re)Defining context here for all.
        setContext(context);
      }
      relativeURL = new URL(protocol, host, port, context + path);
    }
    catch (MalformedURLException e) {
      log.error("----> Exception in setRelativeURL()!", e);
    }

    log.trace("[ENV] setup URL: " + relativeURL.toString());
    return relativeURL.toString();
  }

  public Object execScript(String javascript, WebElement element) {
    return ((JavascriptExecutor) driver).executeScript(javascript, element);
  }

  public Object execScript(String javascript) {
    return ((JavascriptExecutor) driver).executeScript(javascript);
  }

  /**
   * Clicks on WebElement using Selenium click method; then uses JavaScript function to click in case it throws an exception.
   * If a "cannot determine loading status" exception error is returned, it needs to be handled in test.
   *
   * @param locator
   * @param timeOut
   */
  public String click(WebElement locator, int timeOut) {
    log.debug("click()");
    String errorMsg = null;
    try {
      waitUntilClickable(timeOut, locator);
      locator.click();
    }
    catch (Exception e) {
      log.debug(e.getMessage(), e);
      errorMsg = e == null ? null : e.getMessage();
      if (errorMsg != null && errorMsg.contains("timeout: cannot determine loading status")) {
        return errorMsg;
      }
      else {
        this.scrollIntoView(locator);
        execScript("arguments[0].click();", locator);
      }
    }
    return errorMsg;
  }

  /**
   * Click on the given locator and time, also check with the error message it should get after the click
   *
   * @param locator          locator to click on
   * @param timeOut          time to wait for the click
   * @param expectedErrorMsg expected message after the click
   * @return the message displayed after the click
   */
  public String click(WebElement locator, int timeOut, String expectedErrorMsg) {
    String error = click(locator, timeOut);
    String log = "Click on " + locator.toString() + " get error [" + error + "], but expected is [" + expectedErrorMsg + "]";
    if (expectedErrorMsg == null) {
      assert (error == null) : log;
    }
    else {
      assert (error.contains(expectedErrorMsg)) : log;
    }
    return error;
  }


  /**
   * Clicks on By element
   *
   * @param locator
   * @param timeOut
   */
  public void click(By locator, int timeOut) {
    log.debug("click()");
    waitUntilClickable(timeOut, locator);
    driver.findElement(locator).click();
  }

  /**
   * Clicks Spectrum locator method
   *
   * @return [Action] for clicking the locator
   */
  protected void actionsClick(WebElement locator) {
    Actions action = new Actions(driver);
    action.moveToElement(locator).click().build().perform();
  }

  /**
   * Uses JavaScript function to click on WebElement, since it has proven as most reliable.
   * In case the function throws an exception, the method tries to click on element using standard Selenium click method.
   *
   * @param locator
   */
  public void clickOnLocator(WebElement locator) {
    log.debug("clickOnLocator()");
    waitUntilVisible(Time.LONGEST, locator);
    waitUntilClickable(Time.SHORTER, locator);
    try {
      String jsCode = "var evObj = new MouseEvent('click', {bubbles: true, cancelable: true, view: window});";
      jsCode += " arguments[0].dispatchEvent(evObj);";
      execScript(jsCode, locator);
    }
    catch (Exception ex) {
      log.warn("JavaScript click function has thrown exception: " + ex.getMessage());
      log.debug("Trying again with selenium click method.");
      locator.click();
    }
  }

  public Object setAttribute(WebElement element, String attributeName, String attributeValue) {
    return ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", element, attributeName, attributeValue);
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  private <T extends Page> T getPage(Class<T> type) {
    T page = null;
    try {
      page = type.getDeclaredConstructor(WebDriver.class).newInstance(driver);
    }
    catch (InstantiationException e) {
      log.error("----> InstantiationException in getPage()!", e);
    }
    catch (IllegalAccessException e) {
      log.error("----> IllegalAccessException in getPage()!", e);
    }
    catch (InvocationTargetException e) {
      log.error("----> InvocationTargetException in getPage()!", e);
    }
    catch (NoSuchMethodException e) {
      log.error("----> NoSuchMethodException in getPage()!", e);
    }
    return page;
  }

  /**
   * @return A TestNg Page that has been mapped to the current Url.
   */
  public <T extends Page> Page getPageForCurrentUrl() {

    URL url = null;
    try {
      url = new URL(getCurrentUrl());
    }
    catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      log.error("----> Exception in getPageForCurrentUrl()!", e);
      return null;
    }
    // getPath() returns just path portion of url, but jsessionid may still be present..
    String path = url.getPath();
    int i = path.indexOf(";jsessionid");
    if (i > 0) {
      path = path.substring(0, i);
    }
    // Added due to new URL format related to New AccountAdmin pages
    String newAccountAdminDelimiter = "#";
    String urlText = url.toString();
    if (urlText.contains(newAccountAdminDelimiter)) {

      String[] bits = urlText.split(newAccountAdminDelimiter);
      if (bits.length > 1) {
        String urlParameters = bits[1];
        path = path + newAccountAdminDelimiter + urlParameters;
      }
      else {
        log.warn("----> getPageForCurrentUrl() unexpected URL: " + urlText);
      }
    }
    Class pageType = pages.get(path);

    assert pageType != null : "getPageForCurrentUrl: No Page found for path [" + path + "] URL: " + urlText;
    log.debug("getPageForCurrentUrl: " + pageType.getName() + " for path: " + path);
    return getPage(pageType);
  }

  /**
   * A more tolerant version that waits for webDriver to more convincingly change pages prior to
   * determining and returning the correlating page wrapper for the new url.
   *
   * @param srcElement A "clickable" object that will change the page.
   * @return A TestNg Page that has been mapped to the current Url.
   */
  public <T extends Page> Page getPageForCurrentUrlUsingWebElement(WebElement srcElement) {
    return getPageForCurrentUrlUsingWebElement(srcElement, null);
  }

  /**
   * A more tolerant version that waits for webDriver to more convincingly change pages prior to
   * determining and returning the correlating page wrapper for the new url.
   *
   * @param srcElement A "clickable" object that will change the page.
   * @return A TestNg Page that has been mapped to the current Url.
   */
  public <T extends Page> Page getPageForCurrentUrlUsingWebElement(WebElement srcElement, Function<WebDriver, Boolean> condition) {

    // Grab current to compare while driver performs the page change.
    String lastKnownUrl = driver.getCurrentUrl();
    // Then start page load using srcElement.
    waitUntilClickable(Time.LONG, srcElement);
    execScript("arguments[0].click();", srcElement);

    // This may take time (more so on IE), so wait until the driver has moved away from the last known url before
    // attempting to narrow the url down to a page wrapper.
    // Ignore staleElement exceptions which may occur within the timing of this page switch-over.
    try {
      (new WebDriverWait(driver, PAGE_WAIT_TIMEOUT_DURATION)).until((condition != null) ? condition : urlHasChanged(lastKnownUrl));
    }
    catch (TimeoutException e) {
      // There may be an companyName Sign error, which may NOT change the page: assert specifically for this.
      AssertJUnit.assertFalse("Hit an companyName Sign server processing error; page wasn't changed", isElementPresent(By.id(ERROR_ID)));
      validateEchosignIsAvailable();

      // IE, sometimes there's a focus issue on the click.
      // This recovery retry solves for IE.
      try {
        execScript("arguments[0].click();", srcElement);
      }
      catch (StaleElementReferenceException e2) {
        log.debug("Caught a staleElement ex during page switch, continuing..");
        if (hasUrlChanged(lastKnownUrl)) {
          log.debug("Stale and page HAS changed.");
          return getPageForCurrentUrl();
        }
      }
      log.debug("Timed-out waiting for page switch to happen from: " + lastKnownUrl + ", trying a click once again.");
      try {
        (new WebDriverWait(driver, PAGE_WAIT_TIMEOUT_DURATION)).until((condition != null) ? condition : urlHasChanged(lastKnownUrl));
      }
      catch (TimeoutException e1) {
        // Provide a clearer error message.
        AssertJUnit.fail("Timed out waiting for page to change from: "
                                 + lastKnownUrl + ", currently on: " + getCurrentUrl()
                                 + ", " + e1.getMessage());
      }
    }
    // Now that driver has changed pages, execute normally.

    return getPageForCurrentUrl();
  }

  /**
   * Implements predicate, allows for passing a parameter to be used as part of the
   * apply() method override.
   *
   * @param lastUrl the last known url prior to the page changing event has occurred.
   * @return true if the page has changed (old url vs current url)
   */
  public Function<WebDriver, Boolean> urlHasChanged(final String lastUrl) {
    final String url = lastUrl;
    return new Function<WebDriver, Boolean>() {
      @Override
      public Boolean apply(WebDriver arg0) {
        return hasUrlChanged(url);
      }
    };
  }

  /**
   * @param url
   * @return true if the driver's current url is different from the one provided.
   */
  protected boolean hasUrlChanged(String url) {
    String currentUrl = getCurrentUrl();
    if (currentUrl == null || currentUrl.equals("")) {
      log.debug("Wow, webDriver returned an empty getCurrentUrl() result, treating as false");
      return false;
    }
    boolean changed = !currentUrl.equals(url);

    if (changed) {
      if (currentUrl.contains("shardRedirect")) {
        return false;
      }
      log.debug("hasUrlChanged from: " + url + ", to: " + currentUrl);
      log.debug("hasUrlChanged: " + changed);
    }
    return changed;
  }

  /**
   * Implements predicate, allows for passing a parameter to be used as part of the apply() method override.
   *
   * @param expectedPath - expected url after the page changing event has occurred.
   * @return true if the page has changed to expected url
   */
  protected Function<WebDriver, Boolean> urlHasChangedToSpecifiedUrl(String expectedPath) {
    final String expPath = expectedPath;
    return new Function<WebDriver, Boolean>() {

      @Override
      public Boolean apply(WebDriver arg0) {
        return hasUrlChangedToSpecifiedUrl(expPath);
      }
    };
  }

  /**
   * @param path
   * @return true if the driver's current path is equal to the one provided.
   */
  protected boolean hasUrlChangedToSpecifiedUrl(String path) {
    log.debug("hasUrlChangedToSpecifiedUrl(" + path + ")");
    String currentUrl = getCurrentUrl();
    if (currentUrl == null || currentUrl.equals("")) {
      log.debug("Wow, webDriver returned an empty getCurrentUrl() result, treating as false");
      return false;
    }
    URL url = null;
    try {
      url = new URL(currentUrl);
    }
    catch (MalformedURLException e) {
      log.error("----> Exception in hasUrlChangedToSpecifiedUrl()!", e);
      return false;
    }
    String currentPath = url.getPath();
    int i = path.indexOf(";jsessionid");
    if (i > 0) {
      path = path.substring(0, i);
    }
    boolean equals = currentPath.equals(path);
    log.debug("hasUrlChangedToSpecifiedUrl(" + path + "): " + equals);
    if (!equals) {
      log.debug("Actual Url: " + currentPath);
    }
    return equals;
  }

  public String getPageSource() {
    return driver.getPageSource();
  }

  public String getTextByLocator(WebElement locator) {
    log.debug("getTextByLocator()");
    waitUntilVisible(Time.LONG, locator);
    return locator.getText();
  }

  public String getTitle() {
    // Wait for until the DOM is ready checking every 500ms for a total wait of up to 1 minute, ensure the page is loaded.
    waitUntilPageReady(Time.LONG * 4);
    String title = "If you are seeing this instead of actual page title, then there was an exception while fetching page title.";
    try {
      title = driver.getTitle();
    }
    catch (Throwable ex) {
      assert false : "Exception occurred while fetching page title. " + ex.getMessage();
    }
    return title;
  }

  // method to interact with browser/tab change
  public String getWindowHandle() {
    return driver.getWindowHandle();
  }

  public Set<String> getWindowHandles() {
    return driver.getWindowHandles();
  }

  public ArrayList getWindowHandlesAsArrayList() {
    return new ArrayList<String>(driver.getWindowHandles());
  }

  public Boolean isUserAgentExplorer() {
    return BrowserUtils.isUserAgentExplorer(); // todo: this is dirty, the page or driver should be aware of its type
  }

  public Page switchWindow(String tabType, String tabValue) {
    if (tabType == "oldTab") {
      // close this tab and focus back to old tab
      driver.close();
    }
    switchWindow(tabValue);
    return this;
  }

  public Page switchWindow(String windowHandle) {
    log.debug("Switching Window Handle (Tab) to [" + windowHandle + "]");
    driver.switchTo().window((windowHandle));
    return this;
  }

  /**
   * Switch from current window to another window.
   * With IE driver, the order in which the windows appear in the collection is not guaranteed,
   * so switching window using index is not reliable.
   * This method works when there are no more than 2 windows, but might not work as expected if there are more windows.
   * It just switches to the next window that is not the same as the provided window handle.
   *
   * @param currentWindowHandle
   * @param expectMultipleWindows (set to false when switching back to original window after closing a new window)
   * @return Page
   */
  public Page switchToOtherWindow(String currentWindowHandle, boolean expectMultipleWindows) {
    log.debug("switchToOtherWindow()");
    DateTimeUtils.sleepMilliSecondsAsLastResortBecauseNothingElseWorks(500);
    if (expectMultipleWindows) {
      //Make sure new window is opened
      assert (areMultipleWindowsOpen()) : "There is only 1 window open. Expected multiple.";
    }

    Set<String> handles = driver.getWindowHandles();
    boolean switched = false;
    for (String handle : handles) {
      if (!handle.equals(currentWindowHandle)) {
        switchWindow(handle);
        switched = true;
        break;
      }
    }
    assert (switched) : "Switching window failed. There is no other window to switch to.";
    return this;
  }

  /**
   * Switch from current window to another window.
   *
   * @param currentWindowHandle
   * @return Page
   */
  public Page switchToOtherWindow(String currentWindowHandle) {
    return switchToOtherWindow(currentWindowHandle, true);
  }

  /**
   * @return true if there are more than 1 window
   */
  public boolean areMultipleWindowsOpen() {
    return areMultipleWindowsOpen(2);
  }

  /**
   * Checks if there are more than 1 window open
   *
   * @param iTime Max time in seconds to wait for more than 1 window open
   * @return {boolean] true if there are more than 1 window
   */
  public boolean areMultipleWindowsOpen(int iTime) {
    log.debug("areMultipleWindowsOpen(" + iTime + ")");
    int i = 0;
    while (driver.getWindowHandles().size() == 1 && i < iTime * 2) {
      i++;
      DateTimeUtils.sleepMilliSecondsAsLastResortBecauseNothingElseWorks(500);
    }
    return driver.getWindowHandles().size() > 1;
  }

  /**
   * Waits for window(s) to close until only 1 is open
   */
  public void waitForWindowClose(String errorMessage) {
    log.debug("waitForWindowClose");
    int i = 0;
    while (areMultipleWindowsOpen() && i < Time.SHORT) {
      DateTimeUtils.sleepMilliSecondsAsLastResortBecauseNothingElseWorks(500);
      i++;
    }
    log.debug("Verify only 1 window is open");
    if (areMultipleWindowsOpen()) {
      if (errorMessage == null) {
        errorMessage = "More than 1 window is open";
      }
      throw new AssertionError(errorMessage);
    }
  }

  /**
   * This method checks whether Alert exists, and returns its text message.
   *
   * @return Text message shown in alert
   */
  public String getAlertMessage() {
    log.debug("getAlertMessage()");
    assert isAlertPresent() : "Alert box is NOT present";
    return driver.switchTo().alert().getText();
  }

  /**
   * This method accepts the alert
   */
  public void acceptAlert() {
    log.debug("acceptAlert()");
    assert isAlertPresent() : "Alert box is NOT present";
    driver.switchTo().alert().accept();
  }


  public boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    }
    catch (Exception ex) {
      return false;
    }
  }

  /**
   * This method waits for alert to show.
   */
  public void waitForAlert(WebDriver driver) {
    log.debug("waitForAlert()");
    int i = 0;
    while (i++ < 5) {
      try {
        driver.switchTo().alert();
        break;
      }
      catch (NoAlertPresentException e) {
        DateTimeUtils.sleepMilliSecondsAsLastResortBecauseNothingElseWorks(1000);
        continue;
      }
    }
  }

  public boolean validateCurrentPage(String urlPath, int timeoutInSeconds) {
    while (timeoutInSeconds > 0) {
      if (validateCurrentPage(urlPath))
        return true;
      else {
        DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(2);
        timeoutInSeconds -= 2;
      }
    }
    return false;
  }

  public boolean validateCurrentPage(String urlPath) {
    log.debug("validateCurrentPage()");
    String currentUrl = validateEchosignIsAvailable();
    // Check to see if we got an unexpected app error and redirect to user message page
    if (currentUrl.contains(PageUrlPaths.USER_MSG) && !urlPath.contains(PageUrlPaths.USER_MSG)) {
      UserMessagePage ump = new UserMessagePage(driver);
      log.debug("[User Message] " + ump.getMessage());
      return false;
    }
    // Check if there is error message on /account/home page when clicked on Manage tab
    else if (currentUrl.contains(PageUrlPaths.HOME) && urlPath.contains(PageUrlPaths.MANAGE)) {
      try {
        String errorMessage = driver.findElement(By.id("global-error-text")).getText();
        if (errorMessage.equals(Locale_en_US.AGREEMENT_NOT_READY)) {
          assert false : "Could not load Manage page. Message:" + errorMessage;
        }
      }
      catch (Exception e) {
        return false;
      }
    }
    // Check if there was an app error when sending document
    else if (currentUrl.contains(PageUrlPaths.PROCESS_SEND) && !urlPath.contains(PageUrlPaths.PROCESS_SEND)) {
      ProcessSend sendErrorPage = new ProcessSend(driver);
      log.debug("[Send Error] " + sendErrorPage.getErrorMessage());
      return false;
    }
    // Check if the server had an issue that ended our session and logged user out
    else if (currentUrl.contains(PageUrlPaths.LOGIN) && !urlPath.contains(PageUrlPaths.LOGIN)) {
      log.debug("[Session End Error] Got logged out unexpectedly.");
      return false;
    }
    // The /public/oauthDemo URL actually is a login page
    else if (currentUrl.contains(PageUrlPaths.OAUTHDEMO) && urlPath.contains(PageUrlPaths.LOGIN)) {
      return true;
    }
    // Fast transition: by the time we check for the processing page it might already be on next page
    else if (currentUrl.contains(PageUrlPaths.AUTHOR) && urlPath.contains(PageUrlPaths.SEND_PROGRESS)) {
      return true;
    }
    else if (currentUrl.contains(PageUrlPaths.REUSABLE_DOCUMENT) && urlPath.contains(PageUrlPaths.NEWFORM)) {
      return true;
    }

    // Otherwise, check that we're on the expected page, but poll the URL for a few seconds
    for (int i = 1; i <= Time.LONG; i++) {
      if (getCurrentUrl().contains(urlPath)) {
        // correct page
        return true;
      }
      else {
        // wrong URL, wait longer ...
        log.debug(i + ", Waiting longer for " + urlPath + " to be contained in " + currentUrl);
        DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(1);
      }
    }
    log.debug("validateCurrentPage() returned false for " + urlPath);
    return false;
  }

  public void validateCurrentPageWithAsserts(String urlPath) {
    log.debug("validateCurrentPage()");
    String currentUrl = validateEchosignIsAvailable();
    // Check to see if we got an unexpected app error and redirect to user message page
    if (currentUrl.contains(PageUrlPaths.USER_MSG) && !urlPath.contains(PageUrlPaths.USER_MSG)) {
      UserMessagePage ump = new UserMessagePage(driver);
      Assert.fail("[User Message] " + ump.getMessage());
    }
    // Check if there is error message on /account/home page when clicked on Manage tab
    else if (currentUrl.contains(PageUrlPaths.HOME) && urlPath.contains(PageUrlPaths.MANAGE)) {
      try {
        String errorMessage = driver.findElement(By.id("global-error-text")).getText();
        if (errorMessage.equals(Locale_en_US.AGREEMENT_NOT_READY))
          Assert.fail("Could not load Manage page. Message:" + errorMessage);
      }
      catch (Exception e) {
        //doNothing
      }
    }
    // Check if there was an app error when sending document
    else if (currentUrl.contains(PageUrlPaths.PROCESS_SEND) && !urlPath.contains(PageUrlPaths.PROCESS_SEND)) {
      ProcessSend sendErrorPage = new ProcessSend(driver);
      Assert.fail("[Send Error] " + sendErrorPage.getErrorMessage());
    }
    // Check if the server had an issue that ended our session and logged user out
    else if (currentUrl.contains(PageUrlPaths.LOGIN) && !urlPath.contains(PageUrlPaths.LOGIN)) {
      Assert.fail("[Session End Error] Got logged out unexpectedly.");
    }
    // Otherwise, check that we're on the expected page, but poll the URL for a few seconds
    for (int i = 1; i <= Time.LONG; i++) {
      if (getCurrentUrl().contains(urlPath)) {
        // correct page
        return;
      }
      else {
        // wrong URL, wait longer ...
        log.debug(i + ", Waiting longer for " + urlPath + " to be contained in " + currentUrl);
        DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(1);
      }
    }
    Assert.fail("validateCurrentPage() returned false for " + urlPath);
  }

  public String validateEchosignIsAvailable() {
    log.debug("validateEchosignIsAvailable()");

    waitForPageTitle();
    String pageTitle = getTitle().toLowerCase();
    String currentURL = getCurrentUrl();

    // When driver launches a new browser, Chrome and IE pre-load a different URL string, so skip the title check
    if (!isDriverStillLoading()) {
      // Skip check for Maintenance Page if we are using the bypass URL
      if (!currentURL.contains("bypass.echosign")) {
        //Added following change to handle Maintainance page when NA1 maint page is up during failover
        if (!PropertiesService.getServiceProperty("na1.failover.enabled").equals("true")) {
          // Otherwise, make sure the maintenance page is not up
          assert !pageTitle.contains(Locale_en_US.MAINTENANCE_PAGE_IDENTIFIER.toLowerCase())
                  : "Maintenance Page is up! " + Locale_en_US.MAINTENANCE_PAGE_IDENTIFIER
                  + ", " + DateTimeUtils.getTimestamp();
        }
      }

      // Check to see browser timeout handler page indicates website is down
      // TODO: Convert to using locale string class here
      assert !isProblemInResponse()
              : "Website appears to be unresponsive at " + DateTimeUtils.getTimestamp() + ", Can't load page, current pageTitle is [" + getTitle() + "]";
    }
    return currentURL;
  }

  public String validationAssertFailureMessage(String pageUrlPath) {
    String currentUrl = getCurrentUrl();
    return "Expected page " + pageUrlPath + " but got " + currentUrl + ". " + getUserMessageText(currentUrl);
  }

  protected void reloadPageIfNecessary(String attemptedURL) {
    // Attempt to re-get page if the browser hasn't stalled when loading page
    String currentUrl = "";
    for (int i = 0; i <= 5; i++) {
      currentUrl = getCurrentUrl();
      if (isProblemWithTitleOrURL()) {
        DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(5);
        // After waiting a bit, check the URL again.  If still bad, reload page
        if (isProblemWithTitleOrURL()) {
          log.debug(i + ", Browser appears stalled. Will reload page... Timestamp: " + DateTimeUtils.getTimestamp());
          driver.get(attemptedURL);
        }
      }
      else {
        break;
      }
    }
  }

  protected boolean isProblemWithTitleOrURL() {
    if (isDriverStillLoading() || isProblemInResponse()) {
      return true;
    }
    return false;
  }

  private boolean isDriverStillLoading() {
    String pageURL = getCurrentUrl();
    if (pageURL.contains("data:") || pageURL.contains("//localhost:")) {
      return true;
    }
    return false;
  }

  protected boolean isProblemInResponse() {
    waitForPageTitle();
    String pageTitle = getTitle();
    if (pageTitle.contains("page can't be displayed") ||
            pageTitle.contains("problem loading page") ||
            pageTitle.contains("not available") ||
            pageTitle.equals("")) {
      return true;
    }
    return false;
  }

  /**
   * Wait until page is loaded with title.
   */
  public void waitForPageTitle() {
    // Make sure we don't do validation - validateEchosignIsAvailable() - before page title is available
    // It seems to be slow sometimes for certain pages and page object init would fail with this error while page is still loading:
    // java.lang.AssertionError: Website appears to be unresponsive at 2015-04-21 12:09:47.481, Can't load page, current pageTitle is []
    // at pages.Page.validateEchosignIsAvailable(Page.java:626)
    // at pages.Page.<init>(Page.java:110)

    int i = 0;
    while (getTitle().equals("") && i < Time.LONG) {
      log.debug(i + " - waitForPageTitle");
      i++;
      DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(1);
    }
  }

  /**
   * Wait until the web element becomes visible and throws error after timeout
   *
   * @param timeOutInSeconds timeOutInSeconds - Timeout interval in seconds
   * @param webElement webElement - Specified web element
   * @param errorMessage Error message to be reported on timeout
   * @return Object Current page object
   */
  public Object waitUntilVisible(int timeOutInSeconds, WebElement webElement, String errorMessage) {
    try {
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds));
      wait.until(ExpectedConditions.visibilityOf(webElement));
      return this;
    }
    catch (TimeoutException e) {
      String error = "Timeout error occurred. ";
      if (errorMessage != null) {
        error = error + errorMessage;
      }
      throw new AssertionError(error, e);
    }
  }


  /**
   * Wait until specified web element becomes visible
   *
   * @param {int}        timeOutInSeconds - Timeout interval in seconds
   * @param {WebElement} webElement - Specified web element
   * @return
   */
  public Object waitUntilVisible(int timeOutInSeconds, WebElement webElement) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds));
    wait.until(ExpectedConditions.visibilityOf(webElement));
    return this;
  }

  /**
   * Wait until specified web element becomes visible
   *
   * @param {int} timeOutInSeconds - Timeout interval in seconds
   * @param {By}  locator - Locator which point to a web element
   * @return
   */
  public Object waitUntilVisible(int timeOutInSeconds, By locator) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds));
    wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    return this;
  }

  /**
   * Wait until specified web element becomes visible
   *
   * @param {int} timeOutInSeconds - Timeout interval in seconds
   * @param {By}  locator - Locator which point to a web element
   * @return
   */
  public Object waitUntilClickable(int timeOutInSeconds, By locator) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds));
    wait.until(ExpectedConditions.elementToBeClickable(locator));
    return this;
  }


  /**
   * Wait until web element in specified location becomes present
   *
   * @param {int}        timeOutInSeconds - Timeout interval in seconds
   * @param {WebElement} byLocator - Specific web element locator
   * @return
   */
  public Object waitUntilPresent(int timeOutInSeconds, By byLocator) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds));
    wait.until(ExpectedConditions.presenceOfElementLocated(byLocator));
    return this;
  }

  /**
   * Wait until specified web element becomes clickable with option to scroll element into view
   *
   * @param {int}        timeOutInSeconds - Timeout interval in seconds
   * @param {WebElement} webElement - Specified web element
   * @param {boolean}    true to scroll into view
   * @return {Object}
   */
  public Object waitUntilClickable(int timeOutInSeconds, WebElement webElement, boolean scrollIntoView) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds));
    wait.until(ExpectedConditions.elementToBeClickable(webElement));
    if (scrollIntoView) {
      this.scrollIntoView(webElement);
    }
    return this;
  }

  /**
   * Wait until specified web element becomes clickable
   *
   * @param {int}        timeOutInSeconds - Timeout interval in seconds
   * @param {WebElement} webElement - Specified web element
   * @return {Object}
   */
  public Object waitUntilClickable(int timeOutInSeconds, WebElement webElement) {
    return waitUntilClickable(timeOutInSeconds, webElement, true);
  }

  /**
   * Gets the desired element into view
   * If no element found, just log warning.
   *
   * @param element
   */
  public void scrollIntoView(WebElement element) {
    try {
      ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        }
    catch (Exception g) {
      log.warn(">>>> Exception in scrollIntoView(element)! possible couldn't find element: " + element);
    }
    // This gets called in other methods like waitUntilClickable()
    // Sometimes a click, executed after waitUntilClickable(), doesn't do anything.
    // It's likely the click happens when the focus is moving, so we need a delay for the scrolling to finish
    DateTimeUtils.sleepMilliSecondsAsLastResortBecauseNothingElseWorks(500);
  }
  /**
   * Gets the desired element into view to the middle of the screen
   * If no element found, just log warning.
   *
   * @param element
   */
  public void scrollElementToMiddleOfScreen(WebElement element) {
    try {
      ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: \"smooth\", block: \"center\", inline: \"nearest\"}))", element);
    }
    catch (Exception g) {
      log.warn(">>>> Exception in scrollIntoView(element)! possible couldnt find element: " + element);
    }
    // This gets called in other methods like waitUntilClickable()
    // Sometimes a click, executed after waitUntilClickable(), doesn't do anything.
    // It's likely the click happens when the focus is moving, so we need a delay for the scrolling to finish
    DateTimeUtils.sleepMilliSecondsAsLastResortBecauseNothingElseWorks(500);
  }
  /**
   * Scroll down to the bottom of the page.
   *
   * @return
   */
  public Object scrollToBottom() {
    log.debug("scrollToBottom()");
    try {
//      ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
      ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));");
    }
    catch (Exception g) {
      log.warn(">>>> Exception in scrollToBottom()!");
    }
    return this;
  }

  /**
   * Wait until specified web element becomes stale
   *
   * @param {int}        timeOutInSeconds - Timeout interval in seconds
   * @param {WebElement} webElement - Specified web element
   * @return
   */
  public Object waitUntilStale(int timeOutInSeconds, WebElement webElement) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds));
    wait.until(ExpectedConditions.stalenessOf(webElement));
    return this;
  }

  /**
   * Wait until specified web element becomes stale or timeout, without reporting timeout error
   *
   * @param timeOutInSeconds Maximum amount of time to wait for the element to become stale
   * @param webElement
   * @return Object
   */
  public Object waitUntilStaleOrTimeout(int timeOutInSeconds, WebElement webElement) {
    try {
      waitUntilStale(timeOutInSeconds, webElement);
    }
    catch (TimeoutException e) {
      //no-ops
    }
    return this;
  }

  /**
   * Wait until specified web element has the text
   *
   * @param timeOutInSeconds Timeout interval in seconds
   * @param webElement       Specified web element
   * @param text             text to be present
   * @return
   */
  public Object waitUntilDisplayText(int timeOutInSeconds, WebElement webElement, String text) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds));
    wait.until(ExpectedConditions.textToBePresentInElement(webElement, text));
    return this;
  }

  /**
   * wait until an element or page to be displayed
   *
   * @param progressLocator
   * @param waitTime
   * @return
   */
  public boolean waitUntilDisplayed(WebElement progressLocator, int waitTime) {
    try {
      if (!isProgressDone(progressLocator)) {
        return true;
      }
      else {
        setDriverImplicitlyWait(driver, waitTime, TimeUnit.SECONDS);
        try {
          WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));
          wait.until(ExpectedConditions.visibilityOf(progressLocator));
          return true;
        }
        catch (Exception e) {
          return false;
        }
      }
    }
    catch (Exception te) {
      AssertJUnit.fail("waitUntilDisplayed: the element is not displayed after " + waitTime + " seconds, error: [" + te + "]");
    }
    return false;
  }

  /**
   * Wait for a frame and switch to it.
   *
   * @param timeOutInSeconds Timeout interval in seconds
   * @param webElement       Specified web element
   * @return
   */
  public Object waitFrameToBeAvailableAndSwitchToIt(int timeOutInSeconds, By webElement) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds));
    wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(webElement));
    return this;
  }

  public String getURLParamValueByKey(String url, String key) {

    List<NameValuePair> parameters = getURLParams(url);

    String returnKeyValue = null;
    if (parameters != null) {
      for (NameValuePair p : parameters) {
        if (p.getName().equals(key)) {
          returnKeyValue = p.getValue();
        }
      }
    }
    return returnKeyValue;
  }

  public List<NameValuePair> getURLParams(String url) {
    List<NameValuePair> parameters = null;
    try {
      parameters = URLEncodedUtils.parse(new URI(url), "UTF-8");
    }
    catch (URISyntaxException e) {
      log.error("----> Exception in getURLParams!", e);
    }
    return parameters;
  }

  /**
   * Get element list on any page for the given by
   * @param by   locator of the element
   * @param parentLocator parent locator of the element
   * @return List<WebElement>
   */
  public List<WebElement> getElementList(By by, WebElement parentLocator, int implicitlyWait) {
    setDriverImplicitlyWait(implicitlyWait, TimeUnit.SECONDS);
    try {
      if (parentLocator == null) {
        return driver.findElements(by);
      }
      return parentLocator.findElements(by);
    }
    finally {
      //make sure implicit wait is reset to default even after exception
      turnOnImplicitWait();
    }
  }

  /**
   * Is the given element defined in the By present in the DOM, using SHORTEST timeout.
   *
   * @param "By" locator
   */
  public boolean isElementPresent(By by) {
    return isElementPresent(by, Time.SHORTEST);
  }

  /**
   * Is the given element defined in the By present in the DOM, using specified timeout.
   *
   * @param {By}  locator
   * @param {int} timeout
   */
  public boolean isElementPresent(By by, int timeout) {
    setDriverImplicitlyWait(timeout, TimeUnit.SECONDS);
    boolean result = isElementPresentUsingDefaultTimeout(by);
    setDriverImplicitlyWait(Time.LONG, TimeUnit.SECONDS);
    return result;
  }


  /**
   * Is the given element defined in the By present in the DOM, used DEFAULT timeout.
   *
   * @param "By" locator
   */
  public boolean isElementPresentUsingDefaultTimeout(By by) {
    boolean result = false;
    try {
      WebElement webElement = driver.findElement(by);
      result = webElement.isDisplayed();
    }
    catch (Exception e) {
      result = false;
    }
    return result;
  }

  /**
   * Is the given element defined in the By present in the DOM, using SHORTEST timeout.
   *
   * @param {WebElement} Locator of Parent Web Element
   * @param {By}         By Locator of Desired Element
   */
  public boolean isElementPresent(WebElement element, By by) {
    setDriverImplicitlyWait(Time.SHORTEST, TimeUnit.SECONDS);

    boolean result = isElementPresentUsingDefaultTimeout(element, by);

    setDriverImplicitlyWait(Time.LONG, TimeUnit.SECONDS);
    return result;
  }

  /**
   * Is the given element defined in the By present in the DOM, using specified timeout.
   *
   * @param {WebElement} Locator of Parent Web Element
   * @param {By}         By Locator of Desired Element
   * @param {int}        timeout
   */
  public boolean isElementPresent(WebElement element, By by, int timeout) {
    setDriverImplicitlyWait(timeout, TimeUnit.SECONDS);
    boolean result = isElementPresentUsingDefaultTimeout(element, by);
    setDriverImplicitlyWait(Time.LONG, TimeUnit.SECONDS);
    return result;
  }


  /**
   * Is the given element defined in the By present in the DOM, used DEFAULT timeout.
   *
   * @param {WebElement} Locator of Parent Web Element
   * @param {By}         By Locator of Desired Element
   */
  public boolean isElementPresentUsingDefaultTimeout(WebElement element, By by) {
    boolean result = false;
    try {
      WebElement webElement = element.findElement(by);
      result = webElement.isDisplayed();
    }
    catch (Exception e) {
      result = false;
    }
    return result;
  }

  /**
   * Check if specified web element is present
   *
   * @param {WebElement} locator - The locator of the element
   * @param {int}        timeout - Specified timeout in seconds
   * @return {boolean} Whether element is present or not
   */
  public boolean isElementPresent(WebElement locator, int timeout) {
    return isElementPresent(locator, timeout, TimeUnit.SECONDS);
  }

  /**
   * Check if specified web element is present, timeout is in chosen time units
   *
   * @param {WebElement} locator - The locator of the element
   * @param {int}        timeout - Specified timeout
   * @param {TimeUnit}   timeUnit - Time unit
   * @return {boolean} Whether element is present or not
   */
  public boolean isElementPresent(WebElement locator, int timeout, TimeUnit timeUnit) {
    long startTime = System.currentTimeMillis();
    try {
      setDriverImplicitlyWait(timeout, timeUnit);
      boolean result = isElementPresent(locator);
      setDriverImplicitlyWait(Time.LONG, TimeUnit.SECONDS);
      return result;
    }
    catch (UnsupportedCommandException e) {
      log.warn("isElementPresent failed with UnsupportedCommandException, falling back to isElementDisplayed. Message: "
                   + e.getMessage());
      //Fallback to isElementDisplayed if UnsupportedCommandException is thrown because of implicitlyWait having
      // issues with Selenium 4 Hub
      //Calculate remaining timeout, with minimum of 1 second
      int remainingTimeoutSec = Math.max(
          (int) timeUnit.toSeconds(timeout) - (int) ((System.currentTimeMillis() - startTime) / 1000), Time.SHORTEST);
      return isElementDisplayed(locator, remainingTimeoutSec);
    }
  }

  /**
   * Check if specified web element is present, used DEFAULT timeout
   *
   * @param {WebElement} locator
   * @return {boolean} Whether element is present or not
   */
  public boolean isElementPresent(WebElement locator) {
    try {
      return locator.isDisplayed();
    }
    catch (final Exception e) {
      return false;
    }
  }

  /**
   * Checks if a locator is present with timeout to account for the time needed to open or close it
   *
   * @param locator  The By element
   * @param expected True if the element is expected to be present; false otherwise
   * @param timeout  Time needed to wait for the element to be open or closed
   * @return boolean True if the element is present; false otherwise
   */
  public boolean isElementPresent(By locator, boolean expected, int timeout) {
    log.debug("isElementPresent()");
    try {
      if (expected) {
        waitUntilVisible(timeout, locator);
      }
      else {
        waitUntilInvisible(locator, timeout);
      }
    }
    catch (Exception e) {
      // No ops
    }
    return isElementPresent(locator, Time.SHORTEST);
  }

  /**
   * Checks whether an element is displayed on a page
   *
   * @param locator         The locator of the element to test
   * @param timeoutInSecond Max wait time in seconds
   * @return {boolean}      true if the element is displayed
   */
  public boolean isElementVisible(WebElement locator, int timeoutInSecond) {
    log.debug("isElementVisible()");
    try {
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSecond));
      wait.until(ExpectedConditions.visibilityOf(locator));
    }
    catch (Exception e) {
      log.debug(e.getMessage() + "; " + e.getStackTrace());
      return false;
    }
    return true;
  }

  /**
   * Verify whether a Element is displayed on a page
   *
   * @param {WebDriver}  driver - WebDriver instance for the test
   * @param {WebElement} locator - The locator of the element to test
   * @param {int}        time - Waiting time in seconds
   * @return {boolean} Whether the element is displayed or not
   */
  public boolean isElementVisible(WebDriver driver, WebElement locator, int time) {
    log.debug("isElementVisible(driver, locator, time)");
    try {
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(time));
      wait.until(ExpectedConditions.visibilityOf(locator));
    }
    catch (Exception e) {
      log.debug(e.getMessage() + "; " + e.getStackTrace());
      return false;
    }
    return true;
  }

  /**
   * A polling wait to find element on page. Keeps trying until timeout even if NoSuchElementException
   *
   * @param webElement
   */
  public void waitForElement(final WebElement webElement, int timeout_seconds) {
    new FluentWait<WebDriver>(driver)
            .withTimeout(Duration.ofSeconds(timeout_seconds))
            .pollingEvery(Duration.ofSeconds(1))
            .ignoring(NoSuchElementException.class)
            .until(ExpectedConditions.and(ExpectedConditions.visibilityOf(webElement), new ExpectedCondition<Boolean>() {
              @Nullable
              @Override
              public Boolean apply(@Nullable WebDriver webDriver) {
                return ((webElement.getSize().height + webElement.getSize().width) > 0);
              }
            }));
  }


  /**
   * A polling wait to find element on page to be clickable. Keeps trying until timeout even if NoSuchElementException
   *
   * @param webElement
   */
  public void waitForElementToBeClickable(final WebElement webElement, int timeout_seconds) {
    new FluentWait<WebDriver>(driver)
            .withTimeout(Duration.ofSeconds(timeout_seconds))
            .pollingEvery(Duration.ofSeconds(1))
            .ignoring(NoSuchElementException.class)
            .until(ExpectedConditions.and(ExpectedConditions.visibilityOf(webElement), ExpectedConditions.elementToBeClickable(webElement), new ExpectedCondition<Boolean>() {
              @Nullable
              @Override
              public Boolean apply(@Nullable WebDriver webDriver) {
                return ((webElement.getSize().height + webElement.getSize().width) > 0);
              }
            }));
  }

  public boolean isElementDisplayed(final WebElement webElement, int timeout) {
    try {
      waitForElement(webElement, timeout);
      return webElement.isDisplayed();
    }
    catch (Throwable t) {
      return false;
    }
  }

  public boolean isElementDisplayed(final WebElement webElement) {
    return isElementDisplayed(webElement, Time.LONG);
  }

  public boolean isElementClickable(final WebElement webElement, int timeout) {
    try {
      waitForElementToBeClickable(webElement, timeout);
      return true;
    }
    catch (Throwable t) {
      return false;
    }
  }

  /**
   * A polling wait to find element on page. Keeps trying until timeout even if NoSuchElementException
   *
   * @param by
   */
  public void waitForElement(final By by, int timeout_seconds) {
    final ConditionFactory<Boolean> factory = new ConditionFactory<Boolean>() {
      @Override
      protected ExpectedCondition<Boolean> parentCondition() {
        return new ExpectedCondition<Boolean>() {
          @Override
          public Boolean apply(WebDriver webDriver) {
            return ExpectedConditions.visibilityOfElementLocated(by).apply(webDriver) != null;
          }
        };
      }

      @Override
      protected ExpectedCondition<Boolean> fallbackCondition() {
        return new ExpectedCondition<Boolean>() {
          @Override
          public Boolean apply(WebDriver webDriver) {
            final WebElement webElement = driver.findElement(by);
            return webElement.getSize().height > 0 || webElement.getSize().width > 0;
          }
        };
      }
    };
    new FluentWait<WebDriver>(driver)
            .withTimeout(Duration.ofSeconds(timeout_seconds))
            .pollingEvery(Duration.ofSeconds(Time.SHORTEST))
            .ignoring(NoSuchElementException.class)
            .until(factory.createCondition());
  }

  public void waitForElement(final By by) {
    waitForElement(by, Time.LONGER);
  }

  /**
   * Return the class name for a given object, usually a Page Object
   *
   * @param obj The class that you want to know the name of
   * @return
   */
  public String getClassName(Object obj) {
    String className = null;
    try {
      className = obj.getClass().getName();
    }
    catch (NullPointerException npe) {
      AssertJUnit.fail("Null Pointer exception caught during getClassName");
    }
    return className;
  }

  /**
   * Extract the message shown on the User Message Page if displayed
   *
   * @return The message shown
   */
  public String getUserMessageText() {
    return getUserMessageText(getCurrentUrl());
  }

  /**
   * /**
   * Extract the message shown on the User Message Page if displayed
   *
   * @param currentUrl
   * @return
   */
  public String getUserMessageText(String currentUrl) {
    if (currentUrl.contains(PageUrlPaths.USER_MSG)) {
      UserMessagePage ump = new UserMessagePage(driver);
      return "A user message is shown: " + ump.getMessage();
    }
    return "";
  }

  /**
   * Use to navigate directly to a page if given relative URL page path
   *
   * @param pageURL the relative page path
   */
  public void navigateToWithoutValidation(String pageURL) {
    // Navigate to requested URL
    log.debug("navigateToWithoutValidation(" + pageURL + ")");
    driver.get(getEchoSignURL(pageURL));
  }

  /**
   * Navigates to the path on the same domain.
   * Intended as a general wrapper to be used in lieu of calling driver.get directly.
   * Utilizes actual domain that page is currently on and appends the path to the current domain to make URL
   *
   * @param path {String} path to get on the current domain
   */
  public void navigateToPathWithinSameDomain(String path) {
    driver.navigate().to(getActualBaseURL(getCurrentUrl()) + path);
  }

  /**
   * Static method
   * Navigates to the path on the same domain.
   * Intended as a general wrapper to be used in lieu of calling driver.get directly.
   * Utilizes actual domain that page is currently on and appends the path to the current domain to make URL
   *
   * @param driver   WebDriver instance
   * @param {String} path to get on the current domain
   */
  public static void navigateToPathWithinSameDomain(WebDriver driver, String path) {
    String desiredURL = getActualBaseURL(driver.getCurrentUrl()) + path;
    log.debug("Navigating to " + desiredURL);
    driver.navigate().to(desiredURL);
  }

  /**
   * This is very useful in circumstances where you know the element is not time dependent, and
   * in some cases you know the query attempt will fail.
   */
  protected void turnOffImplicitWait() {
    setDriverImplicitlyWait(0, TimeUnit.SECONDS);
  }

  /**
   * This is very useful in circumstances where you know the element is not time dependent, and
   * in some cases you know the query attempt will fail.
   */
  protected void turnOnImplicitWait() {
    setDriverImplicitlyWait(Time.LONG, TimeUnit.SECONDS);
  }

  /**
   * This method will select all text field's content and replace it by new keys.
   * This is useful when text field cannot be empty - webelement.clear() will not work in this case.
   *
   * @param element
   * @param keys
   */
  public void clearAndType(WebElement element, String keys) {
   // element.clear();
    clearUsingJS(element);
    CharSequence charSequence = Keys.CONTROL + "a";
    if (BrowserUtils.getPlatform(driver).toString().toLowerCase().contains("mac")) {
      charSequence = Keys.COMMAND + "a";
    }
    element.sendKeys(charSequence);
    element.sendKeys(Keys.DELETE);
    //Waiting because firefox and Chrome sometime don't like sending ctrl + a and other keys so quickly
    DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(2);
    element.sendKeys(keys);
  }

  /**
   * This method will select all text field's content and replace it by new keys.
   * This is useful when text field cannot be empty - webelement.clear() will not work in this case.
   *
   * @param element
   * @param keys
   */
  public void clearAndTypeText(WebElement element, String keys) {
    element.clear();

    while(!element.getAttribute("value").equals("")){
      element.sendKeys(Keys.BACK_SPACE);
    }
    // In some cases element.clear() fails to clear the text fields; thus trying key combination for select all + delete for windows as well as mac
    if (element.getText().length() > 0) {
      clearUsingJS(element);
    }

    if (element.getText().length() > 0) {
      element.sendKeys(Keys.SHIFT, Keys.ARROW_UP);
      element.sendKeys(Keys.DELETE);
    }

    if (element.getText().length() > 0) {
      element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
      element.sendKeys(Keys.BACK_SPACE);
    }

    /*Waiting because firefox and Chrome sometime don't like sending ctrl + a and other keys so quickly*/
    DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(2);
    element.sendKeys(keys);
  }

  /**
   * This method clears a text field using JS
   */
  public void clearUsingJS(WebElement element) {
    JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
    jsExecutor.executeScript("arguments[1].value = arguments[0]; ","", element);
  }

  /**
   * This method types text in text field using JS
   * @param element
   */
  public void typeUsingJS(WebElement element, String text) {
    JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
    jsExecutor.executeScript("arguments[0].setAttribute=('value', '" + text + "');", element);
  }

  /**
   * This method will navigate browser back
   */
  public void navigateBrowserBack() {
    driver.navigate().back();
  }

  public void browserRefresh() {
    driver.navigate().refresh();
  }

  /**
   * Used to synchronize a web page change with the selenium driver.  This variation supports the idea
   * that not all pages are part of echosign page heirarchy.
   *
   * @param lastKnownUrl The url used to detect that the browser has moved away.
   */
  public String waitOnUrlChange(String lastKnownUrl, int timeOut) {
    try {
      (new WebDriverWait(driver, Duration.ofSeconds(timeOut))).until(urlHasChanged(lastKnownUrl));
    }
    catch (TimeoutException e) {
      AssertJUnit.fail("Timed out waiting for page to change away from: "
                               + lastKnownUrl + ", currently on: " + getCurrentUrl());
    }
    return driver.getCurrentUrl();
  }

  public String waitOnUrlChange(String lastKnownUrl) {
    return waitOnUrlChange(lastKnownUrl, PAGE_WAIT_TIMEOUT);
  }


  /**
   * Used to synchronize a web page change with the selenium driver.
   * This method checks whether the url has been changed or not
   *
   * @param lastKnownUrl     The url used to detect that the browser has moved away.
   * @param timeoutInSeconds
   * @return True, if the URL has changed, otherwise False
   */

  public boolean isUrlChanged(String lastKnownUrl, int timeoutInSeconds) {
    log.debug("isUrlChanged(" + lastKnownUrl + ")");
    boolean hasUrlChanged;
    try {
      (new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))).until(urlHasChanged(lastKnownUrl));
      hasUrlChanged = true;
    }
    catch (TimeoutException e) {
      hasUrlChanged = false;
    }
    return hasUrlChanged;
  }

  /**
   * Used to synchronize a web page change with the selenium driver.
   * This method checks whether the url has been changed or not
   *
   * @param lastKnownUrl
   * @return True, if the URL has changed, otherwise False
   */
  public boolean isUrlChanged(String lastKnownUrl) {
    log.debug("isUrlChanged(" + lastKnownUrl + ")");
    return isUrlChanged(lastKnownUrl, PAGE_WAIT_TIMEOUT);
  }

  /**
   * Wait on the provided selector.
   *
   * @param selector
   * @return boolean waited
   */
  public boolean waitUntilInvisible(By selector) {
    final ConditionFactory<Boolean> factory = new ConditionFactory<Boolean>() {
      @Override
      protected ExpectedCondition<Boolean> parentCondition() {
        return ExpectedConditions.invisibilityOfElementLocated(selector);
      }

      @Override
      protected ExpectedCondition<Boolean> fallbackCondition() {
        return new ExpectedCondition<Boolean>() {
          @Override
          public Boolean apply(WebDriver webDriver) {
            final WebElement webElement = driver.findElement(selector);
            return webElement.getSize().height <= 0 || webElement.getSize().width <= 0;
          }
        };
      }
    };
    if (driver.findElement(selector).isDisplayed()) {
      WebDriverWait wait = new WebDriverWait(driver, TimeDuration.LONGEST);
      return wait.until(factory.createCondition());
    }
    return false;
  }

  /**
   * Wait on the selector to become invisible and reports error after timeout
   *
   * @param selector
   * @param timeout        in seconds
   * @param implicitlyWait in seconds
   * @param errorMessage Error message to be reported on timeout
   */
  public void waitUntilInvisible(By selector, int timeout, int implicitlyWait, String errorMessage) {
    // In case the locator never exists or has gone before checking the condition,
    // set implicit wait to overwrite the default so it won't take a long time to determine the element doesn't exist
    setDriverImplicitlyWait(implicitlyWait, TimeUnit.SECONDS);
    driver.switchTo().defaultContent();

    try {
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
      wait.until(ExpectedConditions.invisibilityOfElementLocated(selector));
    }
    catch (TimeoutException e) {
      String error = "Timeout error occurred. ";
      if (errorMessage != null) {
        error = error + errorMessage;
      }
      throw new AssertionError(error, e);
    }
    finally {
      //make sure implicit wait is reset to default even after exception
      turnOnImplicitWait();
    }
  }

  public void waitUntilInvisible(By selector, int timeout, String errorMessage) {
    waitUntilInvisible(selector, timeout, (timeout > Time.SHORTER ? Time.SHORTER : timeout), errorMessage);
  }

  /**
   * Wait on the provided selector to be invisible in specified timeout.
   *
   * @param selector
   * @param timeout        in seconds
   * @param implicitlyWait in seconds
   */
  public void waitUntilInvisible(By selector, int timeout, int implicitlyWait) {
    // In case the locator never exists or has gone before checking the condition,
    // set implicit wait to overwrite the default so it won't take a long time to determine the element doesn't exist
    setDriverImplicitlyWait(implicitlyWait, TimeUnit.SECONDS);
    driver.switchTo().defaultContent();

    try {
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
      wait.until(ExpectedConditions.invisibilityOfElementLocated(selector));
    }
    finally {
      //make sure implicit wait is reset to default even after exception
      turnOnImplicitWait();
    }
  }

  /**
   * Utility method to set the implicitLyWait timeout with Selenium 4, requiring minimum code change. <br/><br/>
   * <b>Details:</b> With Selenium 4 <code>implicitlyWait(long time, TimeUnit unit)</code> has been deprecated and
   * <code>implicitlyWait(Duration duration)</code> is suggested instead. This method takes paramemeter similar to the
   * deprecated format and hence requires minimal code change
   *
   * @return WebDriver.Timeouts
   */
  public WebDriver.Timeouts setDriverImplicitlyWait(long timeout, TimeUnit timeUnit) {
    return setDriverImplicitlyWait(driver, timeout, timeUnit);
  }

  /**
   * Static utility method to set the implicitLyWait timeout with Selenium 4, requiring minimum code change. <br/><br/>
   * <b>Details:</b> With Selenium 4 <code>implicitlyWait(long time, TimeUnit unit)</code> has been deprecated and
   * <code>implicitlyWait(Duration duration)</code> is suggested instead. This method takes paramemeter similar to the
   * deprecated format and hence requires minimal code change
   *
   * @return WebDriver.Timeouts
   */
  public static WebDriver.Timeouts setDriverImplicitlyWait(WebDriver driver, long timeout, TimeUnit timeUnit) {

    assert driver != null : "Driver is null";

    Duration duration;
    if (timeUnit == TimeUnit.SECONDS) {
      duration = Duration.ofSeconds(timeout);
    }
    else if (timeUnit == TimeUnit.MILLISECONDS) {
      duration = Duration.ofMillis(timeout);
    }
    else if (timeUnit == TimeUnit.MINUTES) {
      duration = Duration.ofMinutes(timeout);
    }
    else if (timeUnit == TimeUnit.HOURS) {
      duration = Duration.ofHours(timeout);
    }
    else if (timeUnit == TimeUnit.DAYS) {
      duration = Duration.ofDays(timeout);
    }
    else {
      duration = Duration.ofSeconds(timeout);
    }
    return driver.manage().timeouts().implicitlyWait(duration);
  }

  public void waitUntilInvisible(By selector, int timeout) {
    waitUntilInvisible(selector, timeout, (timeout > Time.SHORTER ? Time.SHORTER : timeout));
  }

  public boolean waitUntilProgressDone(By selector, int timeout) {
    // In case the locator never exists or has gone before checking the condition,
    // set implicit wait to overwrite the default so it won't take a long time to determine the element doesn't exist
    setDriverImplicitlyWait(timeout, TimeUnit.SECONDS);

    try {
      waitUntilInvisible((By) selector, timeout);
      return true;
    }
    catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    finally {
      //make sure implicit wait is reset to default even after exception
      turnOnImplicitWait();
    }
  }

  /**
   * test to see if the progress is done, or whether any element is no longer displaying
   *
   * @param progressLocator
   * @return
   */
  public boolean isProgressDone(WebElement progressLocator) {
    boolean isDone = false;
    try {
      String progress = getAttributeValue(progressLocator, "style");
      isDone = (progress.contains("none"));
    }
    catch (Exception te) {
      log.debug("There is no progress web element with style attribute.");
      // AssertJUnit.fail("isProgressDone: the style of the elmenet is invalid, error: [" + te + "]");
    }
    return isDone;
  }

  /**
   * @return true if the driver is chrome.
   */
  public boolean isChrome() {
    if (driver != null) {
      return driver.toString().contains("chrome");
    }
    return false;
  }

  /**
   * This method checks whether the browser is Internet Explorer
   *
   * @return True, if the browser is Internet Explorer, otherwise False
   */
  public boolean isExplorer() {
    log.debug("isExplorer()");
    return getBrowserName().equals("internet explorer");
  }

  /*
  public String getShardUrl(String shardName) {
    log.debug("getShardUrl(" + shardName + ")");
  	String host = DriverFactory.properties.getHost();
  	int firstDot = host.indexOf(".");
  	int secondDot = host.indexOf(".", firstDot);
  	String middleString = host.substring(firstDot, secondDot);
  	if(middleString.contains(Locale_en_US.PRODUCT_NAME.toLowerCase())) {
  		return new StringBuffer(host).insert(firstDot + 1, shardName + ".").toString();
  	}
  	else if (.shardList.contains(middleString)) {
  		return host.replace(middleString, shardName);
  	}
  	return host;
  }*/

  /**
   * Checks that the user's specific shard is found within the the current url.
   *
   * @param user
   * @return boolean true if user on correct shard.
   */
  public boolean isValidShardForUser(RegisteredUser user) {
    if (user.hasHomeShard()) {
      log.debug("isValidShardForUser: " + user + ", actual: " + getCurrentUrl());
      return getCurrentUrl().contains("." + user.getHomeShard() + ".");
    }
    else {
      return true;
    }
  }

  /**
   * Returns the value of the cookie: WEB_CLIENT_TOKEN_COOKIE
   */
  public String getWebClientToken() {
    return getCookieValue(WebClientData.WEB_CLIENT_TOKEN_COOKIE_KEY_NAME);
  }

  /**
   * Returns the value of the cookie: JSESSIONID
   */
  public String getJSessionId() {
    return getCookieValue(WebClientData.JSESSION_ID_KEY_NAME);
  }

  /**
   * Returns the value of a cookie
   */
  protected String getCookieValue(String cookieName) {
    Cookie cookie = driver.manage().getCookieNamed(cookieName);
    AssertJUnit.assertNotNull("Cookie not found for: " + cookieName, cookie);
    return cookie.getValue();
  }

  /**
   * Get the value of the attribute
   *
   * @param {WebElement} locator - the locator for the attribute
   * @param {String}     attributeName - the name of the attribute
   * @return {String} Value of the specified attribute
   */
  public String getAttributeValue(WebElement locator, String attributeName) {
    try {
      if (locator == null)
        return null;
      String attributeValue = locator.getAttribute(attributeName);
      if (attributeValue != null) {
        return attributeValue;
      }
    }
    catch (Exception e) {
      log.debug(e.getMessage() + "; " + e.getStackTrace());
    }
    return null;
  }

  /**
   * Extracts the value of the provided key from page source
   *
   * @param key
   * @return String value of the key
   */
  public String extractKeyValueFromPageSource(String key) {
    log.debug("extractKeyValueFromPageSource()");
    String keyValuePair = StringUtils.extractKeyValuePair(key, getPageSource());
    assertNotNull(keyValuePair, key + " key not found.");
    log.debug("original keyValuePair: " + keyValuePair);
    keyValuePair = StringEscapeUtils.unescapeJava(keyValuePair);
    log.debug("unescaped keyValuePair: " + keyValuePair);

    if (keyValuePair.indexOf("\"") > 0) {
      return StringUtils.getStringFromKeyValuePair(key, keyValuePair);
    }
    else {
      return String.valueOf(StringUtils.getIntFromKeyValuePair(key, keyValuePair));
    }
  }

  /**
   * This method can be used to simulate a switch between webdriver instances
   *
   * @param currentWebDriver
   * @param webDriverToFocus
   */
  public static void switchDriverInstance(WebDriver currentWebDriver, WebDriver webDriverToFocus, String... informationalMessage) {
    log.debug("switchDriverInstance()");
    log.debug("URL for the currentWebDriver -- " + currentWebDriver.getCurrentUrl());
    log.info("Information -- " + informationalMessage);
    currentWebDriver.manage().window().setPosition(new Point(-2000, 0));
    // webDriverToFocus.manage().window().maximize(); -- Removed and added folllowing change as the present change is causing issue during switch.
    webDriverToFocus.manage().window().setSize(new Dimension(1024, 1024));
    log.debug("URL for the webDriverToFocus -- " + webDriverToFocus.getCurrentUrl());
  }

  /**
   * wait until a alert box shows up and click on ok to close it (accept the alert box)
   */
  public void clickOKOnAlertBox() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(300));  //wait for 5 mins
    Alert alert = wait.until(ExpectedConditions.alertIsPresent());
    alert.accept();
  }

  public String getAlert(int waitTimeInSeconds) {
    if (this.isElementPresent(alertLocator, waitTimeInSeconds)) {
      return alertLocator.getText();
    }
    return null;
  }

  public String getModalAlert(int waitTimeInSeconds) {
    if (isElementPresent(modalbodyLocator, waitTimeInSeconds)) {
      return modalbodyLocator.getText();
    }
    return null;
  }

  public String getAlert() {
    return getAlert(Time.TWOSECOND);
  }

  public String getRedWidgetAlert() {
    if (this.isElementPresent(alertRedLocator))
      return alertRedLocator.getText();
    return null;
  }

  /**
   * Returns error message in red banner if present
   *
   * @param timeoutInSeconds Maximum time to wait for error message to appear
   * @return String Error message. Null if error doesn't exist
   */
  public String getAlertDangerMsg(int timeoutInSeconds) {
    log.debug("getAlertDangerMsg()");
    if (this.isElementPresent(alertDangerLocator, timeoutInSeconds))
      return alertDangerLocator.getText();
    return null;
  }

  public String getAlertDangerMsg() {
    return getAlertDangerMsg(Time.SHORTER);
  }

  public String getAlertText(WebElement alertLocator) {
    if (this.isElementPresent(alertLocator))
      return alertLocator.getText();
    return null;
  }

  /**
   * Retrieves the message shown in the toast container
   *
   * @param reportNotFound when toast message is not present throws an error if true or returns null if false
   * @return String Toast container message
   */
  public String getToastMessage(boolean reportNotFound) {
    log.debug("getToastMessage()");
    if (reportNotFound) {
      assert isElementVisible(toastMessageLocator, Time.SHORTER) : "Toast message not found";
    }
    else {
      if (!isElementVisible(toastMessageLocator, Time.SHORTER)) {
        return null;
      }
    }
    String message = toastMessageLocator.getText();
    int i = 0;
    while(i < 5 && (message == null || message.isEmpty())) {
      DateTimeUtils.sleepMilliSecondsAsLastResortBecauseNothingElseWorks(400);
      message = toastMessageLocator.getText();
    }
    return message;
  }

  /**
   * Retrieves the message shown in the toast container
   *
   * @return String Toast container message
   */
  public String getToastMessage() {
    return getToastMessage(true);
  }

  /**
   * Retrieves the message shown in the Error toast container
   *
   * @param reportNotFound when Error toast message is not present throws an error if true or returns null if false
   * @return String Toast Error container message
   */
  public String getToastError(boolean reportNotFound) {
    log.debug("getToastError()");
    if (reportNotFound) {
      assert isElementVisible(errorMessageLocator, Time.SHORTER) : "Toast message not found";
    }
    else {
      if (!isElementVisible(errorMessageLocator, Time.SHORTER)) {
        return null;
      }
    }
    String message = errorMessageLocator.getText();
    int i = 0;
    while(i < 5 && (message == null || message.isEmpty())) {
      DateTimeUtils.sleepMilliSecondsAsLastResortBecauseNothingElseWorks(400);
      message = errorMessageLocator.getText();
    }
    return message;
  }

  /**
   * Retrieves the message shown in the toast Error container
   *
   * @return String Toast Error container message
   */
  public String getToastError() {
    log.debug("getToastError()");
    return getToastError(true);
  }

  /**
   * Verify the message shown in the Error toast container
   */
  public boolean verifyToastError(String expectedError) {
    log.debug("verifyToastError()");
    waitForElement(toastMessageLocator, Time.LONGEST);
    for (int i = 0; i < toastErrorLocators.size(); i++) {
      String error = toastErrorLocators.get(i).getText();
      if (error.equals(expectedError)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Verify the global error message shown
   *
   * @param expectedError
   * @return boolean - Whether the message displayed or not
   */
  public boolean verifyGlobalError(String expectedError) {
    log.debug("verifyGlobalError()");
    try {
      waitForElement(globalErrorLocator, Time.LONGEST);
    }
    catch (StaleElementReferenceException e) {
    }
    waitForElement(globalErrorLocator, Time.LONGEST);
    for (int i = 0; i < globalErrorLocators.size(); i++) {
      String error = globalErrorLocators.get(i).getText();
      if (error.equals(expectedError)) {
        return true;
      }
    }
    return false;
  }
  /**
   * Verify the count for Toast Message
   *
   * @param expectedCount - Expected toast message count
   * @return boolean
   */
  public boolean toastMessageCount(Integer expectedCount) {
    log.debug("verifyToastError()");
    waitForElement(toastMessageLocator, Time.LONGEST);
    if(toastErrorLocators.size() == expectedCount)
        return true;
    else
    return false;
  }

  /**
   * Waits until the toast message container fades away
   */
  public void waitForToastMessageDiappear() {
    log.debug("waitForToastMessageDiappear()");
    try {
      waitUntilInvisible(By.cssSelector(toastMessageCssSelector), Time.SHORT, Time.SHORTEST);
    }
    catch (Exception e) {
      log.debug(e.getMessage());
    }
  }

  /**
   * verify if a page is loaded based on an element on that page with element id named elementId is visible after waiting for certain seconds (timeOut)
   *
   * @param driver
   * @param timeOut   waiting time in seconds
   * @param elementId any element's id on the loading page that will be visible after the page is loaded
   * @return
   */
  public boolean isPageUrlLoaded(WebDriver driver, int timeOut, String elementId) {
    try {
      WebDriverWait wait = new WebDriverWait(driver, PAGE_WAIT_TIMEOUT_DURATION);
      WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
      if (element != null)
        return true;
    }
    catch (Exception te) {
      AssertJUnit.fail("waitUntilPageUrlLoaded: page is not loaded after " + PAGE_WAIT_TIMEOUT + " seconds, error [" + te + "]");
    }
    return false;
  }


  public void makeVisible(WebElement element) {
    execScript("arguments[0].style.display = 'inline';", element);
    execScript("arguments[0].style.​visibility = 'visible';", element);
    // sometimes MSIE may need these additional properties set
    execScript("arguments[0].style.overflow = 'visible';", element);
    execScript("arguments[0].style.opacity = '1';", element);
    waitUntilVisible(Time.SHORT, element);
  }

  public void clickViaExecutor(WebElement element, WebDriver driver) {
    JavascriptExecutor executor = (JavascriptExecutor) driver;
    executor.executeScript("arguments[0].click();", element);
  }

  /**
   * Checks if it's the "Sorry" page
   *
   * @param driver
   * @return {boolean} true if Error Code is present
   */
  public boolean isSorryPage(WebDriver driver) {
    log.debug("isSorryPage()");
    if (isElementPresent(By.tagName("h1"))) {
      if (driver.findElement(By.tagName("h1")).getText().contains(Locale_en_US.SORRY)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Retrieves Error Code from the Sorry page
   *
   * @param driver
   * @return {String} the error code
   */
  public String getErrorCode(WebDriver driver) {
    log.debug("getErrorCode()");
    if (isSorryPage(driver)) {
      String body = driver.findElement(By.tagName("body")).getText();
      return "Error Code: " + StringUtils.extractKeyValuePair("Error Code", body);
    }
    else {
      return "";
    }
  }

  public boolean isFirefox() {
    return ((RemoteWebDriver) driver).getCapabilities().getBrowserName().contains("firefox");
  }


  /**
   * hovers mouse at a particular point determined by pixels. e.g. with new Point(120,140) param, the action would be a mouseHover on 120,140 pixel position on the browser
   * and perform the said action at that point.
   *
   * @param p,     point on which moveOver or click has to be performed.
   * @param action action to perform on the given coordinate.
   */
  public void mouseActionAtCoordinate(Point p, Action action) {
    WebElement htmlPage = this.driver.findElement(By.tagName("body"));
    Actions actions = new Actions(this.driver);

    /*
     * lref is left reference, or pixels which we need to move to left to be at left of the page.
     * Same with tref, which is top reference, or pixels which we need to move to top to be at the top of the page.
     * We use these offsets to move to left of the page so that from there we can move relatively to the coordinates provided in point p
     */

    int lref = -(htmlPage.getSize().getWidth() / 2) - htmlPage.getLocation().x;
    int tref = -(htmlPage.getSize().getHeight() / 2) - htmlPage.getLocation().y;

    try {
      actions.moveToElement(htmlPage, lref + p.x, tref +p.y).perform();
      if (action != null)
        action.perform();
    }
    catch (Throwable t) {
      assert false : "If you are seeing this, and the out of bound error suggests point to move on is on right-bottom side of the viewport then try with doesMoveToElementTakesToCenter=false for the browser.";
    }
  }

  /**
   * hovers mouse at a particular point determined by pixels. e.g. with new Point(120,140) param, the action would be a mouseHover on 120,140 pixel position on the browser
   * and based on value of click var, it performs click.
   *
   * @param p,    point on which moveOver or click has to be performed.
   * @param click whether to click or just move over the coordinate.
   */
  public void moveOrClickAtCoordinates(Point p, boolean click) {
    mouseActionAtCoordinate(p, click ? new Actions(this.driver).click().build() : null);
  }

  /**
   * clicks at a particular point determined by pixels. e.g. with new Point(120,140) param, the action would be a click on 120,140 pixel position on the browser
   *
   * @param p
   */
  public void clickAtCoordinates(Point p) {
    moveOrClickAtCoordinates(p, true);
  }

  public void doubleClickAtCoordinates(Point p) {
    mouseActionAtCoordinate(p, new Actions(this.driver).doubleClick().build());
  }

  /**
   * right clicks at a particular point determined by pixels. e.g. with new Point(120,140) param, the action would be a click on 120,140 pixel position on the browser
   *
   * @param p
   */
  public void rightClickAtCoordinates(Point p) {
    mouseActionAtCoordinate(p, new Actions(this.driver).contextClick().build());
  }

  /**
   * hovers mouse at a particular point determined by pixels. e.g. with new Point(120,140) param, the action would be a mouseHover on 120,140 pixel position on the browser
   *
   * @param p
   */
  public void mouseHoverAtCoordinates(Point p) {
    moveOrClickAtCoordinates(p, false);
  }

  public void clickUsingJavaScript(WebElement locator) {
    this.scrollIntoView(locator);
    execScript("arguments[0].click();", locator);
  }

  public void waitUntilPageReady(int timeOut) {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    //Initially bellow given if condition will check ready state of page.
    if (js.executeScript("return document.readyState").toString().equals("complete"))
      return;
    //This loop will rotate for 25 times to check If page Is ready after every 1 second. //You can replace your value with 25 If you wants to Increase or decrease wait time.
    for (int i = 0; i < timeOut; i++) {
      try {
        Thread.sleep(500);
      }
      catch (InterruptedException e) {
      }
      //To check page ready state.
      if (js.executeScript("return document.readyState").toString().equals("complete")) {
        break;
      }
    }
  }

  /**
   * Verifies if alert danger message is present
   *
   * @return {boolean}
   */
  public boolean isAlertDangerMessagePresent() {
    log.debug("isAlertDangerMessagePresent()");
    return isElementPresent(alertDangerLocator);
  }

  /**
   * Gets alert danger message
   *
   * @return {String}
   */
  public String getAlertDangerMessageText() {
    log.debug("getAlertDangerMessageText()");
    assert isAlertDangerMessagePresent() : "Alert Danger Message is not present";
    return alertDangerLocator.getText();
  }

  /**
   * Verifies if the alert danger message is correct
   *
   * @param alertMessage
   * @return {boolean}
   */
  public boolean verifyAlertMessage(String alertMessage) {
    log.debug("verifyAlertMessage()");
    assert isAlertDangerMessagePresent() : "Alert Danger Message is not present";
    String actualAlertMessageText = getAlertDangerMsg();
    return actualAlertMessageText.contains(alertMessage);
  }

  /**
   * This method dismiss the alert
   */
  public void dismissAlert() {
    log.debug("dismissAlert()");
    assert isAlertPresent() : "Alert box is NOT present";
    driver.switchTo().alert().dismiss();
  }

  /**
   * Clicks on an element using Actions class
   * @param elem
   */
  public void clickUsingAction(WebElement elem) {
    log.debug("clickUsingAction()");
    WebDriverWait wait = new WebDriverWait(driver, TimeDuration.LONGEST);
    wait.until(ExpectedConditions.elementToBeClickable(elem));
    Actions actions = new Actions(driver);
     actions.moveToElement(elem).click().build().perform();
  }

  /**
   * Gets the hex value of the RGB value
   *
   * @param rgbVal rgba(126, 75, 243, 0.2)
   */
  public String getHexColorFromRGB(String rgbVal) {
    String[] rgbs = rgbVal.replaceAll("[^0-9,]", "").split(",");
    if (rgbs.length < 3)
      return "#000000";
    String color = "#" + Integer.toHexString(Integer.parseInt(rgbs[0]));
    color += Integer.toHexString(Integer.parseInt(rgbs[1]));
    color += Integer.toHexString(Integer.parseInt(rgbs[2]));
    return color.toUpperCase();
  }

  /**
   * Retrieves the rgb color values from RGB color code
   *
   * @param rgbColorCode The RGB code like rgb(x, x, x) or rgba(x, x, x, x)
   * @return List<Integer> An array of integers for rgb colors
   */
  public List<Integer> getRgbValues(String rgbColorCode) {
    Pattern p = Pattern.compile("[0-9]+");
    Matcher m = p.matcher(rgbColorCode);
    List<Integer> rgb = new ArrayList<>();
    while (m.find()) {
        int n = Integer.parseInt(m.group());
        rgb.add(n);
    }
    return rgb;
  }

  /**
   * Check if the RGB color is a shade of red
   *
   * @param rgbColorCode
   * @return {boolean} True if shade of red; otherwise false
   */
  public boolean isColorRed(String rgbColorCode) {
    List<Integer> rgb = getRgbValues(rgbColorCode);
    return rgb.get(0) > 127 && rgb.get(1) < 128 && rgb.get(2) < 128;
  }

  /**
   * Check if the RGB color is a shade of green
   *
   * @param rgbColorCode
   * @return {boolean} True if shade of green; otherwise false
   */
  public boolean isColorGreen(String rgbColorCode) {
    List<Integer> rgb = getRgbValues(rgbColorCode);
    return rgb.get(0) < 128 && rgb.get(1) > 127 && rgb.get(2) < 128;
  }

  /**
   * right clicks on a web element using Actions class
   *
   * @param targetElement
   */
  public void rightClickOnWebElement(WebElement targetElement) {
    new Actions(this.driver).moveToElement(targetElement).contextClick(targetElement).build().perform();
  }

  /**
   * Double clicks on a web element using Actions class
   *
   * @param targetElement
   */
  public void doubleClickOnWebElement(WebElement targetElement) {
    new Actions(this.driver).moveToElement(targetElement).doubleClick().build().perform();
  }

  public void getSearchBoxUsingActionsClass() {
    Actions action=new Actions(driver);
    String osName=System.getProperty("os.name");
    if(osName.startsWith("Mac")) {
      action.keyDown(Keys.COMMAND).sendKeys("F").keyUp(Keys.COMMAND).build().perform();
    }
    else{
      action.keyDown(Keys.CONTROL).sendKeys("F").keyUp(Keys.CONTROL).build().perform();
    }
  }

  public WebDriver getDriver() {
    return this.driver;
  }

  /**
   * Do type on the given input element on any page
   * @param e input element to enter input
   * @param text text string to enter
   */
  public void type(WebElement e, String text) {
    click(e, Time.TWOSECOND);
    e.clear();
    DateTimeUtils.sleepMilliSecondsAsLastResortBecauseNothingElseWorks(200);
    e.sendKeys(text);
    e.sendKeys(Keys.TAB);
    if (!e.getAttribute("value").equals(text)) {
      log.error(String.format("[%s] is not typed correctly into the text field. Re-type...", text));
      e.clear();
      e.sendKeys(text);
      e.sendKeys(Keys.TAB);
    }
  }

  /**
   * check title of the modal pop up after login - JIT
   *
   * @return boolean
   */
  public boolean checkACRSTitle(WebDriver driver, String titleString) {
    DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(Time.SHORT);
    assert isElementPresent(acrsTitle, Time.SHORT) : "Not landed on JIT Page";
    for(int i=0;i<12;i++){
      if (titleString.equalsIgnoreCase(acrsTitle.getText())) {
        acrsCloseButton.click();
        return true;
      }
      else
        DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(10);
    }
    return false;
  }

  /**
   * Checks whether the element is enabled
   * @return boolean
   */
  public boolean isElementEnabled(WebElement element) {
    isElementPresent(element,Time.TWOSECOND);
    return element.isEnabled();
  }

  /**
   *
   * @return log from the console
   */
  public String getConsoleLog() {
    log.debug("getConsoleLog()");
    LogEntries consoleLogEntries = this.driver.manage().logs().get(LogType.BROWSER);
    String consoleLog = "";
    for (LogEntry entry : consoleLogEntries) {
      consoleLog = consoleLog + entry.getMessage() + "\r\n";
    }
    return consoleLog + "\r\n" + Time.getCurrentTimeInSpecifiedFormat("yyyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("UTC"));
  }

  /**
   * This method gets driver's capabilities and returns browser's name
   *
   * @return {String} - browser name
   */
  public String getBrowserName() {
    log.debug("getBrowserName()");
    Capabilities cap = ((RemoteWebDriver) driver).getCapabilities();
    return cap.getBrowserName();
  }

  /**
   * This method gets driver's capabilities and returns browser's version
   *
   * @return {String} - browser version
   */
  public String getBrowserVersion() {
    log.debug("getBrowserVersion()");
    Capabilities cap = ((RemoteWebDriver) driver).getCapabilities();
    return cap.getBrowserVersion();
  }

  public String jsessionID() {
    String jsessionid = "";
    if (driver.manage().getCookieNamed("JSESSIONID") != null) {
      jsessionid = driver.manage().getCookieNamed("JSESSIONID").getValue();
    }
    return jsessionid;
  }

  public void getSessionDebugInfo() {
    log.info("BasePage.getSessionDebugInfo() URL: " + getCurrentUrl());
    log.info("BasePage.getSessionDebugInfo() Browser: " + this.getBrowserName());
    log.info("BasePage.getSessionDebugInfo() Browser Version: " + this.getBrowserVersion());
    log.info("BasePage.getSessionDebugInfo() Cookies: " + this.driver.manage().getCookies().toString());
    long currentMS = System.currentTimeMillis() - (1000 * 60 * 15); // subtract 15 minutes in milliseconds
    String start = "@" + currentMS;
    long end = currentMS + (1000 * 60 * 15); // add 15 minutes in milliseconds
    String domain = "@_sourceHost=*" + ShardService.getDomain();
    log.info("BasePage.getSessionDebugInfo() Sumologic URL Helper: " + "https://service.sumologic.com/ui/index.html#section/search/" + start + "," + end + domain + "%20%22" + jsessionID() + "%22");
  }

  /**
   * Delete All cookies on SignOut
   */
  public WebDriver deleteAllCookies(WebDriver driver) {
    // delete the cookies
    driver.manage().deleteAllCookies();
    // If the delete didn't work try again.
    if (!driver.manage().getCookies().isEmpty()) {
      getSessionDebugInfo();
      DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(3); // Wait three seconds and then delete again.
      // Let's try to clear cookies via JS
      if (driver instanceof JavascriptExecutor) {
        final JavascriptExecutor jsDriver = (JavascriptExecutor) driver;
        jsDriver.executeScript("localStorage.clear();");
        jsDriver.executeScript("sessionStorage.clear();");
      }
      driver.manage().deleteAllCookies();
    }
    DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(3);
    if (!isChrome() && !isFirefox() && !isExplorer()) {
      /* Chrome, firefox and IE run in private mode, even if cookies aren't deleted here, they are when the window closes.
       * Every other browser gets asserted that cookies are empty */
      assert (driver.manage().getCookies().isEmpty());
    }
    return driver;
  }

  /**
   * Press keys CMD/CTRL + C
   */
  public void clickCopyKeysFromKeyBoard() {
    Actions action=new Actions(driver);
    String osName=System.getProperty("os.name");
    if(osName.startsWith("Mac")) {
      action.keyDown(Keys.COMMAND).sendKeys("C").keyUp(Keys.COMMAND).build().perform();
    }
    else{
      action.keyDown(Keys.CONTROL).sendKeys("C").keyUp(Keys.CONTROL).build().perform();
    }
  }

  /**
   * Press keys CMD/CTRL + X
   */
  public void clickCutKeysFromKeyBoard() {
    Actions action=new Actions(driver);
    String osName=System.getProperty("os.name");
    if(osName.startsWith("Mac")) {
      action.keyDown(Keys.COMMAND).sendKeys("X").keyUp(Keys.COMMAND).build().perform();
    }
    else{
      action.keyDown(Keys.CONTROL).sendKeys("X").keyUp(Keys.CONTROL).build().perform();
    }
  }

  /**
   * Press keys CMD/CTRL + P
   */
  public void clickPasteKeysFromKeyBoard() {
    Actions action=new Actions(driver);
    String osName=System.getProperty("os.name");
    if(osName.startsWith("Mac")) {
      action.keyDown(Keys.COMMAND).sendKeys("V").keyUp(Keys.COMMAND).build().perform();
    }
    else{
      action.keyDown(Keys.CONTROL).sendKeys("V").keyUp(Keys.CONTROL).build().perform();
    }
  }

  /**
   * Press keys Left Arrow
   */
  public void clickLeftArrowKey() {
    Actions action=new Actions(driver);
    action.keyDown(Keys.ARROW_LEFT).keyUp(Keys.ARROW_LEFT).build().perform();
  }

  /**
   * Press keys Right Arrow
   */
  public void clickRightArrowKey() {
    Actions action=new Actions(driver);
    action.keyDown(Keys.ARROW_RIGHT).keyUp(Keys.ARROW_RIGHT).build().perform();
  }

  /**
   * Press keys Up Arrow
   */
  public void clickUpArrowKey() {
    Actions action=new Actions(driver);
    action.keyDown(Keys.ARROW_UP).keyUp(Keys.ARROW_UP).build().perform();
  }

  /**
   * Press keys Down Arrow
   */
  public void clickDownArrowKey() {
    Actions action=new Actions(driver);
    action.keyDown(Keys.ARROW_DOWN).keyUp(Keys.ARROW_DOWN).build().perform();
  }

  /**
   * Explicit wait for the spinner to be done
   * @param timeOut  time in seconds
   * @return boolean whether the spinner is done or not after the given time
   */
  public boolean isSpinnerDone(int timeOut) {
    if (waitUntilProgressDone(spinnerByLocator, timeOut)) {
      return true;
    }
    log.debug("isSpinnerDone: The Spinner is not done after " + timeOut + " seconds.");
    return false;
  }

  /**
   * Check if the given progressLocator is no longer present after the given timeOut time
   * @param progressLocator the locator to check
   * @param timeOut         time out in seconds
   * @return boolean
   */
  public boolean isProgressDone(By progressLocator, int timeOut) {
    if (waitUntilProgressDone(progressLocator, timeOut)) {
      return true;
    }
    log.debug("isProgressDone: The progressLocator is not done after " + timeOut + " seconds.");
    return false;
  }

  /**
   * Wait for the action progress bar to be done or timeout
   * This is for progress bar in microservice
   * @param timeOut max time to wait
   */
  public void waitForActionProgressBarDone(int timeOut) {
    boolean isProgressDone = waitUntilProgressDone(actionProgressbarByLocator, timeOut);
    assert isProgressDone : "Progress bar is not done after " + timeOut + "s";
  }

  /**
   * Waits for the spinner to be dismissed and reports error after timeout
   *
   * @param timeout Max time to wait for spinner to disappear, in seconds
   */
  public void waitForSpectrumSpinner(int timeout) {
    log.debug("waitForSpectrumSpinner()");
    waitForSpectrumSpinner(timeout, Time.SHORTEST);
  }

  /**
   * Waits for the spinner to be dismissed and reports error after timeout
   *
   * @param timeout Max time to wait for spinner to disappear, in seconds
   * @param implicitlyWait Max time to wait if spinner never exists or has gone before checking the condition
   */
  public void waitForSpectrumSpinner(int timeout, int implicitlyWait) {
    log.debug("waitForSpectrumSpinner()");
    setDriverImplicitlyWait(implicitlyWait, TimeUnit.SECONDS);
    try {
      if (isElementVisible(spectrumSpinnerLocator, Time.SHORTEST)) {
        assert waitUntilProgressDone(spectrumSpinnerByLocator, timeout) : String
            .format("Spinner is still present after waiting for %s seconds", timeout);
      }
    }
    finally {
      // make sure implicit wait is reset to default even after exception
      turnOnImplicitWait();
    }
  }
}
