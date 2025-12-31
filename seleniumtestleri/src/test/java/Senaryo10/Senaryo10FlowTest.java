package Senaryo10;

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

public class Senaryo10FlowTest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:1313";

    private static final String EMAIL = "s10_chain@mail.com";
    private static final String PHONE = "05500000030";

    private static final int MONTH = 2;
    private static final int YEAR = 2028;
    private static final String AMOUNT = "120";

    @Test
    void senaryo10_bastanSona() {
        step1_sakinKayitVeyaGiris();
        step2_adminAidatOlustur();
        step3_sakinOdemeYap();
        step4_sakinAidatlariGor();
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

    private WebElement inputByLabelFlexible(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "'] or .//*[normalize-space() = '" + labelText + "']]//input"));
    }

    private WebElement selectByLabelFlexible(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "'] or .//*[normalize-space() = '" + labelText + "']]//select"));
    }

    private void residentLogin() {
        driver.get(BASE_URL + "/sakin/giris");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Girişi')]")));

        WebElement emailInput = inputByLabelFlexible("E-posta");
        emailInput.clear();
        emailInput.sendKeys(EMAIL);

        WebElement phoneInput = inputByLabelFlexible("Telefon");
        phoneInput.clear();
        phoneInput.sendKeys(PHONE);

        driver.findElement(By.xpath("//button[normalize-space()='Giriş Yap']")).click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlToBe(BASE_URL + "/sakin"),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Anasayfa']"))
        ));
    }

    private void adminLogin() {
        driver.get(BASE_URL + "/admin/giris");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='username']"))).sendKeys("admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='current-password']"))).sendKeys("1234");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/admin"));
    }

    private void step1_sakinKayitVeyaGiris() {
        withDriver(() -> {
            driver.get(BASE_URL + "/sakin/kayit");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(normalize-space(),'Sakin Kaydı')]")),
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Hesap oluştur']"))
            ));

            wait.until(d -> d.findElements(By.xpath("//label[.//div[normalize-space()='Daire']]//option[contains(.,'Kapı 2')] ")).size() > 0);

            inputByLabelFlexible("Ad").sendKeys("S10");
            inputByLabelFlexible("Soyad").sendKeys("User");
            inputByLabelFlexible("Telefon").sendKeys(PHONE);
            inputByLabelFlexible("E-posta").sendKeys(EMAIL);
            new Select(selectByLabelFlexible("Tür")).selectByVisibleText("Kiracı");

            Select flatSelect = new Select(selectByLabelFlexible("Daire"));
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

            boolean wentToResident = false;
            try {
                wait.until(ExpectedConditions.or(
                        ExpectedConditions.urlToBe(BASE_URL + "/sakin"),
                        ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Anasayfa']")),
                        ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[normalize-space()='Hata']"))
                ));
                wentToResident = driver.getCurrentUrl().contains("/sakin") || driver.findElements(By.xpath("//h1[normalize-space()='Anasayfa']")).size() > 0;
            } catch (Exception ignored) {
            }

            if (!wentToResident) {
                residentLogin();
            }

            assertTrue(driver.getCurrentUrl().contains("/sakin"), "Kayıt/giriş sonrası /sakin altında olmalı");
        });
    }

    private void step2_adminAidatOlustur() {
        withDriver(() -> {
            adminLogin();
            driver.get(BASE_URL + "/admin/aidatlar");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Aidatlar']")));

            WebElement monthInput = inputByLabelFlexible("Ay");
            monthInput.clear();
            monthInput.sendKeys(String.valueOf(MONTH));

            WebElement yearInput = inputByLabelFlexible("Yıl");
            yearInput.clear();
            yearInput.sendKeys(String.valueOf(YEAR));

            WebElement amountInput = inputByLabelFlexible("Tutar");
            amountInput.clear();
            amountInput.sendKeys(AMOUNT);

            wait.until(d -> d.findElements(By.xpath("//label[.//div[normalize-space()='Daire'] or .//*[normalize-space()='Daire']]//option[contains(.,'Kapı 2')] ")).size() > 0);
            Select flatSelect = new Select(selectByLabelFlexible("Daire"));
            boolean selected = false;
            for (WebElement opt : flatSelect.getOptions()) {
                if (opt.getText() != null && opt.getText().contains("Kapı 2")) {
                    flatSelect.selectByVisibleText(opt.getText());
                    selected = true;
                    break;
                }
            }
            assertTrue(selected, "Kapı 2 daire seçilemedi");

            driver.findElement(By.xpath("//button[normalize-space()='Oluştur']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '" + MONTH + "/" + YEAR + "')]")));
            assertTrue(driver.findElement(By.tagName("body")).getText().contains(MONTH + "/" + YEAR));
        });
    }

    private void step3_sakinOdemeYap() {
        withDriver(() -> {
            residentLogin();

            driver.get(BASE_URL + "/sakin/odeme");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Ödeme Yap']")),
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Hoş geldiniz')]"))
            ));

            assertTrue(driver.getCurrentUrl().contains("/sakin/odeme"), "Ödeme Yap sayfasına erişilemedi (login/role kontrolü olabilir)");

            wait.until(d -> d.findElements(By.xpath("//label[.//div[normalize-space()='Aidat'] or .//*[normalize-space()='Aidat']]//option[contains(.,'" + MONTH + "/" + YEAR + "')] ")).size() > 0);

            Select duesSelect = new Select(selectByLabelFlexible("Aidat"));
            boolean selected = false;
            for (WebElement opt : duesSelect.getOptions()) {
                if (opt.getText() != null && opt.getText().contains(MONTH + "/" + YEAR)) {
                    duesSelect.selectByVisibleText(opt.getText());
                    selected = true;
                    break;
                }
            }
            assertTrue(selected, "Ödeme sayfasında aidat seçeneği bulunamadı");

            WebElement amount = inputByLabelFlexible("Tutar");
            amount.clear();
            amount.sendKeys("50");
            driver.findElement(By.xpath("//button[normalize-space()='Ödemeyi Gönder']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Ödeme kaydedildi')]")));
            assertTrue(driver.findElement(By.tagName("body")).getText().toLowerCase().contains("ödeme kaydedildi"));
        });
    }

    private void step4_sakinAidatlariGor() {
        withDriver(() -> {
            residentLogin();

            driver.get(BASE_URL + "/sakin/aidatlarim");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='Aidatlarım']")),
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Hoş geldiniz')]"))
            ));

            assertTrue(driver.getCurrentUrl().contains("/sakin/aidatlarim"), "Aidatlarım sayfasına erişilemedi (login/role kontrolü olabilir)");

            String body = driver.findElement(By.tagName("body")).getText();
            assertTrue(body.contains("Ödenmemiş Aidatlar"));
            assertTrue(body.contains(MONTH + "/" + YEAR), "Aidatlarım sayfasında dönem görünmeli");
        });
    }
}
