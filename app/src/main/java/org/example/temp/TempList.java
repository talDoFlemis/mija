package org.example.temp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Data
@Builder
public class TempList {
    public Temp head;
    public TempList tail;

    public TempList(Temp[] tempList) {
        if (tempList != null && tempList.length > 0) {
            for (Temp temp : tempList) {
                add(temp);
            }
        } else {
            head = null;
            tail = null;
        }
    }

    public void add(Temp temp) {
        if (head == null) {
            head = temp;
        } else if (tail == null) {
            tail = new TempList(temp, null);
        } else {
            tail.add(temp);
        }
    }
}
