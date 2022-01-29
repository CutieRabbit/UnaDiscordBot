package sigtuna.discord.classes;

public class ContestInfo {
	
	public String contestName;
	public String contestID;
	public long durationSeconds;
	public long startTime;
	public ContestInfo(String cn, String ci, long st, long ds) {
		contestName = cn;
		contestID = ci;
		startTime = st;
		durationSeconds = ds;
	}

}
