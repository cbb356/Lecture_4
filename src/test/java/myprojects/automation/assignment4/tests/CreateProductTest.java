package myprojects.automation.assignment4.tests;

import myprojects.automation.assignment4.BaseTest;
import myprojects.automation.assignment4.model.ProductData;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static myprojects.automation.assignment4.utils.logging.CustomReporter.log;

public class CreateProductTest extends BaseTest {

    private ProductData productData = ProductData.generate();

    @DataProvider(name = "product")
    public Object[][] product() {
        return new Object[][] {
                {productData}};
    }

    @DataProvider(name = "authentication")
    public Object[][] authentication() {
        return new Object[][] {
                {"webinar.test@gmail.com", "Xcg7299bnSmMuRLp9ITw"}};
    }

    @Test(dataProvider = "authentication")
    public void login(String login, String password){
        actions.login(login, password);
        log("Logged successfull");
    }

    @Test(dependsOnMethods = "login", dataProvider = "product")
    public void createNewProduct(ProductData product) {
        actions.createProduct(product);
        log(String.format("%s created", product.getName()));
    }

    @Test(dependsOnMethods = "createNewProduct", dataProvider = "product")
    public void checkNewProduct(ProductData product) {
        actions.checkProduct(product);
        log(String.format("%s verified", product.getName()));
    }
}
