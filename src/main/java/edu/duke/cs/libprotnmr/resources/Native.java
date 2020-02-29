/*
 * This file is part of LibProtNMR
 *
 * Copyright (C) 2020 Bruce Donald Lab, Duke University
 *
 * LibProtNMR is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * You should have received a copy of the GNU General Public License
 * along with LibProtNMR.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact Info:
 *    Bruce Donald
 *    Duke University
 *    Department of Computer Science
 *    Levine Science Research Center (LSRC)
 *    Durham
 *    NC 27708-0129
 *    USA
 *    e-mail: www.cs.duke.edu/brd/
 *
 * <signature of Bruce Donald>, February, 2020
 * Bruce Donald, Professor of Computer Science
 */

package edu.duke.cs.libprotnmr.resources;

import java.io.File;


public class Native {

	static {

		String libName = "native";
		File libFile = new File("native/build/lib/main/debug/lib" + libName + ".so");

		// first, try to load the library from the current directory
		if (libFile.exists()) {

			try {
				System.load(libFile.getAbsolutePath());
			} catch (UnsatisfiedLinkError err) {
				String message =
					"Unable to load required library!"
						+ " Library found at path:"
						+ "\n" + libFile.getAbsolutePath()
						+ "\nbut loading failed. If running on linux, make sure CGAL package is installed.";
				throw new Error(message, err);
			}

		} else {

			try {
				System.loadLibrary(libName);
			} catch (UnsatisfiedLinkError err) {
				String message =
					"Unable to find required library!"
						+ " These directories were checked:"
						+ "\n" + System.getProperty( "java.library.path" )
						+ "\nOr, try putting it here:"
						+ "\n" + libFile.getAbsolutePath();
				throw new Error(message, err);
			}
		}
	}

	public static void init() {
		// nothing to do here actually,
		// but if we can run this method,
		// then the static initializer is guaranteed to have run
	}
}
