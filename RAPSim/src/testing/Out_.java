package testing;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Simple output class providing methods for printing to "standard" output, 
 * files or Message Windows.
 * 
 * It is intended to be used in an introductory programming course 
 * when classes, packages and exceptions are unknown at the beginning. 
 * To use it, simply copy Out.class into the current directory.
 *
 * @author      Dietmar Jannach / IWAS - Uni Klagenfurt
 * @author      Rudolf Melcher / ISYS - Uni Klagenfurt
 * @author      Kristofer Schweiger / Uni Klagenfurt
 * @author		based on in.java from Prof.Moessenboeck / Uni Linz
 * 
 * @version     7.3 (Toolbox) 2011-10-01 
*/
public class Out_ {

    public static PrintStream out;
    private static PrintStream stack[] = new PrintStream[8];
    private static int sp = 0;
    private static boolean done = true;

    static { // initializer
        out = System.out; // "standard" output stream
    }
	/** Constructor **/
    public Out_() {
    	//empty
    }
    
//  ----------------------------------------------------------------------
    
    public static void setOut(PrintStream newOut) {
    	out = newOut;
    }
    
    public static boolean done() {
        return done && !out.checkError();
    }
    
//  ----------------------------------------------------------------------
    
    /**
     * Opens an output dialog to display a message.
     *
     * @param 	msg       a string containing the message
	*/
    public static void showMessageWindow(String msg) {
    	Out_.showMessageWindow(msg, "");
    }
    /**
     * Opens an output dialog to display a message.
     * The dialog is initialized with a dialog title and a message. 
     *
     * @param 	msg       a string containing the message
     * @param 	title     a dialog title
	*/    
	public static void showMessageWindow(String msg, String title) {
    	javax.swing.JOptionPane.showMessageDialog(null, msg, title, javax.swing.JOptionPane.PLAIN_MESSAGE);	
    	//java.awt.Frame f = javax.swing.JOptionPane.getRootFrame();
    }
    
//  ----------------------------------------------------------------------
    
	/** 
	 * Write a given string to a file with a given filename.
	 * If the third parameter is boolean true the string will be appended
	 * to the file, in the case that it exists. A file is created otherwise.
	 * 
     * @param 	filename     a string containing the filenam
     * @param 	content      a string containing the content to write
     * @param 	append       if true content will be appended to the file
	*/
	public static void writeFile(String filename, String content, boolean append){

	   try { 
      	  FileWriter fw = new FileWriter(filename, append); 
      	  BufferedWriter bw = new BufferedWriter(fw); 
          bw.write(content); 
          bw.close(); 
       } 
       catch (IOException ioe) { 
          out.println("IOException: "+ioe); 
       } 
	}


	/** Open the file with the name filename as the current output file.
	 * All subsequent output goes to this file until it is closed.
	 * The old output file will be restored when the new output file is closed. 
	*/
    public static void open(String filename)
    {
        try
        {
            PrintStream printstream = new PrintStream(new FileOutputStream(filename));
            stack[sp++] = out;
            out = printstream;
        }
        catch(Exception exception)
        {
            done = false;
        }
    }

	/** Close the current output file.
	 * The previous output file is restored and becomes the current output file. 
	*/
    public static void close()
    {
        out.flush();
        out.close();
        if(sp > 0)
        {
            out = stack[--sp];
        }
    }
    
//  ----------------------------------------------------------------------

	// Output method "print()" for all datatypes;
	/* 
	 * Print the boolean value b either as "true" or "false". 
	*/
    public static void print(boolean flag) 	{ out.print(flag);  }
    public static void print(char c) 		{ out.print(c);		}
    public static void print(int i) 		{ out.print(i); 	}

    public static void print(long l) 		{ out.print(l);		}
    public static void print(float f) 		{ out.print(f); 	}
    public static void print(double d) 		{ out.print(d);		}
    public static void print(char ac[]) 	{ out.print(ac);	}
    public static void print(String s) 		{ out.print(s);		}
    public static void print(Object obj) 	{ out.print(obj);	}

	
	// Output method "println()" for all datatypes;
 	/** 
 	 * Terminate the current line by writing a line separator string.
 	 * On windows this is the character sequence '\r' and '\n' 
	*/
    public static void println() 			{ out.println(); 	}
    public static void println(boolean flag){ out.print(flag);  }
    public static void println(char c) 		{ out.print(c);		}
    public static void println(int i) 		{ out.print(i); 	}

    public static void println(long l) 		{ out.print(l);		}
    public static void println(float f) 	{ out.print(f); 	}
    public static void println(double d) 	{ out.print(d);		}
    public static void println(char ac[]) 	{ out.print(ac);	}
    public static void println(String s) 	{ out.print(s);		}
    public static void println(Object obj) 	{ out.print(obj);	}
	
//  ----------------------------------------------------------------------
    
    static public void p  () 			{  out.print("");   }
    static public void p  (byte b)		{  out.print(b);    }	//umsonst
    static public void p  (char c)		{  out.print(c);    }
    static public void p  (int  d)		{  out.print(d);    }
    static public void p  (long d)		{  out.print(d);    }
    static public void p  (float f)		{  out.print(f);    }
    static public void p  (double f)	{  out.print(f);    }
    static public void p  (String s) 	{  out.print(s);    }
    static public void p  (Object o)	{  out.print(o);    }
    static public void p  (byte[] a)	{  for(int i=0;i<a.length;i++) out.print(a[i]+" ");  pl();}
    static public void p  (char[] a)	{  for(int i=0;i<a.length;i++) out.print(a[i]+" ");  pl();}
    static public void p  (int[]  a)	{  for(int i=0;i<a.length;i++) out.print(a[i]+" ");  pl();}
    static public void p  (long[] a)	{  for(int i=0;i<a.length;i++) out.print(a[i]+" ");  pl();}
    static public void p  (float[] a)	{  for(int i=0;i<a.length;i++) out.print(a[i]+" ");  pl();}
    static public void p  (double[] a)	{  for(int i=0;i<a.length;i++) out.print(a[i]+" ");  pl();}
    static public void p  (Object[] a)	{  for(int i=0;i<a.length;i++) out.print(a[i]+" ");  pl();}
    static public void p  (String[] a)	{  for(int i=0;i<a.length;i++) out.print(a[i]+" ");  pl();}
    static public void p  (byte[][] a)	{  for(int i=0;i<a.length;i++) {for(int j=0;j<a[i].length;j++) out.print(a[i][j]+" "); pl();} }
    static public void p  (char[][] a)	{  for(int i=0;i<a.length;i++) {for(int j=0;j<a[i].length;j++) out.print(a[i][j]+" "); pl();} }
    static public void p  (int[][]  a)	{  for(int i=0;i<a.length;i++) {for(int j=0;j<a[i].length;j++) out.print(a[i][j]+" "); pl();} }
    static public void p  (long[][] a)	{  for(int i=0;i<a.length;i++) {for(int j=0;j<a[i].length;j++) out.print(a[i][j]+" "); pl();} }
    static public void p  (float[][]a)	{  for(int i=0;i<a.length;i++) {for(int j=0;j<a[i].length;j++) out.print(a[i][j]+" "); pl();} }
    static public void p  (double[][]a)	{  for(int i=0;i<a.length;i++) {for(int j=0;j<a[i].length;j++) out.print(a[i][j]+" "); pl();} }
    static public void p  (String[][]a)	{  for(int i=0;i<a.length;i++) {for(int j=0;j<a[i].length;j++) out.print(a[i][j]+" "); pl();} }
    static public void p  (Object[][]a)	{  for(int i=0;i<a.length;i++) {for(int j=0;j<a[i].length;j++) out.print(a[i][j]+" "); pl();} }
    
    static public void pl ()		  	{  out.println();   }
    static public void pl (byte b)		{  out.println(b);  }	//umsonst -> int
    static public void pl (char c)		{  out.println(c);  }
    static public void pl (int  d)		{  out.println(d);  }
    static public void pl (long d)		{  out.println(d);  }
    static public void pl (float f)		{  out.println(f);  }
    static public void pl (double d)	{  out.println(d);  }
    static public void pl (String s) 	{  out.println(s);  }
    static public void pl (Object o)	{  out.println(o);  }
    static public void pl (byte[] b)	{  for(int i=0;i<b.length;i++) out.println(b[i]);  pl();}
    static public void pl (char[] c)	{  for(int i=0;i<c.length;i++) out.println(c[i]);  pl();}
    static public void pl (int[]  a)	{  for(int i=0;i<a.length;i++) out.println(a[i]);  pl();}
    static public void pl (long[] a)	{  for(int i=0;i<a.length;i++) out.println(a[i]);  pl();}
    static public void pl (float[] a)	{  for(int i=0;i<a.length;i++) out.println(a[i]);  pl();}
    static public void pl (double[] a)	{  for(int i=0;i<a.length;i++) out.println(a[i]);  pl();}
    static public void pl (Object[] a)	{  for(int i=0;i<a.length;i++) out.println(a[i]);  pl();}
    static public void pl (String[] a)	{  for(int i=0;i<a.length;i++) out.println(a[i]);  pl();}
    static public void pl (byte[][] a)	{  for(int i=0;i<a.length;i++) {for(int j=0;j<a[i].length;j++) out.print(a[i][j]+" "); pl();} }
    static public void pl (char[][] a)	{  for(int i=0;i<a.length;i++) {for(int j=0;j<a[i].length;j++) out.print(a[i][j]+" "); pl();} }
    static public void pl (int[][]  a)	{  for(int i=0;i<a.length;i++) {for(int j=0;j<a[i].length;j++) out.print(a[i][j]+" "); pl();} }
    static public void pl (long[][] a)	{  for(int i=0;i<a.length;i++) {for(int j=0;j<a[i].length;j++) out.print(a[i][j]+" "); pl();} }
    static public void pl (float[][]a)	{  for(int i=0;i<a.length;i++) {for(int j=0;j<a[i].length;j++) out.print(a[i][j]+" "); pl();} }
    static public void pl (double[][]a)	{  for(int i=0;i<a.length;i++) {for(int j=0;j<a[i].length;j++) out.print(a[i][j]+" "); pl();} }
    static public void pl (String[][]a) {  for(int i=0;i<a.length;i++) {for(int j=0;j<a[i].length;j++) out.print(a[i][j]+" "); pl();} }
    static public void pl (Object[][]a)	{  for(int i=0;i<a.length;i++) {for(int j=0;j<a[i].length;j++) out.print(a[i][j]+" "); pl();} }
    
    @SuppressWarnings("rawtypes")
	static public void pl (List list)		{  for(Object o : list) pl(o);  }
    @SuppressWarnings("rawtypes")
    static public void pl (List[] listA)	{  for(List list : listA) for(Object o : list) pl(o);  }
    
    /** print bytes of string **/
    static public void pl_Bytes (String s) 	{  for(int i=0; i<s.length(); i++) out.print((byte)s.charAt(i)+","); Out_.pl();  }
    
    /**
     * Print a map.
     * Problem: TODO: Always uses Object.toString() ??
     * @param map
     */
    static public void pl_Map (Map<Object,Object> map)     { 
    	
		Set<Entry<Object,Object>> entrySet = map.entrySet();
		
		int i=0;
		for(Entry<Object,Object> e : entrySet) {
			Object key = e.getKey();
			Object value = e.getValue();
			
			p(""+i+".) key = '"); 
			p(key); 
			p("', value = '"); 
			p(value); 
			p("'");
			pl();
			i++;
		}
    }
    
    
    // --- more stuff ------------------------------
    
    /**
     * Normal sleep with try+catch
     * @return: true if error
     */
    static public boolean sleep(long ms)	{  					
    	try{ 
    		Thread.sleep(ms);
    	}
    	catch(Exception e){
    		return true;		//error
    	}
    	return false;			//no error
    }
    

}
