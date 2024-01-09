package scrapers;

import db.DBManager;
import debug.ScraperDebug;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;


public class EmploiScraper {
    protected DBManager dbManager = DBManager.getInstance();
    protected String siteName = "Emploi";
    protected String baseUrl = "https://www.emploi.ma";
    protected String pagesUrlFormat = "https://www.emploi.ma/recherche-jobs-maroc?page={X}";
    protected int pagesNumber;
    protected ArrayList<String> pagesUrl = new ArrayList<>();
    protected  ArrayList<String> postsUrl = new ArrayList<>();
    protected ArrayList<DataItem> posts = new ArrayList<>();
    protected int maxPageToScrape = 20; // change back to 4 // or maybe make it 30
    public static final int maxPostsToScrape = 500; // change to 1000 later on
    protected ScraperListener listener = null;

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
            Document doc = Jsoup.connect("https://www.emploi.ma/recherche-jobs-maroc").get();
            String selector = "a[title=\"Aller à la dernière page\"]";

            Element page = doc.selectFirst(selector);
            assert page != null;
            String output = page.text();
            return Integer.parseInt(output);
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
            String postTitleSelector = ".job-description-wrapper";
            Elements postsTitles = pageDoc.select(postTitleSelector);

            ScraperDebug.debugPrint("Number of posts found: " + postsTitles.size());
            for(Element title: postsTitles) {
                this.postsUrl.add(title.attr("data-href"));
                ScraperDebug.debugPrint("Post Url: " + title.attr("data-href"));
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
    }
    
    public void fetchAllPostsAttributes() {
        int max = maxPostsToScrape;
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
            String selector = "h1.title";
            String content = doc.selectFirst(selector).text();
            ScraperDebug.debugPrint("Post title: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching title");
            return "Not specified";
        }
    }

    protected String fetchPostPublishDate(Document doc){
        try {
            String selector = "div.job-ad-publication-date";
            String content = doc.selectFirst(selector)
                    .text()
                    .replace("Publiée le ", "")
                    .trim();
            ScraperDebug.debugPrint("Publish date: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching publish date");
//            e.printStackTrace();
            return "";
        }
    }
    protected String fetchPostCompanyName(Document doc){
        try {
            String selector = ".job-ad-company .company-title";
            String content = doc.selectFirst(selector).text();
            ScraperDebug.debugPrint("Company name: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching company name");
            return "";
        }
    }

    protected String fetchPostCompanyAddress(Document doc){
        try {
            String selector = "div.job-ad-company-description > a";
            String content = doc.selectFirst(selector).attr("href");
            doc = Jsoup.connect(this.baseUrl + content).get();
            Elements details = doc.select("#company-profile-details > tbody > tr");
            String country = "";
            String city = "";
            for(Element d: details) {
                if(d.selectFirst("#company-profile-city") != null){
                    city = d.selectFirst("td:nth-child(3)").text();
                }
                if(d.selectFirst("#company-profile-country") != null){
                    country = d.selectFirst("td:nth-child(3)").text();
                }
            }
            content = country + ", " + city;
            ScraperDebug.debugPrint("Company address: " + content);
            return content;
        }catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching company address");
            // e.printStackTrace();
            return "";
        }
    }
    protected String fetchPostCompanyWebsite(Document doc) {
        try {
            String selector = ".website-url > a";
            String content = doc.selectFirst(selector).attr("href");
            ScraperDebug.debugPrint("Company website: " + content);
            return content;
        }catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching company website");
            return "";
        }
    }
    protected String fetchPostCompanyDescription(Document doc){
        try {
            String selector = "div.job-ad-company-description > a";
            String content = doc.selectFirst(selector).attr("href");
            doc = Jsoup.connect(this.baseUrl + content).get();
            content = doc.selectFirst(".company-profile-description").text().substring(27).trim();
            ScraperDebug.debugPrint("Company description: " + content);
            return content;
        }catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching company description");
            // e.printStackTrace();
            return "";
        }
    }

    protected String fetchPostDescription(Document doc){
        try {
            // gotta remove the title from the description
            int titleLength = doc.select(".jobs-ad-details > .inner > .content > div:nth-child(1) > .ad-ss-title").text().length();
            String selector = ".jobs-ad-details > div > div > div:nth-child(1)";
            String content = doc.select(selector).text().substring(titleLength);
            ScraperDebug.debugPrint("Post description: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post description.");
            // e.printStackTrace();
            return "";
        }
    }

    protected String fetchPostCity(Document doc){
        try {
            String selector = ".job-ad-criteria tr";
            Elements els = doc.select(selector);
            String content = "";
            for(Element el: els) {
                if(el.selectFirst("td").text().trim().equals("Ville :")) {
                    content = el.select("td").get(1).text().trim().split(" ")[0];
                    break;
                }
            }
            ScraperDebug.debugPrint("Post city: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post city.");
            // e.printStackTrace();
            return "";
        }
    }

    protected String fetchPostRegion(Document doc){
        try {
            String selector = ".job-ad-criteria tr";
            Elements els = doc.select(selector);
            String content = "";
            for(Element el: els) {
                if(el.selectFirst("td").text().trim().equals("Région :")) {
                    content = el.select("td").get(1).text().trim();
                    break;
                }
            }
            ScraperDebug.debugPrint("Post region: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post region.");
//            e.printStackTrace();
            return "";
        }
    }

    protected String fetchPostSector(Document doc){
        try {
            String selector = ".job-ad-criteria tr";
            Elements els = doc.select(selector);
            String content = "";
            for(Element el: els) {
                if(el.selectFirst("td").text().trim().equals("Secteur d´activité :")) {
                    content = el.select("td").get(1).text().trim();
                    break;
                }
            }
            ScraperDebug.debugPrint("Post sector: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post sector.");
//            e.printStackTrace();
            return "";
        }
    }
    protected String fetchPostContractType(Document doc){
        try {
            String selector = ".job-ad-criteria tr";
            Elements els = doc.select(selector);
            String content = "";
            for(Element el: els) {
                if(el.selectFirst("td").text().trim().equals("Type de contrat :")) {
                    content = el.select("td").get(1).text().trim().split(" ")[0];
                    break;
                }
            }
            ScraperDebug.debugPrint("Post contract: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post contract.");
//            e.printStackTrace();
            return "";
        }
    }

    protected String fetchPostEducationLevel(Document doc){
        try {
            String selector = ".job-ad-criteria tr";
            Elements els = doc.select(selector);
            String content = "";
            for(Element el: els) {
                if(el.selectFirst("td").text().trim().equals("Niveau d'études :")) {
                    content = el.select("td").get(1).text().trim();
                    break;
                }
            }
            ScraperDebug.debugPrint("Education level: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post education level.");
//            e.printStackTrace();
            return "";
        }
    }

    protected String fetchPostDiploma(Document doc){
        try {
            return "";
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post diploma.");
//            e.printStackTrace();
            return "";
        }
    }

    protected String fetchPostExperience(Document doc){
        try {
            String selector = ".job-ad-criteria tr";
            Elements els = doc.select(selector);
            String content = "";
            for(Element el: els) {
                if(el.selectFirst("td").text().trim().equals("Niveau d'expérience :")) {
                    content = el.select("td").get(1).text().trim();
                    break;
                }
            }
            ScraperDebug.debugPrint("Post Experiences: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post experience.");
//            e.printStackTrace();
            return "";
        }
    }

    protected String fetchPostProfileSearched(Document doc){
        try {
            // gotta remove the title from the description
            int titleLength = doc.select(".jobs-ad-details > div > div > div:nth-child(3) > .ad-ss-title").text().length();
            String selector = ".jobs-ad-details > div > div > div:nth-child(3)";
            String content = doc.select(selector).text().substring(titleLength);
            ScraperDebug.debugPrint("Post searched profile: " + content + "...");
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post searched profile.");
//            e.printStackTrace();
            return "";
        }
    }

    protected String fetchPostLanguage(Document doc){
        try {
            String selector = ".job-ad-criteria tr";
            Elements els = doc.select(selector);
            String content = "";
            for(Element el: els) {
                if(el.selectFirst("td").text().trim().equals("Langues exigées :")) {
                    content = el.select("td").get(1).selectFirst("span").text().trim();
                    break;
                }
            }
            ScraperDebug.debugPrint("Language: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post education level.");
//            e.printStackTrace();
            return "";
        }
    }
    protected String fetchPostLanguageLevel(Document doc){
        try {
            String selector = ".job-ad-criteria tr";
            Elements els = doc.select(selector);
            String content = "";
            for(Element el: els) {
                if(el.selectFirst("td").text().trim().equals("Langues exigées :")) {
                    content = el.select("td").get(1).select("span").get(2).text().trim();
                    break;
                }
            }
            ScraperDebug.debugPrint("Language Level: " + content);
            return content;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching post education level.");
//            e.printStackTrace();
            return "";
        }
    }

    protected ArrayList<String> fetchPostHardSkills(Document doc){
        try{
            String selector = ".job-ad-criteria tr";
            Elements els = doc.select(selector);
            ArrayList<String> hardSkills = new ArrayList<>();
            for(Element el: els) {
                if(el.selectFirst("td").text().trim().equals("Compétences clés :")) {
                    Elements skills = el.select(".last-cell > div > div > .field-item");
                    for (Element elt: skills) {
                        hardSkills.add(elt.text());
                    }
                    break;
                }
            }
            ScraperDebug.debugPrint("Post Hard Skills: " + hardSkills.toString());
            return hardSkills;
        } catch (Exception e) {
            ScraperDebug.debugPrint("Error fetching Hard Skills");
            return new ArrayList<>();
        }
    }

    protected String fetchPostRemote(Document doc){

        ScraperDebug.debugPrint("Post Remote: Not specified");

        return "Non";
    }

    protected String fetchPostApplyDate(Document doc){
        return "";
    }

    protected String fetchPostJob(Document doc){
        return "";
    }

    protected String fetchPostPersonalityTraits(Document doc){
        return "";
    }

    protected ArrayList<String> fetchPostSoftSkills(Document doc){
        return new ArrayList<>();
    }

    protected ArrayList<String> fetchPostRecommendedSkills(Document doc){
        return new ArrayList<>();
    }

    protected String fetchPostSalary(Document doc){
        return "";
    }
    protected String fetchPostSocialAdvantages(Document doc){
        return "";
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
            // e.printStackTrace();
        }
    }

    public ArrayList<DataItem> getPosts() {
        return posts;
    }

    public int getPagesNumber() {
        return pagesNumber;
    }

    public ArrayList<String> getPagesUrl() {
        return pagesUrl;
    }

    public ArrayList<String> getPostsUrl() {
        return postsUrl;
    }
}
