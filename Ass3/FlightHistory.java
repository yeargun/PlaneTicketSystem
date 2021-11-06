import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
class FlightHistory implements Comparable{

    
    public ArrayList<Edge> edgeHistory;
    public Date currentDateTimeForFlightPlan;
    public Map<String, Boolean> isVisitedCountries;
    public int minDiff,hourDiff;
    public Date flightPlanStartTime;
    
    public int flightPlanCost(){
        int sum=0;
        for(Edge edge1:edgeHistory){
            sum+=edge1.getPrice();
        }
        return sum;
    }

    public void calculateDuration(){
        String hourDiffStr="";
        String minDiffStr="";

        if(edgeHistory==null || edgeHistory.size()==0)
            return;
        Date flightPlanStartTime=edgeHistory.get(0).getStartDateTime();
        Date flightPlanFinishTime = edgeHistory.get(edgeHistory.size()-1).getFinishDateTime();
        
        //time difference in milliseconds
        long difference_In_Time = flightPlanFinishTime.getTime() - flightPlanStartTime.getTime();
        long difference_In_Minutes
            = (difference_In_Time/ (1000 * 60)) % 60;
        long difference_In_Hours
            = (difference_In_Time/ (1000 * 60 * 60));
        
        this.hourDiff=(int) difference_In_Hours;
        this.minDiff = (int) difference_In_Minutes;
    }

    public String getDuration(){
        String hourDiffStr="";
        String minDiffStr="";

        if(edgeHistory==null || edgeHistory.size()==0)
            return "";
        Date flightPlanStartTime=edgeHistory.get(0).getStartDateTime();
        Date flightPlanFinishTime = edgeHistory.get(edgeHistory.size()-1).getFinishDateTime();
        
        //time difference in milliseconds
        long difference_In_Time = flightPlanFinishTime.getTime() - flightPlanStartTime.getTime();
        long difference_In_Minutes
            = (difference_In_Time/ (1000 * 60)) % 60;
        long difference_In_Hours
            = (difference_In_Time/ (1000 * 60 * 60));
        
        this.hourDiff=(int) difference_In_Hours;
        this.minDiff = (int) difference_In_Minutes;
        if(difference_In_Hours<10)
            hourDiffStr = "0"+Long.toString(difference_In_Hours);
        else
            hourDiffStr=Long.toString(difference_In_Hours);

        if(difference_In_Minutes<10)
            minDiffStr = "0" + Long.toString(difference_In_Minutes);
        else
            minDiffStr = Long.toString(difference_In_Minutes);
        
        return hourDiffStr+":"+minDiffStr;
    }

    public boolean constainsCountry(String country1){
        if(isVisitedCountries.get(country1)==null)
            return false;
        return true;
    }

    FlightHistory(){
        edgeHistory=new ArrayList<>();
        isVisitedCountries = new HashMap<>();
    }
    FlightHistory(FlightHistory o, Date currentTimeUpdate,Date flightPlanStartTime,Edge edge1){
        edgeHistory = new ArrayList<>(o.edgeHistory);
        edgeHistory.add(edge1);
        isVisitedCountries = new HashMap<>(o.isVisitedCountries);
        isVisitedCountries.put(edge1.getEndingNode().getCountry(), true);
        currentDateTimeForFlightPlan = (Date)currentTimeUpdate.clone();
        //flightPlanStartTime = (Date) flightPlanStartTime.clone();
    }

    public boolean isUniqueCity(Node node1){
        if(edgeHistory==null)
            return true;
        String city = node1.getCountry();
        for(Edge edge1 : edgeHistory){
            if(edge1.getStartingNode().getCountry().equals(city) ||
            edge1.getEndingNode().getCountry().equals(city) ){
                return false;
            }
        }
        return true;
    }

    public long getFlightDurationInMins(){
        return ((long)hourDiff*60) + minDiff;
    }

    @Override
    public int compareTo(Object o) {
        if(this.flightPlanCost() < ((FlightHistory) o).flightPlanCost())
            return -1;
        else if(this.flightPlanCost() > ((FlightHistory) o).flightPlanCost())
            return 1;
        return 0;
    }

    public void printHistory(){
        Edge edge1;
        for(int i=0;i<edgeHistory.size();i++){
            edge1 = edgeHistory.get(i);
            System.out.print(edge1.getId()+"\t"+edge1.getStartingNode().getName()+"->"+
            edge1.getEndingNode().getName());
            if(i==edgeHistory.size()-1){
                break;
            }
            System.out.print("||");
        }
        System.out.println("\t"+ getDuration()+ "/"+flightPlanCost());
    }
    
}
