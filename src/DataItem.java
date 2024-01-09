import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

public class DataItem {
    private String url;
    private String siteName;
    private String publishDate;
    private String applyDate;
    private String companyName;
    private String companyAddress;
    private String companyWebsite;
    private String companyDescription;
    private String description;
    private String title;
    private String city;
    private String region;
    private String sector;
    private String job;
    private String contractType;
    private String educationLevel;
    private String diploma;
    private String experience;
    private String profileSearched;
    private String personalityTraits;
    private ArrayList<String> hardSkills;
    private ArrayList<String> softSkills;
    private ArrayList<String> recommendedSkills;
    private String language;
    private String languageLevel;
    private String salary;
    private String socialAdvantages;
    private String remote;

    public DataItem(String siteName, String url) {
        this.siteName = siteName;
        this.url = url;
        this.hardSkills = new ArrayList<>();
        this.softSkills = new ArrayList<>();
        this.recommendedSkills = new ArrayList<>();
    }

    public int getMemorySize() {
        int size = 0;
        size = 20 + this.url.length() * 2 + size;
        size = 20 + this.siteName.length() * 2 + size;
        size = 20 + this.publishDate.length() * 2 + size;
        size = 20 + this.applyDate.length() * 2 + size;
        size = 20 + this.companyName.length() * 2 + size;
        size = 20 + this.companyAddress.length() * 2 + size;
        size = 20 + this.companyWebsite.length() * 2 + size;
        size = 20 + this.companyDescription.length() * 2 + size;
        size = 20 + this.description.length() * 2 + size;
        size = 20 + this.city.length() * 2 + size;
        size = 20 + this.region.length() * 2 + size;
        size = 20 + this.sector.length() * 2 + size;
        size = 20 + this.job.length() * 2 + size;
        size = 20 + this.contractType.length() * 2 + size;
        size = 20 + this.educationLevel.length() * 2 + size;
        size = 20 + this.diploma.length() * 2 + size;
        size = 20 + this.experience.length() * 2 + size;
        size = 20 + this.profileSearched.length() * 2 + size;
        size = 20 + this.language.length() * 2 + size;
        size = 20 + this.languageLevel.length() * 2 + size;
        size = 20 + this.salary.length() * 2 + size;
        size = 20 + this.socialAdvantages.length() * 2 + size;
        size = 20 + this.remote.length() * 2 + size;

        for(String str: this.hardSkills){
            size = 20 + str.length() * 2 + size;
        }
        for(String str: this.softSkills){
            size = 20 + str.length() * 2 + size;
        }
        for(String str: this.recommendedSkills){
            size = 20 + str.length() * 2 + size;
        }
        return size;
    }

    public String getFormatedSize() {
        String ext = "bytes";
        float size = this.getMemorySize();
        if(size >= 1024) {
            ext = "KB";
            size = size / 1024;
        }
        if(size >= 1024) {
            ext = "MB";
            size = size / 1024;
        }
        if(size >= 1024) {
            ext = "GB";
            size = size / 1024;
        }
        return size + " " + ext;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(String applyDate) {
        this.applyDate = applyDate;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getDiploma() {
        return diploma;
    }

    public void setDiploma(String diploma) {
        this.diploma = diploma;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getProfileSearched() {
        return profileSearched;
    }

    public void setProfileSearched(String profileSearched) {
        this.profileSearched = profileSearched;
    }

    public String getPersonalityTraits() {
        return personalityTraits;
    }

    public void setPersonalityTraits(String personalityTraits) {
        this.personalityTraits = personalityTraits;
    }

    public ArrayList<String> getHardSkills() {
        return hardSkills;
    }

    public void setHardSkills(ArrayList<String> hardSkills) {
        this.hardSkills = hardSkills;
    }

    public ArrayList<String> getSoftSkills() {
        return softSkills;
    }

    public void setSoftSkills(ArrayList<String> softSkills) {
        this.softSkills = softSkills;
    }

    public ArrayList<String> getRecommendedSkills() {
        return recommendedSkills;
    }

    public void setRecommendedSkills(ArrayList<String> recommendedSkills) {
        this.recommendedSkills = recommendedSkills;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguageLevel() {
        return languageLevel;
    }

    public void setLanguageLevel(String languageLevel) {
        this.languageLevel = languageLevel;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getSocialAdvantages() {
        return socialAdvantages;
    }

    public void setSocialAdvantages(String socialAdvantages) {
        this.socialAdvantages = socialAdvantages;
    }

    public String getRemote() {
        return remote;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    @Override
    public String toString() {
        return "DataItem{" +
                "siteName='" + siteName + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    public PreparedStatement getInsertStatement(Connection conn) throws java.sql.SQLException {
        String query = "INSERT INTO jobs(" +
                "site_name, url, publish_date, apply_date, company_name, " +
                "company_address, company_website, company_description, " +
                "description, title, city, region, sector, job, contract_type, education_level, diploma, experience, profile_searched," +
                "personality_traits, hard_skills, soft_skills, recommended_skills," +
                "lang, lang_level, salary, social_advantages, remote" +
                ") VALUES(" +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? " +
                ");";
        PreparedStatement st = conn.prepareStatement(query);
        st.setString(1, this.siteName.replace("'", " "));
        st.setString(2, this.url.replace("'", " "));
        st.setString(3, this.publishDate.replace("'", " "));
        st.setString(4, this.applyDate.replace("'", " "));
        st.setString(5, this.companyName.replace("'", " "));
        st.setString(6, this.companyAddress.replace("'", " "));
        st.setString(7, this.companyWebsite.replace("'", " "));
        st.setString(8, this.companyDescription.replace("'", " "));
        st.setString(9, this.description.replace("'", " "));
        st.setString(10, this.title.replace("'", " "));
        st.setString(11, this.city.replace("'", " "));
        st.setString(12, this.region.replace("'", " "));
        st.setString(13, this.sector.replace("'", " "));
        st.setString(14, this.job.replace("'", " "));
        st.setString(15, this.contractType.replace("'", " "));
        st.setString(16, this.educationLevel.replace("'", " "));
        st.setString(17, this.diploma.replace("'", " "));
        st.setString(18, this.experience.replace("'", " "));
        st.setString(19, this.profileSearched.replace("'", " "));
        st.setString(20, this.personalityTraits.replace("'", " "));
        st.setString(21, String.join(", ", this.hardSkills).replace("'", " "));
        st.setString(22, String.join(", ", this.softSkills).replace("'", " "));
        st.setString(23, String.join(", ", this.recommendedSkills));
        st.setString(24, this.language.replace("'", " "));
        st.setString(25, this.languageLevel.replace("'", " "));
        st.setString(26, this.salary.replace("'", " "));
        st.setString(27, this.socialAdvantages.replace("'", " "));
        st.setString(28, this.remote.replace("'", " "));
        return st;
    }

    public PreparedStatement getTempInsertStatement(Connection conn) throws java.sql.SQLException {
        String query = "INSERT INTO jobstemp(" +
                "site_name, url, publish_date, apply_date, company_name, " +
                "company_address, company_website, company_description, " +
                "description, title, city, region, sector, job, contract_type, education_level, diploma, experience, profile_searched," +
                "personality_traits, hard_skills, soft_skills, recommended_skills," +
                "lang, lang_level, salary, social_advantages, remote" +
                ") VALUES(" +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? " +
                ");";
        PreparedStatement st = conn.prepareStatement(query);
        st.setString(1, this.siteName.replace("'", " "));
        st.setString(2, this.url.replace("'", " "));
        st.setString(3, this.publishDate.replace("'", " "));
        st.setString(4, this.applyDate.replace("'", " "));
        st.setString(5, this.companyName.replace("'", " "));
        st.setString(6, this.companyAddress.replace("'", " "));
        st.setString(7, this.companyWebsite.replace("'", " "));
        st.setString(8, this.companyDescription.replace("'", " "));
        st.setString(9, this.description.replace("'", " "));
        st.setString(10, this.title.replace("'", " "));
        st.setString(11, this.city.replace("'", " "));
        st.setString(12, this.region.replace("'", " "));
        st.setString(13, this.sector.replace("'", " "));
        st.setString(14, this.job.replace("'", " "));
        st.setString(15, this.contractType.replace("'", " "));
        st.setString(16, this.educationLevel.replace("'", " "));
        st.setString(17, this.diploma.replace("'", " "));
        st.setString(18, this.experience.replace("'", " "));
        st.setString(19, this.profileSearched.replace("'", " "));
        st.setString(20, this.personalityTraits.replace("'", " "));
        st.setString(21, String.join(", ", this.hardSkills).replace("'", " "));
        st.setString(22, String.join(", ", this.softSkills).replace("'", " "));
        st.setString(23, String.join(", ", this.recommendedSkills));
        st.setString(24, this.language.replace("'", " "));
        st.setString(25, this.languageLevel.replace("'", " "));
        st.setString(26, this.salary.replace("'", " "));
        st.setString(27, this.socialAdvantages.replace("'", " "));
        st.setString(28, this.remote.replace("'", " "));
        return st;
    }
}

