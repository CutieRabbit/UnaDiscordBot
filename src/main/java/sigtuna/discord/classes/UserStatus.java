package sigtuna.discord.classes;

import cfapi.main.CodeForcesStatus;
import cfapi.main.CodeForcesSubmissionData;
import com.ibm.icu.text.Transliterator;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class UserStatus {

    String account = "";
    List<CodeForcesSubmissionData> problemSubmission = new ArrayList<>();
    List<CodeForcesSubmissionData> acSubmission = new ArrayList<>();
    Map<String, List<CodeForcesSubmissionData>> dateToACSubmission = new HashMap<>();

    public UserStatus(String account){
        this.account = account.toLowerCase();
        load();
    }

    public List<CodeForcesSubmissionData> getAllSubmission(){
        return problemSubmission;
    }

    public List<CodeForcesSubmissionData> getAcSubmission(){
        List<CodeForcesSubmissionData> acSubmission = new ArrayList<>();
        List<String> solvedList = new ArrayList<>();
        for(CodeForcesSubmissionData data : problemSubmission) {
            String problemID = data.getProblemID();
            String verdict = data.getVerdict();
            if (verdict.equals("OK") && !solvedList.contains(problemID)) {
                solvedList.add(problemID);
                acSubmission.add(data);
            }
        }
        return acSubmission;
    }

    private void makeDayToAC(){
        for(CodeForcesSubmissionData submissionData : acSubmission){
            String time = submissionData.getTime();
            List<CodeForcesSubmissionData> timeSubmissionList = dateToACSubmission.getOrDefault(time, new ArrayList<>());
            timeSubmissionList.add(submissionData);
            dateToACSubmission.put(time, timeSubmissionList);
        }
    }

    public Map<Integer, List<CodeForcesSubmissionData>> getRatingToProblemMap(List<CodeForcesSubmissionData> list) {
        Map<Integer, List<CodeForcesSubmissionData>> map = new HashMap<>();
        for(CodeForcesSubmissionData submissionData : list){
            int rating = Integer.parseInt(submissionData.getRating());
            List<CodeForcesSubmissionData> temp = map.getOrDefault(rating, new ArrayList<>());
            temp.add(submissionData);
            map.put(rating, temp);
        }
        return map;
    }

    private void load(){
        try {
            CodeForcesStatus status = new CodeForcesStatus(account);
            String text = status.getDoc().text();
            problemSubmission = CodeForcesStatus.make(text);
            acSubmission = getAcSubmission();
            makeDayToAC();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getUserTotalSolved(){
        return acSubmission.size();
    }

    public List<Integer> convertSubmissionListToIntegerRatingList(List<CodeForcesSubmissionData> list){
        List<Integer> ratingList = new ArrayList<>();
        for(CodeForcesSubmissionData submissionData : list){
            int rating = Integer.parseInt(submissionData.getRating());
            if(rating == 0) continue;
            ratingList.add(rating);
        }
        return ratingList;
    }

    public Pair<Object, Object> getMaxAndMin(List<Integer> list){
        if(list.size() == 0){
            return new Pair<> ("－－", "－－");
        }
        Collections.sort(list);
        int size = list.size();
        int min = list.get(0);
        int max = list.get(size-1);
        Pair<Object, Object> pair = new Pair<>(min, max);
        return pair;
    }

    public Object getMedian(List<Integer> list){
        if(list.size() == 0){
            return "－－";
        }
        Collections.sort(list);
        int size = list.size();
        if(size % 2 != 0){
            return list.get(size/2);
        }else{
            return (list.get(size/2) + list.get(size/2-1))/2;
        }
    }

    public List<String> getMode(List<Integer> list){
        if(list.size() == 0){
            return new ArrayList<>();
        }
        Map<Integer, Integer> map = new HashMap<>();
        int maxValue = 0;
        for(int i = 0; i < list.size(); i++){
            int value = list.get(i);
            if(!map.containsKey(value)){
                map.put(value, 0);
            }
            int count = map.get(value);
            maxValue = Math.max(maxValue, count+1);
            map.put(value, count+1);
        }
        List<String> arrayList = new ArrayList<>();
        for(Map.Entry<Integer, Integer> entry : map.entrySet()){
            if(entry.getValue() == maxValue){
                arrayList.add(String.valueOf(entry.getKey()));
            }
        }
        return arrayList;
    }

    public String formatACData(int year, int month, List<Integer> dayData){
        DateTime dateTime = new DateTime();
        List<List<String>> array = new ArrayList<>();
        for(int i = 0; i < 8; i++){
            List<String> temp = new ArrayList<>();
            for(int j = 0; j < 7; j++){
                temp.add("－－");
            }
            array.add(temp);
        }
        dateTime.withZone(DateTimeZone.forID("Asia/Taipei"));
        dateTime = dateTime.withDate(year, month, 1);
        dateTime = dateTime.withTime(0,0,0, 0);
        int startWeek = 1;
        int startDay = dateTime.getDayOfWeek();
        int day = 1;
        while(month == dateTime.getMonthOfYear()){
            day = dateTime.getDayOfMonth();
            startDay = dateTime.getDayOfWeek();
            if(startDay == 7) startWeek += 1;
            Transliterator tl = Transliterator.getInstance("Halfwidth-Fullwidth");
            String data;
            if(dateTime.getMillis() > System.currentTimeMillis()){
                data = "－－";
            }else {
                data = tl.transliterate(String.valueOf(String.format("%02d", dayData.get(day))));
            }
            array.get(startWeek).set(startDay%7, data);
            dateTime = dateTime.plusDays(1);
        }
        String data = "";
        for(int i = 1; i < array.size()-1; i++){
            List<String> list = array.get(i);
            data += "｜" + String.join("｜", list) + "｜" + "\n";
        }
        return data;
    }

    public List<Integer> getMonthofDaySolvedCountList(int year, int month){
        List<Integer> list = new ArrayList<Integer>();
        DateTime dateTime = new DateTime(year, month, 1, 0, 0, 0, 0);
        list.add(0);
        while(dateTime.getMonthOfYear() == month){
            String format = dateTime.toString("yyyy-MM-dd");
            if(dateToACSubmission.containsKey(format)){
                List<CodeForcesSubmissionData> daySolved = dateToACSubmission.get(format);
                int size = daySolved.size();
                list.add(size);
            }else{
                list.add(0);
            }
            dateTime = dateTime.plusDays(1);
        }
        return list;
    }

    public List<CodeForcesSubmissionData> getMonthSolvedList(int year, int month){
        List<CodeForcesSubmissionData> list = new ArrayList<CodeForcesSubmissionData>();
        DateTime dateTime = new DateTime(year, month, 1, 0, 0, 0, 0);
        while(dateTime.getMonthOfYear() == month){
            String format = dateTime.toString("yyyy-MM-dd");
            if(dateToACSubmission.containsKey(format)){
                List<CodeForcesSubmissionData> daySolved = dateToACSubmission.get(format);
                list.addAll(daySolved);
            }
            dateTime = dateTime.plusDays(1);
        }
        return list;
    }

    public List<CodeForcesSubmissionData> getDaySolvedList(int year, int month, int day){
        List<CodeForcesSubmissionData> list = new ArrayList<CodeForcesSubmissionData>();
        DateTime dateTime = new DateTime(year, month, day, 0, 0, 0, 0);
        String format = dateTime.toString("yyyy-MM-dd");
        if(dateToACSubmission.containsKey(format)){
            list.addAll(dateToACSubmission.get(format));
        }
        return list;
    }

    public List<String> getProblemIDList(List<CodeForcesSubmissionData> list){
        List<String> problemIDList = new ArrayList<>();
        for(CodeForcesSubmissionData submissionData : list){
            problemIDList.add(submissionData.getProblemID());
        }
        return problemIDList;
    }

    public String getMonthSolvedData(int year, int month){
        List<Integer> ratingList = getMonthofDaySolvedCountList(year, month);
        return formatACData(year, month, ratingList);
    }

    public int getMonthDayCount(int year, int month){
        DateTime dateTime = new DateTime(year, month, 1, 0, 0, 0, 0);
        int day = 0;
        while(dateTime.getMonthOfYear() == month){
            day++;
            dateTime = dateTime.plusDays(1);
        }
        return day;
    }

}
