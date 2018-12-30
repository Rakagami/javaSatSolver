import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

//  ((p∧((q∧(r∨(¬r∧(x∧¬z))))∨(¬q∧((r∧(x∧z))∨(¬r∧(x∨(¬x∧(¬y∧¬z))))))))∨(¬p∧((q∧((r∧x)∨(¬r∧z)))∨(¬q∧((r∧(x∨(¬x∧(y∨(¬y∧z)))))∨(¬r∧(x∧z)))))))

public class DS {
	
	public static void main(String[] args) {
		Expression exp;
			System.out.println("WELCOME TO THE 'TOO LAZY TO DO PGdP BUT STILL WANT TO DO JAVA' Mini-Project");
		while(true) {
			System.out.println("Enter any Formula with correct Syntax!");
			System.out.println("Disjunction: 'v', '∨' ;Conjunction: '^', '∧' ;Negation: '¬'; All Variables have to be 1 Char long");
			String stmt = DS.askString("> ");
			System.out.println("All Interpretations:");
			exp = parseStmt(stmt);
			checkBelegung(exp);
			System.out.println();
		}
	}
	
	public static void checkBelegung(Expression exp) {
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(exp.vars());
		int[][] matrix = matrixTest(list.size());
		
		for(int i=0; i<list.size(); i++) {
			System.out.print(list.get(i));
			if(i<list.size()-1)
				System.out.print("|");
		}
		
		System.out.println();
		
		for(int i=0; i<matrix.length; i++) {
			for(int a=0; a<matrix[0].length; a++) {
				System.out.print(matrix[i][a]);
				if(a<matrix[0].length-1)
					System.out.print("|");
			}
			System.out.println(" --> " + exp.evaluate(intArrToHashMap(list, matrix[i])));
		}
		
	}
	
	public static HashMap<String, Boolean> intArrToHashMap(ArrayList<String> list, int[] arr){
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();
		if(list.size() != arr.length)
			throw new IllegalArgumentException("Array and HashMap don't match");
		for(int i=0; i<list.size(); i++) {
			map.put(list.get(i), arr[i]!=0);
		}
		return map;
	}
	
	public static int[][] matrixTest(int n) {
		int m = (int)Math.pow(2, n);
		int[][] matrix = new int[m][0];
		String help = "";
		for(int i=0; i<m; i++) {
			help = Integer.toBinaryString(i);
			help = complementToNBit(help, n);
			matrix[i] = stringToIntArr(help);
		}
		return matrix;
	}
	
	public static String complementToNBit(String str, int N) {
		while(str.length() < N) {
			str = "0" + str;
		}
		return str;
	}
	
	public static int[] stringToIntArr(String str) {
		int[] ret = new int[str.length()];
		for(int i=0; i<str.length(); i++) {
			ret[i] = Integer.parseInt(str.charAt(i) + "");
		}
		return ret;
	}
	
	/*
	 * Parses Statement and gives it as an expression back.
	 */
	public static Expression parseStmt(String stmt) {
		
		//removes unnecessary brackets
		while(DS.sumBrackets(stmt) == 1 && ( stmt.charAt(0)=='(' && stmt.charAt(stmt.length()-1)==')' )) {
			stmt = removeOuterBracket(stmt);
		}
		
		Expression exps;
		if(stmt.length() > 2) {
			String[] splitStmt = splitBrackets(stmt);
			switch(splitStmt[1]) {
			case "^":
			case "∧": 
//				System.out.println("splitStmt 0: " + splitStmt[0]);
//				System.out.println("splitStmt 2: " + splitStmt[2]);
				exps = new DS().new Conjunction(parseStmt(splitStmt[0]), parseStmt(splitStmt[2]));
				break;
			case "v":
			case "∨": 
//				System.out.println("splitStmt 0: " + splitStmt[0]);
//				System.out.println("splitStmt 2: " + splitStmt[2]);
				exps = new DS().new Disjunction(parseStmt(splitStmt[0]), parseStmt(splitStmt[2]));
				break;
			default:
//				System.out.println(splitStmt[1]);
				throw new IllegalArgumentException("Please enter a valid Binary Operator");
			}
		}else if(stmt.length() == 2){
			exps = new DS().new Negation(new DS().new Const("" + stmt.charAt(1)));
		}else {
			exps = new DS().new Const(stmt);
		}
		return exps;
	}
	
	public static boolean correctlyBracketed(String str) {
		int sum = 0;
		for(int i=0; i<str.length(); i++) {
			if(str.charAt(i) == '(') {
				sum++;
			}else if(str.charAt(i) == ')') {
				sum--;
			}
			
			if(sum<0)
				return false;
		}
		return sum == 0;
	}
	
	public static int sumBrackets(String str) {
		int sum = 0;
		int ret = 0;
		for(int i=0; i<str.length(); i++) {
			if(str.charAt(i) == '(' && sum == 0) {
				ret++;
			}if(str.charAt(i) == '(') {
				sum++;
			}else if(str.charAt(i) == ')') {
				sum--;
			}
			
			if(sum<0)
				return -1;
		}
		return ret;
	}
	
	/*
	 * Splits a bracketed statement.
	 * Example: before split: "(stm1(stm2))(stm3)"
	 * 			after split	: String[]{ "(stm1(stm2))", "(stm3)" }
	 */
	public static String[] splitBrackets(String str) {
		String splitWords = "";
		
		//Assumption is that every entered String has to start and end with a bracket and at least 3 Characters long
		if(str.length() <= 2 || !correctlyBracketed(str)) {
			throw new IllegalArgumentException("Please enter a correctly bracketed statement");
		}
		
		int checkSum = 0;
		int start = 1;
		for(int i=0; i<str.length(); i++) {
			if(str.charAt(i) == '¬' && checkSum == 0) {
				splitWords += "::" + str.charAt(i) + str.charAt(i+1);
				i++;
			}else if(str.charAt(i) == '(' && checkSum == 0) {
				start = i;
				checkSum++;
			}else if(str.charAt(i) == '(') {
				checkSum++;
			}else if(str.charAt(i) == ')' && checkSum == 1) {
				splitWords += "::" + str.substring(start, i+1);
				checkSum--;
			}else if(str.charAt(i) == ')') {
				checkSum--;
			}else if(checkSum == 0) {
				splitWords += "::" + str.charAt(i);
			}
			if(checkSum < 0)
				throw new IllegalArgumentException("Please enter a correctly bracketed statement");
		}
		splitWords = splitWords.substring(2);
		return splitWords.split("::");
	}
	
	//Assuming the entered String is of the form "(something)", this is important
	public static String removeOuterBracket(String str) {
		return str.substring(1, str.length()-1);
	}
	
	//Terminal Copy-Pasta-----------------------------------------------------------------------------
	/** Ein Reader-Objekt, das bei allen Lesezugriffen verwendet wird. */
	  private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	  
	  public static String askString(String str) {
		System.out.print(str);
	    try {
	      return in.readLine();
	    } catch (IOException e) {
	      throw new Error(e);
	    }
	  }
	//Copy-Pasta End--------------------------------------------------------------------------------

	
	//Inner Classes-------------------------------------------------------------------------------------------------
	//---------------------------------------------------------------------------------------------------------------
	
	
	
	abstract class Expression {
		protected HashSet<String> vars;
		
		public abstract Boolean evaluate(HashMap<String, Boolean> map);
		public abstract String toString();
		
		public HashSet<String> vars(){
			return vars;
		}
	}
	
	abstract class BinOp extends Expression{
		protected Expression value1;
		protected Expression value2;
		
		public BinOp(Expression var1, Expression var2) {
			value1 = var1;
			value2 = var2;
			
			vars = new HashSet<String>();
			vars.addAll(var1.vars());
			vars.addAll(var2.vars());
		}
		
	}
	
	abstract class UnOp extends Expression{
		protected Expression value;
		
		public UnOp(Expression value) {
			this.value = value;
			
			vars = new HashSet<String>();
			vars.addAll(value.vars());
		}
	}
	
	class Const extends Expression{
		String var;
		
		public Const(String var) {
			this.var = var;
			vars = new HashSet<String>();
			vars.add(var);
		}
		
		public Boolean evaluate(HashMap<String, Boolean> map) {
			if(!map.containsKey(var))
				throw new IllegalArgumentException("Please use a correct HashMap");
			return map.get(var);
		}

		public String toString() {
			return var;
		}
	}
	
	class Negation extends UnOp {

		public Negation(Expression value) {
			super(value);
		}
		
		public Boolean evaluate(HashMap<String, Boolean> map) {
			return !value.evaluate(map);
		}
		
		public String toString() {
			return "!(" + value.toString() + ")";
		}
	}
	
	class Conjunction extends BinOp{

		public Conjunction(Expression bool1, Expression bool2) {
			super(bool1, bool2);
		}
		
		public Boolean evaluate(HashMap<String, Boolean> map) {
			return value1.evaluate(map) & value2.evaluate(map);
		}
		
		public String toString() {
			return "(" + value1.toString() + " ^ " + value2.toString() + ")";
		}
		
	}

	class Disjunction extends BinOp {

		public Disjunction(Expression value1, Expression value2) {
			super(value1, value2);
		}
		
		public Boolean evaluate(HashMap<String, Boolean> map) {
			return value1.evaluate(map) | value2.evaluate(map);
		}
		
		public String toString() {
			return "(" + value1.toString() + " v " + value2.toString() + ")";
		}

	}
	
}
