package LA;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    final static int ROW = 500;
    final static int COL = 4423;

    public static void main(String[] args) {
        System.out.print("K 값을 입력하세요 : ");
        Scanner scan = new Scanner(System.in);
        Random rand = new Random();
        int K = scan.nextInt();
        int KRepeat = 0;
        int MINJclustIndex = 0;
        double MINJclust = 99999;
        double[][] RawData = new double[ROW][COL];
        double[][] Mean = new double[K][COL];
        String[] Title = new String[500];
        ArrayList<ArrayList<Integer>> SortedData = new ArrayList<>();
        for (int i = 0; i < K; i++) {
            ArrayList<Integer> row = new ArrayList<>();
            SortedData.add(row);
        }
        ArrayList<ArrayList<Double>> Jclust = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ArrayList<Double> row = new ArrayList<>();
            Jclust.add(row);
        }
        ArrayList<ArrayList<Integer>> Final = new ArrayList<>();
        for (int i = 0; i < K; i++) {
            ArrayList<Integer> row = new ArrayList<>();
            Final.add(row);
        }

        RawData = readMap("termDocMatrix.txt");                             //파일을 읽고 rotate시켜주고 있다.
        RawData = rotate(RawData);
        Title = readTitle("Title.txt");
        while(KRepeat < 10) {
            int JRepeat = 0;
            for (int i = 0; i < K; i++) {                                                             //Random값 K수만큼 대입
                SortedData.get(i).add(rand.nextInt(500));
                for (int j = 0; j < i; j++) { //중복 제거 하는 로직
                    if (Objects.equals(SortedData.get(i).get(0), SortedData.get(j).get(0))) {
                        i--;
                        break;
                    }
                }
            }
            double totalJ = 0.0;                        //처음으로 Rawdata를 sort 하는 코드
            for (int i = 0; i < ROW; i++) {
                double MINJ = 9999;
                int MINJindex = 0;
                for (int k = 0; k < K; k++) {
                    double tempJ = 0;
                    for (int j = 0; j < COL; j++) {
                        tempJ += Math.pow((RawData[SortedData.get(k).get(0)][j] - RawData[i][j]), 2);
                    }
                    if (MINJ > tempJ) {                 //centroid와 500개의 vector의 거리를 쟤서 최소값을 찾아 clustering해주는 코드
                        MINJ = tempJ;
                        MINJindex = k;
                    }
                }
                SortedData.get(MINJindex).add(i);
                totalJ += MINJ;
            }
            Jclust.get(KRepeat).add(totalJ);
            JRepeat++;

            for (int k = 0; k < K; k++) {                                   //Mean 구하는 코드이다.
                for (int j = 0; j < COL; j++) {
                    double mean = 0;
                    for (int i : SortedData.get(k)) {
                        mean += RawData[i][j];
                    }
                    mean /= (double) SortedData.get(k).size();
                    Mean[k][j] = mean;
                }
            }

            while(JRepeat < 20) {                                           //Centroid값이 Mean이 되어서 새로운 코드로 Jclust를 구하고 있다.
                for (int k = 0; k < K; k++) {
                    SortedData.get(k).clear();
                }
                totalJ = 0.0;
                for (int i = 0; i < ROW; i++) {
                    double MINJ = 9999;
                    int MINJindex = 0;
                    for (int k = 0; k < K; k++) {
                        double tempJ = 0;
                        for (int j = 0; j < COL; j++) {
                            tempJ += Math.pow((Mean[k][j] - RawData[i][j]), 2);     //이 코드 제외 위의 코드와 같은 코드이다.
                        }
                        if (MINJ > tempJ) {
                            MINJ = tempJ;
                            MINJindex = k;
                        }
                    }
                    SortedData.get(MINJindex).add(i);
                    totalJ += MINJ;
                }
                Jclust.get(KRepeat).add(totalJ);
                JRepeat++;

                for (int k = 0; k < K; k++) {
                    for (int j = 0; j < COL; j++) {
                        double mean = 0;
                        for (int i : SortedData.get(k)) {
                            mean += RawData[i][j];
                        }
                        mean /= (double) SortedData.get(k).size();
                        Mean[k][j] = mean;
                    }
                }
                if(Objects.equals(Jclust.get(KRepeat).get(JRepeat - 1), Jclust.get(KRepeat).get(JRepeat - 2)))
                    break;
            }
//            finalJClustIndex를 구해라
            if (MINJclust > Collections.min(Jclust.get(KRepeat))) {                         //Jclust를 20번 돌고 Jclust들을 비교해서 Jclust가 가장 작았을때는 고르고 Final배열에 clone 시켜주는 코드이다.
                MINJclust = Collections.min(Jclust.get(KRepeat));
                MINJclustIndex = KRepeat;
                Final = (ArrayList<ArrayList<Integer>>) SortedData.clone();
            }
            KRepeat++;
        }
        //출력부분 담당
        for (int i = 0; i < 10; i++) {
            System.out.println(Jclust.get(i));
        }
        System.out.println(MINJclustIndex + 1);

        for (int i = 0; i < K; i++) {
            int five = 1;
            for (int j : Final.get(i)) {
                System.out.println(Title[j]);
                if (five == 5)
                    break;
                five++;
            }
            System.out.println("--------------------------------");
        }
    }

    public static double[][] readMap(String filename) { //readMap 함수 선언

        double[][] map = null; //array null로 초기화
        File file = new File(filename);
        try {
            Scanner fileScan = new Scanner(file);
            map = new double[COL][ROW];
            for (int i = 0; i < map.length; i++) { //층의 갯수
                for (int j = 0; j < map[i].length; j++) {
                    map[i][j] = fileScan.nextDouble();
                }
            }
            fileScan.close(); //system.in이 close 가 되는게 아니다.

        } catch (FileNotFoundException e) {
            System.out.println("파일이 존재하지 않습니다.");
            e.printStackTrace(); //error가 난 메시지를 다 출력해주겠다.
        }
        //
        return map;

    }

    public static double[][] rotate(double[][] arr) {
        int n = arr.length;
        int m = arr[0].length;
        double[][] rotate = new double[m + 20][n];

        for (int i = 0; i < rotate.length - 20; i++) {
            for (int j = 0; j < rotate[i].length; j++) {
                rotate[i][j] = arr[n - 1 - j][i];
            }
        }

        return rotate;
    }

    public static String[] readTitle(String filename) { //readMap 함수 선언

        String[] map = null; //array null로 초기화
        File file = new File(filename);
        try {
            Scanner fileScan = new Scanner(file);
            map = new String[ROW];
            for (int i = 0; i < map.length; i++) { //층의 갯수
                map[i] = fileScan.nextLine();
            }
            fileScan.close(); //system.in이 close 가 되는게 아니다.

        } catch (FileNotFoundException e) {
            System.out.println("파일이 존재하지 않습니다.");
            e.printStackTrace(); //error가 난 메시지를 다 출력해주겠다.
        }
        //
        return map;

    }
}
