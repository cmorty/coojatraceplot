import se.sics.cooja.mspmote._ 

import plot._

import info.monitorenter.gui.util.ColorIterator

val p = Plot("Stack")


//Limit number of motes to plot
val maxid = 5

//
val colit = new ColorIterator
colit.setSteps(maxid)



for((id, mote) <- sim.motes; if id <= maxid) {
	val mspm : MspMote = mote.javamote
	val maxstack = mspm.getCPU.getDisAsm().getMap().stackStartAddress
	val col = 	colit.next()

	val dest_stack = Trace(p, mote.toString, 10000, col )
	log(dest_stack,  maxstack - mote.cpu.stackPointer)

}
