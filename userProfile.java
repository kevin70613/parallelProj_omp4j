
public class userProfile {
	
	private String userID;
	private String userAge;
	private String userGender;
	private String userOccupation;
	private String movieID;
	private String movieName;
	private String movieRDate;
	private String genreStr;
	private String rating;
	private String totalGenre;
	
	public userProfile(String s1,String s2,String s3,String s4,String s5,String s6,String s7,String s8,String s9 ){
		userID = s1;
		userAge = s2;
		userGender = s3;
		userOccupation = s4;
		movieID = s5;
		movieName = s6;
		movieRDate = s7;
	    genreStr = s8;
	    rating = s9;
	    
	}
	
	void setTotalGenre(String s){
		totalGenre = s;
	}
	
	String getTotalGenre(){
		return totalGenre;
	}
	
	String getUserID(){
		return userID;
	}
	
	String getAge(){
		return userAge;
	}
	
	String getGender(){
		return userGender;
	}
	
	String getOccupation(){
		return userOccupation;
	}
	
	String getMovieID(){
		return movieID;
	}
	
	String getMovieName(){
		return movieName;
	}
	
	String getMovieRDate(){
		return movieRDate;
	}
	
	String getGenre(){
		return genreStr;
	}

	String getRating(){
		return rating;
	}
	
}
