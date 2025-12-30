package Senaryo3;

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

public class Senaryo3_Test1_AdminSakinOlusturTest {

    private WebDriver driver;

    private static final String BASE_URL = "http://localhost:1313";

    static final String RESIDENT_FIRST_NAME = "S3";
    static final String RESIDENT_LAST_NAME = "TicketLimit";
    static final String RESIDENT_EMAIL = "s3_ticketlimit@mail.com";
    static final String RESIDENT_PHONE = "05500000013";

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
    void adminSakinOlusturabilmeli() {
        adminLogin();

        driver.get(BASE_URL + "/admin/sakinler");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Sakinler']")));

        inputByLabel("Ad").sendKeys(RESIDENT_FIRST_NAME);
        inputByLabel("Soyad").sendKeys(RESIDENT_LAST_NAME);
        inputByLabel("Telefon").sendKeys(RESIDENT_PHONE);
        inputByLabel("E-posta").sendKeys(RESIDENT_EMAIL);

        Select typeSelect = new Select(selectByLabel("Tür"));
        typeSelect.selectByVisibleText("Kiracı");

        driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + RESIDENT_EMAIL + "')]")));
        assertTrue(driver.findElement(By.tagName("body")).getText().contains(RESIDENT_EMAIL), "Yeni sakin listede görünmeli");
    }
}
