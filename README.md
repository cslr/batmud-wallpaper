# batmud-wallpaper
BatMUD wallpaper changer plugin (based on location/whereami)
tomas.ukkonen@iki.fi, 2022

Currently this plugin shows randomly chosen background pictures (jpg and png files) from the directory "C:\batmud-pictures\".
Picture changes everytime area changes (global coordinates of BatMUD whereami command changes within area).
Because this plugin overrides use of whereami command to get coordinates, you cannot use 'whereami' command when using this plugin. Instead, there is a partial support for "tellmelocation" command which reports almost same information. 

Pictures (png and jpg) must be in directory: "System.getProterty("user.dir") / batmud-pictures/". The location is told when the plugin loads if the directory doesn't exist.

It is also possible to create "pictures.txt" file containing coordinates of area and filename of a picture per each line which shows specific picture for that area. Example file is below. There is no support but report serious bugs to me.

```
# Pictures file pictures.txt
# Format:
# <coordinates> <filename-picture-file-without-spaces>
# <description> <filename-picture-file-without-spaces>

# Laenor 
8557x, 8666y arelium.jpg
8555x, 8664y arelium.jpg

## bug: continent-wilderness backgrounds don't work for now
# which is on the continent of Laenor laenor.jpg

# Calythien
7266x, 9285y calythien.jpg

## bug: continent-wilderness backgrounds don't work for now
# which is on the continent of Desolathya desolathya.jpg
```

