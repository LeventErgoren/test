package Senaryo1;

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

public class Senaryo1FlowTest {

    private WebDriver driver;

    private static final String BASE_URL = "http://localhost:1313";

    private static final String RESIDENT_FIRST_NAME = "Selenium";
    private static final String RESIDENT_LAST_NAME = "User";
    private static final String RESIDENT_EMAIL = "selenium1@mail.com";
    private static final String RESIDENT_PHONE = "05500000001";

    private static final String TARGET_FLAT_TEXT_CONTAINS = "Kapı 2";

    private static final String DUES_MONTH = "1";
    private static final String DUES_YEAR = "2026";
    private static final String DUES_AMOUNT = "100";

    @Test
    void senaryo1_bastanSona() {
        step1_sakinKayitOlustur();
        step2_adminAidatOlustur();
        step3_sakinAidatGoruntule();
        step4_sakinAidatOde();
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

    private WebElement selectByLabel(String labelText) {
        return driver.findElement(By.xpath("//label[.//div[normalize-space() = '" + labelText + "']]//select"));
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

    private void residentLogin() {
        driver.get(BASE_URL + "/sakin/giris");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement emailInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='email']"))
        );
        WebElement phoneInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='tel']"))
        );

        emailInput.sendKeys(RESIDENT_EMAIL);
        phoneInput.sendKeys(RESIDENT_PHONE);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/sakin"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Sakin Paneli')]")));
    }

    private void step1_sakinKayitOlustur() {
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

    private void step2_adminAidatOlustur() {
        withDriver(() -> {
            adminLogin();

            driver.findElement(By.linkText("Aidatlar")).click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.urlContains("/admin/aidatlar"));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(),'Aidatlar')]")));

            Select flatSelect = new Select(selectByLabel("Daire"));
            boolean selected = false;
            for (WebElement opt : flatSelect.getOptions()) {
                if (opt.getText() != null && opt.getText().contains(TARGET_FLAT_TEXT_CONTAINS)) {
                    flatSelect.selectByVisibleText(opt.getText());
                    selected = true;
                    break;
                }
            }
            assertTrue(selected, "Aidat ekranında '" + TARGET_FLAT_TEXT_CONTAINS + "' bulunan bir daire seçilemedi.");

            WebElement monthInput = inputByLabel("Ay");
            WebElement yearInput = inputByLabel("Yıl");
            WebElement amountInput = inputByLabel("Tutar");

            monthInput.clear();
            monthInput.sendKeys(DUES_MONTH);
            yearInput.clear();
            yearInput.sendKeys(DUES_YEAR);
            amountInput.clear();
            amountInput.sendKeys(DUES_AMOUNT);

            driver.findElement(By.cssSelector("button[type='submit']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + DUES_MONTH + "/" + DUES_YEAR + "')]")));
            String body = driver.findElement(By.tagName("body")).getText();
            assertTrue(body.contains(DUES_MONTH + "/" + DUES_YEAR), "Oluşturulan aidat dönemi listede görünmeli");
        });
    }

    private void step3_sakinAidatGoruntule() {
        withDriver(() -> {
            residentLogin();

            driver.findElement(By.linkText("Aidatlarım")).click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.urlContains("/sakin/aidatlarim"));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(),'Aidatlarım')]")));

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + DUES_MONTH + "/" + DUES_YEAR + "')]")));
            String body = driver.findElement(By.tagName("body")).getText();
            assertTrue(body.contains(DUES_MONTH + "/" + DUES_YEAR), "Aidat dönemi sakin ekranında görünmeli");
        });
    }

    private void step4_sakinAidatOde() {
        withDriver(() -> {
            residentLogin();

            driver.findElement(By.linkText("Ödeme Yap")).click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.urlContains("/sakin/odeme"));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(),'Ödeme Yap')]")));

            Select duesSelect = new Select(selectByLabel("Aidat"));
            boolean selected = false;
            for (WebElement opt : duesSelect.getOptions()) {
                if (opt.getText() != null && opt.getText().contains(DUES_MONTH + "/" + DUES_YEAR)) {
                    duesSelect.selectByVisibleText(opt.getText());
                    selected = true;
                    break;
                }
            }
            assertTrue(selected, "Ödeme ekranında " + DUES_MONTH + "/" + DUES_YEAR + " aidatı seçilemedi.");

            WebElement amountInput = inputByLabel("Tutar");
            amountInput.clear();
            amountInput.sendKeys(DUES_AMOUNT);

            driver.findElement(By.xpath("//button[normalize-space()='Ödemeyi Gönder']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Ödeme kaydedildi.')]")));

            driver.findElement(By.linkText("Aidatlarım")).click();
            wait.until(ExpectedConditions.urlContains("/sakin/aidatlarim"));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Ödenmiş Aidatlar')]")));

            String body = driver.findElement(By.tagName("body")).getText();
            assertTrue(body.contains(DUES_MONTH + "/" + DUES_YEAR), "Ödenen aidat döneminin listede görünmesi beklenir");
        });
    }
}
