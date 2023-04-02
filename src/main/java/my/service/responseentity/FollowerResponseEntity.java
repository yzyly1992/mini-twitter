package my.service.responseentity;

import my.service.dto.FollowerDTO;

public class FollowerResponseEntity extends BasicResponseEntity {

    private FollowerDTO data;

    public FollowerDTO getData() {
        return data;
    }

    public void setData(FollowerDTO data) {
        this.data = data;
    }
}
