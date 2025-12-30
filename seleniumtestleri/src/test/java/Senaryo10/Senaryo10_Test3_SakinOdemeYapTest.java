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

public class Senaryo10_Test3_SakinOdemeYapTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    private static final String EMAIL = Senaryo10_Test1_SakinKayitTest.EMAIL;
    private static final String PHONE = Senaryo10_Test1_SakinKayitTest.PHONE;

    private static final int MONTH = Senaryo10_Test2_AdminAidatOlusturTest.MONTH;
    private static final int YEAR = Senaryo10_Test2_AdminAidatOlusturTest.YEAR;

    @BeforeEach
    void setup() { driver = new ChromeDriver(); }

    @AfterEach
    void teardown() { if (driver != null) driver.quit(); }

    private WebElement inputByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "'] or .//*[normalize-space() = '" + labelText + "']]//input"));
    }

    private WebElement selectByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "'] or .//*[normalize-space() = '" + labelText + "']]//select"));
    }

    private void residentLogin() {
        driver.get(BASE_URL + "/sakin/giris");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Girişi')]")));

        WebElement emailInput = inputByLabel("E-posta");
        emailInput.clear();
        emailInput.sendKeys(EMAIL);

        WebElement phoneInput = inputByLabel("Telefon");
        phoneInput.clear();
        phoneInput.sendKeys(PHONE);
        driver.findElement(By.xpath("//button[normalize-space()='Giriş Yap']")).click();

        // '/sakin/giris' zaten '/sakin' içerdiği için urlContains tuzak; gerçekten panele girmeyi bekle.
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlToBe(BASE_URL + "/sakin"),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Anasayfa']"))
        ));
    }

    @Test
    void sakinOdemeYapabilmeli() {
        residentLogin();

        driver.get(BASE_URL + "/sakin/odeme");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.or(
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Ödeme Yap']")),
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Hoş geldiniz')]"))
        ));

        assertTrue(driver.getCurrentUrl().contains("/sakin/odeme"), "Ödeme Yap sayfasına erişilemedi (login/role kontrolü olabilir)");

        // Aidat seçenekleri async yükleniyor; ilgili dönem gelene kadar bekle.
        wait.until(d -> d.findElements(By.xpath("//label[.//div[normalize-space()='Aidat'] or .//*[normalize-space()='Aidat']]//option[contains(.,'" + MONTH + "/" + YEAR + "')] ")).size() > 0);

        Select duesSelect = new Select(selectByLabel("Aidat"));
        boolean selected = false;
        for (WebElement opt : duesSelect.getOptions()) {
            if (opt.getText() != null && opt.getText().contains(MONTH + "/" + YEAR)) {
                duesSelect.selectByVisibleText(opt.getText());
                selected = true;
                break;
            }
        }
        assertTrue(selected, "Ödeme sayfasında aidat seçeneği bulunamadı");

        WebElement amount = inputByLabel("Tutar");
        amount.clear();
        amount.sendKeys("50");
        driver.findElement(By.xpath("//button[normalize-space()='Ödemeyi Gönder']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Ödeme kaydedildi')]")));
        assertTrue(driver.findElement(By.tagName("body")).getText().toLowerCase().contains("ödeme kaydedildi"));
    }
}
