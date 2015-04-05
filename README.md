# MultiTouchPlaceholderView
Gesture-based image layer, to move it into placeholders (empty content within a bitmap)

This POC shows how to move,rotate, resize and change alpha an inner bitmap within an ImageView using gestures, and when you wish, prepare an output bitmap containing the changes.

Note: the POC uses a library called ["android-gesture-detectors"](https://github.com/Almeros/android-gesture-detectors) in order to handle the various gestures.

**Advantages**

 - Allows resizing using either 2 fingers or a single one (tap+dragTouch).
 - Allows panning
 - Allows shoving (moving of 2 fingers up/down)
 - Allows rotating using 2 fingers
 - Output image uses the background you've given, so memory usage is minimal (uses only 2 bitmaps - the moving one and the background).
 
 **Sample demo animation**

Here's an animation to show what it's capable of:

![enter image description here](https://raw.githubusercontent.com/AndroidDeveloperLB/MultiTouchPlaceholderView/master/demo.gif)

**License**

This project is licensed with the 2-clause BSD license, as [the library](https://github.com/Almeros/android-gesture-detectors) this POC uses is under this license.
