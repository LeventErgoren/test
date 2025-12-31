package Senaryo8;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Senaryo8FlowTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    private static final String BLOCK_NAME = "S8 Blok";
    private static final String DOOR_NUMBER = "808";

    @Test
    void senaryo8_bastanSona() {
        step1_adminBlokOlustur();
        step2_ayniBlokAdindaHata();
        step3_blokaDaireEkle();
        step4_doluBlokSilinememeli();
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

    private void step1_adminBlokOlustur() {
        withDriver(() -> {
            adminLogin();
            driver.get(BASE_URL + "/admin/bloklar");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Bloklar']")));

            inputByLabel("Blok adı").sendKeys(BLOCK_NAME);
            inputByLabel("Toplam kat").sendKeys("4");
            driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + BLOCK_NAME + "')]")));
            assertTrue(driver.findElement(By.tagName("body")).getText().contains(BLOCK_NAME));
        });
    }

    private void step2_ayniBlokAdindaHata() {
        withDriver(() -> {
            adminLogin();
            driver.get(BASE_URL + "/admin/bloklar");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Bloklar']")));

            inputByLabel("Blok adı").sendKeys(BLOCK_NAME);
            inputByLabel("Toplam kat").sendKeys("2");
            driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZİÖÜŞĞÇ','abcdefghijklmnopqrstuvwxyzıöüşğç'), 'blok') and contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZİÖÜŞĞÇ','abcdefghijklmnopqrstuvwxyzıöüşğç'), 'zaten')]")));

            String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
            assertTrue(body.contains("blok") && body.contains("zaten"));
        });
    }

    private void step3_blokaDaireEkle() {
        withDriver(() -> {
            adminLogin();
            driver.get(BASE_URL + "/admin/daireler");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Daireler']")));

            inputByLabel("Kapı No").sendKeys(DOOR_NUMBER);
            inputByLabel("Kat").sendKeys("1");
            new Select(selectByLabel("Durum")).selectByVisibleText("Boş");

            new Select(selectByLabel("Blok")).selectByVisibleText(BLOCK_NAME);

            Select typeSelect = new Select(selectByLabel("Daire tipi"));
            if (!typeSelect.getOptions().isEmpty()) {
                typeSelect.selectByIndex(0);
            }

            driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + DOOR_NUMBER + "')]")));
            assertTrue(driver.findElement(By.tagName("body")).getText().contains(DOOR_NUMBER));
        });
    }

    private void step4_doluBlokSilinememeli() {
        withDriver(() -> {
            adminLogin();
            driver.get(BASE_URL + "/admin/bloklar");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Bloklar']")));

            driver.findElement(By.xpath("//tr[.//*[contains(normalize-space(.), '" + BLOCK_NAME + "')]]//button[normalize-space()='Sil']")).click();

            Alert confirm = wait.until(ExpectedConditions.alertIsPresent());
            confirm.accept();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZİÖÜŞĞÇ','abcdefghijklmnopqrstuvwxyzıöüşğç'), 'silinemez')]")));

            String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
            assertTrue(body.contains("silinemez"), "Dolu blok silinemez hatası görünmeli");
        });
    }
}
