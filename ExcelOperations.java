// "static void main" must be defined in a public class.
public class Excel {
    int rows;
    int columns;
    int[][] cells;
    Map<String, Set<String>> backwardLinkMap;
    Map<String, String> forwardLinkMap;
    
    Excel(int h, char w){
        rows=h+1;
        columns=w-'A'+1;
        cells=new int[rows][columns];
        backwardLinkMap = new HashMap<String, Set<String>>();
        forwardLinkMap = new HashMap<String, String>();
    }
    
    public void set(int row, char col, int val){
        String cellName=String.valueOf(col)+row;
        removePreviousLink(cellName);
        cells[row][col-'A']=val;
        update(cellName);
    }
    
    public void set(int row, char col, String expr){
        String cellName=String.valueOf(col)+row;
        parseExpr(expr, cellName);
        int sum=eval(expr);
        cells[row][col-'A']=sum;
        update(cellName);
    }
    
    public int get(int row, char col){
        return cells[row][col-'A'];
    } 
    
    private void update(String cellName){
        Queue<String> cellQueue=new LinkedList<>();
        cellQueue.offer(cellName);
        while(!cellQueue.isEmpty()){
            String cell=cellQueue.poll();
            Set<String> st=backwardLinkMap.get(cell);
            if(st==null){
                continue;
            }
            for(String s: st){
                String expr=forwardLinkMap.get(s);
                int sum=eval(expr);
                cellQueue.offer(s);
                int row=Integer.parseInt(s.substring(1));
                char col=s.charAt(0);
                cells[row][col-'A']=sum;
            }
        }
    }
    
    private void removePreviousLink(String cellName){
        String expression = "";
        if(forwardLinkMap.containsKey(cellName)){
            expression=forwardLinkMap.get(cellName);
            forwardLinkMap.remove(cellName);
        }
        
        int i=0, m=expression.length();
        while(i<m){
            if(expression.charAt(i)-'A'>=0 && expression.charAt(i)-'Z'<=0){
                String cell="";
                while(i<m && !isOperator(expression.charAt(i))){
                    cell+=expression.charAt(i);
                    i++;
                }
                Set<String> s=backwardLinkMap.get(cell);
                s.remove(cellName);
            }
            else{
                i++;
            }
        }
    }
    
    private void parseExpr(String expr, String cellName){
        removePreviousLink(cellName);
        
        int i=0, m=expr.length();
        forwardLinkMap.put(cellName, expr);
        while(i<m){
            if(expr.charAt(i)-'A'>=0 && expr.charAt(i)-'Z'<=0){
                String cell="";
                while(i<m && !isOperator(expr.charAt(i))){
                    cell+=expr.charAt(i);
                    i++;
                }
                if(backwardLinkMap.containsKey(cell)){
                    Set<String> s=backwardLinkMap.get(cell);
                    s.add(cellName);
                    backwardLinkMap.put(cell, s);
                }
                else{
                    Set<String> s=new HashSet<>();;
                    s.add(cellName);
                    backwardLinkMap.put(cell, s);
                }
            }
            else{
                i++;
            }
        }
    }
    
    private int eval(String expr){
        Stack<Integer> st=new Stack<>();
        int i=0, m=expr.length();
        int num=0;
        char sign='+';
        
        while(i<m){
            if(expr.charAt(i)-'A'>=0 && expr.charAt(i)-'Z'<=0){
                String cell="";
                while(i<m && !isOperator(expr.charAt(i))){
                    cell+=expr.charAt(i);
                    i++;
                }
                i--;
                int row=Integer.parseInt(cell.substring(1));
                char col=cell.charAt(0);
                num=cells[row][col-'A'];
            }
            else if(Character.isDigit(expr.charAt(i))){
                num=num*10+expr.charAt(i)-'0';
            }
            if((!Character.isDigit(expr.charAt(i)) && expr.charAt(i)!=' ') || i==m-1){
                if(sign=='+'){
                    st.push(num);
                }
                else if(sign=='-'){
                    st.push(-1*num);
                }
                else if(sign=='*'){
                    st.push(st.pop()*num);
                }
                else if(sign=='/'){
                    st.push(st.pop()/num);    
                }
                num=0;
                sign=expr.charAt(i);
            }
            i++;
        }
        
        int ans=0;
        for(int res: st){
            ans+=res;
        }
        return ans;
    }
    
    private boolean isOperator(char c){
        if(c=='+' || c=='-' || c=='*' || c=='/'){
            return true;
        }
        return false;
    }
    
    public static void main(String[] args) {
        Excel excel=new Excel(20, 'G');
//         excel.set(1, 'A', 2);
//         excel.set(2, 'A', 3);
//         excel.set(2, 'B', "A1+A2");
        
        
//         System.out.println(excel.get(1, 'A'));
//         System.out.println(excel.get(2, 'A'));
//         System.out.println(excel.get(2, 'B'));
        
//         excel.set(1, 'A', 3);
//         System.out.println(excel.get(2, 'B'));
        
        excel.set(11, 'A', 5);
        excel.set(2, 'B', 6);
        
        System.out.println("A11="+excel.get(11, 'A'));
        System.out.println("B2="+excel.get(2, 'B'));
        
        excel.set(2, 'E', "A11+B2");
        System.out.println("E2="+excel.get(2, 'E'));
        
        excel.set(2, 'A', "D1+B2");
        System.out.println("A2="+excel.get(2, 'A'));
        
        excel.set(4, 'E', "E2+A11");
        System.out.println("E4="+excel.get(4, 'E'));
        
        excel.set(11, 'A', "2*B2");
        System.out.println("after updating A11="+excel.get(11, 'A'));
        System.out.println("after updating A11 to "+excel.get(11, 'A')+" E2="+excel.get(2, 'E'));
        System.out.println("after updating A11 to "+excel.get(11, 'A')+" E4="+excel.get(4, 'E'));
        
        excel.set(11, 'A', 10);
        System.out.println("after updating A11="+excel.get(11, 'A'));
        System.out.println("after updating A11 to "+excel.get(11, 'A')+" E2="+excel.get(2, 'E'));
        System.out.println("after updating A11 to "+excel.get(11, 'A')+" E4="+excel.get(4, 'E'));
        
        excel.set(2, 'E', "B2+B2");
        System.out.println("E2="+excel.get(2, 'E'));
        System.out.println("E4="+excel.get(4, 'E'));
        
        excel.set(2, 'B', 1);
        System.out.println("E2="+excel.get(2, 'E'));
        System.out.println("E4="+excel.get(4, 'E'));
    }
}
