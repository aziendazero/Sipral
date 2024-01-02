package com.phi.cs.view.util;

import java.util.Comparator;

import com.phi.cs.view.bean.Suggestion;

public class SuggestionComparator implements Comparator<Suggestion> {

	@Override
	public int compare(Suggestion firstSuggestion, Suggestion secondSuggestion) {

		if (firstSuggestion == null && secondSuggestion == null) {
			return 0;
		} else if (firstSuggestion == null) {
			return -1;
		} else if (secondSuggestion == null) {
			return 1;
		}
		
		String firstLabel = firstSuggestion.getLabel();
		String secondLabel = secondSuggestion.getLabel();
		
		if (firstLabel == null) {
			return -1;
		} else if (secondLabel == null) {
			return 1;
		}
		
		return firstLabel.compareTo(secondLabel);
	}

}
