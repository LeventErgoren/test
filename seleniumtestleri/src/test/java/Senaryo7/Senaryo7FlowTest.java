package Senaryo7;

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

public class Senaryo7FlowTest {

    private WebDriver driver;

    private static final String BASE_URL = "http://localhost:1313";

    private static final String OWNER1_FIRST = "S7";
    private static final String OWNER1_LAST = "Owner";
    private static final String OWNER1_EMAIL = "s7_owner1@mail.com";
    private static final String OWNER1_PHONE = "05500000018";

    private static final String OWNER2_FIRST = "S7";
    private static final String OWNER2_LAST = "Owner2";
    private static final String OWNER2_EMAIL = "s7_owner2@mail.com";
    private static final String OWNER2_PHONE = "05500000019";

    private static final String TARGET_FLAT_TEXT_CONTAINS = "Kapı 2";

    @Test
    void senaryo7_bastanSona() {
        step1_evSahibiKayitOlustur();
        step2_ikinciEvSahibiHata();
        step3_evSahibiProfil();
        step4_sakinAdminSayfasinaGirememeli();
    }

    private void withDriver(Runnable step) {
        driver = new ChromeDriver();
        try {
            step.run();
        } finally {
            try {
                if (driver != null) driver.quit();
            } finally {
                driver = null;
            }
        }
    }

    private WebElement inputByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//input"));
    }

    private WebElement selectByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//select"));
    }

    private void residentLogin(String email, String phone) {
        driver.get(BASE_URL + "/sakin/giris");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Girişi')]")));

        inputByLabel("E-posta").sendKeys(email);
        inputByLabel("Telefon").sendKeys(phone);
        driver.findElement(By.xpath("//button[normalize-space()='Giriş Yap']")).click();

        wait.until(ExpectedConditions.urlContains("/sakin"));
    }

    private void step1_evSahibiKayitOlustur() {
        withDriver(() -> {
            driver.get(BASE_URL + "/sakin/kayit");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Kaydı')]")));

            inputByLabel("Ad").sendKeys(OWNER1_FIRST);
            inputByLabel("Soyad").sendKeys(OWNER1_LAST);
            inputByLabel("E-posta").sendKeys(OWNER1_EMAIL);
            inputByLabel("Telefon").sendKeys(OWNER1_PHONE);

            new Select(selectByLabel("Tür")).selectByVisibleText("Ev sahibi");

            Select select = new Select(selectByLabel("Daire"));
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

            assertTrue(driver.getCurrentUrl().contains("/sakin"));
        });
    }

    private void step2_ikinciEvSahibiHata() {
        withDriver(() -> {
            driver.get(BASE_URL + "/sakin/kayit");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Kaydı')]")));

            inputByLabel("Ad").sendKeys(OWNER2_FIRST);
            inputByLabel("Soyad").sendKeys(OWNER2_LAST);
            inputByLabel("E-posta").sendKeys(OWNER2_EMAIL);
            inputByLabel("Telefon").sendKeys(OWNER2_PHONE);

            new Select(selectByLabel("Tür")).selectByVisibleText("Ev sahibi");

            Select flat = new Select(selectByLabel("Daire"));
            boolean selected = false;
            for (WebElement opt : flat.getOptions()) {
                if (opt.getText() != null && opt.getText().contains(TARGET_FLAT_TEXT_CONTAINS)) {
                    flat.selectByVisibleText(opt.getText());
                    selected = true;
                    break;
                }
            }
            assertTrue(selected);

            driver.findElement(By.xpath("//button[normalize-space()='Kayıt Ol']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZİÖÜŞĞÇ','abcdefghijklmnopqrstuvwxyzıöüşğç'), 'owner') or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZİÖÜŞĞÇ','abcdefghijklmnopqrstuvwxyzıöüşğç'), 'yasal sahibi')]")));

            String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
            assertTrue(body.contains("owner") || body.contains("yasal sahibi"), "Tek ev sahibi kuralı hatası görünmeli");
        });
    }

    private void step3_evSahibiProfil() {
        withDriver(() -> {
            residentLogin(OWNER1_EMAIL, OWNER1_PHONE);

            driver.get(BASE_URL + "/sakin/profil");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Profil']")));

            String body = driver.findElement(By.tagName("body")).getText();
            assertTrue(body.contains("Ev sahibi"), "Profilde Ev sahibi görünmeli");
            assertTrue(body.contains(OWNER1_EMAIL), "Profilde e-posta görünmeli");
        });
    }

    private void step4_sakinAdminSayfasinaGirememeli() {
        withDriver(() -> {
            residentLogin(OWNER1_EMAIL, OWNER1_PHONE);

            driver.get(BASE_URL + "/admin");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlToBe(BASE_URL + "/"),
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Hoş geldiniz')]"))
            ));

            assertTrue(driver.getCurrentUrl().equals(BASE_URL + "/") || driver.getCurrentUrl().endsWith("/"), "Admin paneline gidince ana sayfaya yönlenmeli");
            assertTrue(driver.findElement(By.tagName("body")).getText().contains("Hoş geldiniz"), "Ana sayfa içeriği görünmeli");
        });
    }
}
