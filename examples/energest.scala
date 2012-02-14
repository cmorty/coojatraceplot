import plot._
import info.monitorenter.gui.util.ColorIterator

val p = Plot("energy")

// constants for sky motes
val voltage = 3 // V
val receiveConsumption = 20.0 * voltage // mW
val transmitConsumption = 17.7 * voltage // mW
val activeConsumption = 1.800 * voltage // mW
val idleConsumption = 0.0545 * voltage // mW


//Limit number of motes to plot
val maxid = 5

//
val colit = new ColorIterator
colit.setSteps(maxid)

for((id, mote) <- sim.motes; if id <= maxid) {
  val receiveTime = timeSum(mote.radio.receiverOn)
  val transmitTime = timeSum(mote.radio.transmitting)
  val activeTime = timeSum(mote.cpuMode === "active")
  val idleTime = timeSum(mote.cpuMode =!= "active")

  val energy: Signal[Double] = receiveTime / 1E6 * receiveConsumption +
    transmitTime / 1E6 * transmitConsumption +
    activeTime / 1E6 * activeConsumption +
    idleTime / 1E6 * idleConsumption

  val dest_energy = Trace(p, mote.toString, 10000, colit.next() )
  log(dest_energy,  energy)
}
