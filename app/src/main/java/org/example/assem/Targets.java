package org.example.assem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.temp.LabelList;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class Targets {
	public LabelList labels;
}