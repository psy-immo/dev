package de.tu_dresden.psy.fca;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.BitSet;

public class Main {
	public static void main(String[] args) throws Exception {
		
		BitSetContext ctx = new BitSetContext(100,150);
		
		ctx.RandomizeContext(0.4);
				
		System.out.println(ctx);
		
		try {
	        BufferedWriter out = new BufferedWriter(new FileWriter("/tmp/context.csv"));
	        out.write(ctx.toCSV());
	        out.close();
	    } catch (IOException e) {
	    }
		
		
		System.out.println("NextClosure:");
		int count = 0;
		for (FormalConcept b = ctx.bottomConcept();b!=null;b= ctx.nextClosure(b)) {
			count++;
		}
		
		System.out.println(count+" concepts.");
	
		
		
	}
}
