package entityClasses;

/**
 * The LessonLearned class represents one lesson learned stored by the
 * application.
 *
 * Each lesson contains an ID, title, description, category, tags,
 * and author.
 */
public class LessonLearned {

	private int id;
	private String title;
	private String description;
	private String category;
	private String tags;
	private String author;

	/**
	 * Constructor used when creating a new lesson before the database
	 * assigns an ID.
	 */
	public LessonLearned(String title, String description,
			String category, String tags, String author) {

		this.id = -1;
		this.title = title;
		this.description = description;
		this.category = category;
		this.tags = tags;
		this.author = author;
	}

	/**
	 * Constructor used when reading an existing lesson from the database.
	 */
	public LessonLearned(int id, String title, String description,
			String category, String tags, String author) {

		this.id = id;
		this.title = title;
		this.description = description;
		this.category = category;
		this.tags = tags;
		this.author = author;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return "Lesson ID: " + id
				+ "\nTitle: " + title
				+ "\nDescription: " + description
				+ "\nCategory: " + category
				+ "\nTags: " + tags
				+ "\nAuthor: " + author;
	}
}