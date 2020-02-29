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
