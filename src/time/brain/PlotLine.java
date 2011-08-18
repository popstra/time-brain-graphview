package time.brain;

import java.util.ArrayList;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

/* Android GraphView
Copyright (C) 2011  Eric Dugre

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class PlotLine extends Path {

	private int x_ticks;
	private ArrayList<Plot> plots;
	private String label = "";
	private Paint line_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	public PlotLine(String title, int plots) {
		setColor(0xff000000);
		setWidth(2f);
		label = title;
		line_paint.setStyle(Paint.Style.STROKE);
		setPlotCount(plots);
	}
	
	/** Sets the number of data plots the PlotLine will display. Generally, you'll just use something like {@code ArrayList.size()}, but you could limit the number of plots to as few or as many as you'd like.
	 * <br/><br/>Essentially, this is the number of times {@link DataRequestReceiver.onDataRequested()} will be called.
	 * @param number the number of plots desired
	 * @return this PlotLine, for chaining
	 */
	public PlotLine setPlotCount(int number) {
		x_ticks = number;
		// Resize array and fill with empty Ticks
		plots = new ArrayList<Plot>(number);
		for(int i=0; i<number; i++) plots.add(new Plot());
		return this;
	}
	
	/** Sets <i>all</i> Plots on this PlotLine to bulge/unbulge. */
	public void setPlotsBulging(boolean show) {
		for(Plot plot : plots) plot.setBulge(show);
	}
	
	/** Returns the title of this PlotLine. This is important to use within a DataRequestReceiver to discern what PlotLine called it. */
	public String getTitle() {
		return label;
	}
	
	public int getPlotCount() {
		return x_ticks;
	}
	
	public ArrayList<Plot> getData() {
		return plots;
	}
	
	protected Paint getPaint() {
		return line_paint;
	}
	
	/** Sets the color of the line that will be drawn by this PlotLine. 
	 * @return this PlotLine, for chaining 
	 */
	public PlotLine setColor(int color) {
		line_paint.setColor(color);
		return this;
	}
	
	/** Sets the width of the line that will be drawn by this PlotLine. 
	 * @return this PlotLine, for chaining 
	 */
	public PlotLine setWidth(float width) {
		line_paint.setStrokeWidth(width);
		return this;
	}
	
	/** Sets data for a specific index on the PlotLine. This can be called manually whenever you want, but generally it is used
	 * in a registered DataRequestReceiver to autonomously fill data from an array.
	 * <br/><br/>This does not check index bounds, so make sure the index you request is not larger than you set in {@link #setPlotCount(int)}, minus 1.
	 * 
	 * @param index the index of the PlotLine to set data. Indices are zero-based
	 * @param data the data to set
	 */
	public void setPlotData(int index, int data) {
		plots.get(index).setData(data);
	}
	
	public class Plot extends Point {
		
		private int data;
		private boolean bulged = false;
		
		protected void setData(int num) {
			data = num;
		}
		
		protected int getData() {
			return data;
		}
		
		/** If enabled, a small circle will be displayed at the Plot's position. */
		protected void setBulge(boolean show) {
			bulged = show;
		}
		
		public boolean isBulged() {
			return bulged;
		}
		
	}
	
}
