# Introduction #

Here you will learn to do the following:

  * Implement GraphView's in XML files
  * Add multiple lines to graphs
  * Use DataRequestReceiver to provide data

These are some noteworthy limitations of the GraphView:

  * PlotLines must be equal in length (aka data arrays of the same size)
  * Only `int` variables are accepted by the graph at this time. This will change in the future.


# Setting Up #

To begin using the GraphView API, you'll need 2 class files and 1 XML file from the project site at googlecode. At the [source tab](http://code.google.com/p/time-brain-graphview/source/browse/), browse to src/time/brain and grab the **2 Java files**. You can do this by simple copy/paste. Put these files in a new package called `time.brain`

Also get the xml file located in res/values/**attrs.xml**. This is a very small file, but it is very important! Place this file in your project's `res/values`. If your projects already has an `attrs.xml`, just copy the text between the `<declare-styleable>` tags (including the tags themselves) and paste it in the existing file. Our tutorial project now looks like this:

```
TestProject
   -src
       -com.test.project
           MainActivity.java

       -time.brain
           GraphView.java
           PlotLine.java
   -res
       -values
           attrs.xml
```

# Writing XML #

Open the `main.xml` file in `/layout` that Eclipse generates for you. Make it look so:

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:timebrain="http://schemas.android.com/apk/time.brain" <!-- NOTE -->
android:orientation="vertical"
android:layout_width="fill_parent"
android:layout_height="fill_parent">

        <time.brain.GraphView
        android:id="@+id/mygraph"
	android:background="#cccccc"
	android:layout_width="fill_parent"
	android:layout_height="200dp"
	/>

</LinearLayout>
```

Notice the line we've added to `LinearLayout`. That'll come in handy later. For now, we can actually run this application displaying this layout and you'll see a grey graph! All it has is a few lines and numbers.. not very fun. Let's add some data to the graph.

# The GraphView Object #

Start out by copying this into your Main.java file (or whatever you called your Activity). For the tutorial, we're going to graph the number of apples I had during one week, hence the `int[]` array with 7 entries. The `DataRequestReceiver` interface is how the GraphView will "talk" to your Activity. It will ask you for data any time it needs it, and you can hand-craft the data you want to give it.

```
public class Main extends Activity implements DataRequestReceiver {
	
    int[] apples = {2,5,3,6,7,4,11};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public void onDataRequested(PlotLine which, int index) {
		
    }
}
```

But our GraphView doesn't have any lines on it yet. Let's add one by inserting the following 2 lines in `onCreate()`

```
GraphView graph = (GraphView) findViewById(R.id.mygraph);
graph.addPlotLine(new PlotLine("Apples", 7).setColor(Color.RED));
```

Clearly, in the first line we got our instance of GraphView and set it to a variable `graph`. Let's look at the second line.. We called `addPlotLine()` on our graph, while also making a new `PlotLine` object.

A `PlotLine` represents just one line on the graph, aka one whole set of data. When we make a new `PlotLine`, we need to make it unique so we provide a String for it's name, and a "size." This is how much data you want to add to the line. We're graphing one week, so 7 days will do. You don't _need_ to use `setColor()`, but it sure makes a graph more appealing. PlotLines also have a `setWidth()` method, which allows you to make some lines thicker than others.

The above code works fine, but we're going to modify it a bit. We're going to add the line `graph.setOnDataRequestReceiver(this)` as well. Here's how your Activity should look now:

```
public class Main extends Activity implements DataRequestReceiver {
	
    int[] apples = {2,5,3,6,7,4,11};
    private final String APPLE_LINE = "Apples";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        GraphView graph = (GraphView) findViewById(R.id.mygraph);
        graph.addPlotLine(new PlotLine(APPLE_LINE, apples.length).setColor(Color.RED));
    }

    @Override
    public void onDataRequested(PlotLine which, int index) {
		
    }
}
```

Using a String variable and `apples.length` make this code more fool-proof, and will help when you add more than one PlotLine to your GraphView.

We still can't run this, however. We've just told the GraphView that the PlotLine called "Apples" needs information 7 times, and that the GraphView needs to talk to the Activity to get it. We need to put some logic into `onDataRequested()` to add data to the graph.

```
@Override
public void onDataRequested(PlotLine which, int index) {
    if(which.getTitle() == APPLE_LINE) which.setPlotData(index, apples[index]);
}
```

Above is the most basic use of the `DataRequestReceiver`. It tells you which PlotLine wants data, and where it wants it. You respond by saying "Ok, if the line is called 'Apples' and the day is Monday, the data is 5." And so on. If you have more than one PlotLine on the GraphView, this is where you would need to provide separate logic per line. If you had something like `BANANA_LINE` and `banana[]`, you'd add an ` else if` and basically the same logic. It is important to note that when you have multiple PlotLines, they should be of equal length.

Run your Activity now. You now have a red line showing the data we gave it! Now try touching the graph and behold- it tells you the value at any point on the graph! If you touch the graph then rotate your device, you will notice a problem: Gah! A force close! This can be fixed, and it is <font color='red'><b>important</b></font> to do so. You don't want your app closing on people, do you? Open your manifest and make the Activity look like this:

```
<activity android:name=".Main"
android:label="@string/app_name"
android:configChanges="orientation|keyboardHidden">
```

The `configChanges` tells Android we don't want to destroy the Activity when we rotate it, just adapt to the new view. `keyboardHidden` isn't necessary, but I'd add that as well to stop soft-keyboards from opening.

# Additional XML Variables #

Now that we can see what our graph looks like, let's review some more options for it's appearance. The variables you have access to are listed in `attrs.xml`, but this is what they'd look like in your xml file:

```
<time.brain.GraphView
android:id="@+id/mygraph"
android:background="#cccccc"
android:layout_width="fill_parent"
android:layout_height="200dp"
android:tag="Apples"
timebrain:numGuidelines="3"
timebrain:maxValue="15"
timebrain:minValue="-5"
timebrain:drawCircles="false"
timebrain:enablePopup="true"
timebrain:verticalGuidelines="true"
timebrain:drawZero="true"
/>
```

  * **android:background** You may change the background to anything you'd like. Guidelines and text will always be a slightly darker version of this color.
  * **android:tag** Setting a tag will enable a display in the upper-right corner of the graph, allowing you to label each GraphView.
  * **numGuidelines** The number of horizontal lines on the graph, meant to make it easier to "eyeball" a value. Also represents the number of scale labels on the left-hand side of the graph.
  * **maxValue/minValue** The min and max of the graph. By default, the graph automatically scales to find the highest and lowest values. If you set just the maxValue, then only the min will autoscale. If you set only the minValue, the max will autoscale. If you set both, autoscaling is disabled. If you know the type and range of data you'll be graphing, these variables can greatly improve the visual appeal of a graph.
  * **drawCircles** By default, the graph is a traditional line graph. By setting this to `true`, the GraphView will graph lines as well as small dots at the exact points.
  * **enablePopup** Set to `false` if you don't want information to display when the user touches the graph.
  * **verticalGuidelines** Set to `false` if you don't want to draw the guidelines that occur at each exact point on the GraphView.
  * **drawZero** Set to `false` to skip drawing of the x-axis, or where x=0.

# Conclusion #

You now have all the information you need to make graphs with multiple lines, and Activites with multiple graphs! These are just basic examples. You can tweak the `DataRequestReceiver` interface to graph complicated data like average child per classroom, profit increase and decrease, and so much more!