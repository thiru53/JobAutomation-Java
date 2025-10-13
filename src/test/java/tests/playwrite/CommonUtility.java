package tests.playwrite;

import com.microsoft.playwright.Page;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CommonUtility {

    static final String DEFAULT_ANS = "Default Answer";
    static Map<String, String> radioOptionmap = new HashMap<>();
    static Map<String, String> textMap = new HashMap<>();
    static Map<String, String> selectMap = new HashMap<>();
    static Map<String, String> questionAnswerMap = new HashMap<>();
    static Page mainPage = null;

    static {
        radioOptionmap.put("What payment modes are you", "Yes");
        radioOptionmap.put("What is your visa status", "H-1B");
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
        radioOptionmap.put("Are you living in or willing to relocate", "Yes");
        radioOptionmap.put("What is the highest level of education you have completed", "Bachelor");

        textMap.put("First name", "Tirupathaiah");
        textMap.put("Last name", "Salla");
        textMap.put("Middle name","");
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
        textMap.put("How many years of work experience do you have with Spring Framework?", "16");
        textMap.put("How many years of work experience do you have ", "6");
        textMap.put("How many years of work experience do you have with Vert.x?", "6");
        textMap.put("How many years of work experience do you have with C++?", "5");
        textMap.put("How many years of work experience do you have with Android?", "5");
        textMap.put("How many years of work experience do you have with Webrtc?", "5");
        textMap.put("How many years of work experience do you have with Software Integration?", "10");
        textMap.put("How many years of work experience do you have with Open Source Platforms?", "16");
        textMap.put("How many years of work experience do you have with Amazon Web Services (AWS)?", "6");
        textMap.put("How many years of work experience do you have with Playwriting?", "8");
        textMap.put("How many years of experience do you have", "16");
        textMap.put("If you currently have a DoD security clearance, what level of security clearance do you maintain?", "No");
        textMap.put("Available start date", "Next Monday");
        textMap.put("Angular", "6");
        textMap.put("State or Province", "Texas");
        textMap.put("Zip/Postal Code", "78613");
        textMap.put("What's your expected salary", "140000");
        textMap.put("What is your primary motivation for exploring new opportunities", "learning and apply new idea");

        selectMap.put("Email", "thirupathaiah.salla@gmail.com");
        selectMap.put("Email address", "thirupathaiah.salla@gmail.com");
        selectMap.put("Phone country code", "United States (+1)");
        selectMap.put("Will you now, or in the future, require sponsorship for employment visa status", "Yes");
        selectMap.put("Are you legally authorized to work in the United States?", "Yes");
        selectMap.put("Do you have a US citizenship/Greencard?Do you have a US citizenship/Greencard?", "No");
        selectMap.put("How many years of external consulting experience do you have?", "10+");
        selectMap.put("Country", "UNITED STATES");
        selectMap.put("Location (city)", "Austin, Texas, United States");
        selectMap.put("Location", "Austin, Texas, United States");
        selectMap.put("What is your current notice period", "Immediately");


        questionAnswerMap.putAll(textMap);
        questionAnswerMap.putAll(radioOptionmap);
        questionAnswerMap.putAll(selectMap);

    }

    public static String getExistingAnswerByLabel(String label) {
        return questionAnswerMap.entrySet().stream().filter(es -> StringUtils.containsIgnoreCase(label, es.getKey())).map(Map.Entry::getValue).findFirst().orElse("");
    }
}
