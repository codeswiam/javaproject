package scrapers;

import db.DBManager;
import debug.ScraperDebug;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RekruteScraper {
    protected DBManager dbManager = DBManager.getInstance();
    protected String siteName = "Rekrute";
    protected String baseUrl = "https://www.rekrute.com";
    protected String pagesUrlFormat = "https://www.rekrute.com/offres.html?p={X}&s=1&o=1&positionId%5B0%5D=13";
    protected int pagesNumber;
    protected ArrayList<String> pagesUrl = new ArrayList<>();
    protected  ArrayList<String> postsUrl = new ArrayList<>();
    protected ArrayList<DataItem> posts = new ArrayList<>();
    protected int maxPageToScrape = 1; // change back to 4 // or maybe make it 30
    protected int maxPostsToScrape = 10; // change to 1000 later on
    protected ScraperListener listener = null;

    List<String> specificWords = Arrays.asList("java", "C#", "C++","python","php","C","django","laravel","DevOps","dataviz","Paas","Spring","Hibernate","SQL","Angular","Javascript","HTML","CSS","JS","Maven", "Jenkins", "Sonar", "Soap UI", "Postman", "Ansible","Git","IHM","API","pipelines CI/CD","Linux","Docker","XML","JSON");
    private static List<String> extractMatchingWords(String inputPhrase, List<String> specificWords) {
        List<String> matchingWords = new ArrayList<>();
        String regex = "\\b(" + String.join("|", specificWords) + ")\\b";

        // Create a pattern with case-insensitive matching
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        // Create matcher object
        Matcher matcher = pattern.matcher(inputPhrase);

        // Find all matches
        while (matcher.find()) {
            matchingWords.add(matcher.group());
        }

        return matchingWords;
    }

    @Override
    public String toString() {
        return "BaseScraper {baseurl: "+ this.baseUrl +", pagesUrlFormat: "+this.pagesUrlFormat+"}";
    }

    public void setListener(ScraperListener listener) {
        this.listener = listener;
    }

    public void fetchPageNumber() {
        int nbr = this.loadPagesNumber();
        this.pagesNumber = nbr;
        if(this.listener != null) {
            this.listener.updateTotalPages(nbr);
        }
        ScraperDebug.debugPrint("Number of pages found: " + nbr);
    }

    protected int loadPagesNumber() {
        try {
            Document doc = Jsoup.connect("https://www.rekrute.com/offres.html?p=2&s=1&o=1").get();
            String selector = "#fortopscroll > div.main.alt-main > div > div > div > div.col-md-9 > div > div.slide-block > div:nth-child(1) > div.section > span";
            Element page = doc.selectFirst(selector);
            assert page != null;
            String output = page.text();
            String[] strPage = output.split(" ");
            String pg = strPage[strPage.length -1];
            return Integer.parseInt(pg);
        } catch (Exception e) {
            this.pagesNumber = 0;
            ScraperDebug.debugPrint(Arrays.toString(e.getStackTrace()));
            return 0;
        }
    }

    public void fetchPagesUrls() {
        ScraperDebug.debugPrint("Generating pages URls...");
        for (int i = 0; i < this.pagesNumber; i++) {
            this.pagesUrl.add(this.pagesUrlFormat.replace("{X}", "" + i));
            if(i < 5) {
                ScraperDebug.debugPrint(this.pagesUrlFormat.replace("{X}", "" + i));
            }
            if(i == 5) {
                ScraperDebug.debugPrint("...");
            }
        }
    }

    protected void fetchAllPostsFromPage(String pageUrl) {
        try {
            Document pageDoc = Jsoup.connect(pageUrl).get();
            String postTitleSelector = "div > div.col-sm-10.col-xs-12 > div > h2 > a";
            Elements postsTitles = pageDoc.select(postTitleSelector);
            ScraperDebug.debugPrint("Number of posts found: " + postsTitles.size());
            for(Element title: postsTitles) {
                this.postsUrl.add(this.baseUrl + title.attr("href"));
                ScraperDebug.debugPrint("Post Url: " + title.attr("href"));
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public void fetchAllPostsUrl() {
        int maxPage = this.maxPageToScrape;
        int nbr = 0;
        for (String pageUrl : this.pagesUrl) {
            nbr++;
            ScraperDebug.debugPrint("Fetching data from page: " + pageUrl);
            this.fetchAllPostsFromPage(pageUrl);
            maxPage--;
            if(ScraperDebug.debug && maxPage == 0) {
                ScraperDebug.debugPrint("...");
                break;
            }
        }
        if(this.listener != null) {
            this.listener.updateTotalPosts(this.postsUrl.size());
        }
    }

    public DataItem fetchAttributesFromPost(String postUrl) {
        DataItem item = new DataItem(this.siteName, postUrl);
        Document doc;
        try {
            doc = Jsoup.connect(postUrl).get();
        } catch (Exception e) {
            ScraperDebug.debugPrint(e.getMessage());
            ScraperDebug.debugPrint("Error loading page. Skipping...");
            return null;
        }
        item.setTitle(this.fetchPostTitle(doc).toLowerCase());
        item.setPublishDate(this.fetchPostPublishDate(doc).toLowerCase());
        item.setApplyDate(this.fetchPostApplyDate(doc).toLowerCase());
        item.setCompanyName(this.fetchPostCompanyName(doc).toLowerCase());
        item.setCompanyAddress(this.fetchPostCompanyAddress(doc).toLowerCase());
        item.setCompanyWebsite(this.fetchPostCompanyWebsite(doc).toLowerCase());
        item.setCompanyDescription(this.fetchPostCompanyDescription(doc).toLowerCase());
        item.setDescription(this.fetchPostDescription(doc).toLowerCase());
        item.setCity(this.fetchPostCity(doc).toLowerCase());
        item.setRegion(this.fetchPostRegion(doc).toLowerCase());
        item.setSector(this.fetchPostSector(doc).toLowerCase());
        item.setJob(this.fetchPostJob(doc).toLowerCase());
        item.setContractType(this.fetchPostContractType(doc).toLowerCase());
        item.setEducationLevel(this.fetchPostEducationLevel(doc).toLowerCase());
        item.setDiploma(this.fetchPostDiploma(doc).toLowerCase());
        item.setExperience(this.fetchPostExperience(doc).toLowerCase());
        item.setProfileSearched(this.fetchPostProfileSearched(doc).toLowerCase());
        item.setPersonalityTraits(this.fetchPostPersonalityTraits(doc).toLowerCase());
        item.setHardSkills(this.fetchPostHardSkills(doc));
        item.setSoftSkills(this.fetchPostSoftSkills(doc));
        item.setRecommendedSkills(this.fetchPostRecommendedSkills(doc));
        item.setLanguage(this.fetchPostLanguage(doc).toLowerCase());
        item.setLanguageLevel(this.fetchPostLanguageLevel(doc).toLowerCase());
        item.setSalary(this.fetchPostSalary(doc).toLowerCase());
        item.setSocialAdvantages(this.fetchPostSocialAdvantages(doc).toLowerCase());
        item.setRemote(this.fetchPostRemote(doc).toLowerCase());

        ScraperDebug.debugPrint("Total Size: " + item.getFormatedSize());

        return item;
    };

    public void fetchAllPostsAttributes() {
        int max = this.maxPostsToScrape;
        int nbr = 0;
        for(String postUrl: this.postsUrl) {
            ScraperDebug.debugPrint("Loading data from post: " + postUrl);
            nbr++;
            if(this.listener != null) {
                this.listener.updateCurrentPost(nbr, postUrl);
            }
            this.posts.add(fetchAttributesFromPost(postUrl));
            max--;
            if(max <= 0) break;
        }
    }

    protected String fetchPostTitle(Document doc){
        try {
            String selector = "h1";
            String content = doc.selectFirst(selector).text();
            ScraperDebug.debugPrint("Post title: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching title");
            // e.printStackTrace();
            return "NULL";
        }
    }

    protected String fetchPostPublishDate(Document doc){
        try {
            String selector = "#fortopscroll > div.container.anno > div:nth-child(2) > div > div.col-md-12.info.blc.noback > div > div > div.col-md-12.col-sm-12.col-xs-12 > span";
            String content = doc.selectFirst(selector)
                    .text()
                    .split("-")[0]
                    .replace("Publiée", "")
                    .replace(" sur ReKrute.com", "")
                    .trim();
            if (content.equals("aujourd'hui")){
                content = String.valueOf(LocalDate.now());
            }
            ScraperDebug.debugPrint("Publish date: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching publish date");
//            e.printStackTrace();
            return "NULL";
        }
    }
    protected String fetchPostCompanyName(Document doc){
        try {
            String selector = "#fortopscroll > div.container.anno > div:nth-child(2) > div > div.col-md-12.info.blc.noback > div > div > div.col-md-2.col-sm-12.col-xs-12 > div > a";
            String profileUrl = doc.selectFirst(selector).attr("href");
            Document docPorfile = Jsoup.connect(this.baseUrl + "/" + profileUrl).get();
            selector = "#fortopscroll > div.main.alt-main > div > div > div > div.col-md-9 > div > div.content-holder > div > div:nth-child(2) > p:nth-child(1) > span > a";
            String content = docPorfile.selectFirst(selector).text();
            ScraperDebug.debugPrint("Company name: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching company name");
//            e.printStackTrace();
            return "NULL";
        }
    }

    protected String fetchPostCompanyAddress(Document doc){
        try {
            String selector = "#address";
            String content = doc.selectFirst(selector).text();
            ScraperDebug.debugPrint("Company address: " + content);
            return content;
        }catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching company name");
            return "NULL";
        }
    }
    protected String fetchPostCompanyWebsite(Document doc) {
        return "NULL";
    }
    protected String fetchPostCompanyDescription(Document doc){
        try {
            String selector = "#recruiterDescription";
            String content = doc.selectFirst(selector).text().substring(12).trim();
            ScraperDebug.debugPrint("Company description: " + content.substring(0, 40) + "...");
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching company description.");
//            e.printStackTrace();
            return "NULL";
        }
    }

    protected String fetchPostDescription(Document doc){
        try {
//                             #fortopscroll > div.container.anno > div:nth-child(2) > div > div:nth-child(5)
            String selector = "#fortopscroll > div.container.anno > div:nth-child(2) > div > div:nth-child(4)";
            if(! (
                    doc.selectFirst(selector) != null &&
                            doc.selectFirst(selector).selectFirst("h2") != null &&
//                    doc.selectFirst(selector).selectFirst("h2").text() != null &&
                            doc.selectFirst(selector).selectFirst("h2").text().equals("Poste :")
            )) {
                selector = "#fortopscroll > div.container.anno > div:nth-child(2) > div > div:nth-child(5)";
            }

            String content = doc.selectFirst(selector).text().substring(7).trim();
            ScraperDebug.debugPrint("Post description: " + content.substring(0, 40) + "...");
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post description.");
            // e.printStackTrace();
            return "NULL";
        }
    }

    protected String fetchPostCity(Document doc){
        try {
            String selector = "li[title=\"Région\"]";
            String[] contents = doc.selectFirst(selector).text().split(" - ")[0].split(" ");
            String content = contents[contents.length - 2].trim();
            if(content.equals("et")){
                content = contents[contents.length - 3];
            }
            ScraperDebug.debugPrint("Post city: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post city.");
            // e.printStackTrace();
            return "NULL";
        }
    }

    protected String fetchPostRegion(Document doc){
        try {
            String selector = "li[title=\"Région\"]";
            String content = doc.selectFirst(selector).text().split(" - ")[1].trim();
            ScraperDebug.debugPrint("Post region: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post region.");
//            e.printStackTrace();
            return "NULL";
        }
    }
    protected String fetchPostSector(Document doc){
        try {
            String selector = "h2.h2italic";
            String content = doc.selectFirst(selector).text();
            ScraperDebug.debugPrint("Post sector: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post sector.");
//            e.printStackTrace();
            return "NULL";
        }
    }
    protected String fetchPostContractType(Document doc){
        try {
            String selector = "span[title=\"Type de contrat\"]";
            String content = doc.selectFirst(selector).text();
            ScraperDebug.debugPrint("Post contract: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post contract.");
//            e.printStackTrace();
            return "NULL";
        }
    }

    protected String fetchPostEducationLevel(Document doc){
        try {
            String selector = "li[title=\"Niveau d'étude et formation\"]";
            String content = doc.selectFirst(selector).text().split(" - ")[0].trim();
            ScraperDebug.debugPrint("Post education level: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post education level.");
//            e.printStackTrace();
            return "NULL";
        }
    }
    protected String fetchPostDiploma(Document doc){
        try {
            String selector = "li[title=\"Niveau d'étude et formation\"]";
            String content = doc.selectFirst(selector).text().split(" - ")[1].trim();
            ScraperDebug.debugPrint("Post diploma: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post diploma.");
//            e.printStackTrace();
            return "NULL";
        }
    }
    protected String fetchPostExperience(Document doc){
        try {
            String selector = "li[title=\"Expérience requise\"]";
            String content = doc.selectFirst(selector).text().split(" - ")[1].trim();
            ScraperDebug.debugPrint("Post experience: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post experience.");
//            e.printStackTrace();
            return "NULL";
        }
    }
    protected String fetchPostProfileSearched(Document doc){
        try {
            String selector = "#fortopscroll > div.container.anno > div:nth-child(2) > div > div:nth-child(5)";
            if(! (
                    doc.selectFirst(selector) != null &&
                            doc.selectFirst(selector).selectFirst("h2") != null &&
//                    doc.selectFirst(selector).selectFirst("h2").text() != null &&
                            doc.selectFirst(selector).selectFirst("h2").text().equals("Profil recherché :")
            )) {
                selector = "#fortopscroll > div.container.anno > div:nth-child(2) > div > div:nth-child(6)";
            }

            String content = doc.selectFirst(selector).text().substring(18).trim();
            ScraperDebug.debugPrint("Post searched profile: " + content.substring(0, 40) + "...");
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post searched profile.");
//            e.printStackTrace();
            return "NULL";
        }
    }

    protected String fetchPostLanguage(Document doc){
        return "NULL";
    }
    protected String fetchPostLanguageLevel(Document doc){
        return "NULL";
    }
    protected ArrayList<String> fetchPostHardSkills(Document doc){
        try {
            String selector = "h2:contains(Profil recherché)";
            Element profileElement = doc.select(selector).first();

            // Check if the profileElement is found
            if (profileElement != null) {
                // Find the next sibling element (ul) after the profileElement
                Element ulElement = profileElement.nextElementSibling();

                // Check if ulElement is found
                if (ulElement != null) {
                    // Select all li elements within the ul
                    Elements liElements = ulElement.select("li");

                    // Extract the text from each li element and add to the hardSkills list
                    ArrayList<String> hardSkills = new ArrayList<>();
                    List<String> result = new ArrayList<>();
                    for (Element liElement : liElements) {
                        result = extractMatchingWords(liElement.text().toLowerCase(), specificWords);
                        ScraperDebug.debugPrint("liElemnent= " + liElement);
                        if(!result.isEmpty() && !hardSkills.contains(result)){
                            // Display the extracted words
                            ScraperDebug.debugPrint("Extracted Words: " + result);
                            hardSkills.addAll(result);}
                    }

                    // Convert hardSkills to a Set to remove duplicates
                    Set<String> uniqueHardSkillsSet = new LinkedHashSet<>(hardSkills);

                    // Convert the Set back to a List
                    ArrayList<String> uniqueHardSkillsList = new ArrayList<>(uniqueHardSkillsSet);

                    ScraperDebug.debugPrint("Post Hard Skills (Unique): " + uniqueHardSkillsList + "...");

                    return uniqueHardSkillsList;
                }
            }

        } catch (Exception e) {
            //e.printStackTrace();
            ScraperDebug.debugPrint("Error fetching Hard Skills");
            return new ArrayList<>();
        }
        return null;
    }

    protected String fetchPostRemote(Document doc){
        try {
            String selector = "span[title=\"Télétravail\"]";
            String content = doc.selectFirst(selector).text().replace("Télétravail :", "").trim();
            ScraperDebug.debugPrint("Post remote: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post remote.");
//            e.printStackTrace();
            return "NULL";
        }
    }

    protected String fetchPostApplyDate(Document doc){
        try {
            String selector = "#fortopscroll > div.container.anno > div:nth-child(2) > div > div.col-md-12.info.blc.noback > div > div > div.col-md-12.col-sm-12.col-xs-12 > span";
            String content = doc.selectFirst(selector)
                    .text()
                    .split("-")[1]
                    .replace("Postulez avant le ", "")
                    .trim();
            ScraperDebug.debugPrint("Apply date: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching apply date");
//            e.printStackTrace();
            return "NULL";
        }
    }
    protected String fetchPostJob(Document doc){
        return "NULL";
    }

    protected String fetchPostPersonalityTraits(Document doc){
        try {
            String selector = "#fortopscroll > div.container.anno > div:nth-child(2) > div > div:nth-child(6)";
            if(! (
                    doc.selectFirst(selector) != null &&
                            doc.selectFirst(selector).selectFirst("h2") != null &&
//                    doc.selectFirst(selector).selectFirst("h2").text() != null &&
                            doc.selectFirst(selector).selectFirst("h2").text().equals("Traits de personnalité souhaités :")
            )) {
                selector = "#fortopscroll > div.container.anno > div:nth-child(2) > div > div:nth-child(7)";
            }
            Element persoTraits = doc.selectFirst(selector);
            String content = "";
            for(Element el: persoTraits.select(".tagSkills")){
                content = content + el.text().trim() + ", ";
            }
            ScraperDebug.debugPrint("Post personality traits: " + content.substring(0, 40) + "...");
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post personality traits.");
//            e.printStackTrace();
            return "NULL";
        }
    }

    protected ArrayList<String> fetchPostSoftSkills(Document doc){
        try {
            String selector = "#fortopscroll > div.container.anno > div:nth-child(2) > div > div:nth-child(6)";
            if(! (
                    doc.selectFirst(selector) != null &&
                            doc.selectFirst(selector).selectFirst("h2") != null &&
//                    doc.selectFirst(selector).selectFirst("h2").text() != null &&
                            doc.selectFirst(selector).selectFirst("h2").text().equals("Traits de personnalité souhaités :")
            )) {
                selector = "#fortopscroll > div.container.anno > div:nth-child(2) > div > div:nth-child(7)";
            }
            Element persoTraits = doc.selectFirst(selector);
            ArrayList<String> content = new ArrayList<>();
            for(Element el: persoTraits.select(".tagSkills")){
                content.add(el.text().trim());
            }
            ScraperDebug.debugPrint("Post SoftSkills: " + content.toString().substring(0, 40) + "...");
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post SoftSkills.");
//            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    protected ArrayList<String> fetchPostRecommendedSkills(Document doc){
        return new ArrayList<>();
    }

    protected String fetchPostSalary(Document doc){
        return "NULL";
    }
    protected String fetchPostSocialAdvantages(Document doc){
        return "NULL";
    }

    public void storeAllPosts() {
        ScraperDebug.debugPrint("Connecting to database...");
        try {
            Connection conn = this.dbManager.makeConnection();
            int counter = 0;
            int success = 0;
            int failed = 0;
            for (DataItem itm: this.posts) {
                counter++;
                try {
                    PreparedStatement st = itm.getInsertStatement(conn);
                    st.execute();
                    ScraperDebug.debugPrint("Insertion success, num: "+counter+".");
                    success++;
                } catch (Exception e) {
                    failed++;
                    ScraperDebug.debugPrint("Insertion Error, num: "+counter+". " + e.getMessage());
                }
                if(this.listener != null){
                    this.listener.updateCurrentStorage(counter);
                }
            }

            if(this.listener != null) {
                this.listener.finishedMysqlStorage(success, failed);
            }
            conn.close();
        } catch (Exception e) {
            ScraperDebug.debugPrint("Connection failed.");
            // e.printStackTrace();
        }
    }

    public void tempStoreAllPosts() {
        ScraperDebug.debugPrint("Connecting to database...");
        try {
            Connection conn = this.dbManager.makeConnection();
            int counter = 0;
            int success = 0;
            int failed = 0;
            for (DataItem itm: this.posts) {
                counter++;
                try {
                    PreparedStatement st = itm.getTempInsertStatement(conn);
                    st.execute();
                    ScraperDebug.debugPrint("Insertion success, num: "+counter+".");
                    success++;
                } catch (Exception e) {
                    failed++;
                    ScraperDebug.debugPrint("Insertion Error, num: "+counter+". " + e.getMessage());
                }
            }

            conn.close();
        } catch (Exception e) {
            ScraperDebug.debugPrint("Connection failed.");
        }
    }
}

