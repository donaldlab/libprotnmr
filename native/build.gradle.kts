
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

				linkerArgs.addAll("-lCGAL", "-lCGAL_Core", "-lgmp", "-lmpfr")
			}
		}
	}
}

tasks {

	val javah by creating {
		group = "build"
		doLast {
			exec {
				val pkg = "edu.duke.cs.libprotnmr"
				commandLine("javah",
					"-cp", "../build/classes/java/main",
					"-o", "src/main/cpp/native.h",
					"$pkg.cgal.spherical.SphericalCgal",
					"$pkg.cgal.spherical.Circle3",
					"$pkg.cgal.spherical.CircularArc3",
					"$pkg.cgal.curves.CurvesCgal",
					"$pkg.cgal.curves.Intersector",
					"$pkg.cgal.curves.EllipticalCurve"
				)
			}
		}
	}
}
