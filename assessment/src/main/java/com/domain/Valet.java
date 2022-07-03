package com.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Valet {
	private List<Observations> observations;

	public List<Observations> getObservations() {
		return observations;
	}

	public void setObservations(List<Observations> observations) {
		this.observations = observations;
	}
}
