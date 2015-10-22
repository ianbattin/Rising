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

===================
SAVING AND LOADING:
===================

Ctrl + s: Save map

Ctrl + o: Open saved map

Ctrl + b: Import tileset


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

***
This tilemap editor only uses a tileset with two types of tiles: blocked and nonblocked.
Nonblocked tiles are on the first row and blocked tiles are on the second row.


NOTE: The Ctrl key gets stuck on keypress after saving or loading. Just tap Ctrl one more time to release it.