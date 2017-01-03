import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class FinalProj {

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

		// get wanted information from u.user.txt
		String[] uUser_userID = new String[uUserNum];
		String[] uUser_age = new String[uUserNum];
		String[] uUser_gender = new String[uUserNum];
		String[] uUser_occupation = new String[uUserNum];

        // omp parallel for
		for (int i = 0; i < uUserNum; i++) {
			String[] str = uUserStr[i].split("\\|");
			uUser_userID[i] = str[0];
			uUser_age[i] = str[1];
			uUser_gender[i] = str[2];
			uUser_occupation[i] = str[3];
		}

		// for(int i=0;i<uUserNum;i++){
		// System.out.println(uUser_userID[i]+" , "+uUser_age[i]+" , "+uUser_gender[i]+" , "+uUser_occupation[i]);
		// }

		// get wanted information from u.data.txt
		String[] uData_userID = new String[uDataNum];
		String[] uData_itemID = new String[uDataNum];
		String[] uData_rating = new String[uDataNum];

        // omp parallel for
		for (int i = 0; i < uDataNum; i++) {
			String[] str = uDataStr[i].split("	");
			uData_userID[i] = str[0];
			uData_itemID[i] = str[1];
			uData_rating[i] = str[2];
		}

		// for(int i=0;i<uDataNum;i++){
		// System.out.println(i+": "+uData_userID[i]+" , "+uData_itemID[i]+" , "+uData_rating[i]);
		// }

		// get wanted information from u.item.txt movie == item
		String[] uItem_movieID = new String[uItemNum];
		String[] uItem_movieTitle = new String[uItemNum];
		String[] uItem_releaseDate = new String[uItemNum];
		String[] uItem_genreStr = new String[uItemNum];

        // omp parallel for
		for (int i = 0; i < uItemNum; i++) {
			String[] str = uItemStr[i].split("\\|");
			uItem_movieID[i] = str[0];
			uItem_movieTitle[i] = str[1];
			uItem_releaseDate[i] = str[2];
			// date,3=>null(vedio release date,4=>IMDB URL
			uItem_genreStr[i] = str[5] + str[6] + str[7] + str[8] + str[9] + str[10] + str[11] + str[12] + str[13]
					+ str[14] + str[15] + str[16] + str[17] + str[18] + str[19] + str[20] + str[21] + str[22] + str[23];
		}

		// for(int i=0;i<uItemNum;i++){
		// System.out.println(uItem_movieID[i]+" , "+uItem_movieTitle[i]+" , "+uItem_releaseDate[i]+" , "+uItem_genreStr[i]);
		// }

		String[][] userFeature = new String[uUserNum][1000];
		// create userProfile object
		userProfile[][] profile = new userProfile[uUserNum][1000];
		// store each movie user see, that is 2D array A[i][j] , each A[i]
		// length
		int[] eachLen = new int[uUserNum];

		for (int j = 0; j < uUserNum; j++) {
			int userMovieCount = 0;
			for (int k = 0; k < uDataNum; k++) {
				// check has same userID
				if (uData_userID[k].equals(uUser_userID[j])) {
					// check has same itemID(movieID)
					for (int l = 0; l < uItemNum; l++) {
						if (uData_itemID[k].equals(uItem_movieID[l])) {
							userMovieCount++;
						}
					}
				}
			}
			eachLen[j] = userMovieCount;
		}

		for (int j = 0; j < uUserNum; j++) {
			int userMovieCount = 0;
			for (int k = 0; k < uDataNum; k++) {
				// check has same userID
				if (uData_userID[k].equals(uUser_userID[j])) {
					// check has same itemID(movieID)
					for (int l = 0; l < uItemNum; l++) {
						if (uData_itemID[k].equals(uItem_movieID[l])) {
							userFeature[j][userMovieCount] = uData_userID[k] + "|" + uUser_age[j] + "|" + uUser_gender[j] + "|" + uUser_occupation[j] + "|" + uData_itemID[k] + "|" + uItem_movieTitle[l] + "|" + uItem_releaseDate[l] + "|" + uItem_genreStr[l] + "|" + uData_rating[k];
							userMovieCount++;
						}
					}
				}
			}
		}

		for (int i = 0; i < uUserNum; i++) {
			userProfile[] eachProfile = new userProfile[eachLen[i]];
			for (int j = 0; j < eachLen[i]; j++) {
				// System.out.println(userFeature[i][j]);
				String[] str = userFeature[i][j].split("\\|");
				eachProfile[j] = new userProfile(str[0], str[1], str[2], str[3], str[4], str[5], str[6], str[7],
						str[8]);
				profile[i][j] = eachProfile[j];
			}
		}

		for (int i = 0; i < uUserNum; i++) {
			int[] f = new int[18];
			String totalGenre = "";
			// initial
			for (int n = 0; n < 18; n++)
				f[n] = 1;

			for (int j = 0; j < eachLen[i]; j++) {
				// System.out.println(profile[i][j].getUserID()+" "+profile[i][j].getAge()+" "+profile[i][j].getGender()+" "+profile[i][j].getOccupation()+" "+profile[i][j].getMovieID()+" "+profile[i][j].getMovieName()+" "+profile[i][j].getMovieRDate()+" "+profile[i][j].getGenre()+" "+profile[i][j].getRating());
				for (int k = 0; k < 18; k++)
					if (profile[i][j].getGenre().charAt(k) == '1')
						f[k] *= 0;
			}

			for (int m = 0; m < 18; m++) {
				if (f[m] == 0)
					totalGenre += "1";
				else
					totalGenre += "0";
			}

			for (int j = 0; j < eachLen[i]; j++) {
				profile[i][j].setTotalGenre(totalGenre);
				// System.out.println(profile[i][j].getUserID()+" "+profile[i][j].getAge()+" "+profile[i][j].getGender()+" "+profile[i][j].getOccupation()+" "+profile[i][j].getMovieID()+" "+profile[i][j].getMovieName()+" "+profile[i][j].getMovieRDate()+" "+profile[i][j].getGenre()+" "+profile[i][j].getRating()+" "+profile[i][j].getTotalGenre());
			}
		}

		//uUserNum = 400;
		
		// assume user 1 is active user
		// get the active user total watched movie number, get 1/3 as training
		int activeUserID =0;
		int trainSize = eachLen[activeUserID] / 3;
		String[] activeSeeMovieID = new String[trainSize];
        double activeTotalRating = 0.0; 
		double activeMeanRating = 0.0;
        
		// get movie ID active user see
		for (int i = 0; i < trainSize; i++) {
			// System.out.println(profile[activeUserID][i].getUserID()+","+profile[activeUserID][i].getMovieID());
			activeSeeMovieID[i] = profile[activeUserID][i].getMovieID();
		}
		
		// get mean rating value of active user 
		for(int i=0;i<trainSize;i++){
		    activeTotalRating += Double.parseDouble(profile[activeUserID][i].getRating());	
		}
		
        activeMeanRating = activeTotalRating / trainSize;
			
		// save all Euclidean(A,j)
		double[][] euclideanArray = new double[populationSize][uUserNum];
		
		/*for(int r=0;r<populationSize;r++){
			for(int s=0;s<euclideanArray.length;s++)
	            euclideanArray[r][s] = 0.0;
		}*/
		
		for(int r=0;r<populationSize;r++){
			for(int s=0;s<uUserNum;s++)
	            euclideanArray[r][s] = 0.0;
		}
		
		// set neighborhood threshold
		double nThreshold;
		
        for(int iter=0;iter<30;iter++){
		// compute Euclidean and fitness
		
		for (int p = 0; p < populationSize; p++) {
			
	        //Step 1: compute all Euclidean value 
			for (int i = 0; i < uUserNum; i++) {
				// this scope is in user i
				if (i != 0) { // if active user is random then modify here: 0 -> random index
				
					double totalEuclidean = 0;
					// compute Euclidean
					for (int j = 0; j < activeSeeMovieID.length; j++) {

						for (int k = 0; k < eachLen[i]; k++) {
							if (activeSeeMovieID[j].equals(profile[i][k].getMovieID())) { // find common movie
								// compute each individual's Euclidean value
								double euclideanVal;
								double[] weight = new double[5];
                                double weightSum = 0.0;

								for (int y = 0; y < 5; y++) {
									weight[y] = myPop.getIndividual(p).getGene(40 - y * 8 - 1) * Math.pow(2, 7)
											+ myPop.getIndividual(p).getGene(40 - y * 8 - 2) * Math.pow(2, 6)
											+ myPop.getIndividual(p).getGene(40 - y * 8 - 3) * Math.pow(2, 5)
											+ myPop.getIndividual(p).getGene(40 - y * 8 - 4) * Math.pow(2, 4)
											+ myPop.getIndividual(p).getGene(40 - y * 8 - 5) * Math.pow(2, 3)
											+ myPop.getIndividual(p).getGene(40 - y * 8 - 6) * Math.pow(2, 2)
											+ myPop.getIndividual(p).getGene(40 - y * 8 - 7) * Math.pow(2, 1)
											+ myPop.getIndividual(p).getGene(40 - y * 8 - 8) * Math.pow(2, 0);
                                    weightSum += weight[y];
								}
								for(int y=0;y<5;y++)
									weight[y] = weight[y]/weightSum;
								// not for loop version here

								// active user is user 1
								String profileA = profile[activeUserID][j].getRating()+"|"+profile[activeUserID][j].getAge()+"|"+profile[activeUserID][j].getGender()+"|"+profile[activeUserID][j].getOccupation()+"|"+profile[activeUserID][j].getTotalGenre();
								String profileJ = profile[i][k].getRating()+"|"+profile[i][k].getAge()+"|"+profile[i][k].getGender()+"|"+profile[i][k].getOccupation()+"|"+profile[i][k].getTotalGenre();
								euclideanVal = calEuclidean(weight, profileA, profileJ);
                                totalEuclidean += euclideanVal;
							}
						}
					// end of total common movie 
					}
					totalEuclidean = Math.sqrt(totalEuclidean);
					euclideanArray[p][i] = totalEuclidean;
				} // check not active user
			// end of one user(not the active user) 
			}
			
			for(int b=0;b<uUserNum;b++){
				if(euclideanArray[p][b] == 0)
					euclideanArray[p][b] = 1000000;
			}
				
			//Step 2: compute fitness
            // find neighbor 
			int neighborNum = 0;
			int[] neighborID = new int[uUserNum]; 
			
			// compute nThreshold
	    	double [] sortArray=new double[uUserNum];
	    	for(int w=0;w<uUserNum;w++){
	    		sortArray[w]= euclideanArray[p][w];
	    	}
	    	// sorted array
	    	Arrays.sort(sortArray);
	    	nThreshold = sortArray[20];
	
			for(int a=0;a<uUserNum;a++){
				if(euclideanArray[p][a]<nThreshold){
					neighborID[neighborNum] = a;
					neighborNum++;
				}
			}
	
			double fitnessValue = 0.0;
			int commonMovieNum = 0;
			
			for (int i = 0; i < activeSeeMovieID.length; i++) {
				// compute sum of 1/Euclidean (that is k)
				int hasCommonMovie = 1;
				double sumEuclidean = 0.0;
				double predictVote = 0.0;
				
				for (int j = 0; j < neighborNum; j++) {
					for (int k = 0; k < eachLen[neighborID[j]]; k++) {
						if (activeSeeMovieID[i].equals(profile[neighborID[j]][k].getMovieID())) { // find common movie
							sumEuclidean += (1/euclideanArray[p][neighborID[j]]);
						}
					}
				}				
				//System.out.println("sumEuclidean : "+sumEuclidean);
				for (int j = 0; j < neighborNum; j++) {
					// compute mean of neighbor
					double meanNeighbor = 0.0;
					for (int k = 0; k < eachLen[neighborID[j]]; k++) {
						meanNeighbor += Double.parseDouble(profile[neighborID[j]][k].getRating());
					}
					meanNeighbor = meanNeighbor / eachLen[neighborID[j]];
					//System.out.println("j: "+j+", meanNeighbor: "+meanNeighbor);
					for (int k = 0; k < eachLen[neighborID[j]]; k++) {
						if (activeSeeMovieID[i].equals(profile[neighborID[j]][k].getMovieID())) { // find common movie
                            predictVote += ( (1/sumEuclidean) * (1/euclideanArray[p][neighborID[j]]) * (Double.parseDouble(profile[neighborID[j]][k].getRating()) - meanNeighbor) );
                           // System.out.println("In loop k, enter if"+(1/sumEuclidean)+" , "+(1/euclideanArray[p][neighborID[j]])+" , "+Double.parseDouble(profile[neighborID[j]][k].getRating())+" , "+meanNeighbor);
						    hasCommonMovie *= 0;
						}
						
					}
					//System.out.println("each predictVote : "+predictVote);
				}
				//System.out.println(p+","+i+": "+predictVote);
				// if someone see common movie then compute fitness value
				if(hasCommonMovie == 0){
				   predictVote += activeMeanRating;
                   fitnessValue += Math.abs(predictVote - Double.parseDouble(profile[activeUserID][i].getRating()) );
                   commonMovieNum++;
				}
			}
			
            if(fitnessValue != 0.0){
			   fitnessValue = fitnessValue/(double)commonMovieNum;
		       // asign fitness value to individual
		       myPop.getIndividual(p).setFitness(fitnessValue);
            }
            
		// end of each individual
		}
		
		for(int p=0;p<populationSize;p++)
	        System.out.println(p+" : "+activeMeanRating+" | "+myPop.getIndividual(p).getFitness()+" | "+myPop.getIndividual(p).toString());

		myPop = Algorithm.evolvePopulation(myPop);
		
        }
		
        // testing dataset
        int bestWeightIndex = 0;
        double bestFitness = myPop.getIndividual(0).getFitness();
        // find best weight index
        for(int q=1;q<populationSize;q++){
        	if(myPop.getIndividual(q).getFitness() < bestFitness){
        		bestWeightIndex = q;
        	}
        }
        // save best weight index Euclidean(A,j) => euclideanArray[bestWeightIndex]

		// get the active user total watched movie number, get 2/3 as training
		int testSize = eachLen[activeUserID] - trainSize;
		String[] activeTestSeeMovieID = new String[testSize];
		
		// get testing movie ID active user see
		for (int i = trainSize; i < eachLen[activeUserID] ; i++) {
			activeTestSeeMovieID[i-trainSize] = profile[activeUserID][i].getMovieID();
		}
			
        // compute fitness
        // find neighbor 
	    int testNeighborNum = 0;
	    int[] testNeighborID = new int[uUserNum]; 
		double testnThreshold;	
			
		// compute nThreshold
	    double [] testSortArray=new double[uUserNum];
	    for(int w=0;w<uUserNum;w++){
	    	testSortArray[w]= euclideanArray[bestWeightIndex][w];
	    }
	    // sorted array
	    Arrays.sort(testSortArray);
	    testnThreshold = testSortArray[20];
	
		for(int a=0;a<uUserNum;a++){
			if(euclideanArray[bestWeightIndex][a]<testnThreshold){
				testNeighborID[testNeighborNum] = a;
				testNeighborNum++;		
			}
		}
		
		double testFitnessValue = 0.0;
		int testCommonMovieNum = 0;
		double[] testPredictVote = new double[activeTestSeeMovieID.length];
		
		for(int a=0;a<activeTestSeeMovieID.length;a++)
			testPredictVote[a] = 0.0;
				
		for (int i = 0; i < activeTestSeeMovieID.length; i++) {
		    // compute sum of 1/Euclidean (that is k)
			int testHasCommonMovie = 1;
			double testSumEuclidean = 0.0;
			double eachTestPredictVote = 0.0;
				
			for (int j = 0; j < testNeighborNum; j++) {
				for (int k = 0; k < eachLen[testNeighborID[j]]; k++) {
					if (activeTestSeeMovieID[i].equals(profile[testNeighborID[j]][k].getMovieID())) { // find common movie
						testSumEuclidean += (1/euclideanArray[bestWeightIndex][testNeighborID[j]]);
					}
				}
			}				
			//System.out.println("testSumEuclidean : "+testSumEuclidean);
			for (int j = 0; j < testNeighborNum; j++) {
				// compute mean of neighbor
				double testMeanNeighbor = 0.0;
				for (int k = 0; k < eachLen[testNeighborID[j]]; k++) {
					testMeanNeighbor += Double.parseDouble(profile[testNeighborID[j]][k].getRating());
				}
				testMeanNeighbor = testMeanNeighbor / eachLen[testNeighborID[j]];
				//System.out.println("j: "+j+", meanNeighbor: "+meanNeighbor);
				for (int k = 0; k < eachLen[testNeighborID[j]]; k++) {
					if (activeTestSeeMovieID[i].equals(profile[testNeighborID[j]][k].getMovieID())) { // find common movie
                        eachTestPredictVote += ( (1/testSumEuclidean) * (1/euclideanArray[bestWeightIndex][testNeighborID[j]]) * (Double.parseDouble(profile[testNeighborID[j]][k].getRating()) - testMeanNeighbor) );
                        // System.out.println("In loop k, enter if"+(1/sumEuclidean)+" , "+(1/euclideanArray[p][neighborID[j]])+" , "+Double.parseDouble(profile[neighborID[j]][k].getRating())+" , "+meanNeighbor);
						testHasCommonMovie *= 0;
					}
						
				}
				//System.out.println("each predictVote : "+testPredictVote);
			}
			//System.out.println(p+","+i+": "+testPredictVote);
			// if someone see common movie then compute fitness value
			if(testHasCommonMovie == 0){
			   eachTestPredictVote += activeMeanRating;
			   testPredictVote[i] = eachTestPredictVote;
               testFitnessValue += Math.abs(eachTestPredictVote - Double.parseDouble(profile[activeUserID][i].getRating()) );
               testCommonMovieNum++;
			}
		}
	    
		double avgDiff = 0.0;
		int tPVNum = 0;
	    for(int w=0;w<activeTestSeeMovieID.length;w++){
            if(testPredictVote[w] != 0.0){
                 System.out.println("Testing movie "+(tPVNum+1)+" ,predict vote: "+testPredictVote[w]+" , actual vote : "+profile[activeUserID][trainSize+w].getRating()+" | "+ (testPredictVote[w] - Double.parseDouble(profile[activeUserID][trainSize+w].getRating())) );    
                 avgDiff += Math.abs(testPredictVote[w] - Double.parseDouble(profile[activeUserID][trainSize+w].getRating()));
                 tPVNum++;
            }
		}
        System.out.println(avgDiff/tPVNum);
	    
        String[] recommendMovie = new String[10000];
        int recommendNum = 0;
        String[] tenRecomMovie = new String[10];
        
        for(int k=0;k<testNeighborNum;k++ ){
        	for(int p=0;p<eachLen[testNeighborID[k]];p++){
                if(Double.parseDouble(profile[testNeighborID[k]][p].getRating()) >= 5){   
        	       recommendMovie[recommendNum] = profile[testNeighborID[k]][p].getMovieName();
        	       recommendNum++;
                }
        	}
        }
           
        for(int f=0;f<10;f++){
           int repeat = 1;
           int index = (int)(Math.random()*recommendNum);
           //System.out.println(index);  
           for(int g=0;g<f;g++){
             if(tenRecomMovie[g].compareTo(recommendMovie[index]) == 0)
            	 repeat *= 0;
           }
           
           if(repeat!=0)
             tenRecomMovie[f] = recommendMovie[index];
           else
        	 f--;
           //System.out.println(f);
        }
		
		System.out.println("We recommend you following movie which you may like :");
        for(int b=0;b<10;b++)
        	System.out.println(b+". "+tenRecomMovie[b]);
		
	}
}
