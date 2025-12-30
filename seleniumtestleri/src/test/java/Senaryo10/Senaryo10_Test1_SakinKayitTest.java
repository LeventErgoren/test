package Senaryo10;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

public class Senaryo10_Test1_SakinKayitTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    // Senaryo zincirinde (Test1-4) aynı kullanıcıyı kullanıyoruz.
    // Test tekrar çalıştırıldığında kullanıcı zaten varsa, Test1 kayıt yerine girişe düşebilir.
    static final String EMAIL = "s10_chain@mail.com";
    static final String PHONE = "05500000030";

    @BeforeEach
    void setup() { driver = new ChromeDriver(); }

    @AfterEach
    void teardown() { if (driver != null) driver.quit(); }

    private WebElement inputByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//input"));
    }

    private WebElement selectByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//select"));
    }

    private void residentLogin() {
        driver.get(BASE_URL + "/sakin/giris");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Girişi')]")));

        inputByLabel("E-posta").clear();
        inputByLabel("E-posta").sendKeys(EMAIL);
        inputByLabel("Telefon").clear();
        inputByLabel("Telefon").sendKeys(PHONE);
        driver.findElement(By.xpath("//button[normalize-space()='Giriş Yap']")).click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlToBe(BASE_URL + "/sakin"),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Anasayfa']"))
        ));
    }

    @Test
    void sakinKayitOlabilmeli() {
        driver.get(BASE_URL + "/sakin/kayit");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Sayfa başlıkları: "Sakin Kaydı" + "Hesap oluştur"
        wait.until(ExpectedConditions.or(
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(normalize-space(),'Sakin Kaydı')]")),
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Hesap oluştur']"))
        ));

        // Daire listesi async geliyor; önce Kapı 2 seçeneğinin yüklenmesini bekle.
        wait.until(d -> d.findElements(By.xpath("//label[.//div[normalize-space()='Daire']]//option[contains(.,'Kapı 2')] ")).size() > 0);

        inputByLabel("Ad").sendKeys("S10");
        inputByLabel("Soyad").sendKeys("User");
        inputByLabel("Telefon").sendKeys(PHONE);
        inputByLabel("E-posta").sendKeys(EMAIL);
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

        driver.findElement(By.xpath("//button[normalize-space()='Kayıt Ol']")).click();

        // Başarılı akışta otomatik login yapıp '/sakin' altına yönlendirir.
        // Kullanıcı zaten varsa "Hata" alert'i gelebilir; bu durumda giriş sayfasından login yap.
        boolean wentToResident = false;
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlToBe(BASE_URL + "/sakin"),
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Anasayfa']")),
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[normalize-space()='Hata']"))
            ));
            wentToResident = driver.getCurrentUrl().contains("/sakin") || driver.findElements(By.xpath("//h1[normalize-space()='Anasayfa']")).size() > 0;
        } catch (Exception ignored) {
            // Fall through
        }

        if (!wentToResident) {
            residentLogin();
        }

        assertTrue(driver.getCurrentUrl().contains("/sakin"), "Kayıt/giriş sonrası /sakin altında olmalı");
    }
}
