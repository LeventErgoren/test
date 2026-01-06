package BagimsizTestler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdminLoginBasariliTest {

    private WebDriver driver;

    private static final String BASE_URL = "http://localhost:1313";
    private static final String ADMIN_LOGIN_URL = BASE_URL + "/admin/giris";

    @BeforeEach
    void setup() {
        driver = new ChromeDriver();
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void adminLoginDogruBilgilerleBasariliOlmali() {
        driver.get(ADMIN_LOGIN_URL);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement usernameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='username']"))
        );
        WebElement passwordInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='current-password']"))
        );

        usernameInput.clear();
        usernameInput.sendKeys("admin");

        passwordInput.clear();
        passwordInput.sendKeys("1234");

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        wait.until(ExpectedConditions.urlContains("/admin"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Yönetim Paneli')]")));

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/admin"), "Login sonrası /admin sayfasına yönlenmeli. Current URL: " + currentUrl);
    }
}
