import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Edge {
    private String id;
    private String deptDate;
    private String deptDuration;
    private int price;
    private Node startingNode;
    private Node endingNode;
    private Date startDateTime;
    private Date finishDateTime;

    
    Edge(String id, Node startingNode, Node endingNode,String deptDate, String deptDuration, String price){
        this.id = id;
        this.startingNode=startingNode;
        this.endingNode=endingNode;
        this.deptDate=deptDate;
        this.deptDuration=deptDuration;
        this.price=Integer.parseInt(price);
        try {
            SimpleDateFormat formatter1=new SimpleDateFormat("dd/MM/yyyy HH:mm");  
            startDateTime = formatter1.parse(deptDate);
            finishDateTime = DateTime.addDurationToDate(startDateTime, 
            Integer.parseInt(deptDuration.split(":")[0]), Integer.parseInt(deptDuration.split(":")[1]));
            } catch (ParseException e) {
                e.printStackTrace();
            }
    }

    @Override
    public String toString(){
        
        return id+"\t"+startingNode.getName()+"->"+endingNode.getName()+"\t"+startDateTime.getHours()+":"+startDateTime.getMinutes()+"->"+finishDateTime.getHours()+":"
        +finishDateTime.getMinutes()+"\t"+price;
    }

    public Date getStartDateTime() {
        return this.startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getFinishDateTime() {
        return this.finishDateTime;
    }

    public void setFinishDateTime(Date finishDateTime) {
        this.finishDateTime = finishDateTime;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeptDate() {
        return this.deptDate;
    }

    public void setDeptDate(String deptDate) {
        this.deptDate = deptDate;
    }

    public String getDeptDuration() {
        return this.deptDuration;
    }

    public void setDeptDuration(String deptDuration) {
        this.deptDuration = deptDuration;
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Node getStartingNode() {
        return this.startingNode;
    }

    public void setStartingNode(Node startingNode) {
        this.startingNode = startingNode;
    }

    public Node getEndingNode() {
        return this.endingNode;
    }

    public void setEndingNode(Node endingNode) {
        this.endingNode = endingNode;
    }

}
