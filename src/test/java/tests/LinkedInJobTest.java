package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import runners.BrowserStackRunner;

import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class LinkedInJobTest extends BrowserStackRunner {

    static final String DEFAULT_ANS = "Default Answer";
    private static final Logger logger = LoggerFactory.getLogger(LinkedInJobTest.class);
    static Map<String, String> radioOptionmap = new HashMap<>();
    static Map<String, String> textMap = new HashMap<>();
    static Map<String, String> selectMap = new HashMap<>();
    static Map<String, String> questionAnswerMap = new HashMap<>();

    static {
        radioOptionmap.put("What payment modes are you", "Yes");
        radioOptionmap.put("This role is only for W2/1099", "Yes");
        radioOptionmap.put("Have you completed the following level of education: Bachelor's Degree?", "Yes");
        radioOptionmap.put("Are you willing to relocate?", "Yes");
        radioOptionmap.put("Do you have the following license or certification: Professional Engineer (PE)?", "Yes");
        radioOptionmap.put("Do you have the following license or certification: Engineer In Training?", "Yes");
        radioOptionmap.put("Will you now, or in the future, require sponsorship for employment visa status", "Yes");
        radioOptionmap.put("Are you legally authorized to work in the United States?", "Yes");
        radioOptionmap.put("Are you comfortable working in a hybrid setting?", "Yes");
        radioOptionmap.put("Are you comfortable working in an onsite setting?", "Yes");
        radioOptionmap.put("Are you willing to undergo a background check, in accordance with local law/regulations?", "Yes");
        radioOptionmap.put("Are you willing to take a drug test, in accordance with local law/regulations?", "Yes");
        radioOptionmap.put("Are you comfortable commuting to this job's location?", "Yes");
        radioOptionmap.put("Do you have the ability to work 100% on-site in Boulder, CO?", "Yes");
        radioOptionmap.put("Do you hold a Bachelor’s degree or higher in computer science, engineering, mathematics, or physical sciences?", "Yes");
        radioOptionmap.put("Do you have at least two years of professional experience building and executing DevOps or DevSecOps solutions using Continuous Integration / Continuous Deployment (CI/CD) such as GitLab-ci?", "Yes");
        radioOptionmap.put("Do you have professional experience using Kubernetes and Docker?", "Yes");
        radioOptionmap.put("Do you have at least one year of professional experience using python3?", "Yes");
        radioOptionmap.put("Do you have at least one year of professional experience using Linux operating systems?", "Yes");
        radioOptionmap.put("Do you have an active DoD security clearance?", "No");
        radioOptionmap.put("disability", "No");
        radioOptionmap.put("Do you have a valid driver's license?", "Yes");
        radioOptionmap.put("Do you have the following license or certification: AWS Certifications?", "No");

        textMap.put("First name", "Tirupathaiah");
        textMap.put("Last name", "Salla");
        textMap.put("Mobile phone number", "7373288723");
        textMap.put("Phone", "7373288723");
        textMap.put("Address", "12700 Ridge line Blvd Cedar Park");
        textMap.put("Street Address", "12700 Ridge line Blvd Cedar Park");
        textMap.put("City", "Austin");
        textMap.put("LinkedIn Profile URL", "www.linkedin.com/in/thirupathaiah-salla");
        textMap.put("How did you hear about us?", "LinkedLin");
        textMap.put("Referred by", "Venkat");
        textMap.put("Headline", "Java Spring Boot | Microservices Architect | Crafting Scalable & Robust Solutions ");
        textMap.put("What is your desired salary?", "145000");
        textMap.put("Why do you want this job?", "I am passionate about this field");
        textMap.put("What is your expected hourly", "65");
        textMap.put("What are your skills?", "Java, Spring-boot");
        textMap.put("Have you completed the following level of education: Bachelor's Degree?", "Yes");
        textMap.put("How many years of work experience do you have with Power Protection?", "6");
        textMap.put("How many years of work experience do you have with Electrical Engineering?", "6");
        textMap.put("How many years of Business Consulting and Services experience do you currently have?", "6");
        textMap.put("How many years of Utilities experience do you currently have?", "6");
        textMap.put("How many years of Engineering experience do you currently have?", "16");
        textMap.put("How many years of work experience do you have with Python?", "6");
        textMap.put("How many years of work experience do you have with Java?", "16");
        textMap.put("How many years of work experience do you have with AMLS?", "6");
        textMap.put("How many years of work experience do you have with Spring Boot?", "16");
        textMap.put("How many years of work experience do you have with Vert.x?", "6");
        textMap.put("How many years of work experience do you have with C++?", "5");
        textMap.put("How many years of work experience do you have with Android?", "5");
        textMap.put("How many years of work experience do you have with Webrtc?", "5");
        textMap.put("How many years of work experience do you have with Software Integration?", "10");
        textMap.put("How many years of work experience do you have with Open Source Platforms?", "16");
        textMap.put("How many years of work experience do you have with Amazon Web Services (AWS)?", "6");
        textMap.put("If you currently have a DoD security clearance, what level of security clearance do you maintain?", "No");
        textMap.put("Available start date", "Next Monday");
        textMap.put("Angular", "6");
        textMap.put("State or Province", "Texas");
        textMap.put("Zip/Postal Code", "78613");

        selectMap.put("Email", "thirupathaiah.salla@gmail.com");
        selectMap.put("Email address", "thirupathaiah.salla@gmail.com");
        selectMap.put("Phone country code", "United States (+1)");
        selectMap.put("Will you now, or in the future, require sponsorship for employment visa status", "Yes");
        selectMap.put("Are you legally authorized to work in the United States?", "Yes");
        selectMap.put("Do you have a US citizenship/Greencard?Do you have a US citizenship/Greencard?", "No");
        selectMap.put("How many years of external consulting experience do you have?", "10+");
        selectMap.put("Country", "UNITED STATES");

        questionAnswerMap.putAll(textMap);
        questionAnswerMap.putAll(radioOptionmap);
        questionAnswerMap.putAll(selectMap);

    }

    private static String findQuestionType(Locator questionElement) {
        if (CollectionUtils.isNotEmpty(questionElement.locator("input[type=text]").all())) {
            return "text";
        }
        if (CollectionUtils.isNotEmpty(questionElement.locator("input[type=radio]").all())) {
            return "radio";
        }
        if (CollectionUtils.isNotEmpty(questionElement.locator("input[type=number]").all())) {
            return "number";
        }
        if (CollectionUtils.isNotEmpty(questionElement.locator("input[type=url]").all())) {
            return "url";
        }
        if (CollectionUtils.isNotEmpty(questionElement.locator("input[type=checkbox]").all())) {
            return "checkbox";
        }
        if (CollectionUtils.isNotEmpty(questionElement.locator("input[type=submit]").all())) {
            return "submit";
        }
        if (CollectionUtils.isNotEmpty(questionElement.locator("input[type=button]").all())) {
            return "button";
        }
        if (CollectionUtils.isNotEmpty(questionElement.locator("select").all())) {
            return "select";
        }
        if (CollectionUtils.isNotEmpty(questionElement.locator("textarea").all())) {
            return "textarea";
        }
        return null;
    }

    @Test
    void linkedInJobTest() throws InterruptedException {
        logger.info("Starting test: LinkedInJobTest");

        try {

            page.navigate("https://www.linkedin.com/");
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Sign in").setExact(true)).click();

            //page.getByLabel("Email or phone").fill("thirupathaiah.salla@gmail.com");
            //page.getByLabel("Password").fill("Thiru@linkedin1");

            page.getByLabel("Email or phone").fill("tirupathaiah.salla@gmail.com");
            page.getByLabel("Password").fill("Thiru@123");
            page.getByLabel("Sign in", new Page.GetByLabelOptions().setExact(true)).click();

            //page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(30000));
            page.waitForLoadState(LoadState.DOMCONTENTLOADED, new Page.WaitForLoadStateOptions().setTimeout(30000));

            Thread.sleep(1000);
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Jobs").setExact(true)).click();

            Thread.sleep(5000);
            Locator jobSearchLoc = page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Search by title, skill, or"));
            jobSearchLoc.fill("spring-boot");
            jobSearchLoc.press("Enter");

            Thread.sleep(5000);

            // Applying filters
            applyJobSearchFilter("Date posted", List.of("Past 24 hours"));
            applyJobSearchFilter("Experience level", List.of("Associate", "Mid-Senior level"));
            applyJobSearchFilter("Easy Apply", List.of());

            Thread.sleep(5000);

            boolean isNextPageAvailable = true;
            while (isNextPageAvailable) {
                Locator currentPage = page.locator("button.jobs-search-pagination__indicator-button--active");
                if (currentPage.isVisible()) {
                    List<Locator> jobItems = page.locator("xpath=//ul//li[contains(@class, 'scaffold-layout__list-item')]").all();
                    System.out.println("Found " + jobItems.size() + " jobItems in Page " + currentPage.innerText());
                    applyToJobList(jobItems);
                }
            }


        } finally {

        }
    }

    private void applyJobSearchFilter(String filterName, List<String> filterValues) throws InterruptedException {
        Thread.sleep(1000);
        List<Locator> liSearchHdr = page.locator("ul.search-reusables__filter-list li.search-reusables__primary-filter").all();
        Optional<Locator> optionalHdrLoc = liSearchHdr.stream().filter(e -> StringUtils.equalsIgnoreCase(e.innerText(), filterName)).findFirst();
        if (optionalHdrLoc.isPresent()) {
            Locator hdrLoc = optionalHdrLoc.get();
            hdrLoc.click();

            Locator dropDownContainer = hdrLoc.locator("fieldset.reusable-search-filters-trigger-dropdown__container");
            if (Objects.nonNull(dropDownContainer) && dropDownContainer.isVisible()) {
                if (CollectionUtils.isNotEmpty(filterValues)) {
                    filterValues.stream().filter(StringUtils::isNoneBlank).forEach(val -> {
                        val = StringUtils.contains(val, "$") ? StringUtils.replace(val, "$", "\\$") : val;
                        dropDownContainer.locator("label").filter(new Locator.FilterOptions().setHasText(Pattern.compile(val))).click();
                    });
                }
                // Click on button
                dropDownContainer.locator("button").filter(new Locator.FilterOptions().setHasText(Pattern.compile("Show"))).click();
            }
        }
    }

    private void applyToJobList(List<Locator> jobItems) {
        for (Locator jobItem : jobItems) {
            String jobTitle = null;
            try {
                jobItem.click();
                Locator jobDetailsMainContent = page.locator("xpath=//div[contains(@class, 'jobs-details__main-content')]");
                jobTitle = jobDetailsMainContent.locator("xpath=//div[contains(@class, 'job-details-jobs-unified-top-card__job-title')]//h1").textContent();
                System.out.println("JobTitle : " + jobTitle);
                Thread.sleep(1000);
                List<Locator> appliedStatus = jobDetailsMainContent.locator("xpath=//div[@role='alert']//span[@class='artdeco-inline-feedback__message']").all();
                if (CollectionUtils.isEmpty(appliedStatus)) {
                    jobDetailsMainContent.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Easy Apply")).click();
                    applyToJobItem(page);
                } else {
                    System.out.println("Already applied. Skipping");
                }
            } catch (Exception exc) {
                System.err.println("JobTitle : " + jobTitle + ", Status : Error");

                takeScreenshot(page, jobTitle);
                //System.err.println("JobTitle : " + jobTitle+", Status : Error, ErrMsg : "+exc.getMessage());
                Locator dismissBtn = page.locator("button.artdeco-modal__dismiss");
                if (dismissBtn.isVisible()) {
                    dismissBtn.click();
                }
                Locator discardBtn = page.locator("button[data-control-name='discard_application_confirm_btn']");
                if (discardBtn.isVisible()) {
                    discardBtn.click();
                }
            }
        }
    }

    private void applyToJobItem(Page page) throws InterruptedException {

        boolean isFoudnQns = true;
        while (isFoudnQns) {

            List<Locator> questions = page.locator("div.jobs-easy-apply-content .jobs-easy-apply-form-section__grouping").all();
            if (CollectionUtils.isEmpty(questions)) {
                questions = page.locator("div[data-test-form-element]").all();
            }
            for (Locator question : questions) {
                fillQuestionAnswers(page, question);
            }
            Locator submit = page.getByLabel("Submit application");
            if (Objects.nonNull(submit) && submit.isVisible()) {
                submit.click();
                isFoudnQns = false;
                Thread.sleep(5000);
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Dismiss")).click();
                return;
            }

            Locator reviewBtn = page.getByLabel("Review your application");
            if (Objects.nonNull(reviewBtn) && reviewBtn.isVisible()) {
                reviewBtn.click();
            }
            Locator continueBtn = page.getByLabel("Continue to next step");
            if (Objects.nonNull(continueBtn) && continueBtn.isVisible()) {
                continueBtn.click();
            }
        }
    }

    private void fillQuestionAnswers(Page page, Locator questionElement) {

        String questionText = questionElement.innerText();
        String answer = questionAnswerMap.entrySet().stream().filter(es -> questionText.contains(es.getKey())).map(Map.Entry::getValue).findFirst().orElse(DEFAULT_ANS);
        String questionType = findQuestionType(questionElement);
        if (!StringUtils.contains(questionText, "country code")) {
            System.out.println("{ question : " + questionText + ", type : " + questionType + ", answer : " + answer + " }");
        }
        if (StringUtils.equalsIgnoreCase(questionType, "text")) {
            Locator textBox = questionElement.locator("input[type=text]");
            if (textBox.isVisible() && StringUtils.isBlank(textBox.inputValue())) {
                String answerForText = questionAnswerMap.get(questionText);
                String ans = Objects.isNull(answerForText) && StringUtils.contains(questionText, "How many") ? "6" : answerForText;
                if (StringUtils.isNoneBlank(ans)) {
                    questionElement.locator("input[type=text]").fill(ans);
                }
            }
        } else if (StringUtils.equalsIgnoreCase(questionType, "radio")) {
            answer = List.of("Yes", "No").contains(answer) ? answer : "No";
            questionElement.locator("label[data-test-text-selectable-option__label=" + answer + "]").click();
            /**
             List<Locator> radioButtons = questionElement.locator("input[type=radio]").all();
             if (CollectionUtils.isNotEmpty(radioButtons)) {
             for (Locator radioBtn : radioButtons) {
             if (StringUtils.startsWith(radioBtn.innerText(), answer)) {
             radioBtn.click();
             }
             }
             } **/
        } else if (StringUtils.equalsIgnoreCase(questionType, "select")) {
            Locator dropdown = questionElement.locator("select");
            if (Objects.nonNull(dropdown) && dropdown.isVisible()) {
                if (StringUtils.contains(questionText, "Email")) {
                    dropdown.selectOption(StringUtils.isBlank(dropdown.inputValue()) ? "thirupathaiah.salla@gmail.com" : dropdown.inputValue());
                } else if (StringUtils.contains(questionText, "country code")) {
                    dropdown.selectOption(StringUtils.isBlank(dropdown.inputValue()) ? "United States (+1)" : dropdown.inputValue());
                } else if (StringUtils.contains(questionText, "Mobile")) {
                    dropdown.selectOption(StringUtils.isBlank(dropdown.inputValue()) ? "7373288723" : dropdown.inputValue());
                } else {
                    dropdown.selectOption("Yes");
                }
            } else if (StringUtils.equalsIgnoreCase(questionType, "textarea")) {
                Locator textArea = page.locator("textarea");
                answer = List.of("Yes", "No").contains(answer) ? answer : "N/A";
                textArea.fill(answer);
            }
        }
    }

    public void takeScreenshot(Page page, String scenarioName) {
        scenarioName = StringUtils.isBlank(scenarioName) ? scenarioName : scenarioName.replaceAll("[^a-zA-Z0-9]", "");
        String fileName = "screenshots/" + scenarioName.replaceAll("\\s+", "_") + "_" + System.currentTimeMillis() + ".png";
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(fileName)));
    }


}
