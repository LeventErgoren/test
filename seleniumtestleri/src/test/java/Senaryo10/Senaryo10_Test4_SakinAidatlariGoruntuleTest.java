package Senaryo10;

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

public class Senaryo10_Test4_SakinAidatlariGoruntuleTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    private static final String EMAIL = Senaryo10_Test1_SakinKayitTest.EMAIL;
    private static final String PHONE = Senaryo10_Test1_SakinKayitTest.PHONE;

    private static final int MONTH = Senaryo10_Test2_AdminAidatOlusturTest.MONTH;
    private static final int YEAR = Senaryo10_Test2_AdminAidatOlusturTest.YEAR;

    @BeforeEach
    void setup() { driver = new ChromeDriver(); }

    @AfterEach
    void teardown() { if (driver != null) driver.quit(); }

    private org.openqa.selenium.WebElement inputByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "'] or .//*[normalize-space() = '" + labelText + "']]//input"));
    }

    private void residentLogin() {
        driver.get(BASE_URL + "/sakin/giris");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Girişi')]")));

        org.openqa.selenium.WebElement emailInput = inputByLabel("E-posta");
        emailInput.clear();
        emailInput.sendKeys(EMAIL);

        org.openqa.selenium.WebElement phoneInput = inputByLabel("Telefon");
        phoneInput.clear();
        phoneInput.sendKeys(PHONE);
        driver.findElement(By.xpath("//button[normalize-space()='Giriş Yap']")).click();

        // '/sakin/giris' zaten '/sakin' içerdiği için urlContains tuzak; gerçekten panele girmeyi bekle.
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlToBe(BASE_URL + "/sakin"),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Anasayfa']"))
        ));
    }

    @Test
    void sakinAidatlariniGorebilmeli() {
        residentLogin();

        driver.get(BASE_URL + "/sakin/aidatlarim");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Aidatlarım']")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Hoş geldiniz')]"))
        ));

        assertTrue(driver.getCurrentUrl().contains("/sakin/aidatlarim"), "Aidatlarım sayfasına erişilemedi (login/role kontrolü olabilir)");

        String body = driver.findElement(By.tagName("body")).getText();
        assertTrue(body.contains("Ödenmemiş Aidatlar"));
        assertTrue(body.contains(MONTH + "/" + YEAR), "Aidatlarım sayfasında dönem görünmeli");
    }
}
