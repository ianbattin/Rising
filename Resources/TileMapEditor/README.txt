If you aren't using windows, run by opening opening the App.java in eclipse and hitting the play button

=========
CONTROLS:
=========

Arrow Keys: Move map

Left Click: Place tile on map/Select tile from tileset

Right Click: Place tile 0 on map (I usually use a clear tile for tile 0, so right click acts as erase)

Middle Click + Drag: Move map

Shift + Arrow Key: Change map size

Ctrl + Left/Right: Move tileset

Alt + Arrow Key: Shift map

t: Change tile size

z: Zoom out

x: Zoom in

m: Toggle gridlines

===================
SAVING AND LOADING:
===================

Ctrl + s: Save map

Ctrl + o: Open saved map

Ctrl + b: Import tileset

===========================
IMPLEMENTING MAP INTO GAME:
===========================

-Go into the class for the level you want to change (Example: Level1State.class)
-Find the init() method
-In the first line you should see something like "tileMap = new TileMap("Resources/Maps/level5.txt", 2);"
-Change the the String parameter in the constructor to be the path to your map.
	-If you placed the map in the correct folder, you should only have to change "level5.txt" to the "NAME_OF_YOUR_MAP.txt"
-If you want to have your map copied to the left or right # of times in game, change the "2" to the amount you wish.

========
EXAMPLE:
========

To run:
java TileMapEditor.App
or double click TileMapEditor.bat

When it starts up, there is no loaded tileset. There is a test tileset you can use as an example.
First thing to do is Ctrl + b, then type in "testtileset.gif" (or whatever tileset you want to use***)
Click a block from the loaded tileset and click anywhere on the map.
The red outline is the map boundary. You can ignore the green outline.
Ctrl + s to save map and Ctrl + o to open map.

NOTE: The blocks with the red X are placeholders to represent blocks that are invisible BUT THAT WILL CAUSE COLLISION IN GAME.
Use block 0 for air (it's the only invisible block that doesn't have a red X) 

***
This tilemap editor only uses a tileset with two types of tiles: blocked and nonblocked.
Nonblocked tiles are on the first row and blocked tiles are on the second row.


NOTE: The Ctrl key gets stuck on keypress after saving or loading. Just tap Ctrl one more time to release it.