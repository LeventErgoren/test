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

public class AracTalepTest3_SakinTalepOlusturTest {

    private WebDriver driver;

    private static final String BASE_URL = "http://localhost:1313";

    private static final String RESIDENT_EMAIL = AracTalepTest1_SakinKayitOlusturTest.RESIDENT_EMAIL;
    private static final String RESIDENT_PHONE = AracTalepTest1_SakinKayitOlusturTest.RESIDENT_PHONE;

    static final String TICKET_TITLE = "Selenium Ticket - AracTalep";
    static final String TICKET_DESC = "Bu bir Selenium test talebidir.";

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

    private WebElement textareaByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//textarea"));
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
    void sakinTalepOlusturabilmeli() {
        residentLogin();

        driver.get(BASE_URL + "/sakin/taleplerim");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Taleplerim']")));

        inputByLabel("Başlık").sendKeys(TICKET_TITLE);
        textareaByLabel("Açıklama").sendKeys(TICKET_DESC);

        driver.findElement(By.xpath("//button[normalize-space()='Talep Oluştur']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Talep oluşturuldu.')]")));

        String body = driver.findElement(By.tagName("body")).getText();
        assertTrue(body.contains(TICKET_TITLE), "Oluşturulan talep listede görünmeli");
        assertTrue(body.contains("Açık") || body.contains("Kapalı"), "Talep durum rozeti görünmeli");
    }
}
