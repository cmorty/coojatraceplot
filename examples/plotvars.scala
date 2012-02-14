import plot._
import info.monitorenter.gui.util.ColorIterator

val p = Plot("data")

/
val colit = new ColorIterator
colit.setSteps(24)

val members = List("resend","loss")
var size = 4 // bytes

def array2Int(arr: Array[Byte]):Int = arr.foldRight(0) { (byte, sum) => (sum << 8) + (byte & 0xFF) }

for(mote <- sim.allMotes) {
  val scan_stats = mote.memory.variable("scan_stats", CArray(size))
  for(s <- members) {
    var index = members.indexOf(s)

  	val data = Trace(p, s + "(" + mote.toString + ")", 10000, colit.next() )
    log(data,  *(&(scan_stats) + index).map(array2Int))
  }
}