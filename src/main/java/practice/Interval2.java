package practice;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class Interval2 {


	// Основные переменные
	private static String fname = "EURUSD.csv"; // файл с данными
	private static  float k[][] = new float[362][4];

	// разделитель данных в файле
	private static final String separator = ";";

	// Основная программа
	public static void main(String[] args) {

		// Считаем данные из файла
		ReadCSV();

		for (int i = 0; i < 362; i++) {
			for (int j = 0; j < 4; j++) {
				System.out.print(k[i][j] + "\t");
			}
			System.out.println("");
		}
	}

	// Прочитать данные из файла в массив
	public static void ReadCSV() {
		File file = new File(fname);
		try(BufferedReader br =
				new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));) {
			String line = "";
			int i=0;
			// Считываем файл построчно  \n
			line = br.readLine();
			while ((line = br.readLine()) != null) {
				String[] elements = line.split(separator);
				k[i][0]=Float.parseFloat(elements[4]);
				k[i][1]=Float.parseFloat(elements[5]);
				k[i][2]=Float.parseFloat(elements[6]);
				k[i][3]=Float.parseFloat(elements[7]);
				i++;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
