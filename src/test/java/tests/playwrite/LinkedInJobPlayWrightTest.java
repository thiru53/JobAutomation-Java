package tests.playwrite;

import base.PlayWriteBaseTest;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.*;
import java.util.regex.Pattern;

public class LinkedInJobPlayWrightTest extends PlayWriteBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(LinkedInJobPlayWrightTest.class);

    @Test
    void linkedEasyJobTest() throws InterruptedException {
        logger.info("Starting test: linkedJobTest");
        try {

            formLogin();

            // Navigate to Global Navigation item
            clickOnGlobalNavItemByName("Jobs");
            logger.info("Navigate to Global Navigation item [Jobs]");

            // Enter JobSearch Keyword
            String keyword = "Spring-boot";
            enterJobSearchText(keyword);
            logger.info("Entered JobSearch keyword : [{}]", keyword);
            //page.locator("input[placeholder='City, state, or zip code']");

            // filter JobSearch Criteria
            Map<String, List<String>> filter1 = getFilter1();
            List<Map<String, List<String>>> filterList = List.of(filter1);
            filterList.forEach(filter -> {
                try {
                    applyJobSearchFilter(filter);
                    Thread.sleep(5000);
                    // 5. Verify Job Listings;
                    //page.waitForSelector(".job-card-container", new Page.WaitForSelectorOptions().setTimeout(10000));
                    List<Locator> jobCards = page.locator("li.scaffold-layout__list-item").all();
                    applyToJobs(jobCards);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            logger.info("End of the TesCase");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void formLogin() {

        // 1. Login to LinkedIn
        page.navigate("https://www.linkedin.com/login");
        page.fill("#username", "thirupathaiah.salla@gmail.com");
        logger.info("Email entered");

        page.fill("#password", "Thiru@linkedin1");
        logger.info("Password entered");

        page.click("button[type='submit']");
        logger.info("Clicked On submit button");

        // 2. Verify Login
        ElementHandle globalNav = page.waitForSelector("nav.global-nav__nav");
        Assert.assertTrue(globalNav.isVisible(), "LoggedIn Failed");
        logger.info("Successfully LoggedIn");
    }

    private void enterJobSearchText(String keyword) {
        Locator jobSearchBox = page.locator("input[componentkey='jobSearchBox']");
        jobSearchBox.fill(keyword);
        jobSearchBox.press("Enter");
        logger.info("Entered {} in job search box", keyword);
        //TimeUnit.SECONDS.sleep(30);

    }

    private String getJobTitle(Locator job) {
        return job.locator(".artdeco-entity-lockup__title").locator("span[aria-hidden='true']").innerText();
        //return job.locator(".artdeco-entity-lockup__title, .job-card-list__title--link, .job-card-job-posting-card-wrapper__title span[aria-hidden='true']").innerText();
    }

    private void applyJobSearchFilter(Map<String, List<String>> filters) throws InterruptedException {

        page.waitForSelector("div#search-reusables__filters-bar");
        List<Locator> liSearchHdr = page.locator("ul li.search-reusables__primary-filter").all();

        filters.forEach((key, values) -> {
            try {
                Optional<Locator> optionalHdrLoc = liSearchHdr.stream().filter(e -> StringUtils.equalsIgnoreCase(e.innerText(), key)).findFirst();
                if (optionalHdrLoc.isPresent()) {
                    Locator hdrLoc = optionalHdrLoc.get();
                    hdrLoc.click();

                    Locator dropDownContainer = hdrLoc.locator("fieldset.reusable-search-filters-trigger-dropdown__container");
                    if (Objects.nonNull(dropDownContainer) && dropDownContainer.isVisible()) {
                        if (CollectionUtils.isNotEmpty(values)) {
                            values.stream().filter(StringUtils::isNoneBlank).forEach(val -> {
                                val = StringUtils.contains(val, "$") ? StringUtils.replace(val, "$", "\\$") : val;
                                dropDownContainer.locator("label").filter(new Locator.FilterOptions().setHasText(Pattern.compile(val))).click();
                            });
                        }
                        // Click on button
                        dropDownContainer.locator("button").filter(new Locator.FilterOptions().setHasText(Pattern.compile("Show"))).click();
                    }
                }
            } catch (Exception e) {
                logger.error("Error while applying filter with {} : {}", key, values);
            }
        });
    }

    private void clickOnGlobalNavItemByName(String itemName) {
        logger.info("Trying to navigate to item : {}", itemName);
        Locator globalNav = page.locator("nav.global-nav__nav");
        Locator matchedGlobalNavItem = globalNav.locator("ul li.global-nav__primary-item").filter(new Locator.FilterOptions().setHasText(itemName));
        matchedGlobalNavItem.click();
        logger.info("Clicked on global navigation item : {}", itemName);
    }

    private void applyToJobs(List<Locator> jobs) {
        logger.info("Total job cards loaded: " + jobs.size());
        jobs.forEach(this::clickOnJobItemInLeftNav);
    }

    private void clickOnJobItemInLeftNav(Locator job) {
        try {
            if (Objects.nonNull(job)) {
                job.scrollIntoViewIfNeeded();
                Thread.sleep(500);
                job.click();
                lookUpJobDetailsAndApply();
            }
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
        }
    }

    private void lookUpJobDetailsAndApply() {

        String jobTitle = null;
        String company = null;
        try {
            Locator jobDetails = page.locator(".jobs-details");
            jobTitle = jobDetails.locator(".job-details-jobs-unified-top-card__job-title").innerText();
            company = jobDetails.locator(".job-details-jobs-unified-top-card__company-name").innerText();
            logger.info("\t=> JobTitle: [{}], at company : [{}]", jobTitle, company);
            Locator jobApplyId = jobDetails.locator("#jobs-apply-button-id").filter(new Locator.FilterOptions().setVisible(true));
            if (StringUtils.equalsIgnoreCase(jobApplyId.innerText(), "Easy Apply")) {
                jobApplyId.click();
                 processEasyAppyJob();
            } else {
                processExternalJob();
            }

            //jobApplyId.click();
        } catch (Exception e) {
            logger.error("\t=> JobTitle: {}, at company : {}", jobTitle, company);
        }

    }

    private void processExternalJob() {
        logger.info("Processing Apply Job");
    }

    private void processEasyAppyJob() {
        logger.info("Processing EasyApply Job");
        Locator jobsEasyApplyModal = page.locator("div.jobs-easy-apply-modal");
        try {
            int maxSteps = 8;
            int step = 0;
            while (jobsEasyApplyModal.isVisible() && step < maxSteps) {
                List<Locator> questions = jobsEasyApplyModal.locator("div[data-test-form-element]").all();
                questions.forEach(this::fillQuestionAnswer);

                Locator footer = jobsEasyApplyModal.locator("footer");
                Locator nextBtn = footer.locator("button").getByText("Next");
                Locator reviewBtn = footer.locator("button").getByText("Review");
                Locator submitButton = footer.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName(Pattern.compile("Submit.*", Pattern.CASE_INSENSITIVE)));

                 if(nextBtn.isVisible()) {
                    nextBtn.click();
                } else if(reviewBtn.isVisible()) {
                    reviewBtn.click();
                } else if(submitButton.isVisible()) {
                    submitButton.click();
                }

                step++;
            }
            System.out.println("Filled all the questions");
        } finally {
             System.out.println("Closing window");

            Locator alertdialog = page.locator("div[role='alertdialog']");
            Locator dialog = page.locator("div[role='dialog']");

            if (jobsEasyApplyModal.isVisible()) {
               jobsEasyApplyModal.locator("button.artdeco-modal__dismiss").click();
            } else if(alertdialog.isVisible()) {
                alertdialog.locator("data-control-name='discard_application_confirm_btn'").click();
            } else if(dialog.isVisible()) {
                dialog.locator("button.artdeco-modal__dismiss").click();
            }
        }


        page.locator("div[role='alertdialog']").waitFor();
        Locator alertdialog = page.locator("div[role='alertdialog']");
        if (alertdialog.isVisible()) {
            alertdialog.locator("button[data-control-name='discard_application_confirm_btn']").click();
        }
    }

    private void fillQuestionAnswer(Locator questionEle) {
        try {
            if (Objects.nonNull(questionEle)) {


                Locator desc = page.locator("div.jobs-easy-apply-repeatable-groupings__groupings");
                String descTitle = null;
                if (desc.isVisible()) {
                    descTitle = desc.innerText();
                }


                String questionLabel = getQuestionLabel(questionEle);
                List<String> SKIP_INPUTS = List.of("Middle name", "Phone country code");
                if (SKIP_INPUTS.stream().anyMatch(s -> StringUtils.equalsIgnoreCase(s, questionLabel))) {
                    return;
                }
                //Locator questionInputEle = questionEle.locator("input.artdeco-text-input--input");
                //String questionType = questionInputEle.getAttribute("type");
                //String existingAns = questionInputEle.getAttribute("value");

                String newAnswer = CommonUtility.getExistingAnswerByLabel(questionLabel);
                String type = null;

                Locator textInput = questionEle.locator("input[type='text'], input[type='tel'], input[type='email'], input.artdeco-text-input--input");
                if (textInput.isVisible()) {
                    if (StringUtils.isBlank(textInput.inputValue())) {
                        textInput.fill(newAnswer);
                        page.keyboard().press("Enter");
                    }

                    type = "text";
                    return;
                }

                List<Locator> radioInputs = questionEle.locator("input[type='radio']").all();
                if (CollectionUtils.isNotEmpty(radioInputs)) {
                    newAnswer = List.of("Yes", "No").contains(newAnswer) ? newAnswer : "No";
                    questionEle.locator("label[data-test-text-selectable-option__label=" + newAnswer + "]").click();
                    type = "radio";
                    return;
                }

                Locator selectInput = questionEle.locator("select");
                if (selectInput.isVisible()) {
                    if (StringUtils.contains(questionLabel, "Email")) {
                        selectInput.selectOption(StringUtils.isBlank(selectInput.inputValue()) ? "thirupathaiah.salla@gmail.com" : selectInput.inputValue());
                    } else if (StringUtils.contains(questionLabel, "country code")) {
                        selectInput.selectOption(StringUtils.isBlank(selectInput.inputValue()) ? "United States (+1)" : selectInput.inputValue());
                    } else if (StringUtils.contains(questionLabel, "Mobile")) {
                        selectInput.selectOption(StringUtils.isBlank(selectInput.inputValue()) ? "7373288723" : selectInput.inputValue());
                    } else {
                        selectInput.selectOption("Yes");
                    }
                    //selectInput.selectOption(newAnswer);
                    type = "select";
                    return;
                }

                Locator textarea = questionEle.locator("textarea");
                if (textarea.isVisible()) {
                    textarea.fill(newAnswer);
                    type = "textarea";
                    return;
                }

                if(StringUtils.isBlank(newAnswer)) {
                    logger.info("Question : {}, Type : {}", questionLabel, type);
                }
            }
        } catch (Exception e) {
            logger.error("Error while read and answering question : {}");
        }
    }

    private String getQuestionLabel(Locator questionEle) {
        String questionLabel = "";
        try {
            questionLabel = questionEle.locator("legend, label").first().innerText();
        } catch (Exception ignore) {
            // fallback to aria-label or placeholder
            try {
                questionLabel = questionEle.locator("[aria-label]").first().getAttribute("aria-label");
            } catch (Exception ignore2) {
                questionLabel = questionEle.locator("* >> nth=0").innerText();
            }
        }
        if(StringUtils.isNoneBlank(questionLabel)) {
            String[] qa = questionLabel.split("\n");
            questionLabel = qa[0];
        }
        return questionLabel;
    }


    private Map<String, List<String>> getFilter1() {
        Map<String, List<String>> filterMap = new LinkedHashMap<>();
        //filterMap.put("Date posted", List.of("Past 24 hours"));
        filterMap.put("Experience level", List.of("Associate", "Mid-Senior level"));
        //filterMap.put("Salary", List.of("$160,000+"));
        //filterMap.put("Remote", List.of("Remote"));
        filterMap.put("Easy Apply", List.of());
        return filterMap;
    }

}
