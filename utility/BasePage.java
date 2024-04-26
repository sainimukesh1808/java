package pages.base.postlogin;

import static org.testng.Assert.assertEquals;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.ImmutableMap;
import custom.strings.Locale_en_US;
import custom.strings.Locale_en_US_Errors;
import io.appium.java_client.AppiumDriver;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.AssertJUnit;
import pages.HelpxSignPage;
import pages.Page;
import pages.PageUrlPaths;
import pages.account.AccountPage;
import pages.account.MySignaturePage;
import pages.account.PersonalPreferencesPage;
import pages.account.SendSettingsPage;
import pages.account.advancedAccountSharing.UnauthorizedPath;
import pages.accountSettingsPage.AddressBookRecipientGroupsPage;
import pages.accountSettingsPage.DataGovernancePage;
import pages.accountSettingsPage.GroupListPage;
import pages.accountSettingsPage.MyProfilePage;
import pages.accountSettingsPage.PrivacyAdminPage;
import pages.accountSettingsPage.containers.NavigationContainer;
import pages.base.containers.AboutBox;
import pages.base.containers.ChatBotPopup;
import pages.base.containers.CookiePreferencesContainer;
import pages.base.containers.GainsightGuidePopup;
import pages.base.containers.Header;
import pages.biReports.BiReportsPage;
import pages.fillAndSign.FillAndSignPage;
import pages.gps.IMSPage;
import pages.home.HomePage;
import pages.home.SectionName;
import pages.homeJS.HomeJSPage;
import pages.login.LoginPage;
import pages.manage.ManagePage;
import pages.manageAgreement.ManagePageJS;
import pages.reports.ReportsPage;
import pages.requestSignatures.RequestSignaturesComposePage;
import pages.saml.OneloginSSOLoginPage;
import pages.send.ArchivePage;
import pages.send.MegaSignPage;
import pages.send.OnlyISignPage;
import pages.sendJS.CreateLibraryTemplateJSPage;
import pages.sendJS.SendJSPage;
import pages.widget.SignVerifyPage;
import pages.widget.WidgetCreatePage;
import pages.workflows.WorkflowsTabBasePage;
import pages.workflows.powerAutomate.PowerAutomateWorkflowsPage;
import service.PropertiesService;
import service.ShardService;
import utils.APIUtils;
import utils.BrowserUtils;
import utils.DateTimeUtils;
import utils.Env;
import utils.FileDownloader;
import utils.FileUtils;
import utils.Time;
import utils.TimeDuration;
import vo.Domain;
import vo.RegisteredUser;
import webdriver.DriverWrapper;
import webdriver.TestProperties;

public abstract class BasePage extends Page {

  boolean IDLE = Boolean.parseBoolean(PropertiesService.getServiceProperty("test.idle", true));
  private final String SIGNOUT_URL = getEchoSignURL(PageUrlPaths.SIGNOUT);
  private final String ACCOUNT_SETTINGS_PAGE_URL = getEchoSignURL(PageUrlPaths.ACCOUNT_SETTING_PAGE);
  public int waitTime = Time.LONGEST;
  public int RETRY = 15;
  private static final String FOCUS_RING = "focus_ring";

  public static String homeShard = ShardService.getHomeShard();
  public static int defaultRetry = getDefaultRetry(5); // Starting 9.3, there will be delay when sending agreement to signer cross shard, so
  // need to wait longer for cross shard testing.
  public static String DOCLOCATION = "documents" + System.getProperty("file.separator") + "ES_AUTOMATION_TEST.docx";
  public static String CSVLOCATION = "documents" + System.getProperty("file.separator") + "MegaSign_EmailAlias.csv";
  protected String sDocLocation = "documents" + System.getProperty("file.separator") + "ES_AUTOMATION_TEST.pdf";

  /////mobile/////
  public static final String APPIUM_CHROMIUM_CONTEXT = "CHROMIUM";
  public static final String APPIUM_NATIVEAPP_CONTEXT = "NATIVE_APP";
  public static final String MOBILE_BROWSER_CHROME = "m_chrome";
  public static final String MOBILE_BROWSER_SAFARI = "m_safari";
  public static final String MODERN_ESIGN_TEST_CASE_IDENTIFIER = "modernESignExperience-";
  public static final String MODERN_ESIGN_JOB_IDENTIFIER = "ModernEsignEnabled";
  public static final String MODERN_ESIGN_TEST_CASE_ANNOTATION_IDENTIFIER = "ModernESign";
  public static String DEFAULT_BUTTON = "btn-default";
  public static String PRIMARY_BUTTON = "btn-primary";

  /*
   * locators
   */

  // The locators for these elements should only be defined once.
  @FindBy(xpath = "//*[@id='home-tab']/a")
  private WebElement dashboardTabLocator;

  @FindBy(xpath = "//h3/a[@href='/public/approve']")
  @CacheLookup
  private WebElement onlyISignLinkLocator;

  @FindBy(xpath = "//h3/a[@href='/public/fillSign']") // LOC: Updated from linkText
  @CacheLookup
  private WebElement fillAndSignLinkLocator;

  @FindBy(xpath = "//h3/a[@href='/public/composeMega']")
  @CacheLookup
  private WebElement megaSignLinkLocator;

  @FindBy(xpath = "//h3/a[@href='/account/addDocumentToLibrary']")
  // LOC: Updated from linkText = Add Document To sLibrary
  private WebElement addTemplatetoLibraryLinkLocator;

  @FindBy(xpath = "//h3/a[@href='/account/archive']") // LOC: Updated from linkText = Archive a Document
  @CacheLookup
  private WebElement archiveDocLocator;

  @FindBy(id = "manage-tab")
  private WebElement manageTabLocator;

  @FindBy(xpath = "//li[@id ='nav-user-tab']/a[1]")
  private WebElement navUserTabLocator;

  @FindBy(id = "show-quick-start-menuitem")
  private WebElement quickStartGuideLinkLocator;

  // Used on page following a Send, including PostSend and Manage
  @FindBy(xpath = "//div[@id='post-send-text']/h1[1]")
  //@FindBy(xpath = "//div[@id='post-send-options']//div[@id='post-send-text']/p[@class='alert']")
  private WebElement sendStatusMessageLocator;

  @FindBy(xpath = "//*[@id='replace-form']/p")
  private WebElement replaceCurrentSignerorApproverMessageLocator;

  @FindBy(id = "post-send-text")
  private WebElement sendStatusMessageWithBodyLocator;

  // Used on page following a Send, incl PostSend and Manage
  @FindBy(id = "global-message-text")
  private WebElement globalMessageLocator;
  private By globalMessageByLocator = new By.ById("global-message-text");

  @FindBy(id = "global-error-text")
  private WebElement globalErrorLocator;

  @FindBy(id = "server-messages")
  private WebElement serverMessagesLocator;


  @FindBy(css = "div.create-widget>h3>a") // LOC: Replaced linkText = "Create Widget"
  @CacheLookup
  private WebElement createWidgetLinkLocator;

  @FindBy(linkText = "Send Settings")
  @CacheLookup
  private WebElement sendSettingstLinkLocator;

  @FindBy(xpath = "//li[@title='Personal Preferences']/div[@class='nav-li-container']/div[contains(@class,'navigation-arrow')]")
  @CacheLookup
  private WebElement personalPreferencesLinkLocator;

  @FindBy(xpath = "//li[@title='Account Settings']/div[@class='nav-li-container']/div[contains(@class,'navigation-arrow')]")
  @CacheLookup
  private WebElement accountSettingsLinkLocator;

  @FindBy(css = "[data-pageid=\"ACCOUNT_SETTINGS\"]")
  private WebElement accountSettingsLocator;

  @FindBy(className = "dropped")
  private WebElement droppedLocator;

  @FindBy(xpath = "//li[@title='My Signature']/div[1]")
  @CacheLookup
  private WebElement mySignatureLinkLocator;

  @FindBy(xpath = "//*[@data-pageid='PRIVACY']//div[@class='navLabel']")
  @CacheLookup
  private WebElement privacyPageLinkLocator;

  @FindBy(xpath = "//*[@data-pageid='Address Book']//div[@class='navLabel parent']")
  private WebElement addressBookLinkLocator;

  @FindBy(xpath = "//*[@data-pageid='Address Book']/div[@class='nav-li-container']")
  private WebElement addressBookNavigationArrowLocator;

  @FindBy(xpath = "//*[@data-pageid='ADDRESS_BOOK_RECIPIENT_GROUPS']//div[@class='navLabel']")
  private WebElement recipientGroupsLinkLocator;

  @FindBy(id = "logo")
  private WebElement logoLocator;

  @FindBy(css = "a.header-logo")
  private WebElement defaultLogoLocator;

  @FindBy(xpath = "//li[contains(@class,'account-tab')]/a[contains(@href,'GROUP')]")
  @CacheLookup
  private WebElement groupTabLocator;

  @FindBy(css = "div#post-send-text h1")
  private WebElement sendStatusHeaderMessageLocator;

  // added by Swathi
  @FindBy(id = "change-account-view")
  private WebElement changeAccountViewLocator;

  @FindBy(id = "id-navbar-dropdown-container")
  private WebElement navbarDropdownTabLocator;

  @FindBy(className = "navbar-dropdown-container-dropped")
  private WebElement navbarDroppedLocator;


  @FindBy(css = "#log-out > a")
  private WebElement logOutLocator;

  @FindBy(css = "a#logOut")
  private WebElement logOutButtonLocator;

  @FindBy(id = "walkme-player")
  private WebElement walkmeLocator;

  @FindBy(css = "#switch-account-menu-item>a")
  private WebElement switchAccountLocator;

  @FindBy(className = "nav-trefoil")
  private WebElement companyNameDocumentCloudLocator;

  private final String dashboardTabCss = "#home-tab";
  private final String sendTabCss = "#send-tab";
  private final String reportsTabCss = "#reports-tab";
  private final String accountTabCss = ".account-tab";

  @FindBy(css = ".tab-nav-wrapper .icon-navbar-dropdown-triangle")
  private WebElement navbarDropdownTriangleLocator;

  @FindBy(id = "id-navbar-dropdown")
  private WebElement navbarDropdownLocator;

  @FindBy(id = "log-out")
  private WebElement logoutLocator;
  @FindBy(css = ".modal.fade.in .modal-dialog .modal-content .modal-footer .btn.btn-default")
  private WebElement cancelButtonLocator;

  @FindBy(className = "btn-primary")
  private WebElement primaryButtonLocator;

  @FindBy(className = "btn-default")
  private WebElement defaultButtonLocator;

  @FindBy(css = ".modal-dialog .btn-primary")
  private WebElement modalPrimaryButtonLocator;

  @FindBy(css = ".modal-dialog .btn-default")
  private WebElement modalDefaultButtonLocator;

  @FindBy(css = ".modal.fade.show .modal-dialog .modal-content .modal-body")
  private WebElement warningMessageLocator;

  @FindBy(css = ".modal.fade.show .modal-dialog .modal-content .modal-footer .btn.btn-primary")
  private WebElement warningOkButtonLocator;

  @FindBy(css = ".modal.fade.show .modal-dialog .modal-content .modal-footer .btn.btn-primary")
  private WebElement warningReloadButtonLocator;

  @FindBy(css = ".modal.fade.show .modal-dialog .modal-content .modal-header .close")
  private WebElement warningCloseButtonLocator;

  @FindBy(xpath = "//img[@class='icon-help']")
  private WebElement helpLinkLocator;

  @FindBy(id = "coreLegalNoticesLink")
  private WebElement legalNoticesLinkLocator;

  @FindBy(xpath = "//a[@data-analytics-click='help-guide']")
  private WebElement helpDropdownUserGuideLocator;

  @FindBy(xpath = "//a[@data-analytics-click='help-tutorial']")
  private WebElement helpDropdownTutorialsLocator;

  @FindBy(xpath = "//a[@data-analytics-click='help-support']")
  private WebElement helpDropdownContactSupportLocator;

  @FindBy(className = "modal-dialog")
  private WebElement modalDialogLocator;

  @FindBy(xpath = "//div[contains(@class,'modal-dialog')]/div[@class='modal-content']//h1[contains(@id,'modal-title') and @class='modal-title']")
  private WebElement modalDialogTitleLocator;

  @FindBy(xpath = "//div[contains(@class,'modal-dialog')]/div[@class='modal-content']//h3[contains(@id,'modal-body') and @class='modal-body']")
  private WebElement modalDialogBodyLocator;

  @FindBy(css = ".confirmation-dialog .modal-dialog ")
  private WebElement openLinkConfirmationDialogLocator;

  private By interstitialByLocator = new By.ByClassName("interstitial");

  @FindBy(className = "interstitial")
  private WebElement interstitialLocator;

  @FindBy(id = "_evidon-accept-button")   //WebElement accept = driver.findElement(By.id("_evidon-accept-button"));
  private WebElement cookiesAcceptButtonLocator;

  @FindBy(id = "_evidon-title")
  private WebElement CookiesAndOtherTechnologiesPopUpTitleLocator;

  @FindBy(xpath = "//*[@data-pageid='DATA_GOVERNANCE']//div[@class='navLabel']")
  private WebElement dataGovernancePageLinkLocator;

  @FindBy(xpath = "/html/body/div[1]/div[1]/div[1]/ul/li/div[2]/ul/li[4]/a/span[2]")
  private WebElement releaseNotesLinLocator;

  @FindBy(css = "li.new-features.navbar-dropdown-divider-above a")
  private WebElement releaseNotesLocator;

  @FindBy(className = "icon-help")
  private WebElement eSignHeaderSupportLocator;

  @FindBy(xpath="//div[@class='footer-lines right']//div[@class='links']/a[4]")
  private WebElement cookiePreferencesLinkLocator;

  @FindBy(xpath = "//div[@id='onetrust-consent-sdk']")
  private WebElement cookieSettingsPopUp;

  @FindBy(className = "user-account-list")
  private WebElement userAccountListLocator;

  @FindBy(className = "switch-user-account-container")
  private WebElement switchUserAccountContainerLocator;

  @FindBy(id = "onetrust-accept-btn-handler")
  private WebElement onetrustAllLocator;

  @FindBy(className = "blueBarSwitchLink")
  private WebElement blueBarSwitchLinkLocator;

  @FindBy(className = "spectrum-Toast-content")
  private WebElement alertMessage;

  @FindBy(className = "click-to-accept-tou")
  private WebElement acceptTOULocator;

  @FindBy(id = "modal-body-view99")
  private WebElement modalBodyView99Locator;

  @FindBy(id = "modal-body-view101")
  private WebElement modalBodyView101Locator;

  @FindBy(className = "modal-body")
  private WebElement modalBodyLocator;

  @FindBy(className = "modal-body")
  private List<WebElement> modalBodyListLocator;

  @FindBy(id = "logOut")
  private WebElement signOutButtonLocator;

  @FindBy(xpath = "//h3[@data-testid='title']")
  private WebElement dialogTitleLocator;

  @FindBy(className = "main-body")
  private WebElement mainBodyMessagesLocator;

  @FindBy(id = "details-send")
  protected WebElement detailsSendLocator;

  @FindBy(css = "#salesforce-chrome .control-button")
  private WebElement bumpoutOkButtonLocator;

  @FindBy(className = "modal-body")
  protected WebElement errorDialogLocator;

  @FindBy(className= "add-electronic-seal")
  protected WebElement addElectronicSealLocator;

  @FindBy(xpath = "//input[@type='button' and @value='Save']")
   private WebElement adminPushButtonLocator;

  @FindBy(className = "electronic-sealer")
  private WebElement electronicSealerLocator;

  @FindBy(css="[class*='ProgressView__topLoaderWaitSpinner']")
  private WebElement spinnerLocator;

  protected Header header;

  @FindBy(css = ".wfd-error-banner-text")
  private WebElement errorMessageBannerLocator;

  /*
   * tab navigation methods
   */

  protected BasePage(WebDriver driver) {
    super(driver);
    header = new Header(driver);
  }

  public boolean isHeaderPresent() {
    return header.isHeaderPresent();
  }

  /**
   * Returns true if Sign In link is present in header
   */
  public boolean isSignInLinkPresent() {
    return header.isSignInLinkPresent();
  }

  /**
   * Clicks Sign In link in header
   *
   * @return Page
   */
  public Page clickSignInLink() {
    return header.clickSignInLink();
  }

  /**
   * Returns the text of Sign In link in header
   */
  public String getSignInLinkText() {
    return header.getSignInLinkText();
  }

  /**
   * Checks if the OK button is present in bumped out Sign page
   *
   * @return {boolean} True if OK button is present; false otherwise
   */
  public boolean isBumpoutOkButtonPresent() {
    log.debug("isBumpoutOkButtonPresent()");
    return isElementPresent(bumpoutOkButtonLocator, Time.SHORTER);
  }

  /**
   * Clicks the OK button in bumped out Sign page to close the browser tab
   */
  public void clickBumpoutOkButton() {
    log.debug("clickBumpoutOkButton()");
    assert isBumpoutOkButtonPresent() : "OK button in bumped out page is not present";
    click(bumpoutOkButtonLocator, Time.SHORTEST);
  }

  /**
   * Dismiss Gainsight guide pop-up if present
   *
   * @param timeoutInSeconds max time to wait for Gainsight pop-up to show up
   */
  public void dismissGainsightGuideIfPresent(int timeoutInSeconds) {
    log.debug("dismissGainsightGuideIfPresent()");
    GainsightGuidePopup gainsightPopup = new GainsightGuidePopup(driver);
    if (gainsightPopup.isPresent(timeoutInSeconds)) {
      gainsightPopup.dismiss();
    }
    else {
      log.debug("no op");
    }
  }


  /**
   * Open Chatbot pop-up
   *
   * @param timeoutInSeconds max time to wait for Chatbot pop-up to show up
   */
  public ChatBotPopup openChatBot(int timeoutInSeconds) {
    log.debug("openChatBot()");
    ChatBotPopup chatBotPopup = new ChatBotPopup(driver);
    if (chatBotPopup.isPresent(timeoutInSeconds)) {
      chatBotPopup.clickChatBotButton();
    }
    else {
      log.debug("no op");
    }
    return chatBotPopup;
  }

  /**
   * Dismiss gainsight guide pop-up if present
   */
  public void dismissGainsightGuideIfPresent() {
    dismissGainsightGuideIfPresent(Time.TWOSECOND);
  }

  /**
   * Click on "Dashboard" tab leads to "Home" page
   *
   * @return {HomePage}
   */
  @Deprecated // tests that are not deprecated should be updated to use clickHomeJSTab()
  public HomePage clickDashboardTab() {
    header.clickHomeTab(false);
    return new HomePage(driver);
  }

  /**
   * Click on "Home" tab
   *
   * @return {LoginPage}
   */
  public LoginPage clickHomePageTab() {
    header.clickHomeTab(false);
    return new LoginPage(driver);
  }

  /**
   * Click on "Home" tab leads to "HomeJS" page
   *
   * @return {HomeJSPage}
   */
  public HomeJSPage clickHomeJSTab() {
    return header.clickHomeTab();
  }

  /**
   *Checks if Home Tab is Enabled
   * @return {boolean} if Home Tab is Enabled or not
   */
  public boolean isDashboardTabEnabled() {
    return header.isHomeTabEnabled();
  }

  /**
   * Checks if Send Tab is Enabled
   * @return {boolean} if Send Tab is Enabled or not
   */
  public boolean isSendTabEnabled() {
    return header.isSendTabEnabled();
  }

  /**
   * Checks if Manage Tab is Enabled
   * @return {boolean} if Manage Tab is Enabled or not
   */
  public boolean isManageTabEnabled() {
    return header.isManageTabEnabled();
  }

  /**
   *Checks if Reports Tab is Enabled
   * @return {boolean} if Reports Tab is Enabled or not
   */
  public boolean isReportsTabEnabled() {
    return header.isReportsTabEnabled();
  }

  /**
   *Checks if Account Tab is Enabled
   * @return {boolean} if Account Tab is Enabled or not
   */
  public boolean isAccountTabEnabled() {
    return header.isAccountTabEnabled();
  }

  /**
   *Checks if Fill & Sign Tab is present
   * @return {boolean} if Fill & Sign  Tab is present or not
   */
  public boolean isFillAndSignTabPresent(){
    return header.isFillAndSignTabPresent();
  }

  /**
   *Checks if Create Library Template Tab is present
   * @return {boolean} if Library Template Tab is present or not
   */
  public boolean isCreateLibraryTemplateTabPresent(){
    return header.isCreateLibraryTemplateTabPresent();
  }

  /**
   *Checks if Create Web Form Tab is present
   * @return {boolean} if Create Web Form Tab is present or not
   */
  public boolean isCreateWebFormTabPresent(){
    return header.isCreateWebFormTabPresent();
  }

  /**
   *Checks if Create Workflows Tab is present
   * @return {boolean} if Create Workflows Tab is present or not
   */
  public boolean isCreateWorkflowTabPresent(){
    return header.isCreateWorkflowTabPresent();
  }

  /**
   *Checks if Fill & Sign Tab is present
   * @return {boolean} if Fill & Sign  Tab is present or not
   */
  public boolean isLogOutButtonDisplayed() {
    return isElementVisible(driver, logOutButtonLocator, Time.SHORTER);
  }

  /**
   * Click on "Only I Sign" link leads to "Only I Sign" page
   *
   * @return {OnlyISignPage}
   */
  public OnlyISignPage clickOnlyISignLink() {
    log.debug("clickOnlyISignLink()");
    isSpinnerDone(Time.SHORT);
    click(onlyISignLinkLocator, Time.SHORT);
    OnlyISignPage onlyISignPage = new OnlyISignPage(driver);
    isSpinnerDone(Time.SHORT);
    return onlyISignPage;
  }

  /**
   * Click on "Fill & Sign" link leads to "Fill & Sign" page
   *
   * @return {FillAndSignPage}
   */
  public FillAndSignPage clickFillAndSignLink() {
    log.debug("clickFillAndSignLink()");
    isSpinnerDone(Time.SHORT);
    click(fillAndSignLinkLocator, Time.SHORT);
    FillAndSignPage fillAndSignPage = new FillAndSignPage(driver);
    isSpinnerDone(Time.SHORT);
    return fillAndSignPage;
  }

  /**
   * Click on "Mega Sign" link leads to "Mega Sign" page
   *
   * @return {MegaSignPage}
   */
  public MegaSignPage clickMegaSignLink() {
    log.debug("clickMegaSignLink()");
    isSpinnerDone(Time.SHORT);
    click(megaSignLinkLocator, Time.LONG);
    assert validateCurrentPage(PageUrlPaths.MEGASIGN) : validationAssertFailureMessage(PageUrlPaths.MEGASIGN);
    MegaSignPage megaSignPage = new MegaSignPage(driver);
    isSpinnerDone(Time.LONGER);
    return megaSignPage;
  }

  /**
   * Click on "Add Template to Library" link leads to new "Template Library" page when ENABLE_SEND_JS is true
   *
   * @return {CreateLibraryTemplateJSPage}
   */
  public CreateLibraryTemplateJSPage clickAddTemplateToLibraryJS() {
    log.debug("clickAddTemplateToLibraryJS()");
    isSpinnerDone(Time.SHORT);
    click(addTemplatetoLibraryLinkLocator, Time.SHORT);
    assert validateCurrentPage(PageUrlPaths.TEMPLATELIBRARY) : validationAssertFailureMessage(PageUrlPaths.TEMPLATELIBRARY);
    CreateLibraryTemplateJSPage createLibraryTemplateJSPage = new CreateLibraryTemplateJSPage(driver);
    isSpinnerDone(Time.SHORT);
    return createLibraryTemplateJSPage;
  }

  /**
   * Click on "Archive a Document" link leads to "Archive" page
   *
   * @return {ArchivePage}
   */
  public ArchivePage clickArchiveDocument() {
    log.debug("clickArchiveDocument()");
    isSpinnerDone(Time.SHORT);
    click(archiveDocLocator,  Time.SHORT);
    assert validateCurrentPage(PageUrlPaths.ARCHIVE) : validationAssertFailureMessage(PageUrlPaths.ARCHIVE);
    ArchivePage archivePage = new ArchivePage(driver);
    isSpinnerDone(Time.SHORT);
    return archivePage;
  }

  /**
   * Click on "Create Widget" link leads to "Widget Create" page
   *
   * @return {WidgetCreatePage}
   */
  public WidgetCreatePage clickCreateWidgetLink() {
    log.debug("clickCreateWidgetLink()");
    isSpinnerDone(Time.SHORT);
    click(createWidgetLinkLocator, Time.SHORT);
    assert validateCurrentPage(PageUrlPaths.WIDGET_CREATE) : validationAssertFailureMessage(PageUrlPaths.WIDGET_CREATE);
    WidgetCreatePage widgetCreatePage = new WidgetCreatePage(driver);
    isSpinnerDone(Time.SHORT);
    return widgetCreatePage;
  }

  /**
   * A generic click tab method to encapsulate common functionality
   *
   * @param locator the locator for the tab to be clicked
   * @param pageUrl the expected URL to land on after clicking
   */
  public void clickTab(WebElement locator, String pageUrl) {
    try {
      isSpinnerDone(Time.SHORT);
      click(locator, Time.SHORT);
      isSpinnerDone(Time.LONG);
      assert validateCurrentPage(pageUrl) : validationAssertFailureMessage(pageUrl);
    }
    catch (TimeoutException te) {
      log.debug("=> Failed to click tab " + locator + ", so navigating directly to " + pageUrl + " instead");
      navigateToWithoutValidation(pageUrl);
    }
  }

  /**
   * Click on "Send" tab leads to "Send" page
   *
   * @return {SendPage}
   */
  public SendJSPage clickSendJSTab() {
    return header.clickSendTab();
  }

  /**
   * Click on "Send" tab leads to "Request Signatures" page
   *
   * @return {RequestSignaturePage}
   */
  public RequestSignaturesComposePage clickSendRSTab() {
    return header.clickSendRSTab();
  }

  /**
   * Click on "Send" tab leads to "Send" page
   *
   * @return {SendPage}
   */
  public void clickSendJSTabOnly() {
    header.clickSendTab(false);
  }

  public UnauthorizedPath clickSendTabAdvAccSwitch() {
    isSpinnerDone(Time.SHORT);
    clickSendJSTabOnly();
    UnauthorizedPath unauthorizedPath = new UnauthorizedPath(driver);
    isSpinnerDone(Time.SHORT);
    return unauthorizedPath;
  }

  public UnauthorizedPath clickReportsTabAdvAccountSwitch() {
    log.debug("clickReportsTab()");
    isSpinnerDone(Time.SHORT);
    assert !header.isReportsTabEnabled() : "Reports tab should not be enabled for sharee with account switch";
    if (header.isReportsTabPresent()) {
      header.clickReportsTabOnly();
      UnauthorizedPath unauthorizedPath = new UnauthorizedPath(driver);
      isSpinnerDone(Time.SHORT);
      return unauthorizedPath;
    }
    return null;
  }

  public BasePage clickReportsTab(boolean isBIReportPage) {
    return header.clickReportsTab(isBIReportPage);
  }

  /**
   * Click on "Manage" tab leads to "Manage" page
   *
   * @return {ManagePage}
   */
  public ManagePage clickManageTab() {
    log.debug("clickManageTab()");
    isSpinnerDone(Time.LONG);
    clickTab(manageTabLocator, PageUrlPaths.MANAGE);
    ManagePage managePage = new ManagePage(driver);
    DateTimeUtils.sleepMilliSecondsAsLastResortBecauseNothingElseWorks(250);
    assert (managePage.isManagePageLoaded(waitTime)) : this.getClass().getCanonicalName()
        + ": manage page is not loaded after clickManageTab";
    return managePage;
  }

  public void clickManageTabOnly() {
    log.debug("clickManageTab()");
    isSpinnerDone(Time.LONG);
    header.clickManageTab(false);
    isSpinnerDone(Time.LONG);
  }

  /**
   * click Manage Tab and will either go to ManagePage or ManagePageJS based on isUsingNewUI
   * @param isUsingNewUI
   * @return
   */
  public Page clickManageTab(boolean isUsingNewUI) {
    if (isUsingNewUI) {
      ManagePageJS managePageJS = header.clickManageTab();
      dismissGainsightGuideIfPresent();
      isSpinnerDone(Time.LONG);
      return managePageJS;
    }
    else {
      throw new AssertionError("Manage V2 is deprecated. Update test to use Manage V4");
    }
  }

  /**
   * Click on "Workflows" tab leads to "WorkflowsPage" page
   *
   * @return {WorkflowsPage}
   */
  public WorkflowsTabBasePage clickWorkflowsTab() {
    log.debug("clickWorkflowsTab()");
    return header.clickWorkflowsTab();
  }

  /**
   * Click on "Workflows" tab leads to "WorkflowsPage" page which loads Power Automate page by default
   * Keeping method for backward compatibility
   *
   * @return {PowerAutomateWorkflowsPage}
   */
  public PowerAutomateWorkflowsPage clickWorkflowsPATab() {
    log.debug("clickWorkflowsPATab()");
    WorkflowsTabBasePage workflowsTabBasePage = clickWorkflowsTab();
    PowerAutomateWorkflowsPage powerAutomateWorkflowsPage =  new PowerAutomateWorkflowsPage(driver);
    if(!powerAutomateWorkflowsPage.isLoaded()) {
      powerAutomateWorkflowsPage = workflowsTabBasePage.clickPowerAutomateOption();
      assert powerAutomateWorkflowsPage.isLoaded() : "'Power Automate' page NOT loaded";
    }
    return powerAutomateWorkflowsPage;
  }

  /**
   * Click on "Reports" tab leads to "Reports" page
   *
   * @return {ReportsPage}
   */
  public ReportsPage clickReportsTab() {
    header.clickReportsTab(false);
    ReportsPage reportsPage =  new ReportsPage(driver);
    isSpinnerDone(Time.SHORT);
    return reportsPage;
  }

  /**
   * Click on "Reports" tab leads to "BiReports" page when BI Reports are enabled
   *
   * @return {BiReportsPage}
   */
  public BiReportsPage clickBiReportsTab() {
    header.clickReportsTab(true);
    waitForPageLoadComplete(driver);
    BiReportsPage biReportsPage = new BiReportsPage(driver);
    if (!isSpinnerDone(Time.SHORT)) {
      browserRefresh();
    }
    assert isSpinnerDone(Time.LONG) : "Click on BI Report Tab, the page is not loaded after 45s";
    DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(1); //to render the number on left panel
    return biReportsPage;
  }

  /**
   * LOC:
   *Checks if Account Settings is dropped
   * @return {boolean} if Account Settings is dropped or not
   */
  private boolean isAccountSettingsSelected(String accountSettingsText) {
    log.debug("isAccountSettingsSelected()");
    accountSettingsLinkLocator = driver.findElement(By.xpath("//li[@title='"+accountSettingsText+"']/div[@class='nav-li-container']/div[contains(@class,'navigation-arrow')]"));
    if (accountSettingsLinkLocator.getAttribute("class").contains("dropped"))
      return true;
    else {
      return false;
    }
  }

  private boolean isUsersSelected(String usersText) {
    log.debug("isUsersSelected()");
    WebElement usersLinkLocator = driver.findElement(By.xpath("//li[@title='"+usersText+"']/div[@class='nav-li-container']/div[contains(@class,'navigation-arrow')]"));
    if (usersLinkLocator.getAttribute("class").contains("dropped"))
      return true;
    else {
      return false;
    }
  }

  /**
   * Click on navigation user dropdown menu
   *
   */
  public BasePage openNavUserMenu() {
    header.openUserProfileMenu();
    return this;
  }

  /**
   * Click on "User Navigation" tab
   *
   * @return {BasePage}
   */
  public BasePage clickNavUserTab() {
    header.clickNavUserTab();
    return this;
  }

  /**
   * Returns Contact Support url
   */
  public String getContactSupportUrl() {
    return header.getContactSupportUrl();
  }

  /**
   * Click on "Contact Support" link
    */
  public void clickContactSupport() {
    header.clickContactSupport();
  }

  /**
   * Navigate to "Account" page. Leads to legacy "Account" page, a.k.a. the legacy Account Admin Settings page
   *
   * @return {AccountPage}
   */
  public AccountPage navigateToLegacyAccountPage() {
    log.debug("navigateToLegacyAccountPage() - behavior is to navigate directly to legacy Account Admin Settings page");
    navigateToPathWithinSameDomain(PageUrlPaths.ACCOUNT);
    // check URL repeatedly up to max time to verify match
    for (int i = 0; i <= Time.SHORT; i++) {
      if (getCurrentUrl().contains(PageUrlPaths.ACCOUNT)) {
        break;
      }
      else {
        DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(1);
      }
    }
    DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(1);

    assert getCurrentUrl().contains(PageUrlPaths.ACCOUNT_SETTING_PAGE) : "Expected to land on Account Admin page, but got "
        + getCurrentUrl();
    assert getCurrentUrl().contains(PageUrlPaths.ACCOUNT) : "Expected to land on legacy Account Admin page, but got " + getCurrentUrl();
    return new AccountPage(driver);
  }

  /**
   * Go to New Account Admin URL directly
   *
   * @return NavigationContainer
   */
  public NavigationContainer clickAccountSettingPage() {
    log.debug("clickAccountSettingPage()");
    // Following line is commented because of possible changes of Base URL
    // this.driver.get(ACCOUNT_SETTINGS_PAGE_URL);
    this.driver.get(getActualBaseURL(driver.getCurrentUrl()) + PageUrlPaths.ACCOUNT_SETTING_PAGE);
    return new NavigationContainer(driver);
  }

  /**
   * Click the tab for Account/Group/Groups Tab, and then navigate to Account Setting page
   *
   * @return {NavigationContainer}
   */
  public NavigationContainer navigateToAccountSettingPage() {
    log.debug("navigateToAccountSettingPage()");
    clickAccountAdminTab();
    this.isSpinnerDone(Time.SHORT);
    waitUntilClickable(Time.SHORT, droppedLocator);
    click(accountSettingsLocator, Time.SHORT);
    NavigationContainer navigationContainer = new NavigationContainer(driver);
    navigationContainer.isSpinnerDone(Time.SHORT);
    return navigationContainer;
  }

  public NavigationContainer navigateToAccountSettingsPage() {
    log.debug("navigateToAccountSettingsPage()");
    return header.clickAccountAdminTab();
  }

  /**
   * Overloading to pass localized text for Account Settings locator
   * @param accountSettingsText
   * @return
   */
  public NavigationContainer navigateToAccountSettingPage(String accountSettingsText) {
    log.debug("navigateToAccountSettingPage()");
    header.clickAccountTab();
    if (!isAccountSettingsSelected(accountSettingsText)) {
      accountSettingsLinkLocator = driver.findElement(By.xpath("//li[@title='"+accountSettingsText+"']/div[@class='nav-li-container']/div[contains(@class,'navigation-arrow')]"));
      click(accountSettingsLinkLocator, Time.LONG);
    }
    return new NavigationContainer(driver);
  }

  /**
   * IMS users now have a list under users showing separately Entitled and Pending user lists, so we need to click on the arrow to make items visible
   *
   * @param usersText - title of the users section
   */
  public NavigationContainer navigateToUsersPage(String usersText) {
    log.debug("navigateToUsersPage()");
    header.clickAccountTab();
    if (!isUsersSelected(usersText)) {
      WebElement usersLinkLocator = driver.findElement(By.xpath("//li[@title='"+usersText+"']/div[@class='nav-li-container']/div[contains(@class,'navigation-arrow')]"));
      click(usersLinkLocator, Time.LONG);
    }
    return new NavigationContainer(driver);
  }

  /**
   * Click the Group tab.
   *
   * @return {NavigationContainer}
   */
  public NavigationContainer clickGroupTab() {
    return header.clickGroupTab();
  }

  /**
   * LOC:
   * Click the Group tab.
   *
   * @return {NavigationContainer}
   */
  public NavigationContainer clickGroupTab(String groupLinkText) {
    log.debug("clickGroupTab()");
    String tabName = header.getAdminTabName();
    assertEquals(tabName, groupLinkText, "Incorrect tab name");
    return clickGroupTab();
  }

  /**
   * Click on "Account" tab leads to Account Settings page
   *
   * @return {NavigationContainer}
   */
  public NavigationContainer clickAccountTab() {
    header.clickAccountTab();
    return new NavigationContainer(driver);
  }

  /**
   * Click on "My Profile" tab leads to "My Profile" page
   *
   * @return {MyProfilePage}
   */
  public MyProfilePage clickMyProfilePage() {
    return header.clickMyProfilePage();
  }

  /**
   * Click on "Sign Out" returns to Login page
   *
   * @return {Page}
   */
  public Page clickSignOut() {
    return header.clickSignOut();
  }

  /**
   * Click on "Quick Start Guide" link
   *
   * @return {BasePage}
   */
  public BasePage clickQuickStartGuideLink() {
    log.debug("clickQuickStartGuideLink()");
    waitUntilVisible(Time.LONGER, quickStartGuideLinkLocator);
    ;
    if (isUserAgentExplorer()) {
      execScript("arguments[0].click()", quickStartGuideLinkLocator);
    }
    else {
      // Hover over the Sign Out text before clicking
      Actions actions = new Actions(driver);
      actions.moveToElement(quickStartGuideLinkLocator).click().perform();
    }
    DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(1); // FIXME: Use something better
    return this;
  }

  /**
   * Open navigation user dropdown menu, then select 'Quick Start Guide' link to open the Guide.
   */
  public void openQuickStartGuide() {
    log.debug("openQuickStartGuide()");
    openNavUserMenu();
    clickQuickStartGuideLink();
  }

  /**
   * Sign Out by navigating directly to the "Logged Out" page.
   *
   * @return LoginPage
   */
  public LoginPage signOutDirect() {
    log.debug("signOutDirect()");
    String url = this.getCurrentUrl();
    navigateToPathWithinSameDomain(PageUrlPaths.SIGNOUT);
    isUrlChanged(url, Time.SHORTER); // wait for url change but don't enforce it. Some tests call this method while already logged out and url won't change
    assert (validateCurrentPage(PageUrlPaths.LOGGEDOUT)
        || validateCurrentPage(PageUrlPaths.SIGNOUT)) : validationAssertFailureMessage(PageUrlPaths.SIGNOUT);
    DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(2); //To avoid "Your session has expired. Please re-login and try again."
    return new LoginPage(deleteAllCookies(driver));
  }

  /**
   * Sign Out by navigating directly to the 'Logged Out' Page without deleting cookies.
   * @return LoginPage
   */
  public LoginPage signOutDirectWithoutDeletingCookies() {
    log.debug("signOutDirectWithoutDeletingCookies()");
    navigateToPathWithinSameDomain(PageUrlPaths.SIGNOUT);
    assert (validateCurrentPage(PageUrlPaths.LOGGEDOUT) || validateCurrentPage(PageUrlPaths.SIGNOUT)) : validationAssertFailureMessage(PageUrlPaths.SIGNOUT);
    return new LoginPage(driver);
  }

  /**
   * Close and reopen new instance of driver..
   *
   * @return LoginPage
   */
  public WebDriver quitAndGetNewDriverInstance() {
    log.debug("quitAndGetNewDriverInstance()");
    if (this.driver != null) {
      try {
        this.driver.quit();
      }
      catch (Throwable t) {
        //Do Nothing
      }
    }
    this.driver = new DriverWrapper().getDriver();
    return this.driver;
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
   * Sign Out by navigating directly to the "Logged Out" page.
   *
   * @return {IMSPage}
   */
  public IMSPage signOutDirectDC() {
    log.debug("signOutDirectDC()");
    navigateToPathWithinSameDomain(PageUrlPaths.SIGNOUT);
//    assert validateCurrentPage("https://echosigntestaccount.onelogin.com/login") : "User didn't land on OneLogin login page!";
    // driver.get(SIGNOUT_URL);
    return new IMSPage(driver).moveTo();
  }

  /**
   * Sign Out by navigating directly to the "OneLogin" login page.
   *
   * @return {OneloginSSOLoginPage}
   */
  public OneloginSSOLoginPage signOutDirectOneLogin() {
    log.debug("signOutDirectOneLogin()");
    navigateToPathWithinSameDomain(PageUrlPaths.SIGNOUT);
    assert validateCurrentPage("https://echosigntestaccount.onelogin.com/login") : "User didn't land on OneLogin login page!";
    return new OneloginSSOLoginPage(driver);
  }

  // Used on page after a Send

  /**
   * Get content of "Send Status" message
   *
   * @param {bIncludeMessageBody} If 'true', whole message, including it's body, will be returned. If 'false', only message title will be returned
   * @param {iWaitTimeInSeconds}  Waiting Time (in seconds)
   * @return {String} Send Status Message
   */
  public String getSendStatusMessage(boolean bIncludeMessageBody, int iWaitTimeInSeconds) {
    log.debug("getSendStatusMessage()");
    String sMessage = "";
    DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(iWaitTimeInSeconds);
    assert isElementPresent(sendStatusMessageLocator, Time.SHORTER) : "Send Status Message is NOT present!";
    if (bIncludeMessageBody) {
      sMessage = sendStatusMessageWithBodyLocator.getText();
    }
    else {
      sMessage = sendStatusMessageLocator.getText();
    }
    return sMessage;
  }

  /**
   * Get content of "Send Status" message
   *
   * @param {bIncludeMessageBody} If 'true', whole message, including it's body, will be returned. If 'false', only message title will be returned
   * @return {String} Send Status Message
   */
  public String getSendStatusMessage(boolean bIncludeMessageBody) {
    return getSendStatusMessage(bIncludeMessageBody, Time.SHORTEST);
  }

  /**
   * Get content of "Send Status" message
   *
   * @param {iWaitTimeInSeconds} Waiting Time (in seconds)
   * @return {String} Send Status Message
   */
  public String getSendStatusMessage(int iWaitTimeInSeconds) {
    return getSendStatusMessage(true, iWaitTimeInSeconds);
  }

  /**
   * Get content of "Send Status" message
   *
   * @return {String} Send Status Message
   */
  public String getSendStatusMessage() {
    return getSendStatusMessage(true, Time.SHORTEST);
  }

  /**
   * Get content of "Replace Current Signer" message
   *
   * @return {String} Replace Current Signer Message
   */
  public String getReplaceCurrentSignerOrApproverMessage() {
    log.debug("getReplaceCurrentSignerOrApproverMessage()");
    String rcsaMessage = "";
    DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(Time.SHORTEST);
    assert isElementPresent(replaceCurrentSignerorApproverMessageLocator,
                            Time.SHORTER) : "Replace Current Signer OR Approver Message is NOT present!";
    rcsaMessage = replaceCurrentSignerorApproverMessageLocator.getText();
    return rcsaMessage;
  }

  /**
   * Get the text of the global message which commonly appears near top in a banner
   */
  public String getGlobalMessage() {
    log.debug("getGlobalMessage()");
    int i = 0;
    while (i < Time.SHORT && !isGlobalMessagePresent()) {
      DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(Time.SHORTEST);
      i++;
    }
    if (isGlobalMessagePresent()) {
      driver = driver.switchTo().defaultContent();
      driver.switchTo().activeElement();
      waitUntilVisible(Time.SHORT, globalMessageLocator);
      return globalMessageLocator.getText();
    }
    return "";
  }

  /**
   * This method waits for the browser to render the success message saying that your agreement was successfully
   * sent and then returns that message.  It is useful in cases when you need to wait for a specific web element to render
   * after an agreement has been sent.
   *
   * @return String The successful companyName Sign message saying that your document has been sent successfully
   */
  public String getSuccessMessage() {
    log.debug("getSuccessMessage()");
    waitUntilVisible(Time.SHORT, globalMessageLocator);
    return globalMessageLocator.getText();
  }

  /**
   * This method waits for the browser to render the success message saying that your agreement was successfully
   * sent and then returns that message.  It is useful in cases when you need to wait for a specific web element to render
   * after an agreement has been sent.  This is an overloaded method of the getSuccessMessage.
   *
   * @param waitTime int The number of seconds for the webdriver to wait
   * @return String The successful companyName Sign message saying that your document has been sent successfully
   */
  public String getSuccessMessage(int waitTime) {
    log.debug("getSuccessMessage()");
    waitUntilVisible(waitTime, globalMessageLocator);
    return globalMessageLocator.getText();
  }

  /**
   * @return the error message in the red box
   */
  public String getErrorMessage() {
    log.debug("getErrorMessage()");
    if (this.hasGlobalError())
      try {
        return globalErrorLocator.getText();
      }
      catch (StaleElementReferenceException ex) {
        return globalErrorLocator.getText();
      }
    return "";
  }

  /**
   * Wait until the error message appears
   * @return Base Page
   */
  public BasePage waitUntilErrorMessageAppears() {
    log.debug("waitUntilErrorMessageAppears()");
    waitUntilVisible(Time.LONG, globalErrorLocator);
    return this;
  }

  /**
   * Verify error or alert on any page
   * @param timeoutInSecond  wait time. shouldn't be more than 5s
   * @param includeAlert     should include check for alert. Note Alert can have successful message
   */
  public void verifyNoErrorAlert(int timeoutInSecond, boolean includeAlert) {
    String errorAlert = includeAlert ? getAllErrorsAndAlerts(timeoutInSecond) : getAlertError(timeoutInSecond);
    assert (errorAlert == null || errorAlert.isEmpty()) : "verifyNoErrorAlert: get error " + errorAlert;
  }

  public String getAllErrors(int timeoutInSecond) {
    String globalErrors = "";
    if (this.hasGlobalError(timeoutInSecond))
      globalErrors = globalErrorLocator.getText();
    String alertError = this.getAlertError(timeoutInSecond);
    alertError = (alertError == null) ? "" : alertError;
    return globalErrors + alertError;
  }

  /**
   * Get all errors and alerts
   * @param timeOutInSec
   * @return
   */
  public String getAllErrorsAndAlerts(int timeOutInSec) {
    String allErrors = getAllErrors(timeOutInSec);
    String alertMessage = this.getAlertText(alertMessageLocator);
    alertMessage = (alertMessage == null) ? "" : alertMessage;
    return allErrors + alertMessage;
  }

  /**
   * Get error for main body
   * @param timeoutInSecond
   * @return
   */
  public String getMainBodyError(int timeoutInSecond) {
    String mainBodyError = null;
    if (isElementPresent(mainBodyMessagesLocator, Time.TWOSECOND)) {
      mainBodyError =  mainBodyMessagesLocator.getText();
    }
    return mainBodyError;
  }


  /*
   *  this method is misleading as it
   *  returns 'false' when THERE IS ERROR MESSAGE PRESENT and vice versa,
   *  when the name of the method is clearly claiming otherwise.
   */
  @Deprecated
  public boolean hasErrorMessage() {
    String error = getErrorMessage();
    if (error.isEmpty() || error == null) {
      return true;
    }
    return false;
  }

  public BasePage verifyGlobalMessage(String sExpectedText, int iTimeOut) {
    log.debug("verifyGlobalMessage()");
    DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(1);
    assert isElementVisible(driver, globalMessageLocator, iTimeOut) : "Global Message is not visible.";
    assert isTextDisplayedInElement(driver, globalMessageByLocator, sExpectedText,
                                    iTimeOut) : "The expected text is NOT displayed on Global Message. We expected the message to contain text: "
        + sExpectedText + ". Actual full message: " + globalMessageLocator.getText() + ".";
    return this;
  }

  public BasePage verifyGlobalMessage(String sExpectedText) {
    return verifyGlobalMessage(sExpectedText, Time.SHORTEST);
  }

  /**
   * Is the commonly displayed global message displayed at page top?
   *
   * @return true if present and visible
   */
  public Boolean isGlobalMessagePresent() {
    log.debug("isGlobalMessagePresent()");
    return isElementVisible(driver, globalMessageLocator); // increased the wait time for chrome
  }

  // Get MSIE working with file upload buttons
  // first turn on their display, then sendKeys, then reset their display back to hidden

  /**
   * Upload a specified document in MS Internet Explorer
   *
   * @param uploadButtonWrapperLocator
   * @param uploadButtonLocator
   * @param docFilePath                - File Path to the desired document
   */
  public void doFileUploadForExplorer(WebElement uploadButtonWrapperLocator, WebElement uploadButtonLocator, String docFilePath) {
    log.debug("doFileUploadForExplorer()");

    // make container visible
    execScript("arguments[0].style.overflow = 'visible';", uploadButtonWrapperLocator);
    execScript("arguments[0].style.opacity = '1';", uploadButtonWrapperLocator);
    // ensure that input field itself is visible
    execScript("arguments[0].style.left =0;", uploadButtonWrapperLocator);
    execScript("arguments[0].style.top = 0;", uploadButtonWrapperLocator);
    execScript("arguments[0].style.left =0;", uploadButtonLocator);
    execScript("arguments[0].style.top = 0;", uploadButtonLocator);

    execScript("arguments[0].style.overflow = 'visible';", uploadButtonLocator);
    execScript("arguments[0].style.opacity = '1';", uploadButtonLocator);

    // enter the file path as input text
    uploadButtonLocator.sendKeys(FileUtils.pwd() + docFilePath);

    // make container hidden again
    execScript("arguments[0].style.overflow = 'hidden';", uploadButtonWrapperLocator);
    execScript("arguments[0].style.opacity = '0';", uploadButtonWrapperLocator);
  }

  /**
   * Switch to iFrame
   *
   * @param iFrameIndex
   * @param locateElementBy
   * @return {WebElement}
   */
  public WebElement switchToiFrame(int iFrameIndex, By locateElementBy) {
    WebElement webElement = null;
    try {
      driver.switchTo().frame(iFrameIndex);
      webElement = driver.findElement(locateElementBy);
      WebDriverWait wait = new WebDriverWait(driver, TimeDuration.LONG);
      wait.until(ExpectedConditions.visibilityOf(webElement));
    }
    catch (NoSuchFrameException nsf) {
      log.debug("NoSuchFrameException()  ");
    }
    catch (NullPointerException npe) {
      assert webElement != null : "Expected- webElement " + locateElementBy.toString() + " should not be Null";
    }
    return webElement;
  }

  /**
   * Switch to iFrame
   *
   * @param {WebElement} iFrameLocator
   * @param {By}         locateElementBy
   * @return {WebElement}
   */
  public WebElement switchToiFrame(WebElement iFrameLocator, By locateElementBy) {
    WebElement webElement = null;
    try {
      driver.switchTo().frame(iFrameLocator);
      DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(3);
      webElement = driver.findElement(locateElementBy);
      WebDriverWait wait = new WebDriverWait(driver, TimeDuration.LONG);
      wait.until(ExpectedConditions.visibilityOf(webElement));
    }
    catch (NoSuchFrameException nsf) {
      log.debug("NoSuchFrameException()  ");
    }
    catch (NullPointerException npe) {
      assert webElement != null : "Expected- webElement " + locateElementBy.toString() + " should not be Null";
    }
    return webElement;
  }

  /**
   * Switch to default context
   */
  public void switchToDefaultContext() {
    driver.switchTo().defaultContent();
  }

  // Method to interact with localization dropdown

  /**
   * Click on Language selector on Localization dropdown box
   *
   * @return {Page}
   */
  public Page clickLanguageSelector() {
    header.clickLanguageSelector();
    return this;
  }

  /**
   * Get selected language in "Localization" dropdown box
   *
   * @return {String} Selected Language
   */
  public String findDefaultLocalizationDropDown() {
    return header.getSelectedLocaleLanguage();
  }

  /**
   * Select specified localization language in "localization" dropdown box
   *
   * @param {String} locale - Desired language (in "en_US" format)
   * @return {Page}
   */
  public Page selectLocalizationLanguage(String locale) {
    header = header.selectLocalizationLanguage(locale);
    return this;
  }

  /**
   * Click on "Send Settings" link leads to "Send Settings" page
   *
   * @return {SendSettingsPage}
   */
  public SendSettingsPage clickSendSettingsLink() {
    log.debug("clickSendSettingsLink()");
    waitUntilVisible(Time.LONGER, sendSettingstLinkLocator);
    sendSettingstLinkLocator.click();
    assert validateCurrentPage(PageUrlPaths.SEND_SETTINGS) : validationAssertFailureMessage(PageUrlPaths.SEND_SETTINGS);
    return new SendSettingsPage(driver);
  }

  /**
   * Click on "Personal Preferences" link leads to "Personal Preferences" page
   *
   * @return {PersonalPreferencesPage}
   */
  public PersonalPreferencesPage clickPersonalPreferencesLink() {
    log.debug("clickPersonalPreferencesLink()");
    waitUntilVisible(Time.LONGER, personalPreferencesLinkLocator);
    personalPreferencesLinkLocator.click();
    assert validateCurrentPage(PageUrlPaths.ACCOUNT_SETTING_PAGE) : validationAssertFailureMessage(PageUrlPaths.ACCOUNT_SETTING_PAGE);
    return new PersonalPreferencesPage(driver);
  }

  /**
   * Click on "My Signature" link leads to "My Signature" page
   *
   * @return {MySignaturePage}
   */
  public MySignaturePage clickMySignatureLink() {
    log.debug("clickMySignatureLink()");
    waitUntilVisible(Time.LONGER, mySignatureLinkLocator);
    mySignatureLinkLocator.click();
    assert validateCurrentPage(PageUrlPaths.ACCOUNT_SETTING_PAGE) : validationAssertFailureMessage(PageUrlPaths.ACCOUNT_SETTING_PAGE);
    return new MySignaturePage(driver);
  }

  /**
   * Click on "Privacy" link leads to "Privacy" page
   *
   * @return {PrivacyAdminPage}
   */
  public PrivacyAdminPage clickPrivacyLink() {
    log.debug("clickPrivacyLink()");
    DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(1);
    waitUntilVisible(Time.LONGER, privacyPageLinkLocator);
    privacyPageLinkLocator.click();
    assert validateCurrentPage(PageUrlPaths.PRIVACY_ADMINISTRATOR_PAGE) : validationAssertFailureMessage(PageUrlPaths.PRIVACY_ADMINISTRATOR_PAGE);
    return new PrivacyAdminPage(driver);
  }

  /**
   * Click on Logo image leads to "Home" page
   *
   * @return {HomePage}
   */
  public HomePage clickLogo() {
    if (header.isCustomLogoPresent()) {
      header = header.clickCustomLogo();
      HomePage homePage = new HomePage(driver);
      assert (homePage.isHomePageLoaded(SectionName.RECENTEVENTS)) : "clickLogo: home page is not loaded after click on logo";
      return homePage;
    }
    else {
      return clickDefaultLogo();
    }
  }

  /**
   * Click on Logo image leads to New "Home" page
   *
   * @return {HomeJSPage}
   */
  public HomeJSPage clickLogoToHomeJS() {
    if (header.isCustomLogoPresent()) {
      header = header.clickCustomLogo();
      HomeJSPage homePage = new HomeJSPage(driver);
      assert (homePage.isHomeJSPageLoaded(Time.SHORT)) : "clickLogo: home page is not loaded after click on logo";
      return homePage;
    }
    else {
      return clickDefaultLogoToHomeJS();
    }
  }

  /**
   * Is Default companyName Sign logo present
   * @return {boolean}
   */
  public boolean isDefaultLogoPresent() {
    return header.isDefaultLogoPresent();
  }

  /**
   * Check if custom logo is present
   * @return
   */
  public boolean isCustomLogoPresent(){
    return header.isCustomLogoPresent();
  }

  /**
   * Click on Default Logo image leads to "Home" page
   *
   * @return {HomePage}
   */
  public HomePage clickDefaultLogo() {
    log.debug("clickDefaultLogo()");
    header = header.clickDefaultLogo();
    return new HomePage(driver);
  }

  /**
   * Click on Default Logo image leads to "HomeJS" page
   *
   * @return {HomeJSPage}
   */
  public HomeJSPage clickDefaultLogoToHomeJS() {
    log.debug("clickDefaultLogoToHomeJS()");
    header = header.clickDefaultLogo();
    return new HomeJSPage(driver);
  }

  /*
  * Click the co-branded, custom logo in header to Home page
  * */
  public HomeJSPage clickCustomLogoToHomeJS() {
    log.debug("clickCustomLogoToHomeJS()");
    header = header.clickCustomLogo();
    HomeJSPage homePage = new HomeJSPage(driver);
    assert (homePage.isHomeJSPageLoaded(Time.LONG)) : "clickCustomLogo: homeJSPage is not loaded after clickCustomLogo()";
    return homePage;
  }

  /**
   * Get Logo Image Source
   *
   * @return {String} Logo Image Source
   */
  public String getLogoImageSrc() {
    return header.getLogoImageSrc();
  }

  /**
   * Get dimensions (height and width) of the custom image logo
   * @return
   */
  public String[] getLogoImageDimensions(){
    return header.getLogoImageDimensions();
  }

  /**
   * Verifies the custonm logo image height and width
   *
   * @param expectedDimensions
   */
  public void verifyCustomLogoDimensions(String [] expectedDimensions){
    header.verifyCustomLogoDimensions(expectedDimensions);
  }

  /**
   * Verifies the custom logo image height and width match the default values
   */
  public void verifyCustomLogoDimensions() {
    header.verifyCustomLogoDimensions();
  }

  /**
   * Verify default logo is present
   */
  public void verifyNoCustomLogo() {
    log.debug("verifyNoCustomLogo()");
    assert header.isDefaultLogoPresent() : "Expected Default Logo, but Custom Logo is present";
  }

  /**
   * Verify custom logo is present
   */
  public void verifyCustomLogo() {
    log.debug("verifyCustomLogo()");
    assert header.isCustomLogoPresent() : "Expected custom logo, but default logo is present";
  }

  /**
   * Verify whether a Element is displayed on a page
   *
   * @param {WebDriver}  driver - WebDriver instance for the test
   * @param {WebElement} locator - The locator of the element to test
   * @return {boolean} whether the element is displayed or not
   */
  public boolean isElementVisible(WebDriver driver, WebElement locator) {
    return isElementVisible(driver, locator, Time.LONG);
  }

  /**
   * Verify whether a Element is not displayed on a page
   *
   * @param {WebDriver}  driver - WebDriver instance for the test
   * @param {WebElement} locator - The locator of the element to test
   * @param {int}        time - Waiting time in seconds
   * @return {boolean} Whether the element is displayed or not
   */
  public boolean isElementInvisible(WebDriver driver, WebElement locator, Duration time) {
    log.debug("isElementInvisible(driver, locator, time)");
    try {
      WebDriverWait wait = new WebDriverWait(driver, time);
      wait.until(ExpectedConditions.invisibilityOfElementLocated((By) locator));
    }
    catch (Exception e) {
      log.debug(e.getMessage() + "; " + e.getStackTrace());
      return false;
    }
    return true;
  }

  /**
   * Verify whether a Element is not displayed on a page
   *
   * @param {WebDriver}  driver - WebDriver instance for the test
   * @param {WebElement} locator - The locator of the element to test
   * @return {boolean} Whether the element is displayed or not
   */
  public boolean isElementInvisible(WebDriver driver, WebElement locator) {
    return isElementInvisible(driver, locator, TimeDuration.SHORTEST);
  }

  /**
   * Verify whether a Element is clickable
   *
   * @param {WebDriver}  driver - WebDriver instance for the test
   * @param {WebElement} locator - The locator of the element to test
   * @param {int}        time - Waiting time in seconds
   * @return {boolean} whether the element is clickable or not
   */
  public boolean isElementClickable(WebDriver driver, WebElement locator, int time) {
    log.debug("isElementVisible(driver, locator, time)");
    try {
      boolean bClickable = false;
      if (isElementVisible(driver, locator, time)) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(time));
        wait.until(ExpectedConditions.elementToBeClickable(locator));
        bClickable = true;
      }
      return bClickable;
    }
    catch (Exception e) {
      log.debug(e.getMessage() + "; " + e.getStackTrace());
      return false;
    }
  }

  /**
   * Verify whether a Element is clickable
   *
   * @param {WebDriver}  driver - WebDriver instance for the test
   * @param {WebElement} locator - The locator of the element to test
   * @return {boolean} whether the element is clickable or not
   */
  public boolean isElementClickable(WebDriver driver, WebElement locator) {
    return isElementClickable(driver, locator, Time.SHORTEST);
  }

  /**
   * Verify whether a Text is displayed in Element
   *
   * @param {WebDriver}  driver - WebDriver instance for the test
   * @param {WebElement} locator - The locator of the element to test
   * @param {String}     text - Specified text
   * @param {int}        time - Waiting time in seconds
   * @return {boolean} Whether the text is displayed in element or not
   */
  public boolean isTextDisplayedInElement(WebDriver driver, By locator, String text, int time) {
    try {
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(time));
      wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
      log.debug("isTextDisplayedInElement(): YES");
      return true;
    }
    catch (Exception e) {
      log.debug(e.getMessage() + "; " + e.getStackTrace());
      log.debug("isTextDisplayedInElement(): NO");
      return false;
    }
  }

  /**
   * Verify whether a Text is displayed in Element
   *
   * @param {WebDriver}  driver - WebDriver instance for the test
   * @param {WebElement} locator - The locator of the element to test
   * @param {String}     text - Specified text
   * @return {boolean} Whether the text is displayed in element or not
   */
  public boolean isTextDisplayedInElement(WebDriver driver, By locator, String text) {
    return isTextDisplayedInElement(driver, locator, text, Time.SHORTEST);
  }

  /**
   * Verify whether a Text is displayed on current page.
   *
   * @param {String} text - Specified text.
   * @param {int}    timeout - Waiting time in seconds.
   * @return {boolean} Whether the text is displayed or not.
   */
  public boolean isTextPresent(String text, int timeout) {
    log.debug("isTextPresent()");
    String msgXpath = "//*[contains(text(), '" + text + "')]";
    return isElementPresent(By.xpath(msgXpath), timeout);
  }

  /**
   * Verify whether a Text is displayed on current page.
   *
   * @param {String} text - Specified text.
   * @param {int}    timeout - Waiting time in seconds.
   * @param {int}    delayBeforActing - time the method is waiting before go on in seconds
   * @return {boolean} Whether the text is displayed or not.
   */
  public boolean isTextPresent(String text, int timeout, int delayBeforActing) {
    log.debug("isTextPresent()");
    DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(delayBeforActing);
    String msgXpath = "//*[contains(text(), '" + text + "')]";
    return isElementPresent(By.xpath(msgXpath), timeout);
  }

  /**
   * Verify whether the attribute is present
   *
   * @param {WebElement} locator - the locator for the attribute to test
   * @param {String}     attribute - The attribute name for testing
   * @return {boolean} whether the attribute is present or not
   */
  public boolean isAttribtuePresent(WebElement locator, String attribute) {
    try {
      String attributeValue = locator.getAttribute(attribute);
      if (attributeValue != null) {
        return true;
      }
    }
    catch (Exception e) {
      log.debug(e.getMessage() + "; " + e.getStackTrace());
    }
    return false;
  }

  public boolean isAttributeExist(WebElement locator, String attributeName) {
    try {
      locator.getAttribute(attributeName);
      return true;
    }
    catch (Exception e) {
      return false;
    }
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
   * Get the value of the attribute of the named link
   *
   * @param {String} linkName - The name of the link
   * @param {String} attributeName - The name of the attribute
   * @return {boolean} true if the specified linktext exists
   */
  public boolean linkExists(String linkName, String attributeName) {
    try {
      String attributeValue;
      if (BrowserUtils.isUserAgentFirefox()) {
        log.warn("In case of Firefox version 32.0.3 or 33, links exist, but link text is not present within html tag");
        attributeValue = driver.findElement(By.cssSelector("a[title='" + linkName + "']")).getAttribute(attributeName);
      }
      else {
        attributeValue = driver.findElement(By.linkText(linkName)).getAttribute(attributeName);
      }
      if (attributeValue != null) {
        return true;
      }
    }
    catch (Exception e) {
      log.debug("Cannot find link with text: [" + linkName + "]");
    }
    return false;
  }

  /**
   * Perform clicking on the link
   *
   * @param {String} linkName - The name of the link
   */
  public void linkClick(String linkName) {
    driver.findElement(By.linkText(linkName)).click();
  }

  /**
   * Get the value of the attribute of the named link
   *
   * @param {String} linkName - The name of the link
   * @param {String} attributeName - The name of the attribute
   * @return {String} Value of the specified attribute
   */
  public String getAttribtueValueOfLinkNamed(String linkName, String attributeName) {
    try {
      String attributeValue;
      if (BrowserUtils.isUserAgentFirefox()) {
        log.warn("In case of Firefox version 32.0.3 or 33, links exist, but link text is not present within html tag");
        attributeValue = driver.findElement(By.cssSelector("a[title='" + linkName + "']")).getAttribute(attributeName);
      }
      else {
        attributeValue = driver.findElement(By.linkText(linkName)).getAttribute(attributeName);
      }
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
   * This method returns specific css value for the specified locator
   *
   * @param {WebElement} locator
   * @param {String}     css parameter
   * @return {String} css value
   */
  public String getCssValue(WebElement locator, String cssParameter) {
    return locator.getCssValue(cssParameter);
  }

  /**
   * Verify if a link is present, clickable and underlined after hovering mouse over it
   *
   * @param {WebDriver}  driver - WebDriver instance for the test
   * @param {WebElement} locator - The locator for the link
   * @param {boolean}    isExpectedToDisplay - Whether the link is expected to display or not
   * @return {boolean} True, if link is clickable and underlined after hovering, otherwise False
   */
  public boolean verifyLink(WebDriver driver, WebElement locator, boolean isExpectedToDisplay) {
    log.debug("verifyLink()");
    boolean isClickable = isElementClickable(driver, locator);
    onMouseMove(locator);
    boolean isUnderlinedAfterHovering = getCssValue(locator, "text-decoration").equals("underline");
    assert (isClickable == isExpectedToDisplay) : locator.toString() + " link is not clickable";
    // Getting css value doesn't work in firefox. This is up to current firefox version (33.0)
    if ((getBrowserName().equals("firefox")) && (isUnderlinedAfterHovering != isExpectedToDisplay)) {
      log.warn("Getting css value doesn't work in Firefox version " + getBrowserVersion());
      return (isClickable == isExpectedToDisplay);
    }
    if ((getBrowserName().equals("chrome")) && (isUnderlinedAfterHovering != isExpectedToDisplay)) {
      log.warn("Getting css value doesn't work in Chrome version " + getBrowserVersion());
      return (isClickable == isExpectedToDisplay);
    }
    assert (isUnderlinedAfterHovering == isExpectedToDisplay) : locator.toString() + " link is not underlined after hovering";
    return ((isClickable & isUnderlinedAfterHovering) == isExpectedToDisplay);
  }

  // /**
  // * Click on "Group" tab leads to "Group" page
  // * @return {GroupPage}
  // */
  // public GroupPage clickGroupTab() {
  // log.debug("clickGroupTab()");
  // groupTabLocator.click();
  // assert validateCurrentPage(PageUrlPaths.GROUP_SETTINGS) : validationAssertFailureMessage(PageUrlPaths.GROUP_SETTINGS);
  // return new GroupPage(driver);
  // }

  // Used on page after a Send

  /**
   * Get content of "Post Send Header" message
   *
   * @return {String} Post Send Header Message
   */
  public String getPostSendHeaderMessage() {
    log.debug("getPostSendHeaderMessage()");
    waitUntilVisible(Time.LONGER, sendStatusHeaderMessageLocator);
    return sendStatusHeaderMessageLocator.getText();
  }

  /**
   * Get user's first name from the nav bar
   *
   * @return {String} User's first name
   */
  public String getFirstname() {
    log.debug("getFirstname()");
    return navUserTabLocator.getText().replace(Locale_en_US.LOGGED_IN_AS + " ", "");
  }

  /**
   * This method takes a locator as an argument and executes javascript against it in order to move mouse over it
   *
   * @param {WebElement} locator - element that need to be hovered by mouse
   */
  public void onMouseOver(WebElement locator) {
    String mouseOverScript = "if(document.createEvent){" +
        "var evObj = document.createEvent('MouseEvents');" +
        "evObj.initEvent('mouseover', true, false); " +
        "arguments[0].dispatchEvent(evObj);} " +
        "else if(document.createEventObject) { " +
        "arguments[0].fireEvent('onmouseover');" +
        "}";
    execScript(mouseOverScript, locator);
  }

  /**
   * This method takes a locator as an argument and use Selenium mouseMove functionality to move mose to the specific location
   *
   * @param {WebElement} locator - element that need to be hovered by mouse
   */
  public void onMouseMove(WebElement locator) {
    log.debug("onMouseMove()");
//    Mouse mouse = ((HasInputDevices) driver).getMouse();
//    mouse.mouseMove(((Locatable) locator).getCoordinates());
    Actions actions = new Actions(driver);
    actions.moveToElement(locator).build().perform();
  }

  /**
   * This method checks whether the test browser is a mobile browser - safari or chrome
   *
   * @return True, if mobile browser, otherwise False
   */
  public boolean isMobileBrowser() {
    log.debug("isMobileBrowser()");
    String browser = BrowserUtils.getUserAgent();
    return browser.equalsIgnoreCase("m_chrome") || browser.equalsIgnoreCase("m_safari");
  }

  /**
   * Added to access the IFrame on the Social Verification page
   *
   * @return initial window handle
   */
  public String changeWindowHandle() {
    String winHandleBefore = getWindowHandle();
    DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(3);
    for (String winHandle : getWindowHandles()) {
      switchWindow(winHandle);
    }
    return winHandleBefore;
  }

  /**
   * Click on "Sign Out" link on the DC Index Page leads to IMSpage
   *
   * @return {IMSPage}
   */
  public IMSPage clickSignOutDC() {
    log.debug("clickSignOutDC()");
    // Hover over the Sign Out text before clicking
    waitUntilClickable(Time.SHORTER, (By) driver.findElement(By.cssSelector("div.dc-username")));
    new Actions(driver).moveToElement(driver.findElement(By.cssSelector("div.dc-username"))).build().perform();
    DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(1);
    if (!isElementPresent((By) driver.findElement(By.cssSelector("#log-out > a")))) {
      clickOnLocator(driver.findElement(By.cssSelector("div.dc-username")));
      DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(1);
    }
    clickOnLocator(driver.findElement(By.cssSelector("#log-out > a")));
    return new IMSPage(driver).moveTo();
  }

  /**
   * @return true if Dashboard tab is present
   */
  public boolean isDashboardTabPresent() {
    return header.isHomeTabPresent();
  }

  /**
   * @return true if Home tab is present
   */
  public boolean isHomeTabPresent(){
    return header.isHomeTabPresent();
  }

  /**
   * @return true if Send tab is present
   */
  public boolean isSendTabPresent() {
    return header.isSendTabPresent();
  }

  /**
   * @return true if Account tab is present
   */
  public boolean isAccountTabPresent() {
    return header.isAccountTabPresent();
  }

  /**
   * @return true if Reports tab is present
   */
  public boolean isReportsTabPresent() {
    return header.isReportsTabPresent();
  }

  /**
   * @return true if Group tab is present
   */
  public boolean isGroupTabPresent() {
    return header.isGroupTabPresent();
  }

  /**
   * This method gets the instance of FormFieldElement and clicks on TAB key
   */
  public void sendTabKey(WebElement locator) {
    log.debug("sendTabKey(WebElement)");
    (new Actions(driver)).sendKeys(locator, Keys.TAB).build().perform();
  }

  /**
   * This method gets the instance of FormFieldElement and clicks on Enter key
   */
  public void sendEnterKey(WebElement locator) {
    log.debug("sendEnterKey(WebElement)");
    (new Actions(driver)).sendKeys(locator, Keys.ENTER).build().perform();
    DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(Time.SHORTEST);
  }

  /**
   * This method gets the instance of FormFieldElement and clicks on Down key
   */
  public void sendDownKey() {
    log.debug("sendDownKey()");
    (new Actions(driver)).sendKeys(Keys.DOWN).build().perform();
    DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(Time.SHORTEST);
  }

  /**
   * This method hits the Up key
   */
  public void sendUpKey() {
    log.debug("sendUpKey()");
    (new Actions(driver)).sendKeys(Keys.UP).build().perform();
    DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(Time.SHORTEST);
  }

  /**
   * This method hits the TAB key
   */
  public void clickTabKey() {
    log.debug("clickTabKey()");
    (new Actions(driver)).sendKeys(Keys.TAB).build().perform();
  }

  /**
   * This method hits the Shift + TAB key
   */
  public void clickShiftTabKeys() {
    log.debug("clickShiftTabKey()");
    (new Actions(driver)).sendKeys(Keys.SHIFT, Keys.TAB).build().perform();
  }

  /**
   * This method hits the Alt + TAB key
   */
  public void sendAltTabKeys() {
    log.debug("sendAltTabKeys()");
    (new Actions(driver)).sendKeys(Keys.ALT, Keys.TAB).build().perform();
  }

  /**
   * This method gets the instance of FormFieldElement and sends on the BACKSPACE key
   */
  public void sendBackSpaceKey(WebElement locator) {
    log.debug("sendBackSpaceKey(WebElement)");
    (new Actions(driver)).sendKeys(locator, Keys.BACK_SPACE).build().perform();
  }

  /**
   * This method hits the BACKSPACE key
   */
  public void sendBackSpaceKey() {
    log.debug("sendBackSpaceKey()");
    (new Actions(driver)).sendKeys(Keys.BACK_SPACE).build().perform();
  }

  /**
   * This method hits the ENTER key
   */
  public void sendEnterKey() {
    log.debug("sendEnterKey()");
    (new Actions(driver)).sendKeys(Keys.ENTER).build().perform();
  }

  /**
   * This method hits the ENTER key
   */
  public void sendSpaceKey() {
    log.debug("sendSpaceKey()");
    (new Actions(driver)).sendKeys(Keys.SPACE).build().perform();
  }

  /**
   * This method hits the Esc key
   */
  public void sendEscapeKey() {
    log.debug("sendEscapeKey()");
    (new Actions(driver)).sendKeys(Keys.ESCAPE).build().perform();
  }

  /**
   * This method moves mouse by offset
   */
  public void moveMouseByOffset(int x, int y){
    log.debug("moveMouseByOffset()");
    (new Actions(driver)).moveByOffset(x, y).build().perform();
  }

  /**
   * This method gets the instance of FormFieldElement and sends on the DELETE key
   */
  public void sendDeleteKey(WebElement locator) {
    log.debug("sendBackSpaceKey(WebElement)");
    (new Actions(driver)).sendKeys(locator, Keys.DELETE).build().perform();
  }

  /**
   * Tabs into a page from Help icon in header
   */
  public void tabToPageContentFromHeader() {
    log.debug("tabToPageContentFromHeader()");
    Header header = new Header(driver);
    // Set focus on help icon in header
    header.clickHelpIcon();
    // Tab to Avatar icon
    clickTabKey();
    if (Env.isSandbox()) {
      // Tab to Learn more link in Sandbox blue bar
      clickTabKey();
    }
    // Tab to the first page element that gets focus
    clickTabKey();
  }

  /**
   * find whether an element exists on the page
   *
   * @param pageLocator locator of the page to find
   * @param findType    the type of findElement method to use. Valid value examples: By.id("locator"), By.className("adminContianer")
   * @return <boolean> whether the element exist or not
   */
  //TODO  Neither of waitForElement(FluentWait) or WebDriverWait wouldn't respect the waitTimeInSeconds setting, it always take up 60s (default timeout)
  public boolean isElementExist(WebElement pageLocator, By findType, int waitTimeInSeconds) {
    try {
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));

      if (findType == null) {
        wait.until(ExpectedConditions.visibilityOf(pageLocator));
      }
      else {
        waitForElement((WebElement) pageLocator.findElement(findType), waitTimeInSeconds);
      }

      if (pageLocator == null && findType != null) {
        waitForElement(findType, waitTimeInSeconds);
      }
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * select a element from dropdown list by text
   *
   * @param selectLocator locator for the dropdown list
   * @param visibleText   displayed text to be selected
   */
  public void selectDropdownByText(WebElement selectLocator, String visibleText) {
    Select select = new Select(selectLocator);
    select.selectByVisibleText(visibleText);
  }

  public void clickDropdownByText(WebElement dropdownLocator, String visibleText) {
    dropdownLocator.click();
    List<WebElement> dropdownOptions = driver.findElements(By.xpath("//div[@role = 'option']/div/span"));
    for (WebElement option : dropdownOptions) {
      if (option.getText().equals(visibleText)) {
        option.click();
        break;
      }
    }
  }

  /**
   * select a element from dropdown list by value
   *
   * @param selectLocator
   * @param value
   */
  public void selectDropdownByValue(WebElement selectLocator, String value) {
    Select select = new Select(selectLocator);
    select.selectByValue(value);
  }

  /**
   * select a element from dropdown list by index
   *
   * @param selectLocator locator for the dropdown list
   * @param rowIndex      index number to be selected
   */
  public void selectDropdownByIndex(WebElement selectLocator, int rowIndex) {
    Select select = new Select(selectLocator);
    select.selectByIndex(rowIndex);
  }

  /**
   * get the Item list in a select dropdown, return null if no item in it.
   *
   * @param selectLocator
   * @return
   * @throws Exception
   */
  public List<WebElement> getSelectDropdownList(WebElement selectLocator) throws Exception {
    if (selectLocator != null) {
      Select sel = new Select(selectLocator);
      List<WebElement> options = sel.getOptions();
      return options;
    }
    return null;
  }

  /**
   * Verify if a locator contains the testing class name
   *
   * @param locator   locator to be tested
   * @param className the string value of the className to be verified
   * @return
   */
  public boolean isClassContainsValue(WebElement locator, String className) {
    if (locator.getAttribute("class").contains(className)) {
      return true;
    }
    return false;
  }

  public boolean isValueInList(String valueString, List<String> testList) {
    if (testList.contains(valueString)) {
      return true;
    }
    return false;
  }

  /**
   * verify if the expected and actual message is the same
   *
   * @param actual   actual message
   * @param expected expected message
   */
  public void verifyMessage(String actual, String expected) {
    assert actual.contains(expected) : "Actual is different than expected. actual: [" + actual + "], expected [" + expected + "]";
  }

  /**
   * get all the elements in a dropdown list
   *
   * @param selectLocator locator for the dropdown list
   * @return List<WebElement> of all elements in the dropdown list
   */
  public List<WebElement> getDropdownList(WebElement selectLocator) {
    Select select = new Select(selectLocator);
    return select.getOptions();
  }

  // This method doesn't work with language selector in v4 header but we can't update it because it's used by other non language selector objects
  // For language selector, please update to use getSelectedLocaleLanguage()
  public String getSelectedLocale(WebElement selectLocator) {
    log.debug("getSelectedLocale()");
    if (selectLocator == null) {
      return null;
    }
    else {
      Select select = new Select(selectLocator);
      return select.getFirstSelectedOption().getText();
    }
  }

  /**
   * Returns the selected language in Language Selector in "English: US" format
   */
  public String getSelectedLocaleLanguage() {
    return header.getSelectedLocaleLanguage();
  }

  /**
   * Returns the selected language code in Language Selector in "en_US" format
   */
  public String getSelectedLocaleCode() {
    return header.getSelectedLocaleCode();
  }

  public String getSelectedWorkflowFromDropdwn(WebElement selectLocator) {
    log.debug("getSelectedWorkflowFromDropdwn()");
    if (selectLocator == null) {
      return null;
    }
    else {
      Select select = new Select(selectLocator);
      return select.getFirstSelectedOption().getText();
    }
  }

  /**
   * Returns true if Language Selector is present
   */
  public boolean isLanguageSelectorPresent() {
    return header.isLanguageSelectorPresent();
  }

  // if this is used with getSelectedLocale(getESFooterLanguageLocator()) which does not work with v4 header. Use getSelectedLocaleLanguage() instead.
  public WebElement getESFooterLanguageLocator() {
    return header.getLanguageLocator();
  }

  /**
   * Get selected language on ESign Page
   *
   * @return {String} Selected Language
   */
  public String getSelectedESFooterLanguage() {
    return getSelectedLocaleLanguage();
  }

  /**
   * Get selected language code on ESign Page
   *
   * @return {String} Selected Language
   */
  public String getSelectedESFooterLanguageCode() {
    return getSelectedLocaleCode();
  }

  public WebElement getLocatorByID(String ID) {
    return this.driver.findElement(By.id(ID));
  }

  public WebElement getLocatorByClassName(String className) {
    return this.driver.findElement(By.className(className));
  }

  public WebElement getSubLocatorByID(WebElement locator, String ID) {
    return locator.findElement(By.id(ID));
  }

  public WebElement getSubLocatorByClassName(WebElement locator, String className) {
    return locator.findElement(By.className(className));
  }

  public WebElement getSubLocatorByCss(WebElement locator, String cssString) {
    return locator.findElement(By.cssSelector(cssString));
  }

  public List<WebElement> getSubLocatorsByCss(WebElement locator, String cssString) {
    return locator.findElements(By.cssSelector(cssString));
  }

  public boolean hasGlobalError() {
    return isElementPresent(globalErrorLocator, Time.SHORTER);
  }

  /**
   * This method checks whether global error message exists or not
   *
   * @param timeout - given timeout during which the check is being done
   * @return True, if the global error message is present, otherwise False
   */
  public boolean hasGlobalError(int timeout) {
    return isElementPresent(globalErrorLocator, timeout);
  }

    /**
     * Opens My Profile Page
     *
     * @return {MyProfilePage}
     */
    public MyProfilePage openMyProfile() {
        header.openUserProfileMenu();
        header.clickMyProfilePage();

        MyProfilePage myProfilePage = new MyProfilePage(driver);
        assert (myProfilePage.isMyProfilePageLoaded(waitTime)) : "My Profile Page did not load.";
        return myProfilePage;
    }

  public Page clickSwitchAccount() {
    log.debug("clickSwitchAccount");
    return header.clickSwitchAccount();
  }

  /**
   * Check if Switch Account is displayed first, select it if it exists, and fail if it doesn't
   * @return true if it is displayed, false if not
   */
  public boolean selectSwitchAccount() {
    header.openUserProfileMenu();
    boolean isSwitchAccount = header.isSwitchAccountLinkPresent();
    if (isSwitchAccount) {
      header.clickSwitchAccount();
    }
    return isSwitchAccount;
  }

  /**
   * verify current Url contains expected Url, with description
   *
   * @param driver
   * @param expectedUrl
   * @param description
   */
  public void verifyCurrentURL(WebDriver driver, String expectedUrl, String description) {
    verifyCurrentURL(driver, expectedUrl, description, true);
  }

  /**
   * Wait for a page to load completely
   */
  public static void waitForPageLoadComplete(WebDriver driver) {
    Wait<WebDriver> wait = new WebDriverWait(driver, TimeDuration.LONGEST);
    wait.until(driver1 -> String
        .valueOf(((JavascriptExecutor) driver1).executeScript("return document.readyState"))
        .equals("complete"));
  }

  /**
   * verify if the current Url should contains the expectedUrl based on hasExpectedValue.
   * if hasExpectedValue is false, then expectedUrl shouldn't be in the current url
   *
   * @param driver
   * @param expectedUrl
   * @param description
   * @param hasExpectedValue
   */
  public void verifyCurrentURL(WebDriver driver, final String expectedUrl, String description, boolean hasExpectedValue) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));
    String currentURL = "";
    if (hasExpectedValue) {
      ExpectedCondition<Boolean> expectedUrlIsPresent = d -> driver.getCurrentUrl().contains(expectedUrl);
      wait.until(expectedUrlIsPresent);

      currentURL = driver.getCurrentUrl();
      assert currentURL.contains(expectedUrl) : "verifyCurrentURL: failed for " + description + ", currentURL [" + currentURL
          + "], expectedUrl [" + expectedUrl + "]";
    }
    else {
      currentURL = driver.getCurrentUrl();
      assert !currentURL.contains(expectedUrl) : "verifyCurrentURL: failed for " + description + ", currentURL [" + currentURL
          + "],  should not contain [" + expectedUrl + "]";
    }
  }

  public boolean checkCurrentURL(String expectedUrl) {
    log.debug("checkCurrentURL");
    String currentURL = driver.getCurrentUrl();
    log.debug("currentURL:" + currentURL);
    log.debug("expectedUrl:" + expectedUrl);
    return currentURL.contains(expectedUrl);
  }

  public void cancelPrint() {
    Robot r;
    try {
      r = new Robot();
      r.keyPress(KeyEvent.VK_ESCAPE);
      r.keyRelease(KeyEvent.VK_ESCAPE);
    }
    catch (AWTException e) {
      AssertJUnit.fail("cancelPrint: cancel print dialog box failed: [" + e + "]");
    }
  }

  public String getExpectedShareAlertMessage(RegisteredUser sender, RegisteredUser shareeRecipient) {
    String expectedAlertMessage = null;
    if (sender.getHomeShard().equals(shareeRecipient.getHomeShard())) {
      if (sender.getUsername().equals(shareeRecipient.getUsername()))
        expectedAlertMessage = Locale_en_US_Errors.SHARING_SELF_ERROR;
    }
    else {
      if (!shareeRecipient.getUsername().contains("non"))
        expectedAlertMessage = Locale_en_US_Errors.SHARING_ACROSS_SHARDS_ERROR;
    }
    return expectedAlertMessage;
  }

  public HomePage clickcompanyNameDocumentCloudLink() {
    waitUntilClickable(waitTime, companyNameDocumentCloudLocator);
    click(companyNameDocumentCloudLocator);

    HomePage homePage = new HomePage(driver);
    homePage.getTitle();
    return homePage;
  }

  /**
   * This method returns the Ordinal Position of Specified Value in Select Web Element
   * If value is not found, method will return 0
   *
   * @param {WebElement} selectEl - Specified Select Web Element
   * @param {String}     text - Select Text
   * @return {int} Ordinal position of selected value
   */
  protected int getPositionOf(WebElement selectEl, String text) {
    int iPosition = 0;
    if (selectEl != null && text != null) {
      Select sel = new Select(selectEl);
      List<WebElement> options = sel.getOptions();

      for (int i = 0; i < options.size(); i++) {
        WebElement el = options.get(i);
        if (el.getText().contains(text)) {
          // if (el.getText().equals(text)) {
          iPosition = i + 1;
          return iPosition;
        }
      }
    }
    return iPosition;
  }

  /**
   * This method returns the Ordinal Position of Specified Value under a Specified group in a Select Web Element
   * If value is not found, method will return 0
   *
   * @param {WebElement} selectGroupEl - Specified Group Web Element
   * @param {String}     text - Select Text
   * @return {int} Ordinal position of selected value
   */
  protected int getPositionOfGroupElement(WebElement selectGroupEl, String text) {
    int iPosition = 0;
    if (selectGroupEl != null && text != null) {
      List<WebElement> options = selectGroupEl.findElements(By.tagName("option"));
      ;
      for (int i = 0; i < options.size(); i++) {
        WebElement el = options.get(i);
        if (el.getText().contains(text)) {
          iPosition = i + 1;
          return iPosition;
        }
      }
    }
    return iPosition;
  }

  /**
   * returns the text of element at 'position' in Select element.
   *
   * @param selectEl
   * @param position
   * @return
   */
  protected String getContentOf(WebElement selectEl, int position) {
    if (selectEl != null) {
      Select sel = new Select(selectEl);
      List<WebElement> options = sel.getOptions();
      if ((position < 0) || (position >= options.size())) {
        assert false : "Invalid position '" + position + "'. Select field '" + selectEl.getTagName() + "' contains " + options.size()
            + " options.";
        return null;
      }
      WebElement el = options.get(position);
      return el.getText();
    }
    return null;
  }

  public String getUrlWithoutPort(String url) {
    String newUrl = null;
    try {
      URL expectedURL = new URL(url);
      newUrl = expectedURL.getProtocol() + "://" + expectedURL.getHost() + expectedURL.getPath();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newUrl;
  }

  public String getUrlHostAndPath(String url) {
    String newUrl = null;
    try {
      URL expectedURL = new URL(url);
      newUrl = expectedURL.getHost() + expectedURL.getPath();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newUrl;
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
   * Waits until the spinner disappears or after timeout
   *
   * @param progressLocator
   * @param waitTime
   * @return
   */
  /*
   * public boolean waitUntilProgressDone(WebElement progressLocator, int waitTime) {
   * try {
   * if (isProgressDone(progressLocator)) {
   * return true;
   * } else {
   * int loopSize = (int) Math.ceil(waitTime / 5);
   * for (int i = 1; i < loopSize; i++) {
   * DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(Time.SHORTER);
   * if (isProgressDone(progressLocator)) return true;
   * }
   * //assert false : "waitUntilProgressDone: manage page is till trying to laod after " + waitTime + " seconds";
   * }
   * } catch (Exception te) {
   * AssertJUnit.fail("waitUntilProgressDone:  the progress is not done after " + waitTime + " seconds, error: [" + te + "]");
   * }
   * return false;
   * }
   */

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
        setDriverImplicitlyWait(waitTime, TimeUnit.SECONDS);
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
   * click on the left header to go to home page. This will work for with or without custom logo.
   *
   * @param basePage
   * @return HomePage
   */
  public HomePage clickLeftHeader(BasePage basePage) {
    HomePage homePage = null;
    try {
      if (isElementPresent(logoLocator)) {
        homePage = basePage.clickLogo();
      }
      else {
        homePage = basePage.clickcompanyNameDocumentCloudLink();
      }
    }
    catch (Exception te) {
      AssertJUnit.fail("clickLeftHeader:  Left Header is invlaid, error: [" + te + "]");
    }
    return homePage;
  }

  /**
   * check if an object is displayed. this can be used to check if something is displayed, like popup, alert/error message,etc, anything
   * that is controlled by style
   *
   * @param locator
   * @return
   */
  public boolean isObjectDisplayed(WebElement locator) {
    String styleValue = getAttributeValue(locator, "style");
    if (styleValue.contains("block"))
      return true;
    return false;
  }

  public String click(WebElement locator) {
    return click(locator, waitTime);

  }

  /**
   * get alert message based on the alertLocator
   *
   * @param alertLocator
   * @return
   */
  public String getAlertText(WebElement alertLocator) {
    try {
      if (isElementPresent(alertLocator, Time.TWOSECOND)) {
        return alertLocator.getText();
      }
    }
    catch (Exception te) {
      AssertJUnit.fail("getAlertText:  alert message is invalid, error: [" + te + "]");
    }
    return null;
  }

  /**
   * get message on model dialog
   *
   * @param dialogLocator
   * @param alertLocator
   * @return
   */
  public String getDialogText(WebElement dialogLocator, WebElement alertLocator) {
    try {
      if (isElementPresent(dialogLocator, 1)) {
        return alertLocator.getText();
      }
    }
    catch (Exception te) {
      //Don't fail here, check the returned message for error out side this
      //AssertJUnit.fail("getDialogText:  alert message is invalid, error: [" + te + "]");
    }
    return null;
  }

  public String getDialogText() {
    return getDialogText(modalDialogLocator, alertDangerLocator);
  }

  public String getModalDialogText() {
    waitUntilDisplayed(modalBodyListLocator.get(1), Time.TWOSECOND);
    return modalBodyListLocator.get(1).getText();
  }

  /**
   * Waits for modal dialog popup and verifies its presence.
   */
  public void waitForOpenLinkConfirmationDialog() {
    log.debug("waitForOpenLinkConfirmationDialog()");
    waitUntilVisible(Time.SHORT, openLinkConfirmationDialogLocator);
  }

  /**
   * Verifies if Open Link Confirmation Dialog is present
   * @return {boolean}
   */
  public boolean isOpenLinkConfirmationDialogPresent(int timeout) {
    log.debug("isOpenLinkConfirmationDialogPresent(" + timeout + ")");
    return isElementPresent(openLinkConfirmationDialogLocator, timeout);
  }

  public boolean waitForJStoLoad() {

    WebDriverWait wait = new WebDriverWait(driver, TimeDuration.LONG);
    // wait for jQuery to load
    ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
      @Override
      public Boolean apply(WebDriver driver) {
        try {
          JavascriptExecutor js = (JavascriptExecutor) driver;
          return ((Long) js.executeScript("return jQuery.active") == 0);
        }
        catch (Exception e) {
          return true;
        }
      }
    };

    // wait for Javascript to load
    ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
      @Override
      public Boolean apply(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return js.executeScript("return document.readyState")
            .toString().equals("complete");
      }
    };

    return wait.until(jQueryLoad) && wait.until(jsLoad);
  }

  public String getAlertError(int timeoutInSecond) {
    if (this.isElementPresent(alertLocator, timeoutInSecond)) {
      return alertLocator.getText();
    }
    return null;
  }

  public String getDetailError(int timeoutInSecond) {
    if (this.isElementPresent(detailsSendLocator, timeoutInSecond)) {
      return detailsSendLocator.getText();
    }
    return null;
  }


  /**
   * Check if there is Server error on the modal that's visible or have the current focus
   * This will work for whethere there are multiple open at the same time or single modal
   * @param timeoutInSecond
   * @return Will return the Server error if exists, and null if not
   */
  public String getServerErrorInModal(int timeoutInSecond) {
    WebElement modal = getVisibleModal();
    if (modal == null) {
      return null;
    }
    String modalText = modal.getText();
    if (modalText.contains("Server error\n")) {
      return modalText;
    }
    return null;
  }

  /**
   * Get text from popup alert dialog box
   * @param timeoutInSecond wait time
   * @return {String} all text on the alert box
   */
  public String getAlertInModal(int timeoutInSecond) {
    if (!isElementPresent(modalBodyView99Locator, timeoutInSecond)) {
      return null;
    }
    String modalText =  modalBodyView99Locator.getText();
    return modalText;
  }

  public void waitForPageLoad(WebDriver driver) {
    ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
      }
    };
    WebDriverWait wait = new WebDriverWait(driver, TimeDuration.LONG);
    wait.until(pageLoadCondition);
  }

  /**
   * @param locator
   * @return
   * @throws Exception
   */
  protected boolean isFocused(WebElement locator) {
    boolean isFouse = false;
    try {
      isFouse = locator.equals(driver.switchTo().activeElement());
      if (isFouse) {
        return true;
      }
      else {
        execScript("arguments[0].focus();", locator);
        isFouse = locator.equals(driver.switchTo().activeElement());
      }
    }
    catch (Exception e) {
      System.out.println("isFocused: locator focus is invid: [" + e + "]");
    }
    return isFouse;
  }

  /**
   * Verifies if locator is focused.
   * Note: Method is used for accessibility testing, and should not be updated to use JS.
   * @param locator
   * @return {boolean}
   */
  protected boolean isFocusedForAccessibility(WebElement locator) {
    boolean isFocused = false;
    try {
      isFocused = locator.equals(driver.switchTo().activeElement());
    }
    catch (Exception e) {
      log.debug("isFocused: locator focus is invalid: [" + e + "]");
    }
    return isFocused;
  }

  /**
   * logout from navbar-dropdown
   */
  public void signout() {
    header.clickSignOut(false);
  }

  /**
   * perform a mouse over to a giving element
   *
   * @param element
   */
  public void mouseOver(WebElement element) {
    Actions builder = new Actions(driver);
    builder.moveToElement(element).build().perform();
  }

  public boolean isWarningMessagePresent() {
    log.debug("isWarningMessagePresent()");
    return isElementPresent(warningMessageLocator, Time.SHORTER);
  }

  public String getWarningMessage() {
    log.debug("getWarningMessage");
    assert isWarningMessagePresent() : "Warning message is NOT present!!!";
    return warningMessageLocator.getText();
  }

  public BasePage clickWarningOkButton() {
    log.debug("clickWarningOkButton()");
    assert isWarningMessagePresent() : "Warning message is NOT present!!!";
    waitUntilVisible(Time.SHORT, warningOkButtonLocator);
    clickOnLocator(warningOkButtonLocator);
    return this;
  }

  public BasePage clickWarningCloseButton() {
    log.debug("clickWarningCloseButton()");
    assert isWarningMessagePresent() : "Warning message is NOT present!!!";
    waitUntilVisible(Time.SHORT, warningCloseButtonLocator);
    clickOnLocator(warningCloseButtonLocator);
    return this;
  }

  public String getHelpLinkUrl() {
    return helpLinkLocator.getAttribute("href");
  }

  public void selectItemInHelpMenuDropdown(String item){
    ////*[@id="id-helpbar-dropdown"]
    Select selectBox = new Select(helpLinkLocator);
    selectBox.selectByValue(item);
  }

  public boolean isHelpButtonVisible() {
    return header.isHelpButtonVisible();
  }

  public boolean isLinkPresentInDropdownHelpMenu(String linkName) {
    return header.isLinkPresentInDropdownHelpMenu(linkName);
  }

  public void mouseHoverHelpLink() {
    header.openHelpMenu();
  }

  /**
   * Verifies if Legal Notices link is present in the Help menu dropdown
   * @return {boolean}
   */
  public boolean isLegalNoticesLinkPresent() {
    log.debug("isLegalNoticesLinkPresent");
    return isElementPresent(legalNoticesLinkLocator, Time.SHORT);
  }

  /**
   * Clicks on 'Legal Notices' link in Help menu
   * @return {AboutBox}
   */
  public AboutBox clickLegalNotices() {
    log.debug("clickLegalNotices");
    return header.clickLegalNotices();
  }

  /**
   * Verifies if User Guide link is present in the Help menu dropdown
   * @return {boolean}
   */
  public boolean isHelpUserGuideLinkPresent() {
    log.debug("isHelpUserGuideLinkPresent");
    return isElementPresent(helpDropdownUserGuideLocator, Time.SHORT);
  }

  /**
   * Verifies if Tutorials link is present in the Help menu dropdown
   * @return {boolean}
   */
  public boolean isHelpTutorialsLinkPresent() {
    log.debug("isHelpTutorialsLinkPresent");
    return isElementPresent(helpDropdownTutorialsLocator, Time.SHORT);
  }

  /**
   * Verifies if Contact Support link is present in the Help menu dropdown
   * @return {boolean}
   */
  public boolean isHelpContactSupportLinkPresent() {
    log.debug("isHelpContactSupportLinkPresent");
    return isElementPresent(helpDropdownContactSupportLocator, Time.SHORT);
  }

  /**
   * Returns list of Help Menu choices
   * @return {List<WebElement>}
   */
  public List<WebElement> getHelpMenuList() {
    log.debug("getHelpMenuList");
    return header.getHelpMenuList();
  }

  /**
   * Returns list of Help Menu titles
   * @return {List<String>}
   */
  public List<String> getHelpMenuListTitles() {
    log.debug("getHelpMenuListTitles");
    List<WebElement> helpMenuElements = getHelpMenuList();
    List<String> helpMenutitles = new ArrayList<>();
    for (WebElement element : helpMenuElements) {
      helpMenutitles.add(element.getText());
    }
    return helpMenutitles;
  }

  /**
   * Explicit wait for the "alert-message" to be done
   * This is to wait for any type of successful message to be displayed and than disappear
   * @param timeOut  time in seconds
   * @return boolean whether the spinner is done or not after the given time
   */
  public boolean isAlertMessageDone(int timeOut) {
    if (waitUntilProgressDone(new By.ByClassName("alert-message"), timeOut)) {
      return true;
    }
    log.debug("isAlertMessageDone: The AlertMessage is not done after " + timeOut + " seconds.");
    return false;
  }

  /**
   * Waits for spinner to disappear and reports error if spinner is still present after given wait time
   *
   * @param timeOut The maximum time to wait (in seconds)
   */
  public void waitForSpinnerDone(int timeOut) {
    assert isSpinnerDone(timeOut) : "Spinner is still present after wait time (seconds): " + timeOut;
  }

  /**
   * This is a method to check if the spinner is done
   * @param timeOut
   * @return
   */
  public boolean isInterstitialDone(int timeOut) {
    if (waitUntilProgressDone(interstitialByLocator, timeOut)) {
      return true;
    }
    return false;
  }

  /**
   * This is to check if Circle loader is done. This i mostly for microserice
   * @param timeOut wait time in seconds
   * @return boolean
   */
  public boolean isCircleLoaderDone(int timeOut) {
    if (waitUntilProgressDone(By.className("spectrum-CircleLoader-track"), timeOut)) {
      return true;
    }
    return false;
  }


  public boolean isInterstitialProgressDone(int timeOut) {
    int retry = (int) Math.ceil(timeOut/2);
    for (int i = 0; i < retry; i++) {
      if (isProgressDone(interstitialLocator)) {
        return true;
      }
    }
    return false;
  }

  public boolean isThumbnailLoaded(int timeOut) {
    if (this.isSpinnerDone(timeOut))
      return true;
    return false;
  }

  public static int getDefaultRetry(int retryTime) {
    String firstSignerShard = ShardService.getShard(0);
    // for single shard, retry 5 times, and for cross shard retry 40 times
    int defaultRetry = (homeShard.equals(firstSignerShard)) ? 2 : retryTime;
    return defaultRetry;
  }

  /**
   * Check if Modal Dialog popup is present
   *
   * @return {boolean} True if Modal Dialog is present
   */
  public boolean isModalDialogPresent() {
    log.debug("isModalDialogPresent()");
    return isElementPresent(modalDialogLocator, Time.SHORT);
  }

  public boolean isModalDialogPresent(int waitTime) {
    log.debug("isModalDialogPresent()");
    return isElementPresent(modalDialogLocator, waitTime);
  }

  /**
   * Check if the modalDialog is closed based on the retry
   * @param retry
   * @return
   */
  public boolean isModalDialogClosed(int retry) {
    boolean isModalOpen = isModalDialogPresent();
    for (int i = 0; i < retry; i++) {
      if (isModalOpen) {
        DateTimeUtils.sleepMilliSecondsAsLastResortBecauseNothingElseWorks(1000);
        isModalOpen = isModalDialogPresent();
        if (!isModalOpen) return true;
      }
      else {
        return true;
      }
    }
    return !isModalOpen;
  }

  /**
   * Check if Title Label on Modal Dialog popup is present
   *
   * @return {boolean} True if Title Label on Modal Dialog is present
   */
  public boolean isModalDialogTitleLabelPresent() {
    log.debug("isModalDialogTitleLabelPresent()");
    assert isModalDialogPresent() : "Modal Dialog is NOT present!";
    return isElementPresent(modalDialogTitleLocator, Time.TWOSECOND);
  }

  /**
   * Check if Title Label on Modal Dialog popup is present
   *
   * @return {boolean} True if Title Label on Modal Dialog is present
   */
  public boolean isJITModalDialogTitlePresent() {
    log.debug("isModalDialogTitlePresent()");
    return isElementPresent(dialogTitleLocator, Time.SHORT);
  }

  /**
   * Get Title of Modal Dialog popup
   *
   * @return {String} Title of Modal Dialog popup
   */
  public String getModalDialogTitle() {
    log.debug("getModalDialogTitle()");
    assert isModalDialogTitleLabelPresent() : "Modal Dialog Title Label is NOT present!";
    return modalDialogTitleLocator.getText();
  }

  /**
   * Get Title of JIT Modal Dialog popup
   *
   * @return {String} Title of Modal Dialog popup
   */
  public String getJITModalDialogTitle() {
    log.debug("getModalDialogTitle()");
    assert isJITModalDialogTitlePresent() : "Modal Dialog Title Label is NOT present!";
    return dialogTitleLocator.getText();
  }

  /**
   * Check if Body of Modal Dialog popup is present
   *
   * @return {boolean} True if Modal Dialog Body is present
   */
  public boolean isModalDialogBodyPresent() {
    log.debug("isModalDialogBodyPresent()");
    assert isModalDialogPresent() : "Modal Dialog Body is NOT present!";
    return isElementPresent(modalDialogBodyLocator, Time.SHORTEST);
  }

  /**
   * Get Body of Modal Dialog popup
   *
   * @return {String} Body of Modal Dialog popup
   */
  public String getModalDialogBody() {
    log.debug("getModalDialogBody()");
    assert isModalDialogBodyPresent() : "Modal Dialog Body is NOT present!";
    return modalDialogBodyLocator.getText();
  }

  /**
   * Create xPath for Modal Dialog Box Locator with Specified Title
   *
   * @param {String} sTitle - Specified Title
   * @return {String} xPath
   */
  private String createXpathForModalDialogLocator(String sTitle) {
    String xPath = "//div[contains(@class,'modal-dialog')]/div[@class='modal-content']//h1[contains(@id,'modal-title') and @class='modal-title' and text()='" + sTitle + "']/ancestor::div[contains(@class,'modal-dialog')]";
    return xPath;
  }

  /**
   * Check if Modal Dialog popup with Specified Title is present
   *
   * @param {String} sTitle - Specified Modal Dialog Title
   * @return {boolean} True if Modal Dialog with specified Title is present
   */
  public boolean isModalDialogPresent(String sTitle) {
    log.debug("isModalDialogPresent(" + sTitle + ")");
    String xPath = createXpathForModalDialogLocator(sTitle);
    return isElementPresent(By.xpath(xPath), Time.SHORTER);
  }

  /**
   * Get Modal Dialog Locator with Specified Title
   *
   * @param {String} sTitle - Specified Title
   * @return {WebElement} Modal Dialog Locator
   */
  protected WebElement getModalDialogLocator(String sTitle) {
    String xPath = createXpathForModalDialogLocator(sTitle);
    assert isElementPresent(By.xpath(xPath), Time.SHORTEST) : "Modal Dialog Box with Title '" + sTitle + "' is NOT present!";
    WebElement modalDialogLocator = driver.findElement(By.xpath(xPath));
    return modalDialogLocator;
  }

  /**
   * Get Body of Modal Dialog popup with Specified Title
   *
   * @param {String} sTitle - Specified Modal Dialog Title
   * @return {String} Body of Modal Dialog with Specified Title
   */
  public String getModalDialogBody(String sTitle) {
    log.debug("getModalDialogBody(" + sTitle + ")");
    WebElement modalDialog = getModalDialogLocator(sTitle);
    String xPathBody = "./div[@class='modal-content']//div[contains(@id,'modal-body') and @class='modal-body']";
    assert isElementPresent(modalDialog, By.xpath(xPathBody), Time.SHORTEST) : "Body of Modal Dialog with Title '" + sTitle + "' is NOT present!";
    WebElement modalDialogBody = modalDialog.findElement(By.xpath(xPathBody));
    String sBody = modalDialogBody.getText();
    return sBody;
  }

  /**
   * Wait till Reload button in the Error dialog box becomes visible
   */
  public void waitUntilReloadButtonVisible() {
    waitUntilVisible(Time.SHORT, warningOkButtonLocator);
  }

  /**
   * Wait till Reload button in the Error dialog box becomes visible
   */
  public void waitUntilErrorReloadButtonVisible(int timeout) {
    log.debug("waitUntilErrorReloadButtonVisible()");
    waitUntilVisible(timeout, warningReloadButtonLocator);
  }

  /**
   * Is the localization language dropdown displayed on the bottom of page
   *
   * @return true if dropdown is present
   */
  public boolean isLanguageDropdownDisplayed() {
    log.info("isLanguageDropdownDisplayed()");
   return isLanguageSelectorPresent();
  }

  /**
   * Is the localization language dropdown enabled on the bottom of page
   *
   * @return true if dropdown is enabled
   */
  public boolean isLanguageDropdownClickable() {
    log.info("isLanguageDropdownClickable()");
    return header.isLanguageSelectorClickable();
  }

  /**
   * check if the Popup is present
   *
   * @return
   */
  public boolean isCookiesAndOtherTechnologiesPopUpOpened() {
    log.debug("Check if Cookies and other technologies Pop-up is opened");
    return isElementPresent(CookiesAndOtherTechnologiesPopUpTitleLocator, 1);
  }

  public void closeCookiesAndOtherTechnologiesPopUp() {
    log.debug("Close Cookies and other technologies Pop-up");
    if (isCookiesAndOtherTechnologiesPopUpOpened()) {
      waitUntilClickable(Time.SHORTEST, cookiesAcceptButtonLocator);
      cookiesAcceptButtonLocator.click();
      DateTimeUtils.sleepMilliSecondsAsLastResortBecauseNothingElseWorks(150);
    }

  }

  /**
   * Type Text into WebElement
   *
   * @param text
   * @param element
   */
  protected void actionsTypeInput(String text, WebElement element) {
    log.debug("typing: " + text + " into: " + element.getAttribute("outerHTML"));
    waitUntilVisible(Time.LONG, element);
    element.clear();
    if(isMobileBrowser()) {
      element.sendKeys(text);
    } else {
      Actions actions = new Actions(driver);
      DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(2);
      element.click();
      DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(2);
      actions.sendKeys(element, text).release().build().perform();
    }
  }

  /**
   * Click on an Element
   *
   * @param element
   */
  protected void actionsClick(WebElement element) {
    log.debug("clicked on: " + element.getAttribute("outerHTML"));
    if(isMobileBrowser()) {
      click(element);
    } else {
      Actions actions = new Actions(driver);
      waitUntilVisible(Time.LONG, element);
      waitUntilClickable(Time.LONG, element);
      actions.click(element).perform();
    }
  }


  /**
   * Click on "Manage" tab
   *
   * @return {ManagePageV4}
   */
  public ManagePageJS clickManageV4Tab() {
    ManagePageJS managePageJS = header.clickManageTab();
    this.isSpinnerDone(Time.LONG);
    //sometimes loading the ManageJS will not have search result,
    // must have the wait below to ensure search result and reduce flapper
    waitForSearchResult(Time.LONG);
    dismissGainsightGuideIfPresent();
    return managePageJS;
  }

  /**
   * CLick on Manage tab without any verification
   */
  public void clickManageOnly() {
    log.debug("clickManageOnly()");
    header.clickManageTab(false);
  }

  public boolean hasNoSearchResult() {
    WebElement manageContent = driver.findElement(By.id("manageContent"));
    String manageContentText = manageContent.getText();
    return (manageContentText.contains(Locale_en_US.NO_VALID_SEARCH_RESULT));
  }

  public boolean waitForSearchResult(int waitTimeInSeconds) {
    boolean hasNoResult = hasNoSearchResult();
    if (!hasNoResult) return true;
    int retry = 0;
    while (retry < waitTimeInSeconds) {
      DateTimeUtils.sleepMilliSecondsAsLastResortBecauseNothingElseWorks(1);
      browserRefresh();
      hasNoResult = hasNoSearchResult();
      if (!hasNoResult)  {
        return true;
      }
      else {
        retry++;
      }
    }
    return false;
  }

  /**
   * Get the parent of a given element
   * @param elementLocator
   * @return
   * @throws Exception
   */
  public WebElement getParentElement(WebElement elementLocator) throws Exception {
    WebElement parent = null;
    try {
      parent = elementLocator.findElement(By.xpath("./.."));
    }
    catch (Exception e) {
      parent = (WebElement) ((JavascriptExecutor) driver).executeScript("return arguments[0].parentNode;", elementLocator);
    }
    return parent;
  }

  /**
   * Get the next sibling of the given element
   * @param elementLocator given element
   * @return WebElement    next sibling of the given element
   */
  public WebElement getNextSiblingElement(WebElement elementLocator) {
    return elementLocator.findElement(By.xpath("following-sibling::*"));
  }


  /**
   * Click on "Data Governance" link
   *
   * @return {DataGovernancePage}
   */
  public DataGovernancePage clickDataGovernanceLink() {
    log.debug("clickDataGovernanceLink()");
    click(dataGovernancePageLinkLocator, Time.LONG);
    assert validateCurrentPage(PageUrlPaths.DATA_GOVERNANCE_PAGE) : validationAssertFailureMessage(PageUrlPaths.DATA_GOVERNANCE_PAGE);
    return new DataGovernancePage (driver);
  }

  /**** New Methods for UMG feature ****/

  /*
   * Checks if the name of the admin Tab is "Groups"
   */
  public boolean isGroupsTabPresent() {
    return header.isGroupsTabPresent();
  }

  /**
   * Get the Admin Tab name. It can be "Account", "Group",
   * and "Groups" when UMG is enabled and the user is group admin for multiple groups
   * @return
   */
  public String getAccountTabName() {
    return header.getAdminTabName();
  }

  /**
   * Click the Groups tab.
   *
   * @return {NavigationContainer}
   */
  public GroupListPage clickGroupsTab() {
    return header.clickGroupsTab();
  }

  /**
   * The the env at runtime
   * @return
   */
  public String getCurrentEnv() {
    log.debug("getCurrentEnv()");
    String EXECUTION_ENV = "test.env.default";
    return APIUtils.getPropertyFileValue(EXECUTION_ENV);
  }

  /**
   * Verify the current domain is the companyName Sign domain
   * @param currentURL
   */
  public void verifycompanyNameSignDomain (String currentURL) {
    log.debug("verifycompanyNameSignDomain()");
    assert currentURL.contains(Locale_en_US.companyNameSIGN_DOMAIN_LOWER_CASE) :
        "The current domain should be in: " + Locale_en_US.companyNameSIGN_DOMAIN_LOWER_CASE + "but it's presented in: " + currentURL;
  }

  ///////////////// Mobile /////////////////

  /**
   * @param driver : WebDriver
   * @return {boolean} Return true if the platformName is Android
   */
  public boolean isAndroid(WebDriver driver) {
    Capabilities capabilities = ((RemoteWebDriver)driver).getCapabilities();
    return capabilities.getPlatformName().name().equalsIgnoreCase("ANDROID");
  }

  /**
   * Switch context to NATIVE for Android
   * @param driver : WebDriver
   */
  public void switchContextToAndroidNative(WebDriver driver) {

    // TODO: Fix AppiumDriver context
    /*
    AppiumDriver appiumDriver = (AppiumDriver) driver;
    appiumDriver.context("NATIVE_APP_CONTEXT");
    Assert.assertTrue("Current context is not `NATIVE_APP` as expected ",
                      appiumDriver.getContext().equalsIgnoreCase("NATIVE_APP_CONTEXT"));

     */
  }

  /**
   * Switch context to "CHROMIUM"
   * @param driver : WebDriver
   */
  public void switchContextToChromium(WebDriver driver) {
    // TODO: Fix AppiumDriver context
        /*
    AppiumDriver appiumDriver = (AppiumDriver) driver;
    appiumDriver.context(APPIUM_CHROMIUM_CONTEXT);
    Assert.assertTrue("Current context is not `CHROMIUM` as expected",appiumDriver.getContext().equalsIgnoreCase(APPIUM_CHROMIUM_CONTEXT));
         */
  }

  /**
   * Switch context to WEBVIEW, for hybrid testing, web and native app or functions.
   * This should work for browsers on both iOS and Android.
   * @param driver : WebDriver
   */
  public void switchContextToWebView(WebDriver driver) {
    // TODO: Fix AppiumDriver context
    /*
    log.debug("switchContextToWebView()");
    String webview = null;
    AppiumDriver appiumDriver = (AppiumDriver) driver;

    //Search for WEBVIEW, usually WEBVIEW + a random string
    Set<String> contextNames = appiumDriver.getContextHandles();
    for (String contextName : contextNames) {
      if (contextName.contains("WEBVIEW")) {
        webview = contextName;
      }
    }
    appiumDriver.context(webview);
    Assert.assertTrue("Current context is not WEBVIEW as expected",appiumDriver.getContext().equalsIgnoreCase(webview));

     */
  }

  /**
   * Get window width
   *
   * @return {String} window width
   */
  public int getWindowWidth(WebDriver driver) {
    log.debug("getWindowWidth()");
    return driver.manage().window().getSize().getWidth();
  }

  /**
   * Get resolution
   *
   * @return {int} window width
   */
  public String getResolutionperWindowWidth(int windowWidth) {
    log.debug("getResolutionperWindowWidth");
    if(windowWidth < 350) {
      return "z50";
    } else if (windowWidth < 500) {
      return "z75";
    }  else if (windowWidth < 1000) {
      return "z100";
    } else {
      return "z200";
    }
  }

  /**
   * Set Portrait screen rotation
   * @param driver : WebDriver
   */
  public void setPortraitScreenRotation(WebDriver driver) {
    //TODO: Fix Appium if necessary
    /*
    log.debug("setAndVerifyPortraitScreenRotation");
    AppiumDriver appiumDriver = (AppiumDriver) driver;
    appiumDriver.rotate(ScreenOrientation.PORTRAIT);

     */
  }

  /**
   * Set LandScapeScreen screen rotation
   * @param driver : WebDriver
   */
  public void setLandScapeScreenRotation(WebDriver driver) {
    //TODO: Fix Appium if necessary
    log.debug("setAndVerifyLandScapeScreenRotation");
    /*
    AppiumDriver appiumDriver = (AppiumDriver) driver;
    appiumDriver.rotate(ScreenOrientation.LANDSCAPE);

     */
  }

  /**
   * Get $('#document').width()
   * @param driver : Webdriver
   * @return {Long} document width
   */
  public Long getDocumentWidth(WebDriver driver) {
    log.debug("getDocumentWidth");
    return (Long) ((JavascriptExecutor) driver).executeScript("return $('#document').width() ;");
  }

  /**
   * Get window.innerWidth
   * @param driver : Webdriver
   * @return {Long} window innder width
   */
  public Long getWindowInnerWidth(WebDriver driver) {
    log.debug("getWindowInnerWidth");
    return ((Long) ((JavascriptExecutor) driver).executeScript("return window.innerWidth "));
  }


  /**
   * Custom class written to set X,Y coordinates to replicate multifinger gesture to zoomIn on a
   * mobile
   */
  public class MultiFingerGesture {

    int fingerOneSrcX = 0;
    int fingerOneSrcY = 0;
    int fingerOneDestX = 0;
    int fingerOneDestY = 0;

    int fingerTwoSrcX = 0;
    int fingerTwoSrcY = 0;
    int fingerTwoDestX = 0;
    int fingerTwoDestY = 0;

    /**
     * Initial/Starting point for finger1
     * @param pointX : x coordinate
     * @param pointY : y coordinate
     */
    public void setFingerOneSrc(int pointX, int pointY) {
      fingerOneSrcX = pointX;
      fingerOneSrcY = pointY;
    }

    /**
     * Destination point for finger1
     * @param pointX : x coordinate
     * @param pointY : y coordinate
     */
    public void setFingerOneDest(int pointX, int pointY) {
      fingerOneDestX = pointX;
      fingerOneDestY = pointY;
    }

    /**
     * Initial/Starting point for finger2
     * @param pointX : x coordinate
     * @param pointY : y coordinate
     */
    public void setFingerTwoSrc(int pointX, int pointY) {
      fingerTwoSrcX = pointX;
      fingerTwoSrcY = pointY;
    }

    /**
     * Destination point for finger2
     * @param pointX : x coordinate
     * @param pointY : y coordinate
     */
    public void setFingerTwoDest(int pointX, int pointY) {
      fingerTwoDestX = pointX;
      fingerTwoDestY = pointY;
    }

  }

  /**
   * ZoomIn using multifinger gesture on mobile
   * @param driver : WebDriver
   * @param multiFingerGesture : custom class created to set coordinates for multifingergesture
   */
  public void zoomIn(WebDriver driver, MultiFingerGesture multiFingerGesture) {
    /*
    log.debug("zoomIn() ");
    AppiumDriver appiumDriver = (AppiumDriver) driver;
    if(isAndroid(appiumDriver)) {
      log.debug("Switch context to Native APP ");
      switchContextToAndroidNative(appiumDriver);
    }
    log.debug("Multi-touch action to zoomin to replicate action of zooming in with two " +
                  "fingers");
    MultiTouchAction multiTouchAction = new MultiTouchAction(appiumDriver);

    log.debug("Long press on calculated x,y points and move finger1 to zoom-in ");
    TouchAction t1 = new TouchAction(appiumDriver);
    t1.longPress(point(multiFingerGesture.fingerOneSrcX, multiFingerGesture.fingerOneSrcY))
        .moveTo(point(multiFingerGesture.fingerOneDestX, multiFingerGesture.fingerOneDestY)).release();

    log.debug("Long press on calculated x,y points and move finger2 to zoom-in ");
    TouchAction t2 = new TouchAction(appiumDriver);
    t2.longPress(point(multiFingerGesture.fingerTwoSrcX, multiFingerGesture.fingerTwoSrcY))
        .moveTo(point(multiFingerGesture.fingerTwoDestX, multiFingerGesture.fingerTwoDestY)).release();

    log.debug("Chain finger1 and finger2 touch actions to zoom-in ");
    multiTouchAction.add(t1).add(t2).perform();

    if(isAndroid(appiumDriver)) {
      log.debug("Switch context to Chromium ");
      switchContextToChromium(appiumDriver);
    }

     */
  }

  /**
   * Scroll using multifinger gesture on mobile
   * @param driver : WebDriver
   */
  public void scroll(WebDriver driver, int pointX, int pointY, WebElement element) {
    log.debug("scroll() ");
    AppiumDriver appiumDriver = (AppiumDriver) driver;
    if(isAndroid(appiumDriver)) {
      log.debug("Switch context to Native APP ");
      switchContextToAndroidNative(appiumDriver);
    }
    log.debug("touch action to scroll action ");
    //TODO: https://www.thegreenreport.blog/articles/the-transition-from-touch-to-w3c-actions-in-selenium/the-transition-from-touch-to-w3c-actions-in-selenium.html
    //TouchActions action = new TouchActions(appiumDriver);
    //action.scroll(element, pointX, pointY);
    //action.perform();

    Assert.fail("Need to Fix Touch Actions for Scroll within BasePage");

    if(isAndroid(appiumDriver)) {
      log.debug("Switch context to Chromium ");
      switchContextToChromium(appiumDriver);
    }

  }

  /**
   * Pinch Zoom using multifinger gesture on mobile
   * @param driver : WebDriver
   * @param multiFingerGesture : custom class created to set coordinates for multifingergesture
   */
  public void pinchZoom(WebDriver driver,MultiFingerGesture multiFingerGesture) {
    /*
    log.debug("pinchZoom() ");
    AppiumDriver appiumDriver = (AppiumDriver) driver;
    if(isAndroid(appiumDriver)) {
      log.debug("Switch context to Native APP ");
      switchContextToAndroidNative(appiumDriver);
    }
    log.debug("Finger1 touch action to replicate pinch action ");
    TouchAction touch1 = new TouchAction(appiumDriver);
    touch1.press(point(multiFingerGesture.fingerOneSrcX, multiFingerGesture.fingerOneSrcY))
        .waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
        .moveTo(point(multiFingerGesture.fingerOneDestX, multiFingerGesture.fingerOneDestY));

    log.debug("Finger2 touch action to replicate pinch action ");
    TouchAction touch2 = new TouchAction(appiumDriver);
    touch2.press(point(multiFingerGesture.fingerTwoSrcX, multiFingerGesture.fingerTwoSrcY))
        .waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
        .moveTo(point(multiFingerGesture.fingerTwoDestX, multiFingerGesture.fingerTwoDestY));

    log.debug("Chain actions to replicate pinch actions ");
    MultiTouchAction action2 = new MultiTouchAction(appiumDriver);
    action2.add(touch1).add(touch2).perform();

    if(isAndroid(appiumDriver)) {
      log.debug("Switch context to Chromium ");
      switchContextToChromium(appiumDriver);
    }
    */
  }

  /**
   * Double tap gesture based on platform type
   * @param driver : WebDriver
   * @param pointX : x coordinate
   * @param pointY : y coordinate
   */
  public void doubleTap(WebDriver driver, int pointX, int pointY) {
    log.debug("doubleTap()");
    AppiumDriver appiumDriver = (AppiumDriver) driver;
    if(isAndroid(appiumDriver)) {
      doubleTapAndroid(appiumDriver,pointX,pointY);
    } else {
      doubleTapIOS(appiumDriver,pointX,pointY);
    }
  }

  /**
   * Double touch gesture, an equalivalent of tap for IOS
   * @param driver : WebDriver
   * @param pointX : x coordinate
   * @param pointY : y coordinate
   */
  public void doubleTapIOS(WebDriver driver, int pointX, int pointY) {
    /*
    log.debug("doubleTapIOS()");
    AppiumDriver appiumDriver = (AppiumDriver) driver;
    log.debug("Finger1 touch action to replicate tap action ");
    TouchAction touch1 = new TouchAction(appiumDriver);
    touch1.tap(point(pointX, pointY)).perform();

    log.debug("Finger2 touch action to replicate tap action ");
    TouchAction touch2 = new TouchAction(appiumDriver);
    touch2.tap(point(pointX, pointY)).perform();

     */
  }

  /**
   * Double tap gesture for Android
   * @param driver : WebDriver
   * @param pointX : x coordinate
   * @param pointY : y coordinate
   */
  public void doubleTapAndroid(WebDriver driver, int pointX, int pointY) {
    /*
    log.debug("doubleTapAndroid()");
    AppiumDriver appiumDriver = (AppiumDriver) driver;
    log.debug("Switch context to Native APP ");
    switchContextToAndroidNative(appiumDriver);

    log.debug("Finger1 touch action to replicate pinch action ");
    TouchAction touch1 = new TouchAction(appiumDriver);
    touch1.press(point(pointX, pointY));

    log.debug("Finger2 touch action to replicate pinch action ");
    TouchAction touch2 = new TouchAction(appiumDriver);
    touch2.press(point(pointX, pointY));

    log.debug("Tap twice on the calculated point positions of the document image in " +
                  "esign page to zoom-in");
    MultiTouchAction action2 = new MultiTouchAction(appiumDriver);
    action2.add(touch1).add(touch2).perform();

    log.debug("Switch context to CHROMIUM ");
    switchContextToChromium(appiumDriver);

     */
  }

  /**
   *
   * @return {boolean} Return true if the browser is a mobile browser
   */
  public boolean isMobile() {
    TestProperties properties = new TestProperties();
    return (properties.getBrowser().toLowerCase().contains(MOBILE_BROWSER_CHROME) || properties.getBrowser().toLowerCase().contains(MOBILE_BROWSER_SAFARI));
  }

  /**
   *
   * @param driver : webdriver
   * @return {String} screen orientation on mobile device
   */
  public String getScreenOrientation(WebDriver driver) {
    log.debug("getScreenOrientation");
    //TODO: fix me
    /*
    AppiumDriver appiumDriver = (AppiumDriver) driver;
    return appiumDriver.getOrientation().toString();

     */
    return "hello";
  }

  /**
   * Scroll action on Mobile
   * @param driver : WebDriver
   */
  public void scroll(WebDriver driver, String direction) {
    log.debug("scroll:" + direction);
    AppiumDriver appiumDriver = (AppiumDriver) driver;
    appiumDriver.executeScript("mobile: scroll", ImmutableMap.of("direction", direction.toLowerCase()));
  }

  /**
   * close native keyboard on Mobile
   *
   * @param driver : WebDriver
   */
  public void closeNativeKeyboard(WebDriver driver) {
    log.debug("closeNativeKeyboard()");
    AppiumDriver appiumDriver = (AppiumDriver) driver;
    if (isAndroid(appiumDriver)) {
      // appiumDriver.hideKeyboard();
      //Can't find a way to verify whether keyboard is dismissed.
      //If We have AndroidDriver, isKeyboardShown() can help here.
    }
    else {
      List<WebElement> elems = appiumDriver.findElements(By.name("Done"));
      elems.get(0).click();
      Assert.assertFalse(isElementVisible(appiumDriver, elems.get(0), Time.SHORT));
    }
  }

  /**
   * Click on Navigate Next button on soft keyboard of iOS
   *
   * @param driver : WebDriver
   */
  public void clickNextBtnOnNativeKeyboardIOS(WebDriver driver) {
    log.debug("clickNextBtnOnNativeKeyboardIOS()");
    AppiumDriver appiumDriver = (AppiumDriver) driver;

    List<WebElement> elems = appiumDriver.findElements(By.name("Next"));
    elems.get(0).click();
  }

  /**
   * Click on Navigate Previous button on soft keyboard of iOS
   *
   * @param driver : WebDriver
   */
  public void clickPreviousBtnOnNativeKeyboardIOS(WebDriver driver) {
    log.debug("clickPreviousBtnOnNativeKeyboardIOS()");
    AppiumDriver appiumDriver = (AppiumDriver) driver;

    List<WebElement> elems = appiumDriver.findElements(By.name("Previous"));
    elems.get(0).click();
  }

  /**
   * Fillin a form field via native keyboard on Mobile
   * Set focus to a form field before call this function and
   * call switchContextToAndroidNative(driver)
   * to change browser context to NATIVE_APP
   *
   * @param driver : WebDriver
   * @param keys   : CharSequence
   */
  public void sendKeys(WebDriver driver, CharSequence keys) {
    log.debug("sendKeys()");
    Assert.fail("AppiumDriver not supported in selenium");
    //AppiumDriver appiumDriver = (AppiumDriver) driver;
    //appiumDriver.getKeyboard().sendKeys(keys);
  }

  /*
   * Method to click "Release Notes" support page  from help navigation menu
   * */
  public HelpxSignPage clickReleaseNotes() {
    header.openHelpMenu();
    header.clickReleaseNotes();
    DateTimeUtils.sleepSecondsAsLastResortBecauseNothingElseWorks(3);
    return new HelpxSignPage(driver);
  }

  /**
   * Open Page via Url
   *
   * @param pageUrl
   * @return {Page}
   */
  public Page openPageViaUrl(String pageUrl) {
    log.debug("openPageViaUrl()");
    driver.get(pageUrl);
    return this;
  }

  /**
   * Opens Page based on domain
   * @param sURL
   * @param domain
   * @return
   */
  public Page open(String sURL, Domain domain) {
    log.debug("open()");
    if (Domain.companyNameSIGN.equals(domain)){
      driver.get(sURL.replace("echosign", "companyNamesign").replace("aws", ""));
    }
    else driver.get(sURL);
    return this;
  }

  /**
   * Get text of alert-message
   * @return
   */
  public String getMessageFromAlertMessage() {
    waitUntilVisible(Time.SHORTEST,alertMessageLocator);
    return this.getAlertText(alertMessageLocator);
  }

  /**
   * Get all alert messages present on the page.
   * This method should be used when we expect multiple error messages,.
   * that are separated only by newline.
   *
   * @return List<String> - list of alert messages
   */
  public List<String> getDialogAlertMessages() {
    log.debug("getDialogAlertMessages()");
    assert isElementPresent(alertMessageLocator, Time.SHORTER) :
        "Not one alert message is currently present!!!";
    String elementContent = alertMessageLocator.getAttribute("innerHTML");
    return Arrays.asList(elementContent.split("<br>"));
  }


  /**
   * Verify the current domain is the 'echosign' domain
   * @param currentURL
   */
  public void verifyEchoSignDomain (String currentURL) {
    log.debug("verifyEchoSignDomain()");
    assert currentURL.contains(Locale_en_US.PRODUCT_NAME.toLowerCase()) :
        "The current domain should be in: " + Locale_en_US.PRODUCT_NAME.toLowerCase() + "but it's presented in: " + currentURL;
  }

  /**
   * Simply click on primary button. This will work for ok/save/update/next button on any page
   *
   */
  public void clickPrimaryButton(boolean isDialog) {
    click(primaryButtonLocator, Time.SHORTER);
    if (isDialog) {
      isInterstitialDone(Time.SHORT);
      isInterstitialProgressDone(Time.SHORT); //progress_bar
    }
    isSpinnerDone(Time.SHORT);    //spinner
  }

  public void clickPrimaryButton() {
    clickPrimaryButton(false);
  }


  /**
   * Click on Primary button  and verify no error and alert
   */
  public void clickPrimaryButtonAndVerify(String expectedError) {
    assert isElementPresent(primaryButtonLocator, Time.SHORTER) : "Primary button is not present";
    click(primaryButtonLocator, Time.SHORTER);
    String error = this.getAllErrors(Time.SHORTEST);
    if (expectedError == null) {
      assert (error == null || error.isEmpty()) : "After click PrimaryButton, get error [" + error + "]";
    }
    else {
      assert (error.contains(expectedError)) : "After click PrimaryButton, expected error is [" + expectedError + ", but actual error is [" + error  + "]";
    }
  }

  /**
   * Simply click on cancel button on a modal
   */
  public void clickDefaultButton() {
    click(defaultButtonLocator, Time.SHORTER);
  }

  public void clickModalPrimaryButton(int timeInSeconds) {
    click(modalPrimaryButtonLocator, timeInSeconds);
  }

  /**
   * Simply click on cancel button on a modal. This will work on any page
   */
  public void clickModalDefaultButton(int timeInSeconds) {
    if (isElementPresent(modalDefaultButtonLocator, timeInSeconds)) {
      click(modalDefaultButtonLocator, timeInSeconds);
    }
  }

  /**
   * Checks if Cookie Preferences Link is present or NOT
   * @return {boolean}
   */
  public boolean isCookiePreferencesLinkPresent(){
    log.debug("isCookiePreferencesLinkPresent()");
    boolean b = false;
    b = isElementPresent(cookiePreferencesLinkLocator, Time.SHORT);
    return b;
  }

  /**
   * Clicks on Cookie Preferences Link
   * @return {Cookie Settings Container}
   */
  public CookiePreferencesContainer clickCookiePreferencesLink(){
    log.debug("clickCookiePreferencesLink()");
    assert isCookiePreferencesLinkPresent() : " Cookie Preferences Link is NOT present. ";
    clickOnLocator(cookiePreferencesLinkLocator);
    return new CookiePreferencesContainer(driver);
  }

  /**
   * Gets Privacy Consent Cookie - feds_privacy_consent
   * @return {cookie value}
   */
  public String getPrivacyConsentCookie(){
    log.debug("getPrivacyConsentCookie()");
    String sValue = driver.manage().getCookieNamed(Locale_en_US.COOKIE_CONSENT).getValue();
    return sValue;
  }

  /**
   * Gets Privacy Consent Cookie based on provided consent
   * @param consent
   * @return {cookie value}
   */
  public String getPrivacyCookieConsentValue(String consent){
    log.debug("getPrivacyCookieConsentValue()");
    String sValue = "";
    DateTimeUtils.sleepMilliSecondsAsLastResortBecauseNothingElseWorks(3000);
    JavascriptExecutor js = (JavascriptExecutor) driver;
    if (consent.equals(Locale_en_US.COOKIE_PROVIDED_CONSENT)) {
      sValue = js.executeScript("return companyNamePrivacy.hasUserProvidedConsent()").toString();
    }
    else {
      sValue = js.executeScript("return companyNamePrivacy.hasUserProvidedCustomConsent()").toString();
    }
    return sValue;
  }

  /**
   * This method will get the element for the displayed modal-dialog when there are multiple modal open at the same time
   */
  public WebElement getVisibleModal() {
    List<WebElement> modalList = driver.findElements(By.className("modal"));
    if (modalList == null) {
      DateTimeUtils.sleepMilliSecondsAsLastResortBecauseNothingElseWorks(500);
      modalList = driver.findElements(By.className("modal"));
    }
    for (WebElement modal : modalList) {
      String styleValue = modal.getAttribute("style");
      if (styleValue.contains("block")) {
        return modal;
      }
    }
    return null;
  }

  /**
   * Get alert message on the visible/focused modal from any page
   * @return Will return alert message if exists, and null if not
   */
  public String getAlertOnVisibleModal() {
    WebElement modal = this.getVisibleModal();
    boolean hasAlert = isElementPresent((WebElement)modal.findElement(By.className("alert")), Time.TWOSECOND);

    if (hasAlert) {
      return modal.findElement(By.className("alert")).getText();
    }
    else {
      return null;
    }
  }

  /**
   * Click on butten on the visible modal when there are multple modal open at the same time
   * @param buttonName
   */
  public void clickOnVisibleButton(String buttonName) {
    WebElement modal = this.getVisibleModal();
    assert (modal != null) : "clickOnVisibleButton: getVisibleModal shouldn't get null";
    WebElement button = modal.findElement(By.className(buttonName));
    click(button, Time.SHORTER);
  }

  /**
   * Check if a button with given buttonName is enabled
   * @param buttonName
   * @return
   */
  public boolean isButtonEnabled(String buttonName) {
    WebElement modal = this.getVisibleModal();
    WebElement button = modal.findElement(By.className(buttonName));
    boolean isEnabled = !button.getAttribute("class").contains("disable");
    //boolean isEnabled = button.findElement(By.className("disable")) == null ? true : false;
    return isEnabled;
  }

  /**
   * Universal method to Check if any given element is disabled
   * @param element a given  element
   * @return {boolean} returns true if  it  is disabled, false if it is not disabled
   */
  public boolean isElementDisabled(WebElement element) {
    assert isElementVisible(element, Time.TWOSECOND) : "isElementDisabled: element is not Present after 2s";
    final String DISABLED = "disabled";
    boolean disableAttr = false;

    log.debug("check if disabled is in class");
    boolean disableClass = element.getAttribute("class").contains(DISABLED);

    log.debug("check if disabled is in attribute");
    String attStr = element.getAttribute(DISABLED);
    if (attStr == null) {
      disableAttr = false;
    }
    else if (attStr != null && attStr.equals("true")){
      disableAttr = true;
    }
    else if (attStr != null && !attStr.equals("true")){
      disableAttr = attStr.contains(DISABLED);
    }
    return (disableClass || disableAttr);
  }

  /**
   * Universal method to Check if any given element is checked
   * @param element a given  element
   * @return {boolean} returns true if it is checked, false if it is not checked
   */
  public boolean isElementChecked(WebElement element) {
    assert isElementPresent(element, Time.TWOSECOND) : "isElementChecked: element is not Present after 2s";
    final String CHECKED = "checked";
    boolean checkedAttr = false;
    boolean checkedClass = element.getAttribute("class").contains(CHECKED);
    String attStr = element.getAttribute(CHECKED);
    if (attStr != null && attStr.equals("true")){
      checkedAttr = true;
    }
    else if (attStr == null) {
      checkedAttr = false;
    }
    return (checkedClass || checkedAttr);
  }

  /**
   * Check if the given element has disabled-gradient in the  class
   * @param element element to check
   * @param waitTimeInSeconds wait time in seconds
   * @return boolean
   */
  public boolean isElementDisableGradient(WebElement element, int waitTimeInSeconds) {
    assert isElementPresent(element, waitTimeInSeconds) : "Given element is not present after " + waitTimeInSeconds + "s";
    String sClass = element.getAttribute("class");
    assert (sClass != null) : "Given element name not found";
    return sClass.contains("disabled-gradient");
  }

  /**
   * This method will click on the Admin tab, this will work for the admin tab for Account/Group/Groups admin
   * @return
   */
  public NavigationContainer clickAccountAdminTab() {
    isSpinnerDone(Time.SHORT);
    return header.clickAccountAdminTab();
  }

  public void clickAccountAdminTabAction() {
    header.clickAccountTab(false);
  }

  /**
   * Get Logo Image ALT Text
   *
   * @return {String} Logo ALT Text
   */
  public String getLogoImageALT() {
    return header.getLogoImageAlt();
  }

  /**
   * returns if the current Page is new home page.
   *
   */
  public boolean isNewHome() {
    validateCurrentPageWithAsserts(PageUrlPaths.HOME);
    return isElementPresent(By.cssSelector("div#mainContent.homejs"), Time.SHORTER);
  }

  /**
   * Click the "Enable all" button from any page if "Make It Your Own" feature is enabled

   */
  public void clickEnableAllButton() {
    if (isElementPresent((WebElement) onetrustAllLocator, Time.TWOSECOND)) {
      click(onetrustAllLocator, Time.TWOSECOND);
    }
  }

  /* Get text from the given element list
   * @param itemList
   * @return
   */
  public List<String> getTextList(List<WebElement> itemList) {
    if (itemList == null) return null;
    List<String> textList = new ArrayList<String>();
    for (WebElement item : itemList) {
      textList.add(item.getText());
    }
    return textList;
  }

  /**
   * Click the item that match expectedItemName from a list
   * @param itemList
   * @param expectedItemName
   * @return item that matches
   */
  public WebElement getMatchItem(List<WebElement> itemList,  String expectedItemName) {
    for (WebElement group : itemList) {
      String groupName = group.getText();
      if (groupName.equals(expectedItemName)) {
        return group;
      }
    }
    return null;
  }

  /**
   * check if blue bar is displayed
   * @return true if blue bar exist, false when it is not
   */
  public boolean hasBlueBarSwitchLink() {
    return isElementPresent(blueBarSwitchLinkLocator, Time.TWOSECOND);
  }

  /**
   * Get the text from blue Bar
   * @return return text on blue bar if it exist, and null if no blue bar
   */
  public String getBlueBarSwitchLinkText() {
    if(hasBlueBarSwitchLink()) {
      return blueBarSwitchLinkLocator.getText();
    }
    return null;
  }

  /**
   * Get text of red alert message
   * @return {String}
   */
  public String getTextFromAlertMessage() {
    return this.getAlertText(alertMessage);
  }

  /**
   * This method types input to any input field
   * @param inputBox
   * @param answer
   */

  public void typeIntoField(WebElement inputBox, String answer){
    log.debug("typeIntoField("+inputBox+" "+answer);
    inputBox.clear();
    inputBox.sendKeys(answer);
  }

  /**
   * Get the downloaded file based on given href, and save to downloadSavePath
   * @param href href for the audit report
   * @param downloadSavePath where the downloaded file should save to. If null, will save to default location
   * @return
   */
  // put in base file so we can get downloaded file from any page, and test
  public File getDownloadFileByHref(String href, String downloadSavePath) throws Exception {
    if (downloadSavePath == null) {
      downloadSavePath = String.format("test-output%s", File.separator);
    }

    FileDownloader fileDownloader = new FileDownloader(driver);
    File file = fileDownloader.getDownloadFileByHref(href, downloadSavePath);
    return file;
  }

  /**
   * Click TermsOfUse button if it exist
   */
  public void clickTermsOfUse() {
    log.debug("clickTermsOfUse()");
    if (isElementPresent(acceptTOULocator, Time.TWOSECOND)) {
      click(acceptTOULocator, Time.TWOSECOND);
    }
  }

  /**
   * Is the TermsOfUse displayed on the bottom of page
   *
   * @return true if TermsOfUse is present
   */
  public boolean isTermsOfUseDisplayed() {
    log.info("isTermsOfUseDisplayed()");
    return isElementVisible(driver, acceptTOULocator);
  }


  /**
   * Checks and reports intermittent app issues: DCES-4319326 server error in eSign / Authoring page and DCES-4319221 Send page error
   */
  protected void checkForErrors() {
    log.debug("checkForErrors()");
    isSpinnerDone(Time.LONG);
    try {
      String alertMsg = this.getAlertDangerMsg(1);
      String serverError = getServerErrorInModal(Time.SHORTEST);
      // checks for a specific server error "Incomplete reply from server"
      if (serverError != null) {
        String url = getCurrentUrl();
        if (url.contains(PageUrlPaths.ESIGN) || url.contains(PageUrlPaths.AUTHOR)
            || url.contains(PageUrlPaths.ESIGN_WIDGET)|| url.contains(PageUrlPaths.WIDGET_EDIT)) {
          assert (!serverError
              .contains("Incomplete reply from server")) : "Intermittent APP issue: DCES-4319326 server error occurred in eSign/Authoring page: ["
              + serverError + "]";
        }
        else {
          String msg = String.format("Server error occurred in [%s] - \n %s", url, serverError);
          throw new AssertionError(msg);
        }
      }
      // Check for send page error
      if (getCurrentUrl().contains(PageUrlPaths.SEND) && getVisibleModal() == null) {
        assert (alertMsg == null || alertMsg.isEmpty()) : "Intermittent APP issue: DCES-4319221 Send page error occurred: [" + alertMsg + "]";
      }
    }
    catch (Exception e) {
      // no ops. Let test continue.
    }

  }

  /**
   * Clicks the "clicking here" link to verify email address when a web form signer opens a signed but not yet verified web form from Manage page
   *
   * @return SignVerifyPage
   */
  public SignVerifyPage clickPostSignVerifyEmailLink() {
    log.debug("clickPostSignVerifyEmailLink()");
    assert isElementPresent(globalMessageLocator, By.tagName("a")) : "The 'clicking here' to verify email address link is not present";
    click(globalMessageLocator.findElement(By.tagName("a")));
    return new SignVerifyPage(driver).moveTo();
  }

  /**
   * Clicks the "Sign Out" button which usually is present only when authorization error occurs
   *
   * @return LoginPage
   */
  public LoginPage clickSignOutButton() {
    click(signOutButtonLocator, Time.SHORT);
    return new LoginPage(driver);
  }

  /**
   * Change the locale of the page from Language drop down using locale code
   * @Param: Locale code
   *
   */
  public void changeLocale(String locale) {
    selectLocalizationLanguage(locale);
  }

  /**
   * Check if a dialog popup is displayed on any page
   * @return boolean true if it is displayed
   *
   */
  public boolean isDialogBoxDisplayed() {
    log.debug("isDialogBoxDisplayed()");
    return isElementPresent(errorDialogLocator, Time.SHORTER);
  }

  /**
   * Get the body text on a popup dialog box on any page
   * @return
   */
  public String getModalDialogBodyText() {
    log.debug("getModalDialogBodyText()");
    if (isDialogBoxDisplayed()) {
      return errorDialogLocator.getText();
    }
    return null;
  }

  //ESeal are added into multiple pages, like Send page and web form page,
  // so put the methods in basePage

  /**
   * Check if Add eSeal link exist. This link can exist on different page, e.g Send page, create WebForm page
   * @return boolean
   */
  public boolean isAddESealLinkExist() {
    return isElementPresent(addElectronicSealLocator, Time.TWOSECOND);
  }

  /**
   * Click on Add eSeal link. Will throw error if it doesn't exist
   * @return Page
   */
  public WebElement clickAddESealLink() {
    if (isAddESealLinkExist()) {
      click(addElectronicSealLocator, Time.TWOSECOND);
      return addElectronicSealLocator;
    }
    return null;
  }

  /**
   * Checks if Workflows Tab is present
   *
   * @return [boolean] if Workflows Tab is present or not
   */
  public boolean isWorkflowsTabPresent() {
    return header.isWorkflowsTabPresent();
  }

  /**
   * Click on Manage tab from anywhere, and check if it is logged out or not to ensure the session is timed out
   * @return boolean true if it loaded login, false if not
   * @throws Exception
   */
  public boolean clickTabAndLogout() {
    boolean isLoginPage = false;
    try {
      clickManageTabOnly();
      assert validateCurrentPage(PageUrlPaths.LOGIN) : validationAssertFailureMessage(PageUrlPaths.LOGIN);
      isLoginPage = true;
    }
    catch (Exception e) {
      isLoginPage = false;
    }
    return isLoginPage;
  }


  public void clickAdminPushButton() {

          click(adminPushButtonLocator);
  }

  /**
   * This method is used to validate the spinner is present or not
   * @return
   */
  public boolean isSpinnerPresent(){
    log.debug("isSpinnerPresent()");
    return isElementPresent(spinnerLocator,Time.SHORTER);
  }

  /**
   * Check if an element exist. This is different from isElementPresent.
   * This verifies if the element exist on the page while isElementPresent verify if the element is displayed.
   * @param byElement The element to test as By element
   * @return  boolean
   */
  public boolean isElementExist(By byElement) {
    try {
      driver.findElements(byElement);
      return true;
    }
    catch (Exception e){
      return false;
    }
  }

  /**
   * Checks if 'Address Book' navigation arrow is present
   * @return {boolean} whether 'Address Book' navigation arrow is present or not
   */
  public boolean isAddressBookNavigationArrowPresent() {
    log.debug("isAddressBookNavigationArrowPresent");
    return isElementPresent(addressBookNavigationArrowLocator, Time.SHORT);
  }

  /**
   * Click on 'Address Book' navigation arrow
   */
  public void clickAddressBookNavigationArrow() {
    log.debug("clickAddressBookNavigationArrow");
    assert isAddressBookNavigationArrowPresent() : "'Address Book' navigation arrow is NOT present!";
    click(addressBookNavigationArrowLocator, Time.SHORT);
  }

  /**
   * Click on "Address Book" link leads to "Address Book -> Recipient Groups" page
   *
   * @return {AddressBookRecipientGroupsPage}
   */
  public AddressBookRecipientGroupsPage clickAddressBookLink() {
    log.debug("clickAddressBookLink()");
    waitUntilVisible(Time.LONGER, addressBookNavigationArrowLocator);
    addressBookNavigationArrowLocator.click();
    AddressBookRecipientGroupsPage addressBookRecipientGroupsPage = new AddressBookRecipientGroupsPage(driver);
    addressBookRecipientGroupsPage.verifyAddressBookRecipientGroupsPageIsLoaded();
    return addressBookRecipientGroupsPage;
  }

  /**
   * Click on "Recipient Groups" link leads to "Address Book -> Recipient Groups" page
   *
   * @return {AddressBookRecipientGroupsPage}
   */
  public AddressBookRecipientGroupsPage clickRecipientGroupsLink() {
    log.debug("clickRecipientGroupsLink()");
    waitUntilVisible(Time.LONGER, recipientGroupsLinkLocator);
    recipientGroupsLinkLocator.click();
    AddressBookRecipientGroupsPage addressBookRecipientGroupsPage = new AddressBookRecipientGroupsPage(driver);
    addressBookRecipientGroupsPage.verifyAddressBookRecipientGroupsPageIsLoaded();
    return addressBookRecipientGroupsPage;
  }

  /**
   * Check if the given element contains the expected class name
   * @param element    Element to test
   * @param expectedClassName  Expected class name
   * @return boolean True if the expectedClassName is in the class name
   */
  public boolean containClassName(WebElement element, String expectedClassName) {
    String classNames = element.getAttribute("class");
    return classNames.contains(expectedClassName);
  }

  /**
   * Check if the given element is focused or not
   * @param element       Locator of the element
   * @param expectedFocus Expect to have focus or not
   * @return boolean Whether the given element has focus
   */
  public boolean verifyFocusRing(WebElement element, boolean expectedFocus) {
    log.info("hasFocus");
    DateTimeUtils.sleepMilliSecondsAsLastResortBecauseNothingElseWorks(500);
    boolean hasFocusRing = containClassName(element, FOCUS_RING);
    assert hasFocusRing == expectedFocus : "Tab to this element, expected focus is " + expectedFocus + ", but actual is " + hasFocusRing;
    return hasFocusRing;
  }

  /**
   * Select an item from dropdown on any page. It will return null if the item is not in the dropdown
   * This method must be called after the dropdown is opened
   * @param optionsInDropdown   The List of option locator in the dropdown
   * @param optionTextToBeSelected  Text to be selected
   * @return WebElement Locator of the selected item
   */
  public WebElement selectFromDropdown(List<WebElement> optionsInDropdown, String optionTextToBeSelected) {
    for (WebElement option: optionsInDropdown) {
      String optionText = option.getText();
      if (optionText.equals(optionTextToBeSelected)) {
        click(option, Time.TWOSECOND);
        return option;
      }
    }
    return null;
  }

  /**
   * Select from any dropdown box on any page
   * @param dropdownLocator  Locator of the dropdown box
   * @param by               By Value of the dropdown items, example: By.className("spectrue_menu_item")
   * @param itemText         Text of the
   * @return
   */
  public WebElement selectFromDropDown(WebElement dropdownLocator, By by,  String itemText) {
    log.debug("get all items in the dropdown list");
    List<WebElement> dropdowneItemList = dropdownLocator.findElements(by);

    WebElement itemLocator = selectFromDropdown(dropdowneItemList, itemText);
    assert (itemLocator != null) : "selectDateRange couldn't found  " + itemText;
    return itemLocator;
  }

  /**
   * Get element list on any page for the given by
   * @param by   locator of the element
   * @param parentLocator parent locator of the element
   * @return List<WebElement>
   */
  public List<WebElement> getElementList(By by, WebElement parentLocator) {
    if (parentLocator == null) {
      return driver.findElements(by);
    }
    return parentLocator.findElements(by);
  }

  /**
   * Check if a dialog box has "is-open" class
   * @param by  locator for dialog
   * @return boolean true if the dialog is display, otherwise false
   */
  public boolean isDialogOpen(By by) {
    WebElement dialogLocator = driver.findElement(by);
    String className = dialogLocator.getAttribute("class");
    return className.contains("is-open");
  }

  /**
   * Check if error message banner is present on page
   *
   * @param {int} iTimeout - Time Out in Seconds
   * @return {boolean} Whether error message is present or not on page
   */
  public boolean isErrorMessageBannerPresent(int iTimeout) {
    log.debug("isErrorMessageBannerPresent(" + iTimeout + ")");
    return isElementVisible(errorMessageBannerLocator, iTimeout);
  }

  /**
   * Check if error message banner is present on page
   *
   * @return {boolean} Whether error message is present or not on page
   */
  public boolean isErrorMessageBannerPresent() {
    log.debug("isErrorMessageBannerPresent()");
    return isElementVisible(errorMessageBannerLocator, Time.SHORTER);
  }

  /**
   * Verify the error message banner.
   *
   */
  public void verifyErrorMessageBanner(String errorMessage) {
    log.debug("verifyErrorMessageBanner(" + errorMessage + ")");
    assert isErrorMessageBannerPresent() : "Error message not present!";
    assert errorMessageBannerLocator.getText().equals(errorMessage) : "Error message mismatch! "
        + "Expected: " + errorMessage + " Actual: " + errorMessageBannerLocator.getText();
  }

  /**
   * Clear the text box content using JavascriptExecutor
   * @param elementToClear
   */
  public void clearTextField(WebElement elementToClear){
    ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", elementToClear);
  }
}
