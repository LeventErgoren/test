package Senaryo6;

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

public class Senaryo6FlowTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    private static final String BLOCK_NAME = "S6 Blok";
    private static final String TYPE_NAME = "S6 Tip";
    private static final String DOOR_NUMBER = "606";

    private static final String RES_EMAIL = "s6_payment@mail.com";
    private static final String RES_PHONE = "05500000017";

    private static final String DUES_MONTH = "1";
    private static final String DUES_YEAR = "2027";
    private static final String DUES_AMOUNT = "100";

    @Test
    void senaryo6_bastanSona() {
        step1_adminBlokTipDaireOlustur();
        step2_adminSakiniDaireyeAta();
        step3_adminAidatOlustur();
        step4_adminFazlaOdemeHata();
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

    private void step1_adminBlokTipDaireOlustur() {
        withDriver(() -> {
            adminLogin();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            driver.get(BASE_URL + "/admin/bloklar");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Bloklar']")));
            inputByLabel("Blok adı").sendKeys(BLOCK_NAME);
            inputByLabel("Toplam kat").sendKeys("5");
            driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + BLOCK_NAME + "')]")));

            driver.get(BASE_URL + "/admin/daire-tipleri");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Daire Tipleri']")));
            inputByLabel("Tip adı").sendKeys(TYPE_NAME);
            inputByLabel("Varsayılan aidat").sendKeys("100");
            driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + TYPE_NAME + "')]")));

            driver.get(BASE_URL + "/admin/daireler");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Daireler']")));
            inputByLabel("Kapı No").sendKeys(DOOR_NUMBER);
            inputByLabel("Kat").sendKeys("2");
            new Select(selectByLabel("Durum")).selectByVisibleText("Boş");
            new Select(selectByLabel("Blok")).selectByVisibleText(BLOCK_NAME);
            new Select(selectByLabel("Daire tipi")).selectByVisibleText(TYPE_NAME);
            driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + DOOR_NUMBER + "')]")));
            assertTrue(driver.findElement(By.tagName("body")).getText().contains(DOOR_NUMBER));
        });
    }

    private void step2_adminSakiniDaireyeAta() {
        withDriver(() -> {
            adminLogin();
            driver.get(BASE_URL + "/admin/sakinler");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Sakinler']")));

            inputByLabel("Ad").sendKeys("S6");
            inputByLabel("Soyad").sendKeys("Payer");
            inputByLabel("Telefon").sendKeys(RES_PHONE);
            inputByLabel("E-posta").sendKeys(RES_EMAIL);

            new Select(selectByLabel("Tür")).selectByVisibleText("Kiracı");

            Select flatSelect = new Select(selectByLabel("Daire"));
            boolean selected = false;
            for (WebElement opt : flatSelect.getOptions()) {
                if (opt.getText() != null && opt.getText().contains("Kapı " + DOOR_NUMBER)) {
                    flatSelect.selectByVisibleText(opt.getText());
                    selected = true;
                    break;
                }
            }
            assertTrue(selected, "S6 oluşturulan daire seçilemedi");

            driver.findElement(By.xpath("//button[normalize-space()='Ekle']")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + RES_EMAIL + "')]")));
            assertTrue(driver.findElement(By.tagName("body")).getText().contains(RES_EMAIL));
        });
    }

    private void step3_adminAidatOlustur() {
        withDriver(() -> {
            adminLogin();
            driver.get(BASE_URL + "/admin/aidatlar");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Aidatlar']")));

            Select flatSelect = new Select(selectByLabel("Daire"));
            boolean selected = false;
            for (WebElement opt : flatSelect.getOptions()) {
                if (opt.getText() != null && opt.getText().contains("Kapı " + DOOR_NUMBER)) {
                    flatSelect.selectByVisibleText(opt.getText());
                    selected = true;
                    break;
                }
            }
            assertTrue(selected, "S6 daire seçilemedi");

            inputByLabel("Ay").sendKeys(DUES_MONTH);
            inputByLabel("Yıl").clear();
            inputByLabel("Yıl").sendKeys(DUES_YEAR);
            inputByLabel("Tutar").sendKeys(DUES_AMOUNT);

            driver.findElement(By.xpath("//button[normalize-space()='Oluştur']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + DUES_MONTH + "/" + DUES_YEAR + "')]")));
            assertTrue(driver.findElement(By.tagName("body")).getText().contains(DUES_MONTH + "/" + DUES_YEAR));
        });
    }

    private void step4_adminFazlaOdemeHata() {
        withDriver(() -> {
            adminLogin();
            driver.get(BASE_URL + "/admin/odemeler");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Ödeme Al']")));

            Select duesSelect = new Select(selectByLabel("Aidat"));
            boolean selected = false;
            for (WebElement opt : duesSelect.getOptions()) {
                if (opt.getText() != null && opt.getText().contains("Kapı " + DOOR_NUMBER)
                        && opt.getText().contains(DUES_MONTH + "/" + DUES_YEAR)) {
                    duesSelect.selectByVisibleText(opt.getText());
                    selected = true;
                    break;
                }
            }
            assertTrue(selected, "S6 aidat seçilemedi");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Seçili aidat')]")));

            inputByLabel("Tutar").clear();
            inputByLabel("Tutar").sendKeys("90");
            driver.findElement(By.xpath("//button[normalize-space()='Ödemeyi Kaydet']")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Ödeme kaydedildi.')]")));

            inputByLabel("Tutar").clear();
            inputByLabel("Tutar").sendKeys("20");
            driver.findElement(By.xpath("//button[normalize-space()='Ödemeyi Kaydet']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZİÖÜŞĞÇ','abcdefghijklmnopqrstuvwxyzıöüşğç'), 'fazla ödeme')]")));

            String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
            assertTrue(body.contains("fazla ödeme"), "Fazla ödeme hatası görünmeli");
        });
    }
}
