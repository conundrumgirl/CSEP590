# CSEP590

I.	This is a simplified step tracker which increments the counter based on the steps taken by the user and provides visual feedback for each step taken.  
Limitations: It does not have the data storage mechanism so the number is reset back to 0 every time the application runs. Also, it expects the user to have a phone in a static position in the hand and does not account for bouncing of other additional motion of the phone which would happen in more realistic conditions (i.e.) taking it out of the pocket, looking at the screen, changing the location etc. 

II.	The accelerometer senses acceleration along its three axes: x, y, and z.  The pedometer will be in an unknown orientation, so the measurement accuracy should not depend on the relationship between the motion direction and the accelerometer's measurement axes. Instead we look at the total increase in acceleration. I am using SENSOR_DELAY_NORMAL  sensitivity setting because we don’t need the sensor to be overly responsive. 
a.	I am using the moving average to smooth the signal coming from the accelerometer. with the window of 45.  
b.	After the signal is smoothed. I apply a low pass filter to remove the gravity component(from https://developer.android.com/reference/android/hardware/SensorEvent.html).  
c.	After the signal is filtered I calculate the magnitude by taking the square root of the sum of squares of each axis value. One caveat is that I have experimentally found that I get a better accuracy if instead of squaring the smoothed value I multiply the smooth value by the raw value. It is possible that my smoothing window is too large but I ran out of time so I used that approach that I know have worked.
d.	 I detect the peak using 3 values (n-1, n, n+1). It is a peak if n-1 <n<n+1.
e.	I also place an additional limitation for the ‘step’ to happen its magnitude must be larger than an experimentally determined constant and it had to have occurred at least .2 seconds after the previous step to prune some potential fake positives.

III.	The feedback interface displays the number of steps user has taken and provides the creative visual representation. The incremented step count provides the immediate feedback while the creative representation updates at a predetermined interval (every 10 steps) to reduce he processing load.
a.	For my creative feedback I am displaying fractal trees that appear and grow with every 10/step count.
b.	The color and position of the trees are random (after the first one which appear in the middle) to add an element of surprise and encourage more activity. It is tested to be viable at 10000 steps level (current recommended amount). Ideally Would be great to have something at goal.

IV.	This was a challenging project due to the large number of knowns for me. I was able to find a number of papers on using accelerometer for step detection which were helpful in understanding the problem1.  Also working through specifics of mobile development took some time. As far as working with sensors but biggest challenge was applying the algorithms to deduce the activity from the signal. There is a large variation on walking patterns and extrapolation from single users’ data to activity relationship is a challenge. I found the debugging to be quite difficult due to the frequency of the events triggered. What I found helpful is being able to adjust the values on the fly from the debugging interface a well as being able to simulate the ‘step’ (img.3 – debugger)

V.	Along with the mechanics of android development, this was the first time I have worked with the signal processing and I have learned a number of concepts, such as low-pass and high-pass filtering, signal smoothing, peak detection etc.
Looking back the one thing I would do differently if I were starting on the project now is I would capture the data first and store it and then would work with the loaded data.  I would also get data samples from other people to see if my algorithm was robust to deal with other walking patterns

User Interface
User interface at 10000 steps
Debugger with
android step sensor
Debugger with
magnitude values








1 Lee H-H, Choi S, Lee M-J. Step Detection Robust against the Dynamics of Smartphones. Sensors. 2015; 15(10):27230-27250.  http://www.mdpi.com/1424-8220/15/10/27230


Lin J, Chan L, Yan H. A Decision Tree Based Pedometer and its Implementation on the Android Platform 

http://www.ee.cityu.edu.hk/~lhchan/Publications/2015%20Conf%20Proc%20SSIP%20Lin%20csit53507.pdf



