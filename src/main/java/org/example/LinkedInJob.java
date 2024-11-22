package org.example;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

public class LinkedInJob {

    static Map<String, String> radioOptionmap = new HashMap<>();
    static Map<String, String> textMap = new HashMap<>();
    static Map<String, String> questionAnswerMap = new HashMap<>();
    static final String DEFAULT_ANS = "Default Answer";
    static {
        radioOptionmap.put("What payment modes are you","Yes");
        radioOptionmap.put("This role is only for W2/1099","Yes");
        radioOptionmap.put("Have you completed the following level of education: Bachelor's Degree?", "Yes");
        radioOptionmap.put("Are you willing to relocate?", "Yes");
        radioOptionmap.put("Do you have the following license or certification: Professional Engineer (PE)?", "Yes");
        radioOptionmap.put("Do you have the following license or certification: Engineer In Training?", "Yes");

        textMap.put("Why do you want this job?", "I am passionate about this field");
        textMap.put("What is your expected hourly","65");
        textMap.put("What are your skills?", "Java, Spring-boot");
        textMap.put("Have you completed the following level of education: Bachelor's Degree?", "");
        textMap.put("How many years of work experience do you have with Power Protection?", "");
        textMap.put("How many years of work experience do you have with Electrical Engineering?", "");
        textMap.put("How many years of Business Consulting and Services experience do you currently have?", "");
        textMap.put("How many years of Utilities experience do you currently have?", "");
        textMap.put("How many years of Engineering experience do you currently have?", "");
        textMap.put("How many years of work experience do you have with Python?", "");
        textMap.put("How many years of work experience do you have with Java?", "");
        textMap.put("How many years of work experience do you have with AMLS?", "");
        textMap.put("How many years of work experience do you have with Spring Boot?", "");
        textMap.put("How many years of work experience do you have with Vert.x?", "");

        questionAnswerMap.putAll(textMap);
        questionAnswerMap.putAll(radioOptionmap);

    }
    public static void main(String[] args) throws InterruptedException {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            page.navigate("https://www.linkedin.com/");
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Sign in").setExact(true)).click();

            page.getByLabel("Email or phone").fill("thirupathaiah.salla@gmail.com");
            page.getByLabel("Password").fill("Thiru@123");
            page.getByLabel("Sign in", new Page.GetByLabelOptions().setExact(true)).click();

            Thread.sleep(1000);
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Jobs").setExact(true)).click();
            //page.getByLabel("Show all Job picks for you").click();

            List<Locator> buttons = page.locator("button:has-text('You are on the messaging overlay')").all();
            if(Objects.nonNull(buttons) && !buttons.isEmpty()) {
                buttons.get(1).click();
            }

            Thread.sleep(1000);
            page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Search by title, skill, or")).fill("spring-boot");
            page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Search by title, skill, or")).press("Enter");
            page.locator("div").filter(new Locator.FilterOptions().setHasText("Jobs search Jobs Filter")).nth(2).click();

            Thread.sleep(1000);
            page.getByLabel("Date posted filter. Clicking").click();
            page.locator("label").filter(new Locator.FilterOptions().setHasText("Past week Filter by Past week")).click();
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(Pattern.compile("Apply current filter to show", Pattern.CASE_INSENSITIVE))).click();

            Thread.sleep(1000);
            page.getByLabel("Experience level filter.").click();
            page.locator("label").filter(new Locator.FilterOptions().setHasText("Associate")).click();
            page.locator("label").filter(new Locator.FilterOptions().setHasText("Mid-Senior level")).click();
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(Pattern.compile("Apply current filter to show", Pattern.CASE_INSENSITIVE))).click();

            Thread.sleep(1000);
            page.getByLabel("Salary filter. Clicking this").click();
            page.locator("label").filter(new Locator.FilterOptions().setHasText("$160,000+")).click();
            Thread.sleep(1000);
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(Pattern.compile("Apply current filter to show", Pattern.CASE_INSENSITIVE))).click();

            page.getByLabel("Easy Apply filter.").click();

            Thread.sleep(5000);
            List<Locator> pages = page.locator("ul.jobs-search-pagination__pages li").all();
            System.out.println("No of Pages :" + pages.size());
            for (int i = 0; i < pages.size(); i++) {
                System.out.println("Selecting Page : " + i);
                if (i > 0) {
                    pages.get(i).click();
                }
                iterateOverJobItem(page);
            }
        }
    }

    private static void iterateOverJobItem(Page page) {
        List<Locator> jobItems = page.locator("xpath=//ul[@class='scaffold-layout__list-container']//li[contains(@class, 'jobs-search-results__list-item')]").all();
        System.out.printf("Found %d jobItems%n", jobItems.size());
        for(Locator jobItem :jobItems) {
            try {
                jobItem.click();
                Locator jobDetailsMainContent = page.locator("xpath=//div[contains(@class, 'jobs-details__main-content')]");
                String jobTitle = jobDetailsMainContent.locator("xpath=//div[contains(@class, 'job-details-jobs-unified-top-card__job-title')]//h1").textContent();
                System.out.println("JobTitle : " + jobTitle);
                Thread.sleep(1000);
                List<Locator> appliedStatus = jobDetailsMainContent.locator("xpath=//div[@role='alert']//span[@class='artdeco-inline-feedback__message']").all();
                if(CollectionUtils.isNotEmpty(appliedStatus)) {
                    return;
                }
                jobDetailsMainContent.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Easy Apply")).click();

                applyToJobItem(page);

            } catch (InterruptedException ie) {
                System.err.println("InterruptedException :: "+ie.getMessage());
            }

        }
    }

    private static void applyToJobItem(Page page) {

        page.getByLabel("Continue to next step").click();
        page.getByLabel("Continue to next step").click();

        boolean isFoudnQns = true;
        while(isFoudnQns) {
            List<Locator> questions = page.locator("div.jobs-easy-apply-content .jobs-easy-apply-form-section__grouping").all();
            for (Locator question : questions) {
                fillQuestionAnswers(page, question, 1);
            }
            /**
             page.getByLabel("What is your expected hourly").click();
             page.getByLabel("What is your expected hourly").fill("65");
             page.getByLabel("Please confirm your work").click();
             page.locator("#ember1238").click();
             page.getByLabel("What payment modes are you").selectOption("Yes");
             page.getByLabel("Review your application").click();
             page.getByLabel("This role is only for W2/1099").selectOption("Yes");
             page.getByLabel("Please confirm your work").click();
             page.getByLabel("Please confirm your work").click();
             page.getByLabel("Please confirm your work").fill("h1b");
             page.getByLabel("Are you willing to go HYBRID").selectOption("Yes");
             page.getByLabel("Please confirm your work").click();
             page.getByText("Enter a decimal number larger than").click();
             page.getByLabel("Please confirm your work").click();
             page.getByLabel("Please confirm your work").fill("");
             page.getByLabel("Please confirm your work").click();
             page.getByLabel("Please confirm your work").fill("4");
             page.getByLabel("Review your application").click();
             page.getByLabel("Submit application").click();
             page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Dismiss")).click();
             **/
            page.getByLabel("Review your application").click();

            Locator submit = page.getByLabel("Submit application");
            if(Objects.nonNull(submit) && submit.isVisible()) {
                submit.click();
                isFoudnQns = false;
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Dismiss")).click();
            }
        }
    }

    private static void fillQuestionAnswers(Page page, Locator questionElement, int i){

        String questionText = questionElement.innerText();
        System.out.println("Question: " + questionText);

        //Locator hiddenTextElement = questionElement.locator("span[aria-hidden='true']");
        //String hiddenText = hiddenTextElement.textContent();
        //System.out.println("hiddenText: " + hiddenText);

        // Retrieve the answer from the map
        String answer = questionAnswerMap.getOrDefault(questionText, DEFAULT_ANS);

        List<Locator> radioButtons = questionElement.locator("input[type=radio]").all();
        if(CollectionUtils.isNotEmpty(radioButtons)) {
            Locator yesLabel = questionElement.locator("label[data-test-text-selectable-option__label='No']");
            if(answer.equalsIgnoreCase("Yes")) {
                yesLabel = questionElement.locator("label[data-test-text-selectable-option__label='Yes']");
            }
            yesLabel.click();
        }

        List<Locator> textBox = questionElement.locator("input[type=text]").all();
        if(CollectionUtils.isNotEmpty(textBox)) {
            String answerForText = questionAnswerMap.get(questionText);
            String ans = Objects.isNull(answerForText) && StringUtils.contains(questionText, "How many") ? "6" : answerForText;
            questionElement.locator("input[type=text]").fill(ans);
        }

        Locator dropdown = questionElement.locator("select");
        if(Objects.nonNull(dropdown) && dropdown.isVisible()) {
            dropdown.selectOption("Yes");
        }
    }

    private void answerToTextQuestions(){

    }
    private void answerToRadioOptions(Locator question, String answer) {
        // Select the radio button with the answer "No"
        Locator radioButtons = question.locator("input[type=radio]");
        for (int j = 0; j < radioButtons.count(); j++) {
            String optionText = radioButtons.nth(j).innerText();
            if (optionText.contains(answer)) {
                radioButtons.nth(j).click();
                break;
            }
        }
    }
}
