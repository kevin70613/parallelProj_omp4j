import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class test1 {

	public static int Diff(String str1, String str2) {
		int flag;
		flag = str1.compareTo(str2);
		if (flag == 0)
			return 0;

		return 1;
	}

	public static double freqDiff(String str1, String str2) {
		int total = 0;
		for (int i = 0; i < 18; i++) {
			if (str1.charAt(i) != str2.charAt(i))
				total++;
		}
		return total / 18;
	}

	static double calEuclidean(double weight[], String str1, String str2) {

		double diffValue = 0, euclidean = 0;
		String[] str1Split = str1.split("\\|");
		String[] str2Split = str2.split("\\|");
		// error here
		for (int j = 0; j < 5; j++) {
			if (j == 4) {
				diffValue = freqDiff(str1Split[4], str2Split[4]);
			}
			if (j == 3) {
				diffValue = Diff(str1Split[3], str2Split[3]);
			}
			if (j == 2) {
				diffValue = Diff(str1Split[2], str2Split[2]);
			}
			if (j == 1) {
				diffValue = Diff(str1Split[1], str2Split[1]);
			}
			if (j == 0) {
				diffValue = Diff(str1Split[0], str2Split[0]);
			}
			euclidean += weight[j] * Math.pow(diffValue, 2);
		}
		return euclidean;

	}

	public static void main(String[] argv) throws IOException {

		int populationSize = 100;

		Population myPop = new Population(populationSize, true);

		String[] uUserStr = new String[1000];
		String[] uItemStr = new String[2000];
		String[] uDataStr = new String[200000];
		int uUserNum = 0;
		int uDataNum = 0;
		int uItemNum = 0;

		Scanner sc_uUser = null;
		Scanner sc_uData = null;
		Scanner sc_uItem = null;
        
        long startTime = System.currentTimeMillis();  // get current time

		try {
			sc_uUser = new Scanner(new FileInputStream("u.user.txt"));

			while (sc_uUser.hasNextLine()) {
				uUserStr[uUserNum] = sc_uUser.nextLine();
				uUserNum++;
			}

			sc_uData = new Scanner(new FileInputStream("u.data.txt"));

			while (sc_uData.hasNextLine()) {
				uDataStr[uDataNum] = sc_uData.nextLine();
				uDataNum++;
			}

			sc_uItem = new Scanner(new FileInputStream("u.item.txt"));

			while (sc_uItem.hasNextLine()) {
				uItemStr[uItemNum] = sc_uItem.nextLine();
				uItemNum++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}

		long ioTime = System.currentTimeMillis() - startTime;    // get total execution time
        System.out.println("I/O time : "+ioTime+"ms");

/*  ====    End of I/O   ====     */			

		String[] uUser_userID = new String[uUserNum];
		String[] uUser_age = new String[uUserNum];
		String[] uUser_gender = new String[uUserNum];
		String[] uUser_occupation = new String[uUserNum];

        // omp parallel for threadNum(4)
		for (int i = 0; i < uUserNum; i++) {
			String[] str = uUserStr[i].split("\\|");
			uUser_userID[i] = str[0];
			uUser_age[i] = str[1];
			uUser_gender[i] = str[2];
			uUser_occupation[i] = str[3];
		}

		String[] uData_userID = new String[uDataNum];
		String[] uData_itemID = new String[uDataNum];
		String[] uData_rating = new String[uDataNum];
        
        // omp parallel for threadNum(4)
		for (int i = 0; i < uDataNum; i++) {
			String[] str = uDataStr[i].split("	");
			uData_userID[i] = str[0];
			uData_itemID[i] = str[1];
			uData_rating[i] = str[2];
		}

		String[] uItem_movieID = new String[uItemNum];
		String[] uItem_movieTitle = new String[uItemNum];
		String[] uItem_releaseDate = new String[uItemNum];
		String[] uItem_genreStr = new String[uItemNum];

        // omp parallel for threadNum(4)
		for (int i = 0; i < uItemNum; i++) {
			String[] str = uItemStr[i].split("\\|");
			uItem_movieID[i] = str[0];
			uItem_movieTitle[i] = str[1];
			uItem_releaseDate[i] = str[2];
			uItem_genreStr[i] = str[5] + str[6] + str[7] + str[8] + str[9] + str[10] + str[11] + str[12] + str[13]
					+ str[14] + str[15] + str[16] + str[17] + str[18] + str[19] + str[20] + str[21] + str[22] + str[23];
		}	

        long preprocessingTime = System.currentTimeMillis() - (ioTime + startTime);    // get total execution time
        System.out.println("Total time : "+preprocessingTime+"ms");

/*  ====    End of data preprocessing   ====      */	

        String[][] userFeature = new String[uUserNum][1000];
		// create userProfile object
		userProfile[][] profile = new userProfile[uUserNum][1000];
		// store each movie user see, that is 2D array A[i][j] , each A[i] length
		int[] eachLen = new int[uUserNum];
        
        // omp parallel for threadNum(4)
		for (int j = 0; j < uUserNum; j++) {
			int userMovieCount = 0;
			for (int k = 0; k < uDataNum; k++) {
				// check has same userID
				if (uData_userID[k].equals(uUser_userID[j])) {
					
					for (int l = 0; l < uItemNum; l++) {  // check has same itemID(movieID)
						// omp critical
						if (uData_itemID[k].equals(uItem_movieID[l])) {
							userMovieCount++;
						}
					}
				}
			}
			eachLen[j] = userMovieCount;
		}
        
        for(int j = 0; j < uUserNum; j++)
        	System.out.println(j+" : "+eachLen[j]);
		
    }
}