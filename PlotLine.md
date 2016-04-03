# Exposed Methods #

  * `public PlotLine setPlotCount(int n)` Sets the length of the PlotLine, in terms of how much data it contains. This is called automatically from the constructor, but users may also resize lines if needed. This clears all data from the PlotLine!

  * `public void setPlotsBulging(boolean do)` Tells the GraphView to draw circles at the points on this PlotLine, or not.

  * `public String getTitle()` Returns the title set by the constructor. Useful within a DataRequestReceiver.

  * `public int getPlotCount()` Returns the number of points on the PlotLine, aka the amount of data it contains. You could use this, for example, to resize all PlotLines on a GraphView to the size of one line.

  * `public ArrayList<Plot> getData()` Returns the ArrayList of data contained in this PlotLine.

  * `public Paint getPaint()` Returns the Paint used to draw this line on the GraphView. You may access the Paint directly to modify it's properties, rather than using the convenience methods.

  * `public PlotLine setColor(int color)` Sets the color of the Paint used to draw this PlotLine.

  * `public PlotLine setWidth(float f)` Sets the stroke width of the Paint used to draw this PlotLine.

  * `public void setPlotData(int index, int data)` Sets the data value of the point at the given index, where zero is the first data entry. Used within a DataRequestReceiver.