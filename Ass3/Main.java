import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static Map<String, Edge> initFlights(String flightPath, Map<String, Node> airports){
        try  
        {  
            File flightFile=new File(flightPath);   
            FileReader fr=new FileReader(flightFile);  
            BufferedReader br=new BufferedReader(fr); 
            String line;
            String[] splitted;
            Node startingNode, endingNode;
            Edge edge1;
            Map<String, Edge> flights = new HashMap<>();
            while((line=br.readLine())!=null){  
                splitted=line.split("\t");
                startingNode=airports.get(splitted[1].split("->")[0]);
                endingNode=airports.get(splitted[1].split("->")[1]);
                edge1= new Edge(splitted[0],startingNode,endingNode,
                splitted[2],splitted[3],splitted[4]);
                startingNode.addOutgoingFlight(edge1);
                flights.put(edge1.getId(), edge1);
            }  
            br.close();
            return flights;
        }    
            catch(IOException e)  
        {  
            e.printStackTrace();  
            return null;
        }
    }

    public static Map<String, Node> initAirports(String airportPath){
        try  
        {  
            File airportFile=new File(airportPath);   
            FileReader fr=new FileReader(airportFile);  
            BufferedReader br=new BufferedReader(fr); 
            String line;
            String[] splitted;
            String[] aliases;
            String country="";
            Map<String, Node> airports = new HashMap<String, Node>();
            while((line=br.readLine())!=null){  
                splitted=line.split("\t");
                country=splitted[0];
                aliases=Arrays.copyOfRange(splitted,1,splitted.length);
                //from splitted line, initializing aliases from same country
                for(String s:aliases){
                    airports.put( s,new Node(s,country));
                }
            }  
            br.close();
            return airports;
        }    
            catch(IOException e)  
        {  
            e.printStackTrace();  
            return null;
        }
    }

    public static void getCommands(String commandPath, Map<String, Node> airports){
        try  
        {  
            File commandFile=new File(commandPath);   
            FileReader fr=new FileReader(commandFile);  
            BufferedReader br=new BufferedReader(fr); 
            String line;
            String[] splitted;
            while((line=br.readLine())!=null){  
                splitted=line.split("\t");
                System.out.println("command : "+line);
                if(splitted[0].equals("diameterOfGraph")){
                    Digraph.diameterOfGraph();
                    System.out.println();
                    continue;
                }
                if(splitted[0].equals("pageRankOfNodes")){
                    Digraph.pageRanks();
                    System.out.println();
                    continue;
                }

                //initializing nodes from given countries(Starting country and destination country) (aliases)
                Digraph.nodesFromSameCountryStart(splitted[1].split("->")[0]);
                Digraph.nodesFromSameCountryDestination(splitted[1].split("->")[1]);
                if(splitted[0].equals("listAll")){
                    Digraph.listAll(splitted);}
                else if(splitted[0].equals("listProper"))
                    Digraph.listProper(splitted);

                else if(splitted[0].equals("listCheapest"))
                    Digraph.listCheapest(splitted);

                else if(splitted[0].equals("listQuickest"))
                    Digraph.listQuickest(splitted);

                else if(splitted[0].equals("listOnlyFrom"))
                    Digraph.listOnlyFrom(splitted); 

                else if(splitted[0].equals("listCheaper"))
                    Digraph.listCheaper(splitted);

                else if(splitted[0].equals("listQuicker"))
                    Digraph.listQuicker(splitted); 

                else if(splitted[0].equals("listExcluding"))
                    Digraph.listExcluding(splitted); 

                System.out.println("\n");
            }  
            br.close();
        }    
            catch(IOException e)  
        {  
            e.printStackTrace();  
        }
    }

  
    //storing default sysout at console
    static PrintStream console;
    public static void redirectOutputPath(String outPath){
        PrintStream o=null;
        console = System.out;
        try {
            o = new PrintStream(new File(outPath));
            System.setOut(o);
        } catch (FileNotFoundException e) {
            System.out.println("Cant write to output.txt");
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
        // Main airportList.txt flightList.txt commandList.txt
        
        redirectOutputPath("output.txt");
  
        Map<String, Node> airports = initAirports(args[0]);

        Map<String, Edge> flights = initFlights(args[1],airports);

        Digraph FlightPlan = new Digraph(airports, flights);
        
        getCommands(args[2],airports);
    }
}
