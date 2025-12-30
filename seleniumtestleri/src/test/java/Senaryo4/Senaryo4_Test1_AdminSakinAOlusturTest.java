package Senaryo4;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Senaryo4_Test1_AdminSakinAOlusturTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    static final String RESIDENT_A_FIRST = "S4A";
    static final String RESIDENT_A_LAST = "PlateUnique";
    static final String RESIDENT_A_EMAIL = "s4a_plate@mail.com";
    static final String RESIDENT_A_PHONE = "05500000014";

    @BeforeEach
    void setup() {
        driver = new ChromeDriver();
    }

    @AfterEach
    void teardown() {
        if (driver != null) driver.quit();
    }

    private void adminLogin() {
        driver.get(BASE_URL + "/admin/giris");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='username']")));
        WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='current-password']")));

        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("1234");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/admin"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Yönetim Paneli')]")));
    }

    private WebElement inputByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//input"));
    }

    private WebElement selectByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//select"));
    }

    @Test
    void adminSakinAOlusturabilmeli() {
        adminLogin();
        driver.get(BASE_URL + "/admin/sakinler");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Sakinler']")));

        inputByLabel("Ad").sendKeys(RESIDENT_A_FIRST);
        inputByLabel("Soyad").sendKeys(RESIDENT_A_LAST);
        inputByLabel("Telefon").sendKeys(RESIDENT_A_PHONE);
        inputByLabel("E-posta").sendKeys(RESIDENT_A_EMAIL);

        new Select(selectByLabel("Tür")).selectByVisibleText("Kiracı");
        driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + RESIDENT_A_EMAIL + "')]")));
        assertTrue(driver.findElement(By.tagName("body")).getText().contains(RESIDENT_A_EMAIL));
    }
}
