package myprojects.automation.assignment4;


import com.google.common.base.Predicate;
import myprojects.automation.assignment4.model.ProductData;
import myprojects.automation.assignment4.utils.Properties;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;

/**
 * Contains main script actions that may be used in scripts.
 */
public class GeneralActions {
    private WebDriver driver;
    private WebDriverWait wait;

    //Login page variables
    private By emailInput = By.id("email");
    private By passwordInput = By.id("passwd");
    private By loginButton = By.name("submitLogin");

    //Dashboard page variables
    private By loadingPage = By.xpath("//*[@id='ajax_running' or @id='ajax_confirmation' and @style='display: none;']");
    private By catalogueTab = By.id("subtab-AdminCatalog");
    private By productsSubTab = By.id("subtab-AdminProducts");

    //Products subtub variables
    private By newProductBtn = By.id("page-header-desc-configuration-add");
    private By productNameInput = By.id("form_step1_name_1");
    private By productAmountInput = By.id("form_step1_qty_0_shortcut");
    private By productPriceInput = By.id("form_step1_price_ttc_shortcut");
    private By productActivateBtn = By.className("switch-input");
    private By popupConfirmationMsg = By.className("growl-close");
    private By productSaveBtn = By.cssSelector("button.js-btn-save");

    //Shop main page variables
    private By allGoodsBtn = By.className("all-product-link");

    //Goods pages variables
    private By allGoodsPage = By.className("block-category");
    private String productName = "//a[contains(text(), '%s')]";
    private By lastPageBtn = By.xpath("//a[contains (@class, 'next')]/../preceding-sibling::*[1]/a");

    //Product page variables
    private String productPageTitle = "//span[contains(text(), '%s')]";
    private By productNameOnProductPage = By.xpath("//h1[@itemprop='name']");
    private By productPriceOnProductPage = By.xpath("//span[@itemprop='price']");
    private By productAmountOnProductPage = By.cssSelector(".product-quantities>span");


    public GeneralActions(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, 30);
    }

    /**
     * Logs in to Admin Panel.
     * @param login
     * @param password
     */
    public void login(String login, String password) {
        driver.get(Properties.getBaseAdminUrl());
        driver.findElement(emailInput).sendKeys(login);
        driver.findElement(passwordInput).sendKeys(password);
        driver.findElement(loginButton).click();
        waitForContentLoad();
    }

    public void createProduct(ProductData newProduct) {
        //Open Products Tab
        wait.until(ExpectedConditions.presenceOfElementLocated(catalogueTab));
        new Actions(driver).moveToElement(driver.findElement(catalogueTab)).build().perform();
        wait.until(ExpectedConditions.elementToBeClickable(productsSubTab));
        driver.findElement(productsSubTab).click();
        waitForContentLoad();

        //Open New Product Page
        driver.findElement(newProductBtn).click();
        waitForContentLoad();

        //Create new Product
        driver.findElement(productNameInput).sendKeys(newProduct.getName());
        driver.findElement(productAmountInput).sendKeys(Keys.BACK_SPACE);
        driver.findElement(productAmountInput).sendKeys(newProduct.getQty().toString());
        driver.findElement(productPriceInput).sendKeys(Keys.BACK_SPACE);
        driver.findElement(productPriceInput).sendKeys(newProduct.getPrice());
        driver.findElement(productActivateBtn).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(popupConfirmationMsg));
        if (driver.findElement(popupConfirmationMsg).isDisplayed()) driver.findElement(popupConfirmationMsg).click();
        driver.findElement(productSaveBtn).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(popupConfirmationMsg));
        if (driver.findElement(popupConfirmationMsg).isDisplayed()) driver.findElement(popupConfirmationMsg).click();
    }

    public void checkProduct(ProductData product) {
        //Open shop main Page
        driver.get(Properties.getBaseUrl());
        driver.findElement(allGoodsBtn).click();

        //Open last All Goods Page
        wait.until(ExpectedConditions.presenceOfElementLocated(allGoodsPage));
        if (Integer.parseInt(driver.findElement(lastPageBtn).getText()) > 1){
            driver.findElement(lastPageBtn).click();
            wait.until(ExpectedConditions.presenceOfElementLocated(allGoodsPage));
        }

        //Check New Product Page
        By selector = By.xpath(String.format(productName, product.getName()));
        WebElement productOnPage = driver.findElement(selector);
        Assert.assertTrue(productOnPage.isDisplayed(), "New Product is not presented on the page");

        //Open New Product Page
        productOnPage.click();
        selector = By.xpath(String.format(productPageTitle, product.getName()));
        wait.until(ExpectedConditions.presenceOfElementLocated(selector));

        //Check New Product Name
        Assert.assertEquals(driver.findElement(productNameOnProductPage).getText(), product.getName().toUpperCase(),
                "New Product Name is not correct");

        //Check New Product Price
        String productPrice = driver.findElement(productPriceOnProductPage).getText();
        int s = productPrice.length();
        productPrice = productPrice.substring(0, s-2);
        Assert.assertEquals(productPrice, product.getPrice(), "New Product Price is not correct");

        //Check New Product Amount
        String productAmount = driver.findElement(productAmountOnProductPage).getText();
        s = productAmount.length();
        if (s < 8) {
            productAmount = productAmount.substring(0, 1);
        } else {
            productAmount = productAmount.substring(0, s - 7);
        }
        Assert.assertEquals(productAmount, product.getQty().toString(), "New Product Amount is not correct");

    }


    /**
     * Waits until page loader disappears from the page
     */
    public void waitForContentLoad() {
        Predicate<WebDriver> driverPredicate = new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        };
        wait.until(driverPredicate);

        wait.until(ExpectedConditions.presenceOfElementLocated(loadingPage));
    }
}
