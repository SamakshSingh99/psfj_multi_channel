/*
    This file is part of PSFj.

    PSFj is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
 */
package knop.psfj.graphics;

import ij.process.ImageProcessor;
import knop.psfj.BeadImage;
import knop.psfj.FovDataSet;
import knop.psfj.PSFj;
import knop.psfj.heatmap.HeatMapGenerator;

/** Heat map of fitted bead signal and its variation from the channel median. */
public class IntensityHeatMap extends FullHeatMap {

	private double medianIntensity = 1.0;

	public IntensityHeatMap(FovDataSet dataSet, BeadImage image) {
		super(dataSet, image, PSFj.CORRECTED_INTENSITY);
		if (dataSet.getColumnSize() > 0) {
			medianIntensity = dataSet.getColumnStatistics(
					PSFj.CORRECTED_INTENSITY).getPercentile(50);
			if (!(medianIntensity > 0.0))
				medianIntensity = 1.0;
		}
		initGenerator(NORMALIZED);
		initGenerator(NOT_NORMALIZED);
	}

	@Override
	public ImageProcessor getGraph() {
		return getGraph(NORMALIZED);
	}

	@Override
	public void initGenerator(int normalized) {
		HeatMapGenerator generator = new HeatMapGenerator(dataSet, image);
		generator.setCurrentLUT("psfj_planarity");
		generator.setScaleDivision(3);
		if (normalized == NORMALIZED) {
			generator.setCurrentColumn(PSFj.NORMALIZED_INTENSITY);
			generator.setMinAndMax(0.5, 1.0, 1.5);
			generator.setPlotMinAndMax(0.5, 1.0, 1.5);
			generator.setUnit("x median");
			generator.setMinAndMaxLabels("50% of median", "median",
					"150% of median");
		} else {
			generator.setCurrentColumn(PSFj.CORRECTED_INTENSITY);
			generator.setMinAndMax(medianIntensity * 0.5, medianIntensity,
					medianIntensity * 1.5);
			generator.setPlotMinAndMax(medianIntensity * 0.5, medianIntensity,
					medianIntensity * 1.5);
			generator.setUnit("AU");
			generator.setMinAndMaxLabels("50% of median", "median",
					"150% of median");
		}
		setGenerator(generator, normalized);
	}

	@Override
	public String getSaveId() {
		return image.getImageNameWithoutExtension() + "_heatmap_intensity";
	}

	@Override
	public String getShortDescription() {
		return "Background-corrected bead intensity across the field of view.";
	}
}
