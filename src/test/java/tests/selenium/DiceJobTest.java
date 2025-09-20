package tests.selenium;

import base.SeleniumBaseTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class DiceJobTest extends SeleniumBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(DiceJobTest.class);

    //@Test
    void diceJobApplyTest() throws InterruptedException {
        logger.info("Starting test: DiceJobApplyTest : Selenium");
        try {

            String url = "https://www.dice.com/dashboard/login";
            driver.get(url);
            logger.info("Opened Url: {}", url);

            WebElement emailEle = driver.findElement(By.xpath("//input[@type='email']"));
            emailEle.sendKeys(("tirupathaiah.salla2@gmail.com"));
            logger.info("Email entered in textbox");

            driver.findElement(By.xpath("//button[@data-testid='sign-in-button']")).click();
            logger.info("SignIn button clicked");

            Thread.sleep(500);
            WebElement passwordEle = driver.findElement(By.xpath("//input[@type='password']"));
            passwordEle.sendKeys("Thiru@123");
            logger.info("Password entered in textbox");

            driver.findElement(By.xpath("//button[@data-testid='submit-password']")).click();
            logger.info("Submitted Email and Password..");

            String jobSearchText = "Spring-boot";
            WebElement jobSearchBox = driver.findElement(By.xpath("//input[@name='q']"));
            jobSearchBox.sendKeys(jobSearchText);
            jobSearchBox.sendKeys(Keys.ENTER);
            logger.info("Entered text:[{}] in Job Search box", jobSearchText);

            /**
            String location = "United States";
            driver.findElement(By.xpath("//input[@name='location']")).sendKeys(location);
            logger.info("Entered text:[{}] in Location Search box", location);
            **/

            // apply filters
            applyFilters();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void applyFilters() {
        logger.info("Applying following Filters");
        List<WebElement> btnEles =  driver.findElements(By.xpath("//button[type='button']"));
        Optional<WebElement> matchedBtnEle = btnEles.stream().filter(we -> StringUtils.equalsIgnoreCase(we.getText(), "All filters")).findFirst();
        if(matchedBtnEle.isPresent()) {
            matchedBtnEle.get().click();
            List<WebElement> labelEls = driver.findElements(By.xpath("//label"));
            Optional<WebElement> easyApplyEle = labelEls.stream().filter(we -> StringUtils.equalsIgnoreCase(we.getText(), "Easy apply")).findFirst();
            easyApplyEle.ifPresent(WebElement::click);

            Optional<WebElement> postedDateEle = labelEls.stream().filter(we -> StringUtils.equalsIgnoreCase(we.getText(), "Today")).findFirst();
            postedDateEle.ifPresent(WebElement::click);

        }

    }

}