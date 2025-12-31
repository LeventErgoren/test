package Senaryo3;

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

public class Senaryo3FlowTest {

    private WebDriver driver;

    private static final String BASE_URL = "http://localhost:1313";

    private static final String RESIDENT_FIRST_NAME = "S3";
    private static final String RESIDENT_LAST_NAME = "TicketLimit";
    private static final String RESIDENT_EMAIL = "s3_ticketlimit@mail.com";
    private static final String RESIDENT_PHONE = "05500000013";

    private static final String TITLE_1 = "S3 Ticket 1";
    private static final String TITLE_2 = "S3 Ticket 2";
    private static final String TITLE_3 = "S3 Ticket 3";
    private static final String TITLE_4 = "S3 Ticket 4";

    @Test
    void senaryo3_bastanSona() {
        step1_adminSakinOlustur();
        step2_sakinIkiTalepOlustur();
        step3_sakinUcuncuTalepOlustur();
        step4_sakinDorduncuTalepteHata();
    }

    private void withDriver(Runnable step) {
        driver = new ChromeDriver();
        try {
            step.run();
        } finally {
            try {
                if (driver != null) {
                    driver.quit();
                }
            } finally {
                driver = null;
            }
        }
    }

    private WebElement inputByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//input"));
    }

    private WebElement textareaByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//textarea"));
    }

    private WebElement selectByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//select"));
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

    private void step1_adminSakinOlustur() {
        withDriver(() -> {
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
        });
    }

    private void createTicket(String title) {
        inputByLabel("Başlık").clear();
        inputByLabel("Başlık").sendKeys(title);
        textareaByLabel("Açıklama").clear();
        textareaByLabel("Açıklama").sendKeys("S3 Selenium test açıklaması");
        driver.findElement(By.xpath("//button[normalize-space()='Talep Oluştur']")).click();
    }

    private void step2_sakinIkiTalepOlustur() {
        withDriver(() -> {
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
        });
    }

    private void step3_sakinUcuncuTalepOlustur() {
        withDriver(() -> {
            residentLogin();

            driver.get(BASE_URL + "/sakin/taleplerim");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Taleplerim']")));

            inputByLabel("Başlık").sendKeys(TITLE_3);
            textareaByLabel("Açıklama").sendKeys("S3 üçüncü talep");
            driver.findElement(By.xpath("//button[normalize-space()='Talep Oluştur']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Talep oluşturuldu.')]")));

            String body = driver.findElement(By.tagName("body")).getText();
            assertTrue(body.contains(TITLE_3), "3. talep listede görünmeli");
        });
    }

    private void step4_sakinDorduncuTalepteHata() {
        withDriver(() -> {
            residentLogin();

            driver.get(BASE_URL + "/sakin/taleplerim");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Taleplerim']")));

            inputByLabel("Başlık").sendKeys(TITLE_4);
            textareaByLabel("Açıklama").sendKeys("S3 dördüncü talep (limit testi)");
            driver.findElement(By.xpath("//button[normalize-space()='Talep Oluştur']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZİÖÜŞĞÇ','abcdefghijklmnopqrstuvwxyzıöüşğç'), 'çok fazla açık taleb')]")));

            String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
            assertTrue(body.contains("çok fazla açık taleb"), "4. talepte limit hatası görünmeli");
        });
    }
}
