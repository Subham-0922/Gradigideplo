package com.gradigi.response;

import java.util.List;
import com.gradigi.model.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CSVFile {

	private Team team;
	private List<Integer> users;
}
