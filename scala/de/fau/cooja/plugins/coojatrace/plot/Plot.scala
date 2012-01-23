/*
 * Copyright (c) 2012, Florian Lukas
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package de.fau.cooja.plugins.coojatrace.rules.logrules



import se.sics.cooja._

import de.fau.cooja.plugins.coojatrace._
import rules._
import logrules._

import java.util.{Observer, Observable}
import javax.swing.JInternalFrame
import java.awt.Color

import info.monitorenter.gui.chart._



/**
 * Package for logging to a plot window.
 */
package plot {

/**
 * Wrapper class for a plot window.
 *
 * @param sim current simulation
 */
case class Plot(title: String)(implicit sim: Simulation) {
  /**
   * Newly created window (actually an [[InternalFrame]]).
   */
  val window = new JInternalFrame(title, true, true, true, true)

  /**
   * Chart to plot data.
   */
  val chart = new Chart2D
  chart.getAxisX.getAxisTitle.setTitle("Time / s")
  chart.getAxisX.setFormatter(new labelformatters.LabelFormatterAutoUnits)

  // add chart to window and show
  window.add(chart)
  window.setSize(300, 500)
  window.show()
  sim.getGUI.getDesktopPane.add(window)
  try {
    window.setSelected(true)
  } catch {
    case e: java.beans.PropertyVetoException => // ignore
  }

  // close window on plugin deactivation
  CoojaTracePlugin.forSim(sim).onCleanUp {
    window.dispose()
  }
}

/**
 * A [[LogDestination]] which draws into a plot window.
 *
 * @param db [[SQLiteQueue]] object for the database in which table is found
 * @param table database table to write to. Will be created or cleared if needed
 * @param limit maximum number of values shown at once
 * @param sim the current [[Simulation]]
 */
case class Trace(plot: Plot, name: String, limit: Int, color: Color = Color.RED)(implicit sim: Simulation) extends LogDestination {
  /**
   * Trace to be plotted in chart.
   */
  val trace = new traces.Trace2DLtd(limit)
  trace.setName(name)
  trace.setColor(color)
  trace.setPhysicalUnits("", "s")
  plot.chart.addTrace(trace)
  
  def log(values: List[_]) {
    // check for right number of columns
    require(values.size == 1, "incorrect column count")

    // convert to double
    val value: Double = values.head match {
      case d: Double => d
      case f: Float => f
      case i: Int => i
      case _ => values.head.toString.toDouble 
    }

    // insert new point
    trace.addPoint(sim.getSimulationTime/1E6, value)
  }

  // active as long as plot window is open
  def active = !plot.window.isClosed
}

} // package plot
