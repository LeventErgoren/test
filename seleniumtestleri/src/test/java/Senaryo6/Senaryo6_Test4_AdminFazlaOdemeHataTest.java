package Senaryo6;

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

public class Senaryo6_Test4_AdminFazlaOdemeHataTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    @BeforeEach
    void setup() { driver = new ChromeDriver(); }

    @AfterEach
    void teardown() { if (driver != null) driver.quit(); }

    private void adminLogin() {
        driver.get(BASE_URL + "/admin/giris");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='username']"))).sendKeys("admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='current-password']"))).sendKeys("1234");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/admin"));
    }

    private WebElement inputByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//input"));
    }

    private WebElement selectByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//select"));
    }

    private void selectS6Dues(WebDriverWait wait) {
        Select duesSelect = new Select(selectByLabel("Aidat"));
        boolean selected = false;
        for (WebElement opt : duesSelect.getOptions()) {
            if (opt.getText() != null && opt.getText().contains("Kapı " + Senaryo6_Test1_AdminBlokTipDaireOlusturTest.DOOR_NUMBER)
                    && opt.getText().contains(Senaryo6_Test3_AdminAidatOlusturTest.DUES_MONTH + "/" + Senaryo6_Test3_AdminAidatOlusturTest.DUES_YEAR)) {
                duesSelect.selectByVisibleText(opt.getText());
                selected = true;
                break;
            }
        }
        assertTrue(selected, "S6 aidat seçilemedi");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Seçili aidat')]")));
    }

    @Test
    void adminFazlaOdemeYapamamali() {
        adminLogin();
        driver.get(BASE_URL + "/admin/odemeler");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Ödeme Al']")));

        selectS6Dues(wait);

        // 90 ödeme (kalan 10)
        inputByLabel("Tutar").clear();
        inputByLabel("Tutar").sendKeys("90");
        driver.findElement(By.xpath("//button[normalize-space()='Ödemeyi Kaydet']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Ödeme kaydedildi.')]")));

        // Fazla ödeme (20) => hata
        inputByLabel("Tutar").clear();
        inputByLabel("Tutar").sendKeys("20");
        driver.findElement(By.xpath("//button[normalize-space()='Ödemeyi Kaydet']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZİÖÜŞĞÇ','abcdefghijklmnopqrstuvwxyzıöüşğç'), 'fazla ödeme')]")
        ));

        String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
        assertTrue(body.contains("fazla ödeme"), "Fazla ödeme hatası görünmeli");
    }
}
