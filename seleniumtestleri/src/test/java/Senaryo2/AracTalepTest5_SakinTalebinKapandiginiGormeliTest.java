package Senaryo2;

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

public class AracTalepTest5_SakinTalebinKapandiginiGormeliTest {

    private WebDriver driver;

    private static final String BASE_URL = "http://localhost:1313";

    private static final String RESIDENT_EMAIL = AracTalepTest1_SakinKayitOlusturTest.RESIDENT_EMAIL;
    private static final String RESIDENT_PHONE = AracTalepTest1_SakinKayitOlusturTest.RESIDENT_PHONE;

    private static final String TICKET_TITLE = AracTalepTest3_SakinTalepOlusturTest.TICKET_TITLE;

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

        inputByLabel("E-posta").sendKeys(RESIDENT_EMAIL);
        inputByLabel("Telefon").sendKeys(RESIDENT_PHONE);

        driver.findElement(By.xpath("//button[normalize-space()='Giriş Yap']")).click();

        wait.until(ExpectedConditions.urlContains("/sakin"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Paneli')]")));
    }

    @Test
    void sakinTalebinKapandiginiGormeli() {
        residentLogin();

        driver.get(BASE_URL + "/sakin/taleplerim");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Taleplerim']")));

        driver.findElement(By.xpath("//button[normalize-space()='Yenile']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tr[.//*[contains(normalize-space(.), '" + TICKET_TITLE + "')]]//*[normalize-space()='Kapalı']")
        ));

        String body = driver.findElement(By.tagName("body")).getText();
        assertTrue(body.contains(TICKET_TITLE), "Talep listede görünmeli");
        assertTrue(body.contains("Kapalı"), "Talep Kapalı olarak görünmeli");
    }
}
