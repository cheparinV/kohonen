package network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
//многослойный персептрон с тремя скрытыми слоями
public class Network {
    private static final String separator = ";";
    public static int s, r=6;
    public static double[][] x;
    // массив входных данных
    public static void main(String[] args) {
        //ОБРАЩЕНИЕ к методу заполнения массива
        ReadCSV();

        // объём входных данных
        int n = s;
        // количество обясняющих переменных
        int m = r-1;
        // количество нейронов на первом скрытом слое
        int n1 = 10;

        // массив нейронов первого скрытого слоя
        double[] h1 = new double[n1];
        // массив весов между входным и первым скрытым слоем
        double[][] wxh1 = new double[n1][m];
        init(n1, m, wxh1);

        // количество нейронов на втором скрытом слое
        int n2 = 10;
        // массив нейронов второго скрытого слоя
        double[] h2 = new double[n2];
        // массив весов между  первым и вторым скрытыми слоями
        double[][] wh1h2 = new double[n2][n1];
        init(n2, n1, wh1h2);
        // количество нейронов на третьем скрытом слое
        int n3 = 2;
        // массив нейронов третьего скрытого слоя
        double[] h3 = new double[n2];
        // массив весов между  вторым и третьим скрытыми слоями
        double[][] wh2h3 = new double[n3][n2];
        init(n3, n2, wh2h3);

        for (int ii = 0; ii < 1; ii++) {

            for (int i = 0; i < x.length; i++) {
                final double[] input = Network.x[i];
                double[] target = new double[2];
                final int y = ((int) input[input.length - 1]);
                target[y] = y;
                h1 = predictLayer(input, wxh1);
                h2 = predictLayer(h1, wh1h2);
                h3 = predictLayer(h2, wh2h3);

                final double[] error = error(target, h3);


                System.out.println();
//                double error = input[input.length - 1] - h1[0];
//                System.out.println(error);
//                for (int i1 = 0; i1 < wxh1[0].length; i1++) {
//                    wxh1[0][i1] += 0.1 * error * input[i1];
//                }
            }
        }
        System.out.println(h1);


        // выходное значение сети
        double y;
    }

    private static double[] predictLayer(double[] input, double[][] weights) {
        double[] neurons = new double[weights.length];
        for (int i = 0; i < weights.length; i++) {
            final double[] weight = weights[i];
            neurons[i] = activation(sum(weight.length, input, weight));
        }
        return neurons;
    }

    private static double[] error(double[] target, double[] output) {
        final double[] error = new double[target.length];
        for (int i = 0; i < target.length; i++) {
            error[i] = target[i] - output[i];
        }
        return error;
    }

    private static double[] backLayer(double[] error, double[][] weights, double[] neurons) {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                weights[i][j] += 0.1 * error[j] * neurons[i];
            }
        }
        return null;
    }



    //МЕТОДЫ
    public static void ReadCSV() {
        // чтение из файла формата CSV входных данных и их запись в двумерный массив
        Scanner sc = new Scanner(System.in);
        System.out.println("Введите навзвание файла с успешными студентами");
        String l1= "/Users/cheparinv/Downloads/phys5.csv";//sc.nextLine();
        s=amountOfLines(l1);
        x = new double[2*s+1][r];
        System.out.println("Введите навзвание файла с неуспешными студентами");
        String l2= "/Users/cheparinv/Downloads/phys3.csv";//sc.nextLine();
        String f5name = l1;

        File file5 = new File(f5name);
        System.out.println(file5);
        try(BufferedReader br =
                new BufferedReader(new InputStreamReader(new FileInputStream(file5), "UTF-8"));) {
            String line = "";
            int i=0;
            // Считываем файл построчно  \n
            while ((line = br.readLine()) != null) {

                String[] elements = line.split(separator);

                x[2*i][0]=Double.parseDouble(elements[0])/100;
                x[2*i][1]=Double.parseDouble(elements[1])/100;
                x[2*i][2]=Double.parseDouble(elements[2])/100;
                x[2*i][3]=Double.parseDouble(elements[3])/100;
                x[2*i][4]=Double.parseDouble(elements[4])/100;
                x[2*i][5]=1;
                //System.out.println(x[2*i][1]);
                i++;
                System.out.println(i);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file3 = new File(l2);
        try(BufferedReader br =
                new BufferedReader(new InputStreamReader(new FileInputStream(file3), "UTF-8"));) {
            String line1 = "";
            int i=0;
            // Считываем файл построчно  \n

            while ((line1 = br.readLine()) != null && i < s) {

                String[] elements = line1.split(separator);
                x[2*i+1][0]=Double.parseDouble(elements[0])/100;
                x[2*i+1][1]=Double.parseDouble(elements[1])/100;
                x[2*i+1][2]=Double.parseDouble(elements[2])/100;
                x[2*i+1][3]=Double.parseDouble(elements[3])/100;
                x[2*i+1][4]=Double.parseDouble(elements[4])/100;
                x[2*i+1][5]=0;

                //System.out.println(x[2*i+1][1]);
                i++;
                System.out.println(i);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void init(int k, int l, double[][] w) {
        // инициализация начальных значений весов
        for (int j = 0; j < l; j++) {
            for (int i = 0; i < k; i++) {
                w[i][j] = Math.random() * 0.2 + 0.1;
            }
        }
    }
    public static void answer() {
        // ответ сети
    }
    public static void test() {
        // использование обученной сети на тестовых примерах
    }
    public static void training() {
        // обучение, пересчёт весов
    }
    public static double activation(int i, double[] x, double[] w) {
        // функция активации
        int y;
        if (sum(i,x,w)> 0.5)
            y = 1;
        else
            y = 0;
        return y;
    }

    public static double activation(double x) {
        if (x > 0.5) {
            return 1;
        } else {
            return 0;
        }
     }

    public static double derivate(int i, double[] x, double[] w) {
        return sum(i, x, w);
    }

    public static double sum(int i, double[] x,double[] w) {
        // сумматорная функция
        double summ=0;
        for (int j = 0; j < i; j++) {
            summ=summ+x[j]*w[j];
        }

        return summ;
    }
    public static void norm() {
        // нормализация входных данных
    }

    // вычислитель объёма входного массива
    public  static int amountOfLines(String filename) {
        File file = new File(filename);
        int count = 0;
        try(LineNumberReader lnr = new LineNumberReader(new FileReader(file))) {
            while (lnr.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("количество = " + count);
        return count;

    }

}
 
 
