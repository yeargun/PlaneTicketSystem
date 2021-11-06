import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


class Digraph{
    private static Map<String,Node> nodes;
    private static Map<String,Edge> edges;

    //static queue, is used while doing bfs
    public static ArrayDeque<Node> queue;

    //aliases (they get initialized before each command according to needed country names)
    public static Map<String,Node> startingAirportAliases;
    public static Map<String,Node> destinationAirportAliases;

    //all possible paths are putted here for each command(findAllPaths does the initialization)
    public static ArrayList<FlightHistory> completePaths;

    //used while calculating graph diameter
    public static ArrayList<FlightHistory> shortestPaths;


    Digraph(Map<String,Node> nodes, Map<String,Edge> edges){
        this.nodes=nodes;
        this.edges=edges;
        queue = new ArrayDeque<Node>();
        completePaths=new ArrayList<>();
    }

    public static void nodesFromSameCountryStart(String country){
        Digraph.startingAirportAliases=new HashMap<>();
        for(Map.Entry<String,Node> node1 : nodes.entrySet()){
            if(node1.getValue().getCountry().equals(country)){

                startingAirportAliases.put(node1.getValue().getName(), node1.getValue());
            }
        }
    }
    public static void nodesFromSameCountryDestination(String country){
        Digraph.destinationAirportAliases=new HashMap<>();
        for(Map.Entry<String,Node> node1 : nodes.entrySet()){
            if(node1.getValue().getCountry().equals(country)){
                destinationAirportAliases.put(node1.getValue().getCountry(), node1.getValue());
            }
        }
    }
    
    public static boolean isInsideDestinationAirportAliases(String country){
        for(Map.Entry<String,Node> node1 : destinationAirportAliases.entrySet()){
            if(node1.getValue().getName().equals(country))
                return true;
        }
        return false;
    }

    public static void findAllPaths(String[] splittedCommand){
        if(destinationAirportAliases==null){System.out.println("theres no airport at destination city");return;}
        if(startingAirportAliases==null){System.out.println("theres no airport at starting city");return;}
        completePaths = new ArrayList<>();
        String date = splittedCommand[2];
        Date flightPlanStartTime;
        Node edgeEndingNode;
        for(Map.Entry<String,Node> startingNode : startingAirportAliases.entrySet()){
        //iterate over starting airport aliases

            //initializing flight plan's start time
            flightPlanStartTime = DateTime.initDate(date);

            Node startNode = startingNode.getValue();
            startNode.flightHistories.add(new FlightHistory());
            startNode.flightHistories.peekFirst().currentDateTimeForFlightPlan = (Date)flightPlanStartTime.clone();
            queue = new ArrayDeque<Node>();
            queue.add(startingNode.getValue());
            FlightHistory startingNodesHistory;

            //BFS Here
            while(queue.size()!=0){
                startNode = queue.remove();
                startingNodesHistory = startNode.flightHistories.pop();
                for(Map.Entry<String,Edge> edge1 : startNode.getOutgoingFlights().entrySet()){
                    edgeEndingNode=edge1.getValue().getEndingNode();

                    //If not obeys country rule or time rule, continue
                    if(startingNodesHistory.constainsCountry(edgeEndingNode.getCountry()) ||
                    !startingNodesHistory.currentDateTimeForFlightPlan.before(edge1.getValue().getStartDateTime())){
                        continue;
                    }

                    /* each node, stores edge history chain(s).
                       A-->B-->T
                       D-->K-->B
                       consider doing a BFS, B's edge chain(history) from A-->B-->T path, can get overwritten by D-->K-->B
                       To resolve this problem, there is queue of edge chain(history) in each node.*/
                    edgeEndingNode.flightHistories.add( new FlightHistory(startingNodesHistory,edge1.getValue().getFinishDateTime(),flightPlanStartTime, edge1.getValue()));
                    
                    //if reached destination
                    if( isInsideDestinationAirportAliases(edgeEndingNode.getName()) ){
                        FlightHistory flightPlan = edgeEndingNode.flightHistories.pop();
                        completePaths.add(flightPlan);
                        flightPlan.calculateDuration();
                            continue;
                    }
                    queue.add(edgeEndingNode);
                }
            }
        }   
    }

    public static void listAll(String[] splittedCommand){
        findAllPaths(splittedCommand);
        if(completePaths.size()==0){System.out.println("No suitable flight plan is found");return;}
        for(FlightHistory fh1: completePaths){
            fh1.printHistory();
        }
        resetStaticLists();
    }

    public static void listProper(String[] splittedCommand){
        findProperPaths(splittedCommand);
        for( FlightHistory plan1 : completePaths){
            plan1.printHistory();
        }
        resetStaticLists();
    }

    public static void removeNonproperPathsFromPathList(){
        if(completePaths.size()==0){return;}
        //sorting all paths by their price ascendingly
        //while index i always < index j, i.price <= j.price
        //if i.duration is also < j.duration, remove j from list
        //because that flight plan is both expensive and has longer duration
        Collections.sort(completePaths);
        for(int i=0;i<completePaths.size();i++){
            for(int j=i;j<completePaths.size();j++){
                if(completePaths.get(i).flightPlanCost()==completePaths.get(j).flightPlanCost())
                    continue;
                if(completePaths.get(i).getFlightDurationInMins()<
                completePaths.get(j).getFlightDurationInMins() ){
                    completePaths.remove(j);
                    j--;
                }
            }
        }
    }
    
    public static void findProperPaths(String[] splittedCommand){
        //stores properPaths to completePaths
        findAllPaths(splittedCommand);
        if(completePaths.size()==0){System.out.println("No suitable flight plan is found");return;}
        //sorting all paths by their price ascendingly
        //while index i always < index j, i.price <= j.price
        //if i.duration is also < j.duration, remove j from list
        //because that flight plan is both expensive and has longer duration
        Collections.sort(completePaths);
        for(int i=0;i<completePaths.size();i++){
            for(int j=i;j<completePaths.size();j++){
                if(completePaths.get(i).flightPlanCost()==completePaths.get(j).flightPlanCost())
                    continue;
                if(completePaths.get(i).getFlightDurationInMins()<
                completePaths.get(j).getFlightDurationInMins() ){
                    completePaths.remove(j);
                    j--;
                }
            }
        }
    }

    public static void listCheapest(String[] splittedCommand){
        findAllPaths(splittedCommand);
        if(completePaths.size()==0){System.out.println("No suitable flight plan is found");return;}
        Collections.sort(completePaths);
        int min=completePaths.get(0).flightPlanCost();
        for(int i=0;i<completePaths.size();i++){
            if(completePaths.get(i).flightPlanCost()>min)
                break;
            completePaths.get(i).printHistory();
        }
        resetStaticLists();
    }

    public static void listQuickest(String[] splittedCommand){
        findAllPaths(splittedCommand);
        if(completePaths.size()==0){System.out.println("No suitable flight plan is found");return;}
        long minDurationInMins=completePaths.get(0).getFlightDurationInMins() ; 
        for(int i=0;i<completePaths.size();i++){
        //finding quickest arrival time
            if(completePaths.get(i).getFlightDurationInMins() < minDurationInMins)
                minDurationInMins = completePaths.get(i).getFlightDurationInMins();
        }

        for(int i=0;i<completePaths.size();i++){
        //printing flight plans which have quickest arrival time
            if(completePaths.get(i).getFlightDurationInMins() == minDurationInMins)
                completePaths.get(i).printHistory();
        }
        resetStaticLists();
    }
   
    public static void listOnlyFrom(String[] splittedCommand){
        if(destinationAirportAliases==null){System.out.println("No suitable flight plan is found");return;}
        if(startingAirportAliases==null){System.out.println("No suitable flight plan is found");return;}
        completePaths = new ArrayList<>();
        String date = splittedCommand[2];
        String airlineCompany =splittedCommand[3];
        Date flightPlanStartTime;
        Node edgeEndingNode;
        for(Map.Entry<String,Node> startingNode : startingAirportAliases.entrySet()){
        //iterate over starting airport aliases

            //initializing flight plan's start time
            flightPlanStartTime = DateTime.initDate(date);

            Node startNode = startingNode.getValue();
            startNode.flightHistories.add(new FlightHistory());
            startNode.flightHistories.peekFirst().currentDateTimeForFlightPlan = flightPlanStartTime;
            queue = new ArrayDeque<Node>();
            queue.add(startingNode.getValue());
            FlightHistory startingNodesHistory;
            
            //BFS Here
            while(queue.size()!=0){
                startNode = queue.remove();
                startingNodesHistory = startNode.flightHistories.pop();
                for(Map.Entry<String,Edge> edge1 : startNode.getOutgoingFlights().entrySet()){
                    //if flight isnt from asked company
                    if(!edge1.getValue().getId().substring(0, 2).equals(airlineCompany))
                        continue;
                    edgeEndingNode=edge1.getValue().getEndingNode();
                    

                    //If not obeys country rule or time rule, continue
                    if(startingNodesHistory.constainsCountry(edgeEndingNode.getCountry()) ||
                    !startingNodesHistory.currentDateTimeForFlightPlan.before(edge1.getValue().getStartDateTime())){
                        continue;
                    }

                    /* each node, stores edge history chain(s).
                       A-->B-->T
                       D-->K-->B
                       consider doing a BFS, since B's edge chain(history) can get overwritten by D-->K-->B
                       To resolve this problem, there is queue of edge chain(history) in each node.*/
                    edgeEndingNode.flightHistories.add( new FlightHistory(startingNodesHistory,edge1.getValue().getFinishDateTime(),flightPlanStartTime, edge1.getValue()));

                    
                    //if reached destination
                    if( isInsideDestinationAirportAliases(edgeEndingNode.getName()) ){
                        FlightHistory flightPlan = edgeEndingNode.flightHistories.pop();
                        completePaths.add(flightPlan);
                            continue;
                    }
                    queue.add(edgeEndingNode);
                }
            }
        }
        removeNonproperPathsFromPathList();
        if(completePaths.size()==0){
            System.out.println("No suitable flight plan is found");return;
        }
        for( FlightHistory plan1 : completePaths){
            plan1.printHistory();
        }
        resetStaticLists();
    }

    public static void listCheaper(String[] splittedCommand){
        int maxPrice = Integer.parseInt(splittedCommand[3]);
        findProperPaths(splittedCommand);
        if(completePaths.size()==0){System.out.println("No suitable flight plan is found");return;}
        int count = 0;
        for(int i=0;i<completePaths.size();i++){
            if(completePaths.get(i).flightPlanCost()>=maxPrice)
                continue;
            count++;
            completePaths.get(i).printHistory();
        }
        if(count==0){System.out.println("No suitable flight plan is found");}
    }

    public static void listQuicker(String[] splittedCommand){
        findProperPaths(splittedCommand);
        if(completePaths.size()==0){System.out.println("No suitable flight plan is found");return;}
        Date deadline=null;
        try {
            deadline = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(splittedCommand[3]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int count=0;
        for(int i=0;i<completePaths.size();i++){
        //finding quickest arrival time
            if(completePaths.get(i).currentDateTimeForFlightPlan.before(deadline)){
                completePaths.get(i).printHistory();
                count++;
            }
        }
        if(count==0){System.out.println("No suitable flight plan is found");}
        resetStaticLists();
    }

    public static void listExcluding(String[] splittedCommand){
        if(destinationAirportAliases==null){System.out.println("No suitable flight plan is found");return;}
        if(startingAirportAliases==null){System.out.println("No suitable flight plan is found");return;}
        completePaths = new ArrayList<>();
        String date = splittedCommand[2];
        String airlineCompany =splittedCommand[3];
        Date flightPlanStartTime;
        Node edgeEndingNode;
        for(Map.Entry<String,Node> startingNode : startingAirportAliases.entrySet()){
        //iterate over starting airport aliases

            //initializing flight plan's start time
            flightPlanStartTime = DateTime.initDate(date);

            Node startNode = startingNode.getValue();
            startNode.flightHistories.add(new FlightHistory());
            startNode.flightHistories.peekFirst().currentDateTimeForFlightPlan = flightPlanStartTime;
            queue = new ArrayDeque<Node>();
            queue.add(startingNode.getValue());
            FlightHistory startingNodesHistory;
            
            //BFS Here
            while(queue.size()!=0){
                startNode = queue.remove();
                startingNodesHistory = startNode.flightHistories.pop();
                for(Map.Entry<String,Edge> edge1 : startNode.getOutgoingFlights().entrySet()){
                    //if flight isnt from asked company
                    if(edge1.getValue().getId().substring(0, 2).equals(airlineCompany))
                        continue;
                    edgeEndingNode=edge1.getValue().getEndingNode();
                    

                    //If not obeys country rule or time rule, continue
                    if(startingNodesHistory.constainsCountry(edgeEndingNode.getCountry()) ||
                    !startingNodesHistory.currentDateTimeForFlightPlan.before(edge1.getValue().getStartDateTime())){
                        continue;
                    }

                    /* each node, stores edge history chain(s).
                       A-->B-->T
                       D-->K-->B
                       consider doing a BFS, since B's edge chain(history) can get overwritten by D-->K-->B
                       To resolve this problem, there is queue of edge chain(history) in each node.*/
                    edgeEndingNode.flightHistories.add( new FlightHistory(startingNodesHistory,edge1.getValue().getFinishDateTime(),flightPlanStartTime, edge1.getValue()));

                    
                    //if reached destination
                    if( isInsideDestinationAirportAliases(edgeEndingNode.getName()) ){
                        FlightHistory flightPlan = edgeEndingNode.flightHistories.pop();
                        completePaths.add(flightPlan);
                            continue;
                    }
                    queue.add(edgeEndingNode);
                    
                }
            }
        }
        removeNonproperPathsFromPathList();
        if(completePaths.size()==0){
            System.out.println("No suitable flight plan is found");return;
        }
        for( FlightHistory plan1 : completePaths){
            plan1.printHistory();
        }
        resetStaticLists();
    }
    
    public static void initShortestPaths(String[] splittedCommand){
        findAllPaths(splittedCommand);
        if(completePaths.size()==0)
            return;
        Collections.sort(completePaths);
        int min=completePaths.get(0).flightPlanCost();
        for(int i=0;i<completePaths.size();i++){
            if(completePaths.get(i).flightPlanCost()>min)
                break;
            shortestPaths.add(completePaths.get(i));
        }
        resetStaticLists();
    }

    public static void diameterOfGraph(){
        shortestPaths = new ArrayList<>();
        ArrayList<String> countryNames = new ArrayList<>();
        //initialize all countries
        //finding shrotest paths between countries is
        //equivalent to find shortest paths between airports
        for(Entry<String, Node> node1:nodes.entrySet()){
            if(!countryNames.contains(node1.getValue().getCountry())){
                countryNames.add(node1.getValue().getCountry());
            }
        }

        //putting the shortest paths between countries to shortestPaths<FlightHistory>
        for(int i=0;i<countryNames.size();i++){
            for(int j=0;j<countryNames.size() && j!=i;j++){
                String[] splittedCommand = {" ", countryNames.get(i)+"->"+countryNames.get(j), "01/01/5"};
                Digraph.nodesFromSameCountryStart(splittedCommand[1].split("->")[0]);
                Digraph.nodesFromSameCountryDestination(splittedCommand[1].split("->")[1]);
                initShortestPaths(splittedCommand);
            }
        }
        if(shortestPaths.size()==0){
            System.out.println("No suitable flight plan is found");
            return;
        }
        FlightHistory longest = shortestPaths.get(0);
        for(FlightHistory fh1 : shortestPaths){
            if(fh1.flightPlanCost()>longest.flightPlanCost())
                longest = fh1;
        }
        //longest shortest
        System.out.println("The diameter of graph : "+longest.flightPlanCost());
        shortestPaths = null;
    }

    public static void pageRanks(){
        Node node;
        Edge edge; 
        Node startingNode;
        double sum;

        //initializing each node's rank as 1/number of nodes in graph
        for(Entry<String, Node> node1:nodes.entrySet() ){
            node = node1.getValue();
            node.setRank((double)1.0/(double)nodes.size());
        }
        
        //each node has rank and nextRank
        //each loop calculates next rank as
        // 
        // NodeA.nextRank = (1 - 0.85) + 0.85(Rank(Node0)/Node0.numberofoutgoingedges + Rank(Node1)/ Node1.numberofoutgoingedges + ...)
        // node0, node1, ... are nodes pointing to node1
        // after calculating nextRank for each node, we put nextRanks to rank of nodes
        for(int i = 0;i<4;i++){
            sum=0.0;
            for(Entry<String, Node> node1:nodes.entrySet() ){
                node = node1.getValue();
                for(Entry<String, Edge> edge1:edges.entrySet()){
                    edge = edge1.getValue();
                    startingNode = edge.getStartingNode();
                    if(edge.getEndingNode() == node){
                        sum += startingNode.getRank() / startingNode.getOutgoingFlights().size();
                    } 
                }
                node.setNextRank(0.15+0.85*(sum));
                sum=0.0;
            }
            for(Entry<String, Node> node1:nodes.entrySet() ){
                node = node1.getValue();
                node.setRank(node.getNextRank());
            }
        }
        for(Entry<String, Node> node1:nodes.entrySet() ){
            node = node1.getValue();
            node.setRank(node.getNextRank());
            System.out.println(node.getName()+" : "+node.getRank());
        }        
    }
    
    
    //since some information has stored as static, we need to reset them after each command
    //to avoid unintended behaviours
    private static void resetStaticLists(){
        destinationAirportAliases = null;
        startingAirportAliases = null;     
        queue = null;  
        completePaths=null; 
    }

    public int numOfNodes(){
        return nodes.size();
    }
    public int numOfEdges(){
        return edges.size();
    }
}