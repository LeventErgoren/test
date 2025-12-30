package Senaryo9;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Senaryo9_Test1_AdminPersonelOlusturTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    static final String STAFF_NAME = "S9 Personel";
    static final String STAFF_PHONE = "05500000020";

    @BeforeEach
    void setup() { driver = new ChromeDriver(); }

    @AfterEach
    void teardown() { if (driver != null) driver.quit(); }

    private void adminLogin() {
        driver.get(BASE_URL + "/admin/giris");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='username']"))).sendKeys("admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='current-password']"))).sendKeys("1234");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/admin"));
    }

    private org.openqa.selenium.WebElement inputByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//input"));
    }

    @Test
    void adminPersonelOlusturabilmeli() {
        adminLogin();
        driver.get(BASE_URL + "/admin/personel");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Personel']")));

        inputByLabel("Ad Soyad").sendKeys(STAFF_NAME);
        inputByLabel("Görev").sendKeys("Güvenlik");
        inputByLabel("Telefon").sendKeys(STAFF_PHONE);
        inputByLabel("Maaş").sendKeys("25000");

        driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + STAFF_NAME + "')]")));
        assertTrue(driver.findElement(By.tagName("body")).getText().contains(STAFF_NAME));
    }
}
