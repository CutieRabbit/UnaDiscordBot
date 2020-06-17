package sigtuna.discord.schedule;

import java.util.TimerTask;

import sigtuna.discord.codeforces.DataBase;

public class AutoCodeForcesDataBaseSave extends TimerTask{
	
	public void run() {
		DataBase.save();
	}

}
