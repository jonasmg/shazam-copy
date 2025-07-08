# Music recognition prototype
Welcome to this Shazam prototype by Jonas.
It uses STFT to calculate a fingerprint and compares the database finerprints to an incoming sound clip.
Remember to open using docker container.

# Main.java
The main file is where the magic is happening.
To use it there are a few parameter lists to fill out and some refrence strings to folders, but they have explenations inside them.
Run the Main file and the output will print in the terminal.
There are some extra setting, like printing the spectrogram to a png and detailed or less detailed print of the results.
But those arent implemented in a nice way and requires knowledge of how it all works together to be changed or commented in.
Right now genre printing doesn't work since the music files are deleted to save space.

# Music database structure
Folder holding all the music: SongList
It then has folders for genres, for example: "rock pop" or "electronic"
In them theres the folders for artists and albums, example: "Pink Floyd - The Wall disc1"
And inside them there should be "Track 1", "Track 2", ...
Filetype being WAV

# Snippet List
Here is an example of how to name a snippet: "Coldplay - a rush of blood to the head_Track 2_cut1mobile"
The "cut1mobile" can be replaced with any other text to describe the clip

# FourierLists
Is the main folder for the music database

# FourierListsInput
Is the main folder for the snippets. Right now it holds all the calculated snippet from snippetListGenerated20.
You can rename the folder to something else and calculate the "snippetListQuality" which holds mobile phone recordings!
Just change the folder used for clips in the main file!

Or you can look at the "snippetListQualityFullOutput.txt" in the "ResultsFolder", where I saved the output of all it prints with the current settings.
