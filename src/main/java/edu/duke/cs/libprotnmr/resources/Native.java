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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;


public class Native {

	static {

		try {

			// unpack the library from the resources path and write it to a temp dir
			String name = "libnative.so";
			Path tmp = Paths.get("/tmp/libprotnmr");
			Files.createDirectories(tmp);
			Path path = tmp.resolve(name);
			try (InputStream in = Resources.get("lib/" + name)) {
				if (in == null) {
					throw new NoSuchElementException("native library is missing from this build.");
				}
				Files.copy(in, path);
			}

			// then load the library
			System.load(path.toAbsolutePath().toString());

		} catch (IOException ex) {
			throw new RuntimeException("can't unpack native library", ex);
		}
	}

	public static void init() {
		// nothing to do here actually,
		// but if we can run this method,
		// then the static initializer is guaranteed to have run
	}
}
