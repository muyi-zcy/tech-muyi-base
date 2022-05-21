package tech.muyi.util.request;

import java.io.Serializable;

/**
 * @author: muyi
 * @date: 2021-01-12 23:18
 */
public class UnitDO implements Serializable {
    private static final long serialVersionUID = -7779110672848186411L;

    public String id;

    public String number;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
