package vo;

public class SearchBucketVO {
	private int bucket_id;
	private int selectedbucket_id; 
	private String image;
	private String title;
	private String searchKeyword;
	private String searchTag;
	private int start;
    private int last;
    
	public int getBucket_id() {
		return bucket_id;
	}
	public void setBucket_id(int bucket_id) {
		this.bucket_id = bucket_id;
	}
	public int getSelectedbucket_id() {
		return selectedbucket_id;
	}
	public void setSelectedbucket_id(int selectedbucket_id) {
		this.selectedbucket_id = selectedbucket_id;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSearchKeyword() {
		return searchKeyword;
	}
	public void setSearchKeyword(String searchKeyword) {
		this.searchKeyword = searchKeyword;
	}
	public String getSearchTag() {
		return searchTag;
	}
	public void setSearchTag(String searchTag) {
		this.searchTag = searchTag;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getLast() {
		return last;
	}
	public void setLast(int last) {
		this.last = last;
	}
	@Override
	public String toString() {
		return "SearchBucketVO [bucket_id=" + bucket_id + ", selectedbucket_id=" + selectedbucket_id + ", image="
				+ image + ", title=" + title + ", searchKeyword=" + searchKeyword + ", searchTag=" + searchTag
				+ ", start=" + start + ", last=" + last + "]";
	}
}
