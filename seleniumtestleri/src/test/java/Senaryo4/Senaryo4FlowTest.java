package Senaryo4;

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

public class Senaryo4FlowTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    private static final String RESIDENT_A_FIRST = "S4A";
    private static final String RESIDENT_A_LAST = "PlateUnique";
    private static final String RESIDENT_A_EMAIL = "s4a_plate@mail.com";
    private static final String RESIDENT_A_PHONE = "05500000014";

    private static final String RESIDENT_B_FIRST = "S4B";
    private static final String RESIDENT_B_LAST = "PlateUnique";
    private static final String RESIDENT_B_EMAIL = "s4b_plate@mail.com";
    private static final String RESIDENT_B_PHONE = "05500000015";

    private static final String PLATE = "34S4PLT01";

    @Test
    void senaryo4_bastanSona() {
        step1_adminSakinAOlustur();
        step2_sakinAAracEkle();
        step3_adminSakinBOlustur();
        step4_sakinBAyniPlakaHata();
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

    private void residentLogin(String email, String phone) {
        driver.get(BASE_URL + "/sakin/giris");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Girişi')]")));

        inputByLabel("E-posta").sendKeys(email);
        inputByLabel("Telefon").sendKeys(phone);
        driver.findElement(By.xpath("//button[normalize-space()='Giriş Yap']")).click();

        wait.until(ExpectedConditions.urlContains("/sakin"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Paneli')]")));
    }

    private void step1_adminSakinAOlustur() {
        withDriver(() -> {
            adminLogin();
            driver.get(BASE_URL + "/admin/sakinler");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Sakinler']")));

            inputByLabel("Ad").sendKeys(RESIDENT_A_FIRST);
            inputByLabel("Soyad").sendKeys(RESIDENT_A_LAST);
            inputByLabel("Telefon").sendKeys(RESIDENT_A_PHONE);
            inputByLabel("E-posta").sendKeys(RESIDENT_A_EMAIL);

            new Select(selectByLabel("Tür")).selectByVisibleText("Kiracı");
            driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + RESIDENT_A_EMAIL + "')]")));
            assertTrue(driver.findElement(By.tagName("body")).getText().contains(RESIDENT_A_EMAIL));
        });
    }

    private void step2_sakinAAracEkle() {
        withDriver(() -> {
            residentLogin(RESIDENT_A_EMAIL, RESIDENT_A_PHONE);

            driver.get(BASE_URL + "/sakin/araclarim");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Araçlarım']")));

            inputByLabel("Plaka").sendKeys(PLATE);
            inputByLabel("Marka").sendKeys("Selenium");
            inputByLabel("Model").sendKeys("S4");
            driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Araç eklendi.')]")));
            assertTrue(driver.findElement(By.tagName("body")).getText().contains(PLATE));
        });
    }

    private void step3_adminSakinBOlustur() {
        withDriver(() -> {
            adminLogin();
            driver.get(BASE_URL + "/admin/sakinler");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Sakinler']")));

            inputByLabel("Ad").sendKeys(RESIDENT_B_FIRST);
            inputByLabel("Soyad").sendKeys(RESIDENT_B_LAST);
            inputByLabel("Telefon").sendKeys(RESIDENT_B_PHONE);
            inputByLabel("E-posta").sendKeys(RESIDENT_B_EMAIL);

            new Select(selectByLabel("Tür")).selectByVisibleText("Kiracı");
            driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + RESIDENT_B_EMAIL + "')]")));
            assertTrue(driver.findElement(By.tagName("body")).getText().contains(RESIDENT_B_EMAIL));
        });
    }

    private void step4_sakinBAyniPlakaHata() {
        withDriver(() -> {
            residentLogin(RESIDENT_B_EMAIL, RESIDENT_B_PHONE);

            driver.get(BASE_URL + "/sakin/araclarim");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Araçlarım']")));

            inputByLabel("Plaka").sendKeys(PLATE);
            inputByLabel("Marka").sendKeys("Selenium");
            inputByLabel("Model").sendKeys("S4-ERR");
            driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZİÖÜŞĞÇ','abcdefghijklmnopqrstuvwxyzıöüşğç'), 'plaka')]")));

            String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
            assertTrue(body.contains("plaka"), "Plaka hatası görünmeli");
            assertTrue(body.contains("zaten"), "Plaka zaten kayıtlı mesajı görünmeli");
        });
    }
}
