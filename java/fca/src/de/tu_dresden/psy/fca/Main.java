package de.tu_dresden.psy.fca;

import java.io.BufferedWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class Main {
	public static void main(String[] args) throws Exception {

		BitSetContext ctx = new BitSetContext(15, 20);

		ctx.RandomizeContext(0.4);

		Lattice l = ctx.conceptLattice();
		
		System.out.println(l.Elements().size());
		
		System.out.println(l.bottom());
		
		System.out.println(l.bottom().equals(l.top()));
		
		

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(
					"/tmp/context.csv"));
			out.write(ctx.toCSV());
			out.close();
		} catch (IOException e) {
		}

		int count = 0, count2 = 0;

		long startTime0 = System.currentTimeMillis();

		System.out.println("NextClosure:");
		for (int q = 0; q < 10; ++q) {
			count = 0;
			for (FormalConcept b = ctx.bottomConcept(); b != null; b = ctx
					.nextClosure(b)) {
				count++;
			}
		}

		long startTime = System.currentTimeMillis();

		System.out.println("NextClosureWorseThanVanilla:");
		for (int q = 0; q < 10; ++q) {
			count = 0;
			for (FormalConcept b = ctx.bottomConcept(); b != null; b = ctx
					.nextClosureWorseThanVanilla(b)) {
				count++;
			}
		}

		long startTime2 = System.currentTimeMillis();

		System.out.println("NextClosureVanilla:");
		for (int q = 0; q < 10; ++q) {
			count2 = 0;

			for (FormalConcept b = ctx.bottomConcept(); b != null; b = ctx
					.nextClosureVanilla(b)) {
				count2++;
			}
		}

		long stopTime = System.currentTimeMillis();

		System.out.println(count + " concepts vs. " + count2
				+ " vanilla concepts.");

		System.out.println((startTime - startTime0) + " vs. "
				+ (startTime2 - startTime) + " vs. " + (stopTime - startTime2));
	}
}
