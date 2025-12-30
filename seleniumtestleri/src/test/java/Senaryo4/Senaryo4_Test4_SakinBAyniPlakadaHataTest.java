package Senaryo4;

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

public class Senaryo4_Test4_SakinBAyniPlakadaHataTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    private static final String EMAIL = Senaryo4_Test3_AdminSakinBOlusturTest.RESIDENT_B_EMAIL;
    private static final String PHONE = Senaryo4_Test3_AdminSakinBOlusturTest.RESIDENT_B_PHONE;

    private static final String PLATE = Senaryo4_Test2_SakinAAracEkleTest.PLATE;

    @BeforeEach
    void setup() {
        driver = new ChromeDriver();
    }

    @AfterEach
    void teardown() {
        if (driver != null) driver.quit();
    }

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
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Paneli')]")));
    }

    @Test
    void sakinBAyniPlakaIleAracEkleyememeli() {
        residentLogin();

        driver.get(BASE_URL + "/sakin/araclarim");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Araçlarım']")));

        inputByLabel("Plaka").sendKeys(PLATE);
        inputByLabel("Marka").sendKeys("Selenium");
        inputByLabel("Model").sendKeys("S4-ERR");
        driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZİÖÜŞĞÇ','abcdefghijklmnopqrstuvwxyzıöüşğç'), 'plaka')]")
        ));

        String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
        assertTrue(body.contains("plaka"), "Plaka hatası görünmeli");
        assertTrue(body.contains("zaten"), "Plaka zaten kayıtlı mesajı görünmeli");
    }
}
