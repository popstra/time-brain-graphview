package time.brain;

import java.text.DecimalFormat;
import java.util.ArrayList;

import time.brain.PlotLine.Plot;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

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
public class GraphView extends ImageView {

	
	private final String XMLNS = "http://schemas.android.com/apk/res/android";
	private String CUSTOM_XMLNS;
	private Context context;
	private int background_color, width, height, y_ticks, max, min = 0;
	/** Represents the value of a single pixel. */
	private double scale;
	private float title_size = 18f;
	private DataRequestReceiver data_receiver;
	private String tag;
	private Paint text_paint = new Paint(Paint.LINEAR_TEXT_FLAG), guideline = new Paint(), circles = new Paint(Paint.ANTI_ALIAS_FLAG);
	private ArrayList<PlotLine> lines = new ArrayList<PlotLine>();
	private PopupWindow popup;
	private boolean alwaysDrawCircles, popupEnabled, userProvidedMax, userProvidedMin;
	
	/** Dismisses the PopupWindow, as well as un-bulging all Plots. */
	private Runnable clearpopup = new Runnable() {
		@Override
		public void run() {
			if(popup != null) popup.dismiss();
			if(!alwaysDrawCircles) for(PlotLine line : lines) line.setPlotsBulging(false);
			invalidate();
		}
	};
	
	public GraphView(Context context) {
		super(context);
		init(context, 0xff000000, "", 5, false, true, 100, 0);
	}

	public GraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		CUSTOM_XMLNS = "http://schemas.android.com/apk/res/" + context.getPackageName();
		int temp_max = attrs.getAttributeIntValue(CUSTOM_XMLNS, "maxValue", -123456);
		if(temp_max == -123456) {
			temp_max = 100;
			userProvidedMax = false;
		}
		else userProvidedMax = true;
		int temp_min = attrs.getAttributeIntValue(CUSTOM_XMLNS, "minValue", 123456);
		if(temp_min == 123456) {
			temp_min = 0;
			userProvidedMin = false;
		}
		else userProvidedMin = true;
		init(
				context,
				attrs.getAttributeIntValue(XMLNS, "background", 0xff000000),
				attrs.getAttributeValue(XMLNS, "tag"),
				attrs.getAttributeIntValue(CUSTOM_XMLNS, "numGuidelines", 7),
				attrs.getAttributeBooleanValue(CUSTOM_XMLNS, "drawCircles", false),
				attrs.getAttributeBooleanValue(CUSTOM_XMLNS, "enablePopup", true),
				temp_max, temp_min);
	}

	public GraphView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		CUSTOM_XMLNS = "http://schemas.android.com/apk/res/" + context.getPackageName();
		int temp_max = attrs.getAttributeIntValue(CUSTOM_XMLNS, "maxValue", -123456);
		if(temp_max == -123456) {
			temp_max = 100;
			userProvidedMax = false;
		}
		else userProvidedMax = true;
		int temp_min = attrs.getAttributeIntValue(CUSTOM_XMLNS, "minValue", 123456);
		if(temp_min == 123456) {
			temp_min = 0;
			userProvidedMin = false;
		}
		else userProvidedMin = true;
		init(
				context,
				attrs.getAttributeIntValue(XMLNS, "background", 0xff000000),
				attrs.getAttributeValue(XMLNS, "tag"),
				attrs.getAttributeIntValue(CUSTOM_XMLNS, "numGuidelines", 7),
				attrs.getAttributeBooleanValue(CUSTOM_XMLNS, "drawCircles", false),
				attrs.getAttributeBooleanValue(CUSTOM_XMLNS, "enablePopup", true),
				temp_max, temp_min);
	}
	
	private void init(Context ctx, int color, String attr_tag, int num_yticks, boolean drawCircles, boolean doPopup, int mymax, int mymin) {
		context = ctx;
		background_color = color;
		if(attr_tag == null) tag = "";
		else tag = attr_tag;
		y_ticks = num_yticks;
		max = mymax;
		min = mymin;
		popupEnabled = doPopup;
		alwaysDrawCircles = drawCircles;
		text_paint.setStyle(Paint.Style.STROKE);
		text_paint.setTextSize(title_size);
		text_paint.setColor(darken(color, 0.8f));
		text_paint.setAntiAlias(true);
		guideline.setStyle(Paint.Style.STROKE);
		guideline.setColor(darken(color, 0.9f));
		guideline.setStrokeWidth(1f);
		circles.setStyle(Paint.Style.FILL_AND_STROKE);
	}
	
	/** Adds a new PlotLine to the GraphView. Forces calls to the DataRequestReceiver to fill the PlotLine(s), so you
	 * <b>must</b> call {@link #setOnDataRequestReceiver(DataRequestReceiver)} before you add any PlotLines.
	 * 
	 * @param line the PlotLine to add.
	 */
	public void addPlotLine(PlotLine line) {
		lines.add(line);
		if(alwaysDrawCircles) line.setPlotsBulging(true);
		requestData();
	}
	
	private void requestData() {
		for(PlotLine line : lines)
			for(int i=0; i<line.getPlotCount(); i++) if(data_receiver != null) data_receiver.onDataRequested(line, i);
	}
	
	/** Registers a DataRequestReceiver to the GraphView. The GraphView will call {@link onDataRequested()} each time it needs you to provide data for the graph.
	 * 
	 * <br/><br/>You must add <b>implements DataRequestReceiver</b> in your Activity. Without a receiver, that GraphView cannot graph.
	 * @param receiver the receiver
	 */
	public void setOnDataRequestReceiver(DataRequestReceiver receiver) {
		data_receiver = receiver;
	}
	
	/** Set the size of the text used to draw the title of the GraphView. The View's title is set via the <b>android:tag</b> xml property, or by {@link #setTitle(String title)}.
	 * @param size the text size in float, ie - 22f
	 */
	public void setTitleTextSize(float size) {
		title_size = size;
	}
	
	/** Sets the title that will be displayed in the upper-right corner of the GraphView. */
	public void setTitle(String title) {
		tag = title;
	}
	
	/** Returns a slightly darker color. */
	private int darken(int color, float amt) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= amt;
		return Color.HSVToColor(hsv);
	}
	
	/** Returns the GraphView's value at a specific pixel-height of the View. */
	private String getValueAtPixelHeight(int ypos) {
		int zero = getPixelHeightofZero();
		double val;
		if(ypos > zero) {
			// This occurs if ypos is LOWER on the View, because Views are measure from top-down
			val = -(Math.abs(zero - ypos) * (double)scale);
		}
		else val = (Math.abs(zero - ypos) * (double)scale);
		return format(val);
	}
	
	/** Returns the pixel-height on the View where the specified value occurs. */
	private float getPixelHeightOfValue(int value) {
		int zero = getPixelHeightofZero();
		float pixheight = (float) (zero - (value/scale));
		return pixheight;
	}
	
	private String format(double value) {
		return new DecimalFormat("@@@").format(value);
	}
	
	/** Returns the position of the x-axis on the View. */
	private int getPixelHeightofZero() {
		return (int) (height - (Math.abs(min)/scale));
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if(!popupEnabled) return false;
		if(e.getAction() != MotionEvent.ACTION_UP) return true;
		// Dismiss existing popup, unbulge Plots and cancel any pending clearpopup call
		if(popup != null) popup.dismiss();
		if(!alwaysDrawCircles) for(PlotLine line : lines) line.setPlotsBulging(false);
		removeCallbacks(clearpopup);
		LinearLayout container = new LinearLayout(context);
		container.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(5, 5, 5, 5);
		int new_color = darken(background_color, 0.7f);
		float[] hsv = new float[3];
		Color.colorToHSV(new_color, hsv);
		new_color = Color.HSVToColor(100, hsv);
		container.setBackgroundColor(new_color);
		// Get closest Plot to touch-x (using first PlotLine)
		float smallest_dist = 999;
		Plot closest_plot = null;
		ArrayList<Plot> plots = lines.get(0).getData();
		for(Plot plot : plots) {
			float diff = Math.abs(e.getX() - plot.x);
			if(diff < smallest_dist) {
				smallest_dist = diff;
				closest_plot = plot;
			}
		}
		int index = plots.indexOf(closest_plot);
		// Get the value of each PlotLine at the index and add text to popup
		for(PlotLine line : lines) {
			line.getData().get(index).setBulge(true);
			TextView tv = new TextView(context);
			tv.setTextColor(line.getPaint().getColor());
			tv.setText(line.getTitle() + " : " + line.getData().get(index).getData());
			container.addView(tv);
		}
		
		popup = new PopupWindow(container);
		popup.setWindowLayoutMode(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		popup.showAsDropDown(this, closest_plot.x, (int)(e.getY() - height));
		postDelayed(clearpopup, 2000);
		invalidate();
		return true;
	}

	@Override
	public void onDraw(Canvas canvas) {
		// Draw tag
		text_paint.setTextAlign(Paint.Align.RIGHT);
		text_paint.setTextSize(title_size);
		canvas.drawText(tag, width-2, text_paint.getTextSize(), text_paint);
		// Draw vertical lines. Uses the first PlotLine to measure
		int count = lines.get(0).getPlotCount();
		int space_between = (int) ((double)width/count);
		guideline.setStrokeWidth(1f);
		for(int i=1; i<count; i++) {
			float xpos = i*space_between;
			canvas.drawLine(xpos, height, xpos, 0, guideline);
		}
		// Draw horizontal lines. Autoscale first, so we know how to label lines
		autoscale();
		text_paint.setTextSize(title_size*0.7f);
		for(int y=1; y<y_ticks+1; y++) {
			double percent = y/(double)(y_ticks+1);
			float ypos = (float) (height - (height*percent));
			canvas.drawLine(0, ypos, width, ypos, guideline);
			// Get the value at this ypos
			String value = getValueAtPixelHeight((int) ypos);
			text_paint.setTextAlign(Paint.Align.LEFT);
			canvas.drawText(value, 2, ypos + text_paint.getTextSize()/2, text_paint);
		}
		// Draw bold line on x-axis
		guideline.setStrokeWidth(2f);
		int ypos_of_zero = getPixelHeightofZero();
		canvas.drawLine(0, ypos_of_zero, width, ypos_of_zero, guideline);
		// Plot all lines. Also sets x/y values to Plots for later use
		for(PlotLine line : lines) {
			line.reset();
			line.setLastPoint(0, getPixelHeightofZero());
			for(int index=0; index<line.getPlotCount(); index++){
				Plot plot = line.getData().get(index);
				plot.x = (index+1)*space_between;
				plot.y = (int) getPixelHeightOfValue(plot.getData());
				line.lineTo(plot.x, plot.y);
				// If Plot is bulged, draw circle. Circles is 3x as big as PlotLine thickness
				if(plot.isBulged()){
					circles.setColor(line.getPaint().getColor());
					canvas.drawCircle(plot.x, plot.y, line.getPaint().getStrokeWidth()*2, circles);
				}
			}
			canvas.drawPath(line, line.getPaint());
		}
		
	}
	
	/** Scans every Plot on every PlotLine to find the actual max and min. However, min will only be set if there is a Plot with
	 * negative value. This prevents creating a non-traditional graph, which would have a min above zero.
	 * 
	 * <br/><br/>After done scanning, this will set the global scale by calling {@link #getScale()}.
	 */
	private void autoscale() {
		int new_max = 0;
		int new_min = 0;
		for(PlotLine line : lines)
			for(Plot plot : line.getData()) {
				if(plot.getData() > new_max) new_max = plot.getData();
				// Only set min if it is negative
				if(plot.getData() < 0 && plot.getData() < new_min) new_min = plot.getData();
			}
		if(!userProvidedMax)max = new_max;
		if(!userProvidedMin)min = new_min;
		double scale = getScale();
		System.out.println("autoscale() max=" + max + " min=" + min + " scale=" + scale);
	}
	
	/** Sets global variable {@link #scale} by using other global variables: min, max and height. */
	private double getScale() {
		// Divide the pixel-height of the View by the total value of graph (values between min/max). Min is either 0 or negative, so subtracting is adding
		scale = (max - min) / (double)height;
		return scale;
	}
	
	@Override
	public void onSizeChanged(int neww, int newh, int oldw, int oldh) {
		width = neww;
		height = newh;
	}
	
	public interface DataRequestReceiver {
		
		/** Sent to the registered DataRequestReceiver when the GraphView needs data to plot.
		 * <br/><br/>Here, you provide the GraphView with data to plot at the given index of a PlotLine. The index is the position on the graph, determined by
		 * the amount of plots set by {@link PlotLine.setPlotCount()}. The most basic use of this receiver is to use a simple int[] array, call {@code PlotLine.setPlotCount(int[].length)}
		 * and in the receiver call {@link PlotLine.setPlotData(index, int[index])}.
		 */
		public void onDataRequested(PlotLine which, int index);
	}
}
