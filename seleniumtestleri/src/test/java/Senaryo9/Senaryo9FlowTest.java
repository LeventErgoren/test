package Senaryo9;

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

public class Senaryo9FlowTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    private static final String STAFF_NAME = "S9 Personel";
    private static final String STAFF_PHONE = "05500000020";

    private static final String EXP_DESC = "S9 Selenium gider";

    private static final String RES_EMAIL = "s9_resident@mail.com";
    private static final String RES_PHONE = "05500000021";

    @Test
    void senaryo9_bastanSona() {
        step1_adminPersonelOlustur();
        step2_adminGiderOlustur();
        step3_adminSakinOlustur();
        step4_sakinProfilGor();
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
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='username']"))).sendKeys("admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='current-password']"))).sendKeys("1234");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/admin"));
    }

    private void residentLogin() {
        driver.get(BASE_URL + "/sakin/giris");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Girişi')]")));

        inputByLabel("E-posta").sendKeys(RES_EMAIL);
        inputByLabel("Telefon").sendKeys(RES_PHONE);
        driver.findElement(By.xpath("//button[normalize-space()='Giriş Yap']")).click();

        wait.until(ExpectedConditions.urlContains("/sakin"));
    }

    private void step1_adminPersonelOlustur() {
        withDriver(() -> {
            adminLogin();
            driver.get(BASE_URL + "/admin/personel");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Personel']")));

            inputByLabel("Ad Soyad").sendKeys(STAFF_NAME);
            inputByLabel("Görev").sendKeys("Güvenlik");
            inputByLabel("Telefon").sendKeys(STAFF_PHONE);
            inputByLabel("Maaş").sendKeys("25000");

            driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + STAFF_NAME + "')]")));
            assertTrue(driver.findElement(By.tagName("body")).getText().contains(STAFF_NAME));
        });
    }

    private void step2_adminGiderOlustur() {
        withDriver(() -> {
            adminLogin();
            driver.get(BASE_URL + "/admin/giderler");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Giderler']")));

            inputByLabel("Açıklama").sendKeys(EXP_DESC);
            inputByLabel("Kategori").sendKeys("Elektrik");
            inputByLabel("Tutar").sendKeys("999.99");

            driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + EXP_DESC + "')]")));
            assertTrue(driver.findElement(By.tagName("body")).getText().contains(EXP_DESC));
        });
    }

    private void step3_adminSakinOlustur() {
        withDriver(() -> {
            adminLogin();
            driver.get(BASE_URL + "/admin/sakinler");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Sakinler']")));

            inputByLabel("Ad").sendKeys("S9");
            inputByLabel("Soyad").sendKeys("Resident");
            inputByLabel("Telefon").sendKeys(RES_PHONE);
            inputByLabel("E-posta").sendKeys(RES_EMAIL);
            new Select(selectByLabel("Tür")).selectByVisibleText("Kiracı");

            Select flatSelect = new Select(selectByLabel("Daire"));
            boolean selected = false;
            for (WebElement opt : flatSelect.getOptions()) {
                if (opt.getText() != null && opt.getText().contains("Kapı 2")) {
                    flatSelect.selectByVisibleText(opt.getText());
                    selected = true;
                    break;
                }
            }
            assertTrue(selected, "Kapı 2 daire seçilemedi");

            driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + RES_EMAIL + "')]")));
            assertTrue(driver.findElement(By.tagName("body")).getText().contains(RES_EMAIL));
        });
    }

    private void step4_sakinProfilGor() {
        withDriver(() -> {
            residentLogin();

            driver.get(BASE_URL + "/sakin/profil");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Profil']")));

            String body = driver.findElement(By.tagName("body")).getText();
            assertTrue(body.contains(RES_EMAIL), "Profilde e-posta görünmeli");
            assertTrue(body.contains("Kiracı") || body.contains("Ev sahibi"), "Profilde tür görünmeli");
        });
    }
}
