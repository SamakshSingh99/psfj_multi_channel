# PSFj
Know your microscope fluorescence

PSFj analyzes a stack of images of fluorescent beads to calculate the resolution of your microscope across the fields of view. PSFj is written in JAVA and compatible with Windows, MacOS X and Linux.


## Multichannel extension by Samaksh Singh

The multichannel analysis and reporting extensions described in this section were implemented by **Samaksh Singh**. They expand the original two-channel workflow while retaining the original PSFj resolution analysis and two-channel compatibility.

### Multichannel loading and configuration

- Supports 1 to 12 channels supplied either as separate image stacks or as channels embedded in a multidimensional image.
- Uses Bio-Formats Z/channel/time indexing when separating embedded channels instead of assuming a fixed two-channel plane order.
- Provides a multi-channel analysis mode for 2 to 12 loaded channels.
- Creates an emission-wavelength control for every loaded channel.
- Refreshes the wavelength controls whenever channels are added, detected in a composite stack, or removed. This avoids the previous issue where only the first two wavelength fields appeared after loading an RGB composite stack.
- Requires a non-zero wavelength for every channel before multichannel processing is considered ready.

### Chromatic-shift analysis

Channel 1 is the common reference and is compared independently with every other loaded channel. For example, a four-channel dataset produces these comparisons:

```text
Channel 1 vs Channel 2
Channel 1 vs Channel 3
Channel 1 vs Channel 4
```

Each reference bead can retain a separate matched bead and signed displacement for every target channel. Previously stored matches are cleared before reprocessing so changes to thresholds or filtering do not reuse stale pairings.

The following measurements are calculated for every matched bead pair:

- Delta X: signed horizontal displacement.
- Delta Y: signed vertical displacement.
- Delta Z: signed axial displacement.
- Delta XY: non-negative lateral distance, `sqrt(deltaX^2 + deltaY^2)`.
- Delta XYZ: non-negative three-dimensional distance, `sqrt(deltaX^2 + deltaY^2 + deltaZ^2)`.

### Interface and exported results

- The heat-map interface contains resolution sections for every channel and a separate chromatic-shift section for every reference-target pair.
- All five chromatic measurements are shown for each pair with pair-specific titles and matched-bead counts.
- Heat-map filenames include the reference and target image names so comparisons cannot overwrite one another.
- The PDF summary contains an independent chromatic-comparison section for every target channel, including all five heat maps and the correct matched-pair count.
- CSV export produces one measurement file per channel and one comparison file per Channel 1-to-target pair.
- Every pairwise comparison CSV contains bead IDs, reference and target coordinates, `delta x0`, `delta y0`, `delta z0`, `delta XY`, and `delta XYZ`.
- A separate chromatic-shift summary CSV contains one row per channel pair. It records channel numbers, image names, wavelengths, matched-pair counts, mean, median, sample standard deviation, minimum, maximum, P5, P25, P75, and P95 for all five shift measurements.

Pairwise files use the following collision-resistant pattern:

```text
channel_1_<reference>_vs_channel_<N>_<target>_comparison.csv
```

The combined summary uses:

```text
channel_1_<reference>_chromatic_shift_summary.csv
```

Both the original CSV option and the pre-existing machine-parsable "Results CSV (version 2)" option inherit the new multichannel pairwise and summary exports.

### Java compatibility and test data

The project source and NetBeans configuration target Java 8 bytecode. A complete command-line build can be produced with:

```bash
mkdir -p build/classes
javac --release 8 -encoding ISO-8859-1 \
  -cp "lib/*" \
  -d build/classes \
  $(find src -name "*.java")
rsync -a --exclude="*.java" src/ build/classes/
```

The repository includes `test-data/synthetic_rgb_composite_psf_stack.tif`, a three-channel, 15-plane synthetic RGB stack for exercising composite-channel detection, wavelength controls, and chromatic-shift processing. Its companion `.ini` file contains the test microscope configuration.


## Download and instructions
Please visit PSFj website [http://www.knoplab.de/psfj/
](http://www.knoplab.de/psfj)


## Developer manual

### Dependencies

All jar dependencies are located in the lib folder. You can use this Netbeans or Eclipse to edit the code.


### Introduction to the data processing

The data processing occurs in several steps using divers configurations elements.
PSFj loads images and detects beads located on the image stack. It then extracts the beads into substacks of images of the size dictated by the variable **Frame size** in order to calculate the x,y and z resolutions. It uses a 2D fitting of the bead image focal plane in order to calculate the x and y resultions, and a 1D fit for the z resolution. Once done, the software compiles the informations of each beads into tables and graphics. In the case, of multichannel analysis, a additional step tries to associate the signals coming from different channels to the same bead. In other words, for each bead detected in one channel, the software tries to find the corresponding bead of the other channel.

After that, the bead data is subject to several filtering that ensure that the displayed results come from fitting presenting a good signal to noise ratio.


### The three most important classes

In order to manipulate at a code level, it's important to understand the role of the different classes involved in the processing.

#### BeadImageManager

This classes deals with data loading, processing and export in a high level. The BeadImageManager was created to allow easy management of the process.



#### Microscope

This classes simply holds the microscope configuration. This classes is widely used to convert pixel data into the metric system.

#### BeadImage

This classes takes care of loading the pixel data from a single stack, and defines diverse parameter of the stack like the focal plane, the segmentation threshold or the frame size.

#### In practice...
~~~java


BeadImageManager manager = new BeadImageManager();

// an object representing the image is created, however, the image is not yet loaded
BeadImage image = new BeadImage("path/to/image");

image.setMicroscope(new Microscope(new IniFile("path/to/microscope_configuration.ini")));

// loads the image from the memory
image.workFromMemory();

// Calculates the focal plane automatically
image.autoFocus();

// set the bead frame to 20 times the thoretical x/y resolution
image.setBeadEnlargement(20);

manager.add(image);

// make sure all the parameters are set
manager.verifyBeadImageParameters();

// do the processing
manager.processFiles();

// will export a PDF sum up in the same folder as the image
manager.exportPDFSumUp(false);

~~~

A good example is to look at the PSFj.java file which more extensively the BeadImageManager and also deals with the multichannel cases.

### Diving into the data model

... coming soon.

## Licencing

PSFj is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
