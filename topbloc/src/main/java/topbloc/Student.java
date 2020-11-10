package topbloc;

public class Student {

	static enum gender {
		NULL, MALE, FEMALE
	}

	private int id; // student id
	private String major; // student major
	private gender genderType; // student gender
	private int testScore; //highest student test score

	// args constructor
	Student(int id, String major, gender genderType) {
		this.id = id;
		this.testScore = 0; // init to 0 b/c do not have test score upon creation of new Student
		this.major = major;
		this.genderType = genderType;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getMajor() {
		return this.major;
	}

	public void setGender(gender genderType) {
		this.genderType = genderType;
	}

	public gender getGender() {
		return this.genderType;
	}

	public void setTestScore(int testScore) {
		this.testScore = testScore;
	}

	public int getTestScore() {
		return this.testScore;
	}
}
