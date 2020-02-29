/*******************************************************************************
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 * 
 * Contact Info:
 * 	Bruce Donald
 * 	Duke University
 * 	Department of Computer Science
 * 	Levine Science Research Center (LSRC)
 * 	Durham
 * 	NC 27708-0129 
 * 	USA
 * 	brd@cs.duke.edu
 * 
 * Copyright (C) 2011 Jeffrey W. Martin and Bruce R. Donald
 * 
 * <signature of Bruce Donald>, April 2011
 * Bruce Donald, Professor of Computer Science
 ******************************************************************************/
package edu.duke.cs.libprotnmr.chart;

import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.swing.JFrame;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlgraphics.java2d.GraphicContext;
import org.apache.xmlgraphics.java2d.ps.EPSDocumentGraphics2D;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public class ChartWriter
{
	/**************************
	 *   Definitions
	 **************************/
	
	private static final Logger m_log = LogManager.getLogger(ChartWriter.class);
	
	private static final int DefaultWidth = 800;
	private static final int DefaultHeight = 600;
	
	
	/**************************
	 *   Static Methods
	 **************************/
	
	public static void writePng( JFreeChart chart, File file )
	throws IOException
	{
		writePng( chart, file, DefaultWidth, DefaultHeight );
	}
	
	public static void writePng( JFreeChart chart, File file, int width, int height )
	throws IOException
	{
		file = changeExtension( file, "png" );
		// NOTE: sometimes rendering of ellipses bugs out, so turn off anti-aliasing if needed
		chart.setAntiAlias( true );
		ChartUtilities.saveChartAsPNG( file, chart, width, height );
		
		m_log.info( "Wrote chart to:\n\t" + file.getAbsolutePath() );
	}
	
	public static void writeEps( JFreeChart chart, File file )
	throws IOException
	{
		writeEps( chart, file, DefaultWidth, DefaultHeight );
	}
	
	public static void writeEps( JFreeChart chart, File file, int width, int height )
	throws IOException
	{
		file = changeExtension( file, "eps" );
		
		OutputStream out = new FileOutputStream( file );
		EPSDocumentGraphics2D g2d = new EPSDocumentGraphics2D( false );
		g2d.setGraphicContext( new GraphicContext() );
		g2d.setupDocument( out, width, height );
		chart.draw( g2d, new Rectangle( width, height ) );
		g2d.finish();
		out.flush();
		out.close();
		
		m_log.info( "Wrote chart to:\n\t" + file.getAbsolutePath() );
	}
	
	public static void writeSvg( JFreeChart chart, File file )
	throws IOException
	{
		writeSvg( chart, file, DefaultWidth, DefaultHeight );
	}
	
	public static void writeSvg( JFreeChart chart, File file, int width, int height )
	throws IOException
	{
		file = changeExtension( file, "svg" );
		
		// create the xml document
		DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation(); 
		Document document = domImpl.createDocument( null, "svg", null ); 
		
		// Create an instance of the SVG Generator
		SVGGraphics2D svgGenerator = new SVGGraphics2D( document );
		svgGenerator.getGeneratorContext().setPrecision( 6 );
		
		chart.draw( svgGenerator, new Rectangle( width, height ) );
		
		// write the file
		svgGenerator.stream( new OutputStreamWriter( new FileOutputStream( file ), "UTF-8"), true );
		
		m_log.info( "Wrote chart to:\n\t" + file.getAbsolutePath() );
    }
	
	public static void show( JFreeChart chart )
	{
		ChartFrame frame = new ChartFrame( chart.getTitle().getText(), chart );
		frame.pack();
		frame.setVisible( true );
	}
	
	public static void showAndWait( JFreeChart chart )
	{
		final ChartFrame frame = new ChartFrame( chart.getTitle().getText(), chart );
		frame.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
		frame.pack();
		frame.setVisible( true );
		
		// create a signal object for thread communication
		final Object signal = new Object();
		
		// when the frame is closed, hit signal
		frame.addWindowListener( new WindowAdapter( )
		{
			@Override
			public void windowClosing( WindowEvent event )
			{
				synchronized( signal )
				{
					frame.setVisible( false );
					signal.notify();
				}
			}
		} );
		
		// wait for the signal
		synchronized( signal )
		{
			while( frame.isVisible() )
			{
				try
				{
					signal.wait();
				}
				catch( InterruptedException ex )
				{
					m_log.warn( "Interrupted while waiting on frame!", ex );
				}
			}
		}
	}
	
	
	/**************************
	 *   Static Functions
	 **************************/
	
	private static File changeExtension( File file, String extension )
	{
		String[] parts = file.getName().split( "\\." );
		String newName = "default." + extension;
		if( parts.length == 1 )
		{
			newName = parts[0] + "." + extension;
		}
		else
		{
			parts[parts.length - 1] = extension;
			StringBuilder buf = new StringBuilder( file.getName().length() + extension.length() + 1 );
			buf.append( parts[0] );
			for( int i=1; i<parts.length; i++ )
			{
				buf.append( "." );
				buf.append( parts[i] );
			}
			newName = buf.toString();
		}
		return new File( file.getParentFile(), newName );
	}
}
