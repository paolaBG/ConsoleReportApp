package project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;

public class Solution {

	static String dataFileUrl;
	static String reportFileUrl;
	static DataModel dataArray = new DataModel();
	static List<DataModel> list = new ArrayList<DataModel>();
	static ReportModel report = new ReportModel();

	static Gson gson = new Gson();

	public static void main(String[] args) throws Exception {

		System.out.println("Please enter path to data.json file");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		dataFileUrl = reader.readLine();

		System.out.println("Please enter path to report.json file");
		BufferedReader reader1 = new BufferedReader(new InputStreamReader(System.in));
		reportFileUrl = reader.readLine();

		parseDataObject();
		parseReportDefinitionObject();
		writeToFile();
	}

	private static void parseDataObject() throws Exception {
		Object obj = new JSONParser().parse(new FileReader(dataFileUrl));
		JSONObject data = (JSONObject) obj;
		JSONArray array = (JSONArray) data.get("Data");
		array.forEach(emp -> parseEmployeeObject((JSONObject) emp));
	}

	private static void parseReportDefinitionObject() throws Exception {
		Object obj = new JSONParser().parse(new FileReader(reportFileUrl));
		JSONObject data = (JSONObject) obj;
		JSONObject object = (JSONObject) data.get("Report definition");
		report = gson.fromJson(object.toString(), ReportModel.class);
	}

	private static void writeToFile() {
		try {
			FileWriter myWriter = new FileWriter("report_results.txt");
			myWriter.write("Result");
			myWriter.write(System.getProperty("line.separator"));
			myWriter.write("Name, Score");
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).salesPeriod <= report.periodLimit) {
					int score = Score(list.get(i));
					myWriter.write(System.getProperty("line.separator"));
					myWriter.write(list.get(i).name + ", " + score);
				}
			}

			myWriter.close();
			System.out.println("Successfully wrote to the file! Please check file the report_results");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	private static int Score(DataModel item) {
		int score;
		if (report.useExperienceMultiplier) {
			score = (int) (item.totalSales / (item.salesPeriod * item.experienceMultiplier));
		} else {
			score = (int) (item.totalSales / item.salesPeriod);
		}
		return score;
	}

	private static void parseEmployeeObject(JSONObject employee) {
		DataModel dataArray = new DataModel();
		
		dataArray.name = (String) employee.get("name");

		dataArray.totalSales = (long) employee.get("totalSales");

		dataArray.salesPeriod = (long) employee.get("salesPeriod");

		dataArray.experienceMultiplier = (double) employee.get("experienceMultiplier");

		list.add(dataArray);
	}
}
