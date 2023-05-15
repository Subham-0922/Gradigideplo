package com.gradigi.sorting;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import com.gradigi.model.Team;

public class TeamSorting implements Comparator<Map.Entry<Team, Double>> {

	@Override
	public int compare(Entry<Team, Double> o1, Entry<Team, Double> o2) {
		return (o2.getValue()).compareTo(o1.getValue());
	}
}
