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

public class Senaryo9_Test4_SakinProfilGoruntuleTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    private static final String EMAIL = Senaryo9_Test3_AdminSakinOlusturTest.RES_EMAIL;
    private static final String PHONE = Senaryo9_Test3_AdminSakinOlusturTest.RES_PHONE;

    @BeforeEach
    void setup() { driver = new ChromeDriver(); }

    @AfterEach
    void teardown() { if (driver != null) driver.quit(); }

    private org.openqa.selenium.WebElement inputByLabel(String labelText) {
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
    void sakinProfiliniGoruntuleyebilmeli() {
        residentLogin();

        driver.get(BASE_URL + "/sakin/profil");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Profil']")));

        String body = driver.findElement(By.tagName("body")).getText();
        assertTrue(body.contains(EMAIL), "Profilde e-posta görünmeli");
        assertTrue(body.contains("Kiracı") || body.contains("Ev sahibi"), "Profilde tür görünmeli");
    }
}
