package org.example;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.util.*;

public class LinkedInJob {
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
            page.getByLabel("Show all Job picks for you").click();

            Thread.sleep(1000);
            page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Search by title, skill, or")).fill("spring-boot");
            page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Search by title, skill, or")).press("Enter");
            page.locator("div").filter(new Locator.FilterOptions().setHasText("Jobs search Jobs Filter")).nth(2).click();

            Thread.sleep(1000);
            page.getByLabel("Date posted filter. Clicking").click();
            page.locator("label").filter(new Locator.FilterOptions().setHasText("Past week Filter by Past week")).click();
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Apply current filter to show")).click();

            page.getByLabel("Experience level filter.").click();
            page.locator("label").filter(new Locator.FilterOptions().setHasText("Associate")).click();
            page.locator("label").filter(new Locator.FilterOptions().setHasText("Mid-Senior level")).click();
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Apply current filter to show")).click();

            page.getByLabel("Salary filter. Clicking this").click();
            page.locator("label").filter(new Locator.FilterOptions().setHasText("$160,000+")).click();

            page.getByLabel("Easy Apply filter.").click();

            List<Locator> pages = page.locator("ul.artdeco-pagination__pages li").all();
            System.out.println("No of Pages :" + pages.size());
            for (int i = 0; i < pages.size(); i++) {
                System.out.println("Clicking Page : " + i);
                pages.get(i).click();
            }

        }
    }
}
