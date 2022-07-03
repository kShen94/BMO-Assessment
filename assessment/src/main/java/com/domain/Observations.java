package com.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Observations {
	 @JsonProperty("AVG.INTWO")
	private AVGINTWO AVGINTWO;
	 @JsonProperty("FXUSDCAD")
	private FXUSDCAD FXUSDCAD;

	
	public FXUSDCAD getFXUSDCAD() {
		return FXUSDCAD;
	}

	public void setFXUSDCAD(FXUSDCAD fXUSDCAD) {
		this.FXUSDCAD = fXUSDCAD;
	}

	public AVGINTWO getAVGINTWO() {
		return AVGINTWO;
	}

	public void setAVGINTWO(AVGINTWO aVGINTWO) {
		this.AVGINTWO = aVGINTWO;
	}
}
