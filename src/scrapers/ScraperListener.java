package scrapers;

public interface ScraperListener {
    public void updateTotalPages(int pages);
    public void updateTotalPosts(int posts);
    public void updateCurrentPost(int post, String url);
    public void updateCurrentStorage(int current);
    public void finishedMysqlStorage(int success, int failed);
}
