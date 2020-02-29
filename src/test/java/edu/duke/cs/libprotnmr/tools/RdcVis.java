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

package edu.duke.cs.libprotnmr.tools;

import edu.duke.cs.libprotnmr.cgal.curves.RdcCurve;
import edu.duke.cs.libprotnmr.geom.Vector3;
import edu.duke.cs.libprotnmr.kinemage.Kinemage;
import edu.duke.cs.libprotnmr.kinemage.KinemageBuilder;
import edu.duke.cs.libprotnmr.kinemage.KinemageColor;
import edu.duke.cs.libprotnmr.kinemage.KinemageWriter;
import edu.duke.cs.libprotnmr.mapping.NameMapper;
import edu.duke.cs.libprotnmr.mapping.NameScheme;
import edu.duke.cs.libprotnmr.nmr.AlignmentTensor;
import edu.duke.cs.libprotnmr.nmr.Rdc;
import edu.duke.cs.libprotnmr.nmr.RdcMapper;
import edu.duke.cs.libprotnmr.nmr.RdcReader;
import edu.duke.cs.libprotnmr.pdb.ProteinReader;
import edu.duke.cs.libprotnmr.protein.AtomAddressInternal;
import edu.duke.cs.libprotnmr.protein.AtomAddressReadable;
import edu.duke.cs.libprotnmr.protein.Protein;
import edu.duke.cs.libprotnmr.resources.Resources;

import java.util.List;


public class RdcVis
{
	public static void main( String[] args )
	throws Exception
	{
		// read in the structure
		Protein protein = new ProteinReader().read(Resources.get("2KIQ.pdb"));
		NameMapper.ensureProtein(protein, NameScheme.New);
		
		// read in some NH RDCs
		List<Rdc<AtomAddressReadable>> unmappedRdcsNh = new RdcReader().read(Resources.get("2KIQ.nh.rdcs.mr"));
		NameMapper.ensureAddresses(protein.getSequences(), unmappedRdcsNh, NameScheme.New);
		List<Rdc<AtomAddressInternal>> rdcsNh = RdcMapper.mapReadableToInternal(protein, unmappedRdcsNh);
		
		// compute an alignment tensor
		AlignmentTensor tensorNh = AlignmentTensor.compute(protein, rdcsNh);
		System.out.println("NH RDC RMSD: " + tensorNh.getRmsd(protein, rdcsNh));
		System.out.println(tensorNh.getStats());
		
		Kinemage kin = new Kinemage();

		// plot the tensor axes
		KinemageBuilder.appendAxes(kin, tensorNh.getXAxis(), tensorNh.getYAxis(), tensorNh.getZAxis());

		// show a few of the RDCs
		int[] indices = { 5, 10, 15 };
		for (int i : indices) {

			Rdc<AtomAddressInternal> rdc = rdcsNh.get(i);

			// skip RDCs we can't visualize
			if (!tensorNh.isRdcInRange(rdc)) {
				continue;
			}

			// use the unmapped RDC to get a human-readable name
			Rdc<AtomAddressReadable> unmappedRdc = unmappedRdcsNh.get(i);
			String rdcName = unmappedRdc.toString();

			// show the curve (both arcs)
			for (int arcnum=0; arcnum<=1; arcnum++) {
				RdcCurve curve = new RdcCurve(tensorNh, rdc.getValue(), arcnum);
				String name = rdcName + ", arc " + arcnum;
				KinemageBuilder.appendCurve(kin, curve, name, KinemageColor.SkyBlue, 1);
			}

			// show points where the bond vectors in the protein hit the curves
			// the points won't hit the curves exactly of course,
			// since the RDC RMSD isn't exactly 0, but they should be sort of close
			Vector3 v = new Vector3();
			v.set(protein.getAtom(rdc.getTo()).getPosition());
			v.subtract(protein.getAtom(rdc.getFrom()).getPosition());
			v.normalize();
			KinemageBuilder.appendPoint(kin, v, rdcName + ", vector", KinemageColor.Orange, 5);
		}

		new KinemageWriter().showAndWait(kin);
	}
}
