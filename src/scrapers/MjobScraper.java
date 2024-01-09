package scrapers;

import db.DBManager;
import debug.ScraperDebug;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

public class MjobScraper {
    protected DBManager dbManager = DBManager.getInstance();
    protected String siteName = "Job-m";
    protected String baseUrl = "https://www.m-job.ma/recherche";
    protected String pagesUrlFormat = "https://www.m-job.ma/recherche?page=";
    protected int pagesNumber;
    protected ArrayList<String> pagesUrl = new ArrayList<>();
    protected  ArrayList<String> postsUrl = new ArrayList<>();
    protected ArrayList<String> offerTitles = new ArrayList<>();
    protected ArrayList<String> offerUrls = new ArrayList<>();

    protected ArrayList<DataItem> posts = new ArrayList<>();
    protected int maxPageToScrape = 1; // change back to 4 // or maybe make it 30
    protected int maxPostsToScrape = 10; // change to 1000 later on
    protected ScraperListener listener = null;
    public static String scrapeTitle(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            return document.title();
        } catch (IOException e) {
           // e.printStackTrace();
            return null;
        }
    }
    public String toString() {
        return "BaseScraper {baseurl: "+ this.baseUrl +", pagesUrlFormat: "+this.pagesUrlFormat+"}";
    }

    public void setListener(ScraperListener listener) {
        this.listener = listener;
    }

    public void fetchPageNumber() {
        int nbr = this.scrapeTotalPages(pagesUrlFormat);
        this.pagesNumber = nbr;
        if(this.listener != null) {
            this.listener.updateTotalPages(nbr);
        }
        ScraperDebug.debugPrint("Number of pages found: " + nbr);
    }
    public int scrapeTotalPages(String baseUrl) {
        int totalPages = -1;
        try {
            int currentPage = 1;

            while (true) {
                String url = baseUrl + currentPage;
                Document document = Jsoup.connect(url).get();
                Element paginationUl = document.selectFirst("ul.pagination");
                Element nextPageElement = paginationUl.selectFirst("li.active + li:not(.disabled)");
                if (nextPageElement == null) {
                    break;
                }
                currentPage++;
            }
            totalPages = currentPage;
        } catch (IOException e) {
            // e.printStackTrace();
        }
        return totalPages;
    }

    public void fetchPagesUrls() {
        ScraperDebug.debugPrint("Generating pages URls...");
        for (int i = 1; i < this.pagesNumber+1; i++) {
            this.pagesUrl.add(this.pagesUrlFormat + String.valueOf(i));

            if(i < 5) {
                ScraperDebug.debugPrint(this.pagesUrlFormat+i);
            }
            if(i == 5) {
                ScraperDebug.debugPrint("...");
            }
        }
    }

    protected void fetchAllPostsFromPage(String pageUrl) {
        try {
            Document pageDoc = Jsoup.connect(pageUrl).get();
            String offerBoxSelector = ".offer-box";  // Update with your actual selector
            Elements offerBoxes = pageDoc.select(offerBoxSelector);
            ScraperDebug.debugPrint("Number of job offers found: " + offerBoxes.size());

            for (Element offerBox : offerBoxes) {
                // Extract offer title
                String title = offerBox.select(".offer-title a").text();
                // Extract offer URL
                this.postsUrl.add(offerBox.select(".offer-title a").attr("href"));
                this.offerTitles.add(offerBox.select(".offer-title a").text());
                ScraperDebug.debugPrint(offerBox.select(".offer-title a").attr("href"));
                ScraperDebug.debugPrint(offerBox.select(".offer-title a").text());


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
    public DataItem fetchAttributesFromPost(String postUrl) {
        DataItem item = new DataItem(this.siteName, postUrl);
        Document document;
        Elements mainDetails;
        Element doc;
        try {
            document = Jsoup.connect(postUrl).get();
            mainDetails = document.select(".main-details");
            doc=mainDetails.first();


        } catch (Exception e) {
            ScraperDebug.debugPrint(e.getMessage());
            ScraperDebug.debugPrint("Error loading page. Skipping...");
            return null;
        }
        item.setTitle(extractJobTitle(doc).toLowerCase());
        item.setPublishDate(extractPublicationDate(doc).toLowerCase());
        item.setApplyDate(fetchPostApplyDate(document).toLowerCase());
        item.setCompanyName(extractCompany(doc).toLowerCase());
        item.setCompanyAddress(fetchPostCompanyAddress(document).toLowerCase());
        item.setCompanyWebsite(fetchPostCompanyWebsite(document).toLowerCase());
        item.setCompanyDescription(extractinfoCompany(doc).toLowerCase());
        item.setDescription(extractJD(doc).toLowerCase());
        item.setCity(extractLocation(doc).toLowerCase());
        item.setRegion(extractLocation(doc).toLowerCase());
        item.setSector(extractSector(doc).toLowerCase());
        item.setJob(extractProfession(doc).toLowerCase());
        item.setContractType(extractContractType(doc).toLowerCase());
        item.setEducationLevel(extractEducationLevel(doc).toLowerCase());
        item.setDiploma(extractEducationLevel(doc).toLowerCase());
        item.setExperience(extractExperienceLevel(doc).toLowerCase());
        item.setProfileSearched(extractProfile(doc).toLowerCase());
        item.setPersonalityTraits(this.fetchPostPersonalityTraits(document).toLowerCase());
        item.setHardSkills(this.fetchPostHardSkills(document));
        item.setSoftSkills(this.fetchPostSoftSkills(document));
        item.setRecommendedSkills(this.fetchPostRecommendedSkills(document));
        item.setLanguage(extractRequiredLanguages(doc).toLowerCase());
        item.setLanguageLevel(this.fetchPostLanguageLevel(document).toLowerCase());
        item.setSalary(extractSalary(doc).toLowerCase());
        item.setSocialAdvantages(this.fetchPostSocialAdvantages(document).toLowerCase());
        item.setRemote(this.fetchPostRemote(document).toLowerCase());

        ScraperDebug.debugPrint("Total Size: " + item.getFormatedSize());
        return item;
    }
    protected String fetchPostPersonalityTraits(Document doc) {
        return "";
    }
    protected String fetchPostCompanyAddress(Document doc) {
        return "";
    }
    protected String fetchPostCompanyWebsite(Document doc) {
        return "";
    }

    protected ArrayList<String> fetchPostHardSkills(Document doc) {
        return new ArrayList<>();    }
    protected String fetchPostApplyDate(Document doc) {
        return "";
    }
    protected ArrayList<String>  fetchPostSoftSkills(Document doc) {
        return new ArrayList<>();
    }
    protected ArrayList<String> fetchPostRecommendedSkills(Document doc) {
        return new ArrayList<>();
    }
    protected String fetchPostLanguageLevel(Document doc) {
        return "";
    }
    protected String fetchPostSocialAdvantages(Document doc) {
        return "";
    }
    protected String fetchPostRemote(Document doc) {
        return "";
    }
    protected static String extractJobTitle(Element mainDetails) {
        Document document = Jsoup.parse(String.valueOf(mainDetails));
        Element jobTitleElement = document.select(".offer-title").first();
        return jobTitleElement != null ? jobTitleElement.text().trim() : "";
    }
    protected static String extractLocation(Element mainDetails) {
        Document document = Jsoup.parse(String.valueOf(mainDetails));
        Element locationElement = document.select(".location span").first();
        return locationElement != null ? locationElement.text().trim() : "";
    }
    protected static String extractCompany(Element mainDetails) {
        Document document = Jsoup.parse(String.valueOf(mainDetails));
        Element companyElement = document.select(".list-details li:contains(Société) h3").first();
        return companyElement != null ? companyElement.text().trim() : "";
    }
    protected static String extractProfile(Element mainDetails) {
        Document document = Jsoup.parse(String.valueOf(mainDetails));
        Element companyElement = document.select(".the-content .heading:contains(Profil) + div").first();
        return companyElement != null ? companyElement.text().trim() : "";
    }

    protected static String extractJD(Element mainDetails) {
        Document document = Jsoup.parse(String.valueOf(mainDetails));
        Element companyElement = document.select(".the-content .heading:contains(poste) + div").first();
        return companyElement != null ? companyElement.text().trim() : "";
    }
    protected static String extractinfoCompany(Element mainDetails) {
        Document document = Jsoup.parse(String.valueOf(mainDetails));
        Element companyElement = document.select(".the-content .heading:contains(le recruteur) + div").first();
        return companyElement != null ? companyElement.text().trim() : "";
    }

    protected static String extractContractType(Element mainDetails) {
        Document document = Jsoup.parse(String.valueOf(mainDetails));
        Element contractTypeElement = document.select(".list-details li:contains(Type de contrat) h3").first();
        return contractTypeElement != null ? contractTypeElement.text().trim() : "";
    }

    protected static String extractSalary(Element mainDetails) {
        Document document = Jsoup.parse(String.valueOf(mainDetails));
        Element salaryElement = document.select(".list-details li:contains(Salaire) h3").first();
        return salaryElement != null ? salaryElement.text().trim() : "";
    }
    protected static String extractSector(Element mainDetails) {
        Document document = Jsoup.parse(String.valueOf(mainDetails));
        Element sectorElement = document.select(".the-content h3:contains(activité) + div").first();
        return sectorElement != null ? sectorElement.text().trim() : "";
    }

    protected static String extractProfession(Element mainDetails) {
        Document document = Jsoup.parse(String.valueOf(mainDetails));
        Element professionElement = document.select(".the-content h3:contains(Métier) + div").first();
        return professionElement != null ? professionElement.text().trim() : "";
    }

    protected static String extractExperienceLevel(Element mainDetails) {
        Document document = Jsoup.parse(String.valueOf(mainDetails));
        Element experienceElement = document.select(".the-content .heading:contains(requis) + div").first();
        return experienceElement != null ? experienceElement.text().trim() : "";
    }

    protected static String extractEducationLevel(Element mainDetails) {
        Document document = Jsoup.parse(String.valueOf(mainDetails));
        Element educationElement = document.select(".the-content h3:contains(exigé) + div").first();
        return educationElement != null ? educationElement.text().trim() : "";
    }

    protected static String extractRequiredLanguages(Element mainDetails) {
        Document document = Jsoup.parse(String.valueOf(mainDetails));
        Element languagesElement = document.select(".the-content h3:contains(Langue(s) exigée(s)) + div").first();
        return languagesElement != null ? languagesElement.text().trim() : "";
    }

    protected static String extractPublicationDate(Element mainDetails) {
        Document document = Jsoup.parse(String.valueOf(mainDetails));
        Element bottomContent = document.selectFirst(".bottom-content span:contains(été publiée)");

        if (bottomContent != null) {
            String dateText = bottomContent.text();

            // Extract the part after "il y a" and before "avant"
            int startIndex = dateText.indexOf("L'offre a été publiée") + "L'offre a été publiée".length();
            int endIndex = dateText.indexOf("avant");

            if (startIndex >= 0 && endIndex >= 0) {
                String relativeTime = dateText.substring(startIndex, endIndex).trim();
                return relativeTime;
            }
        }

        return "";
    }
    public void storeAllPosts() {
        ScraperDebug.debugPrint("Connecting to database...");
        try {
            Connection conn = this.dbManager.makeConnection();
            int counter = 0;
            int success = 0;
            int failed = 0;
            for (DataItem itm : this.posts) {
                counter++;
                try {
                    PreparedStatement st = itm.getInsertStatement(conn);
                    st.execute();
                    ScraperDebug.debugPrint("Insertion success, num: " + counter + ".");
                    success++;
                } catch (Exception e) {
                    failed++;
                    ScraperDebug.debugPrint("Insertion Error, num: " + counter + ". " + e.getMessage());
                }
                if (this.listener != null) {
                    this.listener.updateCurrentStorage(counter);
                }
            }

            if (this.listener != null) {
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


    public int getPagesNumber() {
        return pagesNumber;
    }

    public ArrayList<String> getPagesUrl() {
        return pagesUrl;
    }

    public ArrayList<String> getPostsUrl() {
        return postsUrl;
    }

    public ArrayList<DataItem> getPosts() {
        return posts;
    }
}



