package Senaryo3;

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

public class Senaryo3_Test2_SakinIkiTalepOlusturTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    private static final String RESIDENT_EMAIL = Senaryo3_Test1_AdminSakinOlusturTest.RESIDENT_EMAIL;
    private static final String RESIDENT_PHONE = Senaryo3_Test1_AdminSakinOlusturTest.RESIDENT_PHONE;

    static final String TITLE_1 = "S3 Ticket 1";
    static final String TITLE_2 = "S3 Ticket 2";

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

    private void createTicket(String title) {
        inputByLabel("Başlık").clear();
        inputByLabel("Başlık").sendKeys(title);
        textareaByLabel("Açıklama").clear();
        textareaByLabel("Açıklama").sendKeys("S3 Selenium test açıklaması");
        driver.findElement(By.xpath("//button[normalize-space()='Talep Oluştur']")).click();
    }

    @Test
    void sakinIkiTalepOlusturabilmeli() {
        residentLogin();

        driver.get(BASE_URL + "/sakin/taleplerim");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Taleplerim']")));

        createTicket(TITLE_1);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Talep oluşturuldu.')]")));

        createTicket(TITLE_2);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Talep oluşturuldu.')]")));

        String body = driver.findElement(By.tagName("body")).getText();
        assertTrue(body.contains(TITLE_1), "1. talep listede görünmeli");
        assertTrue(body.contains(TITLE_2), "2. talep listede görünmeli");
    }
}
