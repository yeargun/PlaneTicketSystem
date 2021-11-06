import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class Node {

    private Map<String, Edge> outgoingFlights;
    private int numOfIncomingFlights;
    private String name;
    private String country;
    private double rank;
    private double nextRank;

   

    

    //each node copies its' current journey history to traversed(next) node
    //by that, when we reach destination, we can get information about journey
    //and for not to overwrite other journey histories, we need to put a queue of journey histories inside each node
    //LIFO behaviour is needed
    public ArrayDeque<FlightHistory> flightHistories;
    
    Node(String name, String country){
        this.numOfIncomingFlights=0;
        this.name=name;
        this.country=country;
        this.outgoingFlights=new HashMap<String, Edge>();
        flightHistories=new ArrayDeque<>();
        this.nextRank = 0;
        this.rank = 0;
    }

    public void addOutgoingFlight(Edge edge1){
        outgoingFlights.put(edge1.getId(),edge1);
    }
   
    @Override
    public String toString(){
        String result="----------\nNode:"+this.name+", City:"+this.country+"\nincomingNum:"+this.numOfIncomingFlights;
        return result;
    }

    public Map<String, Edge> getOutgoingFlights() {
        return this.outgoingFlights;
    }


    public int getNumOfIncomingFlights() {
        return this.numOfIncomingFlights;
    }

    public void setNumOfIncomingFlights(int numOfIncomingFlights) {
        this.numOfIncomingFlights = numOfIncomingFlights;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRank() {
        return this.rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getNextRank() {
        return this.nextRank;
    }

    public void setNextRank(double nextRank) {
        this.nextRank = nextRank;
    }
}
