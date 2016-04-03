# Exposed Methods #

  * `public void addPlotLine(PlotLine line)` Adds the provided PlotLine to the GraphView. Forces a call to `requestData()`, so you should register a DataRequestReceiver before adding PlotLines.

  * `public void requestData()` Re-fills the data within _all_ PlotLines added to the GraphView. Calling this triggers requests to the DataRequestReceiver, if not null.

  * `public void setOnDataRequestReceiver(DataRequestReceiver receiver)` Registers a DataRequestReceiver to this GraphView. This must be called at least once, or the GraphView will not work.

  * `public void setTextSize(float f)` Sets the size of the text that is used to draw the GraphView's title and text labels on the y-axis.

  * `public void setTitle(String title)` Sets the title of the GraphView to be displayed in the upper-right corner. This can also be set via the xml attribute `android:tag`.