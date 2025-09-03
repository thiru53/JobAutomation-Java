package tests;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import runners.BrowserStackRunner;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class JobRightTest extends BrowserStackRunner {

    private static final Logger logger = LoggerFactory.getLogger(JobRightTest.class);

    @Test
    void jobRightTest() throws InterruptedException {
        logger.info("Starting test: JobRightTest");
        try{

            page.navigate("https://jobright.ai");

            // click on sign in button
            page.locator("#firstpage").getByText("SIGN IN").click();
            Locator formLocator = page.locator("form[id='basic']");
            formLocator.locator("input[id='basic_email']").fill("tirupathaiah.salla2@gmail.com");
            formLocator.locator("input[id='basic_password']").fill("Thiru@123");
            formLocator.locator("button").getByText("SIGN IN").click();

            Thread.sleep(4000);
            Locator sideNavBar = page.locator("div.ant-layout-sider-children");
            Assertions.assertTrue(sideNavBar.isVisible(), "Login Failed");

            Thread.sleep(10000);
            // close dialog if exists
            Locator dialogCloseBtn = page.locator("button.ant-modal-close");
            if(dialogCloseBtn.isVisible()) {
                dialogCloseBtn.click();
            }

            List<Locator> jobItems = page.locator("div[class*='index_job-card_']").all();
            for(Locator jobItem : jobItems) {

                Thread.sleep(5000);

                Locator applyBtn2 = jobItem.locator("button").getByText("APPLY NOW");
                if (applyBtn2.isVisible()) {
                    applyBtn2.click();
                }
                Thread.sleep(5000);

                Locator btn2 = page.locator("button").getByText("Fix My Resume Now");
                if (btn2.isVisible()) {
                    btn2.click();
                }
                Thread.sleep(5000);
                Locator generateResume = page.locator("button").getByText("Generate My New Resume");
                if(generateResume.isVisible()) {
                    generateResume.click();
                }
                Thread.sleep(5000);
                Locator l = page.locator("div[class*='index_tailor-resume-loading-linear-progress_']");
                while (l.isVisible()) {
                    TimeUnit.MINUTES.sleep(1);
                }
                Thread.sleep(5000);
                Locator applyBtn = page.locator("button").getByText("Apply Now");
                if(applyBtn.isVisible()) {
                    applyBtn.click();
                }

            }

            System.out.println("Email and Password entered");
            Thread.sleep(10000);


        } finally {

        }
    }
}
