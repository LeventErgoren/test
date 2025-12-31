package Senaryo2;

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

public class Senaryo2FlowTest {

    private WebDriver driver;

    private static final String BASE_URL = "http://localhost:1313";

    private static final String RESIDENT_FIRST_NAME = "Selenium";
    private static final String RESIDENT_LAST_NAME = "AracTalep";
    private static final String RESIDENT_EMAIL = "selenium_arac_talep@mail.com";
    private static final String RESIDENT_PHONE = "05500000009";

    private static final String TARGET_FLAT_TEXT_CONTAINS = "Kapı 2";

    private static final String PLATE_1 = "34SEL001";
    private static final String PLATE_2 = "34SEL002";
    private static final String PLATE_3 = "34SEL003";

    private static final String EXPECTED_LIMIT_ERROR_CONTAINS = "en fazla 2 araç";

    private static final String TICKET_TITLE = "Selenium Ticket - AracTalep";
    private static final String TICKET_DESC = "Bu bir Selenium test talebidir.";

    @Test
    void senaryo2_bastanSona() {
        step1_sakinKayit();
        step2_ikiAracEkle_ucuncondeHata();
        step3_sakinTalepOlustur();
        step4_adminTalebiKapat();
        step5_sakinTalebinKapandiginiGor();
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

    private void adminLogin() {
        driver.get(BASE_URL + "/admin/giris");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement usernameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='username']"))
        );
        WebElement passwordInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='current-password']"))
        );

        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("1234");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/admin"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Yönetim Paneli')]")));
    }

    private void addVehicle(String plate, String brand, String model) {
        inputByLabel("Plaka").clear();
        inputByLabel("Plaka").sendKeys(plate);
        inputByLabel("Marka").clear();
        inputByLabel("Marka").sendKeys(brand);
        inputByLabel("Model").clear();
        inputByLabel("Model").sendKeys(model);

        driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();
    }

    private void step1_sakinKayit() {
        withDriver(() -> {
            driver.get(BASE_URL + "/sakin/kayit");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Kaydı')]")));

            inputByLabel("Ad").sendKeys(RESIDENT_FIRST_NAME);
            inputByLabel("Soyad").sendKeys(RESIDENT_LAST_NAME);
            inputByLabel("E-posta").sendKeys(RESIDENT_EMAIL);
            inputByLabel("Telefon").sendKeys(RESIDENT_PHONE);

            WebElement flatSelect = selectByLabel("Daire");
            Select select = new Select(flatSelect);
            boolean selected = false;
            for (WebElement opt : select.getOptions()) {
                if (opt.getText() != null && opt.getText().contains(TARGET_FLAT_TEXT_CONTAINS)) {
                    select.selectByVisibleText(opt.getText());
                    selected = true;
                    break;
                }
            }
            assertTrue(selected, "Kayıt ekranında '" + TARGET_FLAT_TEXT_CONTAINS + "' bulunan bir daire seçilemedi.");

            driver.findElement(By.xpath("//button[normalize-space()='Kayıt Ol']")).click();

            wait.until(ExpectedConditions.urlContains("/sakin"));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Paneli')]")));

            assertTrue(driver.getCurrentUrl().contains("/sakin"), "Kayıt sonrası /sakin'e yönlenmeli");
        });
    }

    private void step2_ikiAracEkle_ucuncondeHata() {
        withDriver(() -> {
            residentLogin();

            driver.get(BASE_URL + "/sakin/araclarim");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Araçlarım']")));

            addVehicle(PLATE_1, "Selenium", "Car1");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Araç eklendi.')]")));

            addVehicle(PLATE_2, "Selenium", "Car2");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Araç eklendi.')]")));

            String bodyAfter2 = driver.findElement(By.tagName("body")).getText();
            assertTrue(bodyAfter2.contains(PLATE_1), "1. araç listede görünmeli");
            assertTrue(bodyAfter2.contains(PLATE_2), "2. araç listede görünmeli");

            addVehicle(PLATE_3, "Selenium", "Car3");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '" + EXPECTED_LIMIT_ERROR_CONTAINS + "')]")));

            String bodyAfter3 = driver.findElement(By.tagName("body")).getText().toLowerCase();
            assertTrue(bodyAfter3.contains(EXPECTED_LIMIT_ERROR_CONTAINS), "3. araç eklemede limit hatası görünmeli");
        });
    }

    private void step3_sakinTalepOlustur() {
        withDriver(() -> {
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
        });
    }

    private void step4_adminTalebiKapat() {
        withDriver(() -> {
            adminLogin();

            driver.get(BASE_URL + "/admin/talepler");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Talepler']")));

            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//tr[.//*[contains(normalize-space(.), '" + TICKET_TITLE + "')]]//button[normalize-space()='Kapat']")
            ));
            closeButton.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//tr[.//*[contains(normalize-space(.), '" + TICKET_TITLE + "')]]//*[normalize-space()='Kapalı']")
            ));

            String body = driver.findElement(By.tagName("body")).getText();
            assertTrue(body.contains(TICKET_TITLE), "Kapatılan talep listede görünmeli");
            assertTrue(body.contains("Kapalı"), "Talep durumu Kapalı olmalı");
        });
    }

    private void step5_sakinTalebinKapandiginiGor() {
        withDriver(() -> {
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
        });
    }
}
