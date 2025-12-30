package Senaryo7;

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

public class Senaryo7_Test4_SakinAdminSayfasinaGirememeliTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    private static final String EMAIL = Senaryo7_Test1_EvSahibiKayitOlusturTest.OWNER1_EMAIL;
    private static final String PHONE = Senaryo7_Test1_EvSahibiKayitOlusturTest.OWNER1_PHONE;

    @BeforeEach
    void setup() { driver = new ChromeDriver(); }

    @AfterEach
    void teardown() { if (driver != null) driver.quit(); }

    private WebElement inputByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//input"));
    }

    private void residentLogin() {
        driver.get(BASE_URL + "/sakin/giris");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Girişi')]")));

        inputByLabel("E-posta").sendKeys(EMAIL);
        inputByLabel("Telefon").sendKeys(PHONE);
        driver.findElement(By.xpath("//button[normalize-space()='Giriş Yap']")).click();

        wait.until(ExpectedConditions.urlContains("/sakin"));
    }

    @Test
    void sakinAdminPanelineGidinceAnaSayfayaYonlenmeli() {
        residentLogin();

        driver.get(BASE_URL + "/admin");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // RequireRole, admin olmayan kullanıcıyı '/' sayfasına yönlendirir.
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlToBe(BASE_URL + "/"),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Hoş geldiniz')]"))
        ));

        assertTrue(driver.getCurrentUrl().equals(BASE_URL + "/") || driver.getCurrentUrl().endsWith("/"), "Admin paneline gidince ana sayfaya yönlenmeli");
        assertTrue(driver.findElement(By.tagName("body")).getText().contains("Hoş geldiniz"), "Ana sayfa içeriği görünmeli");
    }
}
