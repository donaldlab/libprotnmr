
import java.nio.file.Paths

plugins {
	`cpp-library`
}

library {

	linkage.set(listOf(Linkage.SHARED))

	binaries.configureEach {

		// add include dirs for the JVM to get JNI
		compileTask.get().apply {

			val jvmDir = Paths.get(System.getProperty("java.home"))
			val jdkIncludeDirs = listOf(
				jvmDir.resolve("../include"),
				jvmDir.resolve("../include/linux")
			)

			for (dir in jdkIncludeDirs) {
				compilerArgs.addAll("-I$dir")
			}
		}

		// tell the linker about the library dependencies
		if (this is ComponentWithSharedLibrary) {
			linkTask.get().apply {

				linkerArgs.addAll("-lCGAL", "-lgmp")
			}
		}
	}
}

tasks {

	val javah by creating {
		group = "build"
		doLast {
			exec {
				commandLine("javah",
					"-cp", "../build/classes/java/main",
					"-o", "src/main/cpp/native.h",
					"edu.duke.cs.libprotnmr.cgal.spherical.SphericalCgal",
					"edu.duke.cs.libprotnmr.cgal.spherical.Circle3",
					"edu.duke.cs.libprotnmr.cgal.spherical.CircularArc3"
				)
			}
		}
	}
}
