package topbloc;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Main {

	public static void main(String[] args) {
		//store student information
		HashMap<Integer, Student> students = readStudentInformationFile(
				"/home/seju/eclipse-workspace/topbloc/src/main/java/topbloc/StudentInfo.xlsx");
		try {
			//update test score for each student
			int classTestScoreSum = readStudentScoresFile(
					"/home/seju/eclipse-workspace/topbloc/src/main/java/topbloc/TestScores.xlsx", 0, students);
			// update retake test score of each student 
			int classRetakeTestScoreSum = readStudentScoresFile(
					"/home/seju/eclipse-workspace/topbloc/src/main/java/topbloc/TestRetakeScores.xlsx",
					classTestScoreSum, students);
			//calculate the class average
			double classRetakeAverage = computeClassAverage(classRetakeTestScoreSum, students.size());

			//get the list of female Computer Science Students
			List<Integer> femaleComputerScienceStudents = getFemaleComputerScienceStudents(students);
			Collections.sort(femaleComputerScienceStudents);

			// create JSON obj with all the data to post to /challenge
			JSONObject obj = new JSONObject();
			obj.put("id", "svora198@gmail.com");
			obj.put("name", "Sejal Vora");
			obj.put("average", classRetakeAverage);
			JSONArray femaleStudents = new JSONArray();
			for (Integer studentId : femaleComputerScienceStudents) {
				femaleStudents.add(Integer.toString(studentId));
			}
			obj.put("studentIds", femaleStudents);

			// post JSON to challenge 
			ApacheHttpClientPost.postToChallenge(obj.toString());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method filters for female computer science students
	 * 
	 * @param students - hashmap of all the students and their information
	 * @return a List<Integer> - a list of int student ids of female computer science students
	 */
	public static List<Integer> getFemaleComputerScienceStudents(HashMap<Integer, Student> students) {
		List<Integer> fCS = students.entrySet().stream()
				.filter(student -> student.getValue().getGender() == Student.gender.FEMALE
						&& student.getValue().getMajor().equals("computer science"))
				.collect(Collectors.mapping(HashMap.Entry::getKey, Collectors.toList()));
		return fCS;
	}

	/**
	 * This method will calculate and return the average for the test scores
	 * @param currentRunningSum - current class test score sum
	 * @param size - the number of total students
	 * @return double - class average
	 */
	public static double computeClassAverage(int currentRunningSum, int size) {
		return currentRunningSum / size;
	}

	/**
	 * This methods will update each student to have the higher test score
	 * @param fileName - String containing file path
	 * @param classRunningSum - current class test score sum
	 * @param students - map of students and their information
	 * @return int - the new class test score sum
	 */
	public static int readStudentScoresFile(String fileName, int classRunningSum, HashMap<Integer, Student> students) {
		try {
			File myFile = new File(fileName);
			FileInputStream fis;
			fis = new FileInputStream(myFile);
			XSSFWorkbook myWorkBook;
			myWorkBook = new XSSFWorkbook(fis);

			// Return first sheet from the XLSX workbook
			XSSFSheet mySheet = myWorkBook.getSheetAt(0);
			// Get iterator to all the rows in current sheet
			Iterator<Row> rowIterator = mySheet.iterator();

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next(); // For each row, iterate through each columns
				Iterator<Cell> cellIterator = row.cellIterator();
				int currStudentId = 0;
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					String header = cell.getSheet().getRow(0).getCell(cell.getColumnIndex()).getRichStringCellValue()
							.toString().trim();

					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						if (header.equals("studentId")) {
							currStudentId = (int) cell.getNumericCellValue();
						}
						if (header.equals("score")) {
							int newScore = (int) cell.getNumericCellValue();

							if (currStudentId != 0 && students.containsKey(currStudentId)) {
								int currScore = students.get(currStudentId).getTestScore();
								if (currScore < newScore) {
									// replace with new higher score
									students.get(currStudentId).setTestScore(newScore);
									classRunningSum -= Math.min(newScore, currScore);
									classRunningSum += Math.max(newScore, currScore);
								}
							}
						}
						break;
					default:
					}
				}
			}
			myWorkBook.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return classRunningSum;
	}

	/**
	 * this method will read and store the student information and return a  map of students
	 * @param fileName - String containing file path
	 * @return HashMap<Integer, Student>
	 */
	public static HashMap<Integer, Student> readStudentInformationFile(String fileName) {
		HashMap<Integer, Student> students = new HashMap<Integer, Student>();
		try {
			File myFile = new File(fileName);
			FileInputStream fis;
			fis = new FileInputStream(myFile);
			XSSFWorkbook myWorkBook;
			myWorkBook = new XSSFWorkbook(fis);

			// Return first sheet from the XLSX workbook
			XSSFSheet mySheet = myWorkBook.getSheetAt(0);
			// Get iterator to all the rows in current sheet
			Iterator<Row> rowIterator = mySheet.iterator();

			if (rowIterator.hasNext()) {
				rowIterator.next();
			}
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next(); // For each row, iterate through each columns
				Iterator<Cell> cellIterator = row.cellIterator();

				int id = 0;
				String major = "";
				Student.gender genderType = Student.gender.NULL;
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					String header = cell.getSheet().getRow(0).getCell(cell.getColumnIndex()).getRichStringCellValue()
							.toString().trim();
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_STRING:
						// System.out.print(cell.getStringCellValue() + "\t");
						if (header.equals("major")) {
							major = cell.getStringCellValue();
						}
						if (header.equals("gender")) {
							String genderString = cell.getStringCellValue();
							if (genderString.equals("F")) {
								genderType = Student.gender.FEMALE;
							} else {
								genderType = Student.gender.MALE;
							}
						}
						break;
					case Cell.CELL_TYPE_NUMERIC:
						if (header.equals("studentId")) {
							id = (int) cell.getNumericCellValue();
						}
						break;
					case Cell.CELL_TYPE_BOOLEAN:
						break;
					default:
					}
				}
				Student newStudent = new Student(id, major, genderType);
				students.put(id, newStudent);
			}
			myWorkBook.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return students;
	}
}
